"""Read-only checks for the repository's Android/plugin engineering contract."""
from pathlib import Path
import argparse
import json
import re
import subprocess
import sys
import unicodedata
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
ANDROID = '{http://schemas.android.com/apk/res/android}'
BUILD_TOLERANCE = 10
LOCALES = ['values', 'values-en', 'values-ar', 'values-es', 'values-fr', 'values-ja',
           'values-ko', 'values-ru', 'values-zh', 'values-zh-rHK', 'values-zh-rTW']

def non_ascii_punctuation(text):
    # Every language uses ASCII punctuation (D:/idea-projects/COMMON_AGENTS.md); U+00B7 stays a value separator.
    return any(ord(ch) > 0x7F and ord(ch) != 0xB7 and unicodedata.category(ch).startswith('P') for ch in text)

def check(root, pending_commit=False):
    errors = []
    def require(condition, message):
        if not condition: errors.append(message)
    props = dict(re.findall(r'^([A-Z_]+)=(.*)$', (root/'version.properties').read_text(encoding='utf-8-sig'), re.M))
    count = int(subprocess.check_output(['git','-C',str(root),'rev-list','--count','HEAD']))
    # VERSION_BUILD follows the commit count within a shared tolerance (D:/idea-projects/COMMON_AGENTS.md).
    drift = int(props['VERSION_BUILD']) - (count + int(pending_commit))
    require(abs(drift) <= BUILD_TOLERANCE, f'VERSION_BUILD must stay within {BUILD_TOLERANCE} of the commit count (off by {drift})')
    settings = (root/'settings.gradle.kts').read_text(encoding='utf-8-sig')
    # Any published release is accepted; shared plugins declared here must use one version.
    versions = re.findall(r'autojs6-(platform-versions|native-alignment)"\) version "(\d+\.\d+\.\d+)"', settings)
    require('platform-versions' in [plugin for plugin, _ in versions], 'platform-versions must come from a published release')
    require(len({version for _, version in versions}) == 1, 'Shared plugin versions must match')
    require('mavenLocal(' not in settings and 'autojs.buildPlugins.includeBuild' not in settings, 'Local platform substitution is forbidden')
    root_name = re.search(r'rootProject.name\s*=\s*"([^"]+)"', settings)
    require(root_name and re.fullmatch('[a-z0-9-]+', root_name[1]), 'rootProject.name must be lowercase kebab-case')
    require(not (root/'gradle/data').exists(), 'Consumer compatibility overrides must be removed')
    for relative in ['LICENSE','app/src/main/res/mipmap/ic_launcher.png', '.python/check_markdown.bat']:
        require((root/relative).is_file(), 'Missing ' + relative)
    for relative in ['AGENTS.md','CLAUDE.md','sign.properties','app/sm003.jks','local.properties','migration.pre-platform-versions.bak']:
        require(subprocess.run(['git','-C',str(root),'check-ignore','-q',relative]).returncode == 0, 'Not ignored: ' + relative)
    manifest = ET.parse(root/'app/src/main/AndroidManifest.xml').getroot()
    app = manifest.find('application')
    require(app.get(ANDROID+'allowBackup') == 'false', 'Backup must be disabled')
    require(app.get(ANDROID+'label') == '@string/app_name', 'Application label must use app_name')
    metadata = {e.get(ANDROID+'name'): e.get(ANDROID+'value') for e in app.findall('meta-data')}
    wake = metadata.get('org.autojs.plugin.WAKE_ACTIVITY')
    activities = [e for e in app.findall('activity') if e.get(ANDROID+'name') == wake]
    require(len(activities) == 1, 'Wake metadata must identify one activity')
    if activities:
        activity = activities[0]
        require(activity.get(ANDROID+'exported') == 'true', 'Wake must be exported')
        require(activity.get(ANDROID+'permission') == 'org.autojs.permission.PLUGIN', 'Wake must require plugin permission')
        require(activity.get(ANDROID+'theme') == '@android:style/Theme.NoDisplay', 'Wake must use NoDisplay')
        filters = activity.findall('intent-filter')
        require(any(any(a.get(ANDROID+'name') == 'org.autojs.plugin.action.WAKE' for a in f.findall('action'))
                    and any(c.get(ANDROID+'name') == 'android.intent.category.DEFAULT' for c in f.findall('category')) for f in filters), 'Wake action/category missing')
    english = None
    for locale in LOCALES:
        path = root/'app/src/main/res'/locale/'strings.xml'
        require(path.is_file(), 'Missing language ' + locale)
        if not path.exists(): continue
        resource = ET.parse(path).getroot()
        strings = resource.findall('string')
        names = [e.get('name') for e in strings]
        require(names == sorted(names), 'Unsorted strings: ' + locale)
        values = {e.get('name'): ''.join(e.itertext()) for file in path.parent.glob('*.xml')
                  for e in ET.parse(file).getroot().findall('string') if e.get('translatable') != 'false'}
        require('plugin_description' in values, 'Missing description: ' + locale)
        require(not any(e.get('translatable') == 'false' or e.get('name') == 'app_name' for e in strings), 'Nontranslatable identity in strings.xml: ' + locale)
        require(not resource.findall('plurals') and not resource.findall('string-array'), 'Mixed resource types: ' + locale)
        require(not any(non_ascii_punctuation(v) for v in values.values()), 'Non-ASCII punctuation: ' + locale)
        require(not re.search(r'[.!?;,:]$', values.get('plugin_description','')), 'Description punctuation: ' + locale)
        if locale == 'values': english = values
        elif locale == 'values-en': require(values == english, 'Default and explicit English differ')
    require('简体中文' in (root/'README.md').read_text(encoding='utf-8-sig'), 'Root README must identify Simplified Chinese')
    require(not list((root/'.changelog').glob('*.md')) or all(p.name.startswith('template') for p in (root/'.changelog').glob('*.md')), 'Generated changelogs must not live in .changelog')
    return errors

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--pending-commit', action='store_true', help='expect the next commit number during review')
    args = parser.parse_args()
    errors = check(ROOT, args.pending_commit)
    print(json.dumps({'repository': ROOT.name, 'errors': errors}, ensure_ascii=False, indent=2))
    return bool(errors)

if __name__ == '__main__':
    sys.exit(main())
