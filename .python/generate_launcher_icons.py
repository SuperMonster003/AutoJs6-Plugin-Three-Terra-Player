"""Generate separate transparent UI icons and adaptive/legacy launcher icons.

The retained source alpha is the artwork. Colors and output geometry are generated,
never inferred from antialiased source RGB. Run with --check to verify without writes.
Launcher colors stay dark in every configuration because launchers can cache a day
icon indefinitely. Only the transparent UI/README icons follow the application theme.
"""

from __future__ import annotations

import argparse
import io
import math
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "app/src/main/res"
SOURCE = ROOT / ".python/icons/three-terra-original.png"
SIZE = 432
SCALE = 4
UI_GLYPH = 0.66
ADAPTIVE_GLYPH = 0.44
DAY_GLYPH = (0x27, 0x27, 0x27)
NIGHT_GLYPH = (0xD8, 0xD8, 0xD8)
NIGHT_BACKGROUND = (0x21, 0x21, 0x21, 255)


def source_alpha() -> Image.Image:
    alpha = Image.open(SOURCE).convert("RGBA").getchannel("A")
    bounds = alpha.getbbox()
    if bounds is None:
        raise ValueError("Icon source has no visible artwork")
    alpha = alpha.crop(bounds)
    radius = 0.5 * ADAPTIVE_GLYPH * 108 * math.hypot(1, alpha.height / alpha.width)
    if radius >= 33:
        raise ValueError(f"Adaptive artwork exceeds the 66 dp safe circle: radius {radius:.2f} dp")
    return alpha


def render(alpha: Image.Image, ratio: float, color: tuple[int, int, int], background=None) -> Image.Image:
    size = SIZE * SCALE
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    if background is not None:
        ImageDraw.Draw(canvas).ellipse((0, 0, size - 1, size - 1), fill=background)
    width = round(size * ratio)
    height = max(1, round(width * alpha.height / alpha.width))
    scaled_alpha = alpha.resize((width, height), Image.Resampling.LANCZOS)
    glyph = Image.new("RGBA", (width, height), (*color, 255))
    glyph.putalpha(scaled_alpha)
    canvas.alpha_composite(glyph, ((size - width) // 2, (size - height) // 2))
    if background is not None:
        return canvas.resize((SIZE, SIZE), Image.Resampling.LANCZOS)
    # Resize only alpha for transparent artwork: premultiplied RGBA resampling can
    # change foreground RGB by one level, including at fully opaque pixels.
    result = Image.new("RGBA", (SIZE, SIZE), (*color, 255))
    result.putalpha(canvas.getchannel("A").resize((SIZE, SIZE), Image.Resampling.LANCZOS))
    return result


def generated_files() -> dict[Path, bytes]:
    alpha = source_alpha()
    images = {
        "mipmap/ic_launcher.png": render(alpha, UI_GLYPH, DAY_GLYPH),
        "mipmap-night/ic_launcher.png": render(alpha, UI_GLYPH, NIGHT_GLYPH),
        "mipmap/ic_launcher_system.png": render(alpha, UI_GLYPH, NIGHT_GLYPH, NIGHT_BACKGROUND),
        "mipmap/ic_launcher_system_foreground.png": render(alpha, ADAPTIVE_GLYPH, NIGHT_GLYPH),
        "mipmap/ic_launcher_monochrome.png": render(alpha, ADAPTIVE_GLYPH, (0, 0, 0)),
    }
    result = {}
    for name, image in images.items():
        output = io.BytesIO()
        image.save(output, format="PNG", optimize=True)
        result[RES / name] = output.getvalue()
    adaptive = '''<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_system_foreground"/>
    <monochrome android:drawable="@mipmap/ic_launcher_monochrome"/>
</adaptive-icon>
'''
    for directory in ("mipmap-anydpi-v26",):
        result[RES / directory / "ic_launcher_system.xml"] = adaptive.encode("utf-8")
    for directory, color in (("values", "#212121"),):
        result[RES / directory / "ic_launcher_background.xml"] = (
            '<?xml version="1.0" encoding="utf-8"?>\n<resources>\n'
            f'    <color name="ic_launcher_background">{color}</color>\n</resources>\n'
        ).encode("utf-8")
    return result


def obsolete_files() -> list[Path]:
    # These exact former resources collided with the transparent UI resource or
    # duplicated launcher layers. Never remove arbitrary files/directories.
    candidates = [RES / "values-night/ic_launcher_background.xml"]
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
