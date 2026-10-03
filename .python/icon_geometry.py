"""AutoJs6 optical icon geometry v1 (2026-10-03).

Keep this small module identical in standalone plugin repositories. There is no
build-time dependency on a sibling checkout. See the workspace icon specification.
"""
from __future__ import annotations

import math
import struct
import zlib

from PIL import Image, ImageDraw

SIZE = 432
SUPERSAMPLE = 4
VISUAL_SIZE = 0.52
VISIBLE_TO_ADAPTIVE = 72 / 108
MEASUREMENT_ALPHA = 16
KEEP_RESOURCE = b'''<?xml version="1.0" encoding="utf-8"?>
<resources xmlns:tools="http://schemas.android.com/tools"
    tools:keep="@mipmap/ic_plugin_center" />
'''


def visual_size(alpha: Image.Image) -> float:
    """Balance the occupied rectangle and alpha-weighted ink, in canvas units.

    The rectangle accounts for tall/wide silhouettes; ink accounts for density.
    Low-alpha ringing does not enlarge the measured rectangle, but is retained
    for drawing and checked independently against the safe circle.
    """
    if alpha.mode != "L":
        raise ValueError("Geometry requires an alpha channel")
    bounds = alpha.point(lambda value: 255 if value >= MEASUREMENT_ALPHA else 0).getbbox()
    if bounds is None:
        raise ValueError("Icon source has no visible artwork")
    area = (bounds[2] - bounds[0]) * (bounds[3] - bounds[1])
    ink = sum(value * count for value, count in enumerate(alpha.histogram())) / 255
    return math.sqrt((area + ink) / 2) / alpha.width


def normalized_ratios(alpha: Image.Image, optical_scale: float = 1.0) -> tuple[float, float]:
    """One optical size for list/legacy artwork and the launcher's visible 72 dp.

    Optical corrections are limited to +/-6% and must be documented alongside
    the artwork; they are not separate per-surface tuning knobs.
    """
    if not 0.94 <= optical_scale <= 1.06:
        raise ValueError("Optical correction must stay within +/-6%")
    ui = VISUAL_SIZE * optical_scale / visual_size(alpha)
    if ui * max(1, alpha.height / alpha.width) > 0.80:
        raise ValueError("Artwork needs an explicit narrow/tall silhouette review")
    return ui, ui * VISIBLE_TO_ADAPTIVE


def positioned_alpha(alpha: Image.Image, ratio: float, optical_x=0.0, optical_y=0.0) -> Image.Image:
    side = SIZE * SUPERSAMPLE
    width = round(side * ratio)
    height = max(1, round(width * alpha.height / alpha.width))
    x = round((side - width) / 2 + optical_x * width)
    y = round((side - height) / 2 + optical_y * height)
    if width <= 0 or x < 0 or y < 0 or x + width > side or y + height > side:
        raise ValueError("Optical placement clips the artwork canvas")
    canvas = Image.new("L", (side, side))
    canvas.paste(alpha.resize((width, height), Image.Resampling.LANCZOS), (x, y))
    return canvas.resize((SIZE, SIZE), Image.Resampling.LANCZOS)


def maximum_radius(alpha: Image.Image) -> float:
    center = (alpha.width - 1) / 2, (alpha.height - 1) / 2
    raw = alpha.tobytes()
    maximum = 0.0
    # On each row the farthest nonzero pixel is one of its two endpoints.
    for y in range(alpha.height):
        row = raw[y * alpha.width:(y + 1) * alpha.width]
        indices = [x for x, value in enumerate(row) if value]
        if indices:
            maximum = max(maximum, *(math.hypot(x - center[0], y - center[1]) for x in (indices[0], indices[-1])))
    return maximum


def validate_circle(alpha: Image.Image, radius: float) -> None:
    maximum = maximum_radius(alpha)
    if maximum > radius:
        raise ValueError(f"Final antialiased artwork exceeds its safe circle: {maximum:.2f} > {radius:.2f} px")


def glyph(alpha: Image.Image, color: tuple[int, ...]) -> Image.Image:
    result = Image.new("RGBA", alpha.size, (*color[:3], 255))
    result.putalpha(alpha)
    return result


def legacy(foreground: Image.Image, color: tuple[int, ...]) -> Image.Image:
    side = SIZE * SUPERSAMPLE
    circle = Image.new("L", (side, side))
    ImageDraw.Draw(circle).ellipse((0, 0, side - 1, side - 1), fill=255)
    background = glyph(circle.resize((SIZE, SIZE), Image.Resampling.LANCZOS), color)
    return Image.alpha_composite(background, foreground)


def render(alpha, ratio, color, background=None, *, adaptive=False, optical_x=0.0, optical_y=0.0):
    placed = positioned_alpha(alpha, ratio, optical_x, optical_y)
    validate_circle(placed, SIZE * (33 / 108 if adaptive else .5))
    foreground = glyph(placed, color)
    return foreground if background is None else legacy(foreground, background)


def encode_png(image: Image.Image) -> bytes:
    """Fixed RGBA/filter/Huffman encoding, independent of Pillow's PNG backend."""
    rgba = image.convert("RGBA")
    pixels = rgba.tobytes()
    stride = rgba.width * 4
    rows = b"".join(b"\x00" + pixels[start:start + stride] for start in range(0, len(pixels), stride))
    compressor = zlib.compressobj(level=9, strategy=zlib.Z_FIXED)
    compressed = compressor.compress(rows) + compressor.flush()

    def chunk(kind, data):
        return struct.pack(">I", len(data)) + kind + data + struct.pack(">I", zlib.crc32(kind + data))

    header = struct.pack(">IIBBBBB", rgba.width, rgba.height, 8, 6, 0, 0, 0)
    return b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", header) + chunk(b"IDAT", compressed) + chunk(b"IEND", b"")
