# -*- coding: utf-8 -*-
"""Per-language self-check for translated documentation sources."""
import argparse
import json
import re
import sys
import unicodedata
from pathlib import Path
from xml.etree import ElementTree

ROOT = Path(__file__).resolve().parents[1]
STRINGS_DIR = {
    "zh-Hans": "values-zh", "zh-Hant-HK": "values-zh-rHK", "zh-Hant-TW": "values-zh-rTW",
    "en": "values-en", "fr": "values-fr", "es": "values-es", "ja": "values-ja",
    "ko": "values-ko", "ru": "values-ru", "ar": "values-ar",
}
CATEGORIES = ("hint", "feature", "fix", "improvement", "dependency")


parser = argparse.ArgumentParser(
    description="Validate one language across README, changelog, and Android string sources",
)
parser.add_argument("code", choices=tuple(STRINGS_DIR), help="language code to validate")
code = parser.parse_args().code
errors = []


def load(path):
    return json.loads(Path(path).read_text(encoding="utf-8"))


def fullwidth(path):
    found = []
    for n, line in enumerate(Path(path).read_text(encoding="utf-8").splitlines(), 1):
        for ch in line:
            if unicodedata.east_asian_width(ch) in ("F", "W") and unicodedata.category(ch)[0] in ("P", "S", "Z"):
                found.append(f"fullwidth {ch!r} (U+{ord(ch):04X}) at {path.name}:{n}")
    return found


def shape(v):
    if isinstance(v, dict):
        return ("dict", tuple(sorted(v)))
    if isinstance(v, list):
        return ("list", len(v), tuple(shape(i) for i in v))
    return type(v).__name__


rn = ROOT / ".readme" / f"lang_{code}.json"
cn = ROOT / ".changelog" / f"lang_{code}.json"
ref = load(ROOT / ".readme" / "lang_zh-Hans.json")
new = load(rn)
if list(ref) != list(new):
    errors.append(f"readme key order/set differs: missing={sorted(set(ref) - set(new))} extra={sorted(set(new) - set(ref))} (order must match too)")
else:
    for k in ref:
        if shape(ref[k]) != shape(new[k]):
            errors.append(f"readme shape differs at {k!r}")

cref = load(ROOT / ".changelog" / "lang_zh-Hans.json")
cnew = load(cn)
if list(cref) != list(cnew):
    errors.append("changelog top-level keys/order differ")
else:
    dref, dnew = cref["$data"], cnew["$data"]
    if list(dref) != list(dnew):
        errors.append("changelog version keys/order differ")
    else:
        for v in dref:
            if set(dref[v]) != set(dnew[v]):
                errors.append(f"{v}: category set differs")
                continue
            if dref[v]["released_date"] != dnew[v]["released_date"]:
                errors.append(f"{v}: released_date differs")
            for cat in CATEGORIES:
                if cat in dref[v] and len(dref[v][cat]) != len(dnew[v][cat]):
                    errors.append(f"{v}: {cat} item count differs")

for p in (rn, cn):
    errors += fullwidth(p)

sx = ROOT / "app" / "src" / "main" / "res" / STRINGS_DIR[code] / "strings.xml"
desc = None
for el in ElementTree.fromstring(sx.read_text(encoding="utf-8")).iter("string"):
    if el.get("name") == "plugin_description":
        desc = (el.text or "").replace("\\'", "'").replace('\\"', '"')
syn = new.get("text_plugin_synopsis", "")
if desc != syn:
    errors.append(f"plugin_description != text_plugin_synopsis\n    xml : {desc!r}\n    json: {syn!r}")
if syn.endswith((".", "!", "?")):
    errors.append("text_plugin_synopsis ends with terminal punctuation")

common = load(ROOT / ".readme" / "common.json")
allowed = set(common) | set(new) | {"version_name"}


def scan(k, val):
    if isinstance(val, str):
        for m in re.findall(r"\{\{\s*([A-Za-z0-9_$.-]+)\s*\}\}", val):
            if m not in allowed:
                errors.append(f"unknown placeholder {{{{ {m} }}}} in readme key {k!r}")
    elif isinstance(val, list):
        for i in val:
            scan(k, i)
    elif isinstance(val, dict):
        for i in val.values():
            scan(k, i)


for k, v in new.items():
    scan(k, v)

if errors:
    print("LANG_FAIL", code)
    for e in errors:
        print(" -", e)
    sys.exit(1)
print("LANG_OK", code)
