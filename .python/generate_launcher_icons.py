"""Generate separate transparent UI icons and adaptive/legacy launcher icons.

The retained source alpha is the artwork. Colors and output geometry are generated,
never inferred from antialiased source RGB. Run with --check to verify without writes.
Fixed light/dark and best-effort automatic launcher variants are independent of
transparent UI icons. Automatic color changes depend on the launcher configuration.
"""

from __future__ import annotations

import argparse
import math
from pathlib import Path

from PIL import Image, ImageDraw
import icon_geometry as geometry

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "app/src/main/res"
SOURCE = ROOT / ".python/icons/three-terra-original.png"
SIZE = 432
SCALE = 4
# Offset is a fraction of the rendered glyph width/height, not the canvas.
# Ember uses optical balance assessed in circular masks at 48/96/160 px.
OPTICAL_X = 0.0
OPTICAL_Y = 0.0
DAY_BACKGROUND = (0xFA, 0xFA, 0xFA, 255)
DAY_GLYPH = (0x27, 0x27, 0x27)
NIGHT_GLYPH = (0xD8, 0xD8, 0xD8)
NIGHT_BACKGROUND = (0x21, 0x21, 0x21, 255)


def source_alpha() -> Image.Image:
    alpha = Image.open(SOURCE).convert("RGBA").getchannel("A")
    bounds = alpha.getbbox()
    if bounds is None:
        raise ValueError("Icon source has no visible artwork")
    alpha = alpha.crop(bounds)
    return alpha


# Optical geometry v1; ratios are derived, not tuned independently by surface.
OPTICAL_SCALE = 1.0
UI_GLYPH, ADAPTIVE_GLYPH = geometry.normalized_ratios(source_alpha(), OPTICAL_SCALE)


def glyph_alpha(alpha: Image.Image, ratio: float, optical_x=OPTICAL_X, optical_y=OPTICAL_Y) -> Image.Image:
    size = SIZE * SCALE
    width = round(size * ratio)
    height = max(1, round(width * alpha.height / alpha.width))
    left = round((size - width) / 2 + width * optical_x)
    top = round((size - height) / 2 + height * optical_y)
    if left < 0 or top < 0 or left + width > size or top + height > size:
        raise ValueError("Optical offset clips the artwork canvas")
    canvas = Image.new("L", (size, size))
    canvas.paste(alpha.resize((width, height), Image.Resampling.LANCZOS), (left, top))
    return canvas.resize((SIZE, SIZE), Image.Resampling.LANCZOS)


def validate_circle(alpha: Image.Image, radius: float) -> None:
    # Inspect actual nonzero alpha, including resampling fringes, after optical
    # placement. Empty corners of an asymmetric glyph's bounding box are not ink.
    center = (SIZE - 1) / 2
    maximum = max(math.hypot(x - center, y - center)
                  for y in range(SIZE) for x in range(SIZE) if alpha.getpixel((x, y)))
    if maximum > radius:
        raise ValueError(f"Artwork exceeds its safe circle: {maximum:.2f} > {radius:.2f} px")


def render(alpha, ratio, color, background=None):
    return geometry.render(alpha, ratio, color, background,
                           adaptive=ratio == ADAPTIVE_GLYPH,
                           optical_x=OPTICAL_X, optical_y=OPTICAL_Y)


