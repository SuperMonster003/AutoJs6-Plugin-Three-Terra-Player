"""Behavioral checks for optical geometry and the shipped Plugin Center artwork."""
from pathlib import Path
import sys
import unittest

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / ".python"))
import icon_geometry as geometry


class IconGeometryTest(unittest.TestCase):
    def test_sparse_artwork_gets_more_space_than_a_solid_shape(self):
        solid = Image.new("L", (100, 100), 255)
        sparse = Image.new("L", (100, 100))
        ImageDraw.Draw(sparse).rectangle((0, 0, 99, 99), outline=255, width=10)
        solid_ratio, _ = geometry.normalized_ratios(solid)
        sparse_ratio, _ = geometry.normalized_ratios(sparse)
        self.assertGreater(sparse_ratio, solid_ratio)
        for alpha, ratio in ((solid, solid_ratio), (sparse, sparse_ratio)):
            rendered = geometry.positioned_alpha(alpha, ratio)
            self.assertAlmostEqual(geometry.visual_size(rendered), .52, delta=.006)

    def test_ui_and_adaptive_artwork_have_the_same_visible_size(self):
        alpha = Image.new("L", (120, 90), 255)
        ui, adaptive = geometry.normalized_ratios(alpha)
        ui_alpha = geometry.positioned_alpha(alpha, ui)
        adaptive_alpha = geometry.positioned_alpha(alpha, adaptive)
        geometry.validate_circle(adaptive_alpha, 132)
        self.assertAlmostEqual(geometry.visual_size(ui_alpha),
                               geometry.visual_size(adaptive_alpha) * 108 / 72, delta=.006)

    def test_empty_clipped_and_unsafe_artwork_is_rejected(self):
        with self.assertRaises(ValueError):
            geometry.normalized_ratios(Image.new("L", (64, 64)))
        with self.assertRaises(ValueError):
            geometry.positioned_alpha(Image.new("L", (64, 64), 255), .6, optical_x=1)
        with self.assertRaises(ValueError):
            geometry.validate_circle(geometry.positioned_alpha(Image.new("L", (64, 64), 255), .8), 132)

    def test_shipped_plugin_center_pair_is_transparent_neutral_and_normalized(self):
        alphas = []
        for directory in ("mipmap", "mipmap-night"):
            with Image.open(ROOT / "app/src/main/res" / directory / "ic_plugin_center.png") as image:
                self.assertEqual(image.mode, "RGBA")
                self.assertEqual(image.size, (432, 432))
                alpha = image.getchannel("A")
                alphas.append(alpha.tobytes())
                self.assertGreater(alpha.histogram()[0], 432 * 432 / 2)
                self.assertEqual(alpha.getpixel((0, 0)), 0)
                geometry.validate_circle(alpha, 216)
                # Icon Studio size policy: verify the requested size, not a fixed band.
                recipe_file = ROOT / ".icons/recipe.json"
                if recipe_file.is_file():
                    import json
                    recipe = json.loads(recipe_file.read_text(encoding="utf-8"))
                    requested = recipe["params"]["geometry"]
                    self.assertEqual(requested["mode"], "normalized")
                    self.assertAlmostEqual(geometry.visual_size(alpha), .52 * requested["scale"], delta=.006)
                else:
                    self.assertGreater(geometry.visual_size(alpha), 0)
                r, g, b, _ = image.split()
                self.assertEqual(r.tobytes(), g.tobytes())
                self.assertEqual(g.tobytes(), b.tobytes())
        self.assertEqual(alphas[0], alphas[1])


if __name__ == "__main__":
    unittest.main()
