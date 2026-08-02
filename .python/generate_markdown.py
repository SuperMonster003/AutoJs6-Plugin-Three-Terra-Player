# -*- coding: utf-8 -*-
import json
import re
from pathlib import Path


LANGUAGE_CODES = [
    "zh-Hans",
    "zh-Hant-HK",
    "zh-Hant-TW",
    "en",
    "fr",
    "es",
    "ja",
    "ko",
    "ru",
    "ar",
]
LANGUAGE_CODE_DEFAULT = "zh-Hans"
ANDROID_CHANGELOG_ALIASES = {
    "zh-Hans": ["zh", "zh-Hans"],
    "zh-Hant-HK": ["zh-rHK", "zh-Hant-HK"],
    "zh-Hant-TW": ["zh-rTW", "zh-Hant-TW"],
}


ROOT = Path(__file__).resolve().parents[1]
README_DIR = ROOT / ".readme"
CHANGELOG_DIR = ROOT / ".changelog"
ANDROID_CHANGELOG_DIR = ROOT / "app" / "src" / "main" / "assets" / "doc"


def load_json(path: Path):
    with path.open("r", encoding="utf-8") as file:
        return json.load(file)


def render_template(text: str, values: dict) -> str:
    def replace(match):
        key = match.group(1).strip()
        if key not in values:
            raise KeyError(f"Missing template value: {key}")
        return str(values[key])

    return re.sub(r"\{\{\s*([A-Za-z0-9_$.-]+)\s*\}\}", replace, text)


def render_dynamic(value, values: dict):
    if isinstance(value, dict):
        return {key: render_dynamic(item, values) for key, item in value.items()}
    if isinstance(value, list):
        return [render_dynamic(item, values) for item in value]
    if isinstance(value, str):
        return render_template(value, values)
    return value


def bullet_list(items):
    return "\n".join(f"- {item}" for item in items)


def markdown_link(label, url):
    return f"[{label}]({url})"


def load_languages():
    common = load_json(README_DIR / "common.json")
    languages = {}
    changelogs = {}
    for code in LANGUAGE_CODES:
        raw_language = load_json(README_DIR / f"lang_{code}.json")
        merged_language = {**common, **raw_language}
        languages[code] = render_dynamic(merged_language, merged_language)

        raw_changelog = load_json(CHANGELOG_DIR / f"lang_{code}.json")
        changelog_values = {key: value for key, value in raw_changelog.items() if key != "$data"}
        changelog_values = render_dynamic(changelog_values, changelog_values)
        changelogs[code] = {
            "values": changelog_values,
            "data": render_dynamic(raw_changelog["$data"], changelog_values),
        }
    return languages, changelogs


def format_changelog_items(changelog, limit=None):
    values = changelog["values"]
    chunks = []
    for index, (version_name, item) in enumerate(changelog["data"].items()):
        if limit is not None and index >= limit:
            break
        lines = [
            f"# {version_name}",
            "",
            f"###### {item['released_date']}",
            "",
        ]
        for category in ["hint", "feature", "fix", "improvement", "dependency"]:
            for text in item.get(category, []):
                lines.append(f"* `{values[f'changelog_label_{category}']}` {text}")
        chunks.append("\n".join(lines).rstrip())
    return "\n\n".join(chunks).rstrip() + "\n"


def build_language_list(target_code, languages):
    repo_url = languages[target_code]["repo_url"]
    lines = []
    for code in LANGUAGE_CODES:
        content = languages[code]
        label = f"{content['$name']} [{code}]"
        if code == target_code:
            lines.append(f"- {label} # {content['text_current_lowercase']}")
        else:
            lines.append(f"- {markdown_link(label, f'{repo_url}/blob/master/.readme/README-{code}.md')}")
    return "\n".join(lines)


def build_readme_values(code, languages, changelogs):
    content = dict(languages[code])
    repo_url = content["repo_url"]
    content["placeholder_ul_languages_all_supported"] = build_language_list(code, languages)
    content["placeholder_features"] = bullet_list(content["features"])
    content["placeholder_security_limits"] = bullet_list(content["security_limits"])
    content["placeholder_latest_release_history"] = format_changelog_items(
        changelogs[code],
        limit=3,
    ).rstrip()
    content["placeholder_read_more_in_changelog_md"] = markdown_link(
        f"CHANGELOG-{code}.md",
        f"{repo_url}/blob/master/app/src/main/assets/doc/CHANGELOG-{code}.md",
    )
    return content


def write_text(path: Path, text: str):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text, encoding="utf-8", newline="\n")
    print(f"Generated {path.relative_to(ROOT)}")


def generate_readmes(languages, changelogs):
    template = (README_DIR / "template_readme.md").read_text(encoding="utf-8")
    for code in LANGUAGE_CODES:
        output = render_template(template, build_readme_values(code, languages, changelogs))
        write_text(README_DIR / f"README-{code}.md", output)
        if code == LANGUAGE_CODE_DEFAULT:
            write_text(ROOT / "README.md", output)


def generate_changelogs(languages, changelogs):
    template = (CHANGELOG_DIR / "template_changelog.md").read_text(encoding="utf-8")
    for code in LANGUAGE_CODES:
        values = dict(languages[code])
        values["placeholder_release_history"] = format_changelog_items(changelogs[code]).rstrip()
        output = render_template(template, values)
        for name in ANDROID_CHANGELOG_ALIASES.get(code, [code]):
            write_text(ANDROID_CHANGELOG_DIR / f"CHANGELOG-{name}.md", output)
        if code == LANGUAGE_CODE_DEFAULT:
            write_text(ANDROID_CHANGELOG_DIR / "CHANGELOG.md", output)


def main():
    if LANGUAGE_CODE_DEFAULT not in LANGUAGE_CODES:
        raise ValueError(f"Default language code {LANGUAGE_CODE_DEFAULT!r} is not supported")
    languages, changelogs = load_languages()
    generate_changelogs(languages, changelogs)
    generate_readmes(languages, changelogs)


if __name__ == "__main__":
    main()