def generated_files() -> dict[Path, bytes]:
    alpha = source_alpha()
    images = {
        "mipmap/ic_launcher.png": render(alpha, UI_GLYPH, DAY_GLYPH),
        "mipmap-night/ic_launcher.png": render(alpha, UI_GLYPH, NIGHT_GLYPH),
        "mipmap/ic_launcher_system.png": render(alpha, UI_GLYPH, NIGHT_GLYPH, NIGHT_BACKGROUND),
        "mipmap/ic_launcher_system_foreground.png": render(alpha, ADAPTIVE_GLYPH, NIGHT_GLYPH),
        "mipmap/ic_launcher_system_light.png": render(alpha, UI_GLYPH, DAY_GLYPH, DAY_BACKGROUND),
        "mipmap/ic_launcher_system_light_foreground.png": render(alpha, ADAPTIVE_GLYPH, DAY_GLYPH),
        "mipmap/ic_launcher_monochrome.png": render(alpha, ADAPTIVE_GLYPH, (0, 0, 0)),
    }
    images["mipmap/ic_plugin_center.png"] = images["mipmap/ic_launcher.png"]
    images["mipmap-night/ic_plugin_center.png"] = images["mipmap-night/ic_launcher.png"]
    result = {}
    for name, image in images.items():
        result[RES / name] = geometry.encode_png(image)
    for suffix, foreground, background in (("", "ic_launcher_system_foreground", "ic_launcher_background"),
                                            ("_light", "ic_launcher_system_light_foreground", "ic_launcher_light_background")):
        adaptive = f'''<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/{background}"/>
    <foreground android:drawable="@mipmap/{foreground}"/>
    <monochrome android:drawable="@mipmap/ic_launcher_monochrome"/>
</adaptive-icon>
'''
        result[RES / "mipmap-anydpi-v26" / f"ic_launcher_system{suffix}.xml"] = adaptive.encode("utf-8")
    result[RES / "values/ic_launcher_background.xml"] = ('''<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#212121</color>
    <color name="ic_launcher_light_background">#FAFAFA</color>
</resources>
''').encode("utf-8")
    # PackageManager eagerly resolves values aliases while parsing Manifest icon
    # IDs. AUTO therefore needs real XML resources, not <item type="mipmap">.
    # Bitmap wrappers avoid duplicate legacy PNG bytes while preserving the ID.
    for qualifier, target in (("", "ic_launcher_system"), ("-notnight", "ic_launcher_system_light")):
        result[RES / f"mipmap{qualifier}" / "ic_launcher_system_auto.xml"] = (
            '<?xml version="1.0" encoding="utf-8"?>\n'
            '<bitmap xmlns:android="http://schemas.android.com/apk/res/android" '
            f'android:src="@mipmap/{target}" />\n'
        ).encode("utf-8")
        result[RES / f"mipmap{qualifier}-anydpi-v26" / "ic_launcher_system_auto.xml"] = result[
            RES / "mipmap-anydpi-v26" / f"{target}.xml"
        ]
    result[RES / "raw/keep_plugin_center_icon.xml"] = geometry.KEEP_RESOURCE
    return result


def obsolete_files() -> list[Path]:
    # These exact former resources collided with the transparent UI resource or
    # duplicated launcher layers. Never remove arbitrary files/directories.
    candidates = [RES / "values-night/ic_launcher_background.xml",
                  RES / "values/ic_launcher_system_auto.xml",
                  RES / "values-notnight/ic_launcher_system_auto.xml"]
    for directory in RES.glob("mipmap*"):
        for name in ("ic_launcher.xml", "ic_launcher_round.xml", "ic_launcher_round.png", "ic_launcher_foreground.png"):
            candidates.append(directory / name)
    candidates.append(RES / "mipmap-night/ic_launcher_monochrome.png")
    candidates.extend(RES / name for name in ("mipmap-notnight/ic_launcher_system.png", "mipmap-notnight/ic_launcher_system_foreground.png", "mipmap-notnight-anydpi-v26/ic_launcher_system.xml", "values-notnight/ic_launcher_background.xml", "mipmap-notnight-v29/ic_launcher_system_foreground.png", "values-notnight-v29/ic_launcher_background.xml"))
    return [path for path in candidates if path.is_file()]


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true", help="Check all generated resources without changing files")
    options = parser.parse_args()
    outputs = generated_files()
    stale = [path for path, expected in outputs.items() if not path.is_file() or path.read_bytes() != expected]
    obsolete = obsolete_files()
    if options.check:
        if stale or obsolete:
            raise SystemExit("Stale icon resources: " + ", ".join(str(p.relative_to(ROOT)) for p in stale + obsolete))
        print(f"Verified {len(outputs)} icon resources")
        return
    for path in obsolete:
        if not path.resolve().is_relative_to(RES.resolve()):
            raise ValueError("Icon output escaped resource directory")
        path.unlink()
    for path, data in outputs.items():
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_bytes(data)
        print(f"Generated {path.relative_to(ROOT).as_posix()}")


if __name__ == "__main__":
    main()
