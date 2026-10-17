# -*- coding: utf-8 -*-
"""Regression tests for the localized Markdown consistency gate."""

from __future__ import annotations

import importlib.util
import re
import tempfile
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
GENERATOR_PATH = ROOT / ".python" / "generate_markdown.py"
SPEC = importlib.util.spec_from_file_location("generate_markdown", GENERATOR_PATH)
if SPEC is None or SPEC.loader is None:
    raise RuntimeError(f"Unable to load generator from {GENERATOR_PATH}")
generate_markdown = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(generate_markdown)


class MarkdownConsistencyTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls) -> None:
        cls.repository_artifacts = generate_markdown.build_artifacts(ROOT)

    def setUp(self) -> None:
        self.temporary_directory = tempfile.TemporaryDirectory()
        self.temporary_root = Path(self.temporary_directory.name)
        self.temporary_artifacts = {}
        for repository_path, text in self.repository_artifacts.items():
            temporary_path = self.temporary_root / repository_path.relative_to(ROOT)
            temporary_path.parent.mkdir(parents=True, exist_ok=True)
            temporary_path.write_text(text, encoding="utf-8", newline="\n")
            self.temporary_artifacts[temporary_path] = text

    def tearDown(self) -> None:
        self.temporary_directory.cleanup()

    def test_repository_artifacts_are_current(self) -> None:
        generate_markdown.check_artifacts(ROOT, self.repository_artifacts)

    def test_stale_artifact_is_rejected(self) -> None:
        readme = self.temporary_root / "README.md"
        readme.write_text(
            readme.read_text(encoding="utf-8") + "\nintentional drift\n",
            encoding="utf-8",
            newline="\n",
        )

        with self.assertRaisesRegex(
            generate_markdown.MarkdownGenerationError,
            r"stale: README\.md",
        ):
            generate_markdown.check_artifacts(self.temporary_root, self.temporary_artifacts)

    def test_missing_artifact_is_rejected(self) -> None:
        readme = self.temporary_root / "README.md"
        readme.unlink()

        with self.assertRaisesRegex(
            generate_markdown.MarkdownGenerationError,
            r"missing: README\.md",
        ):
            generate_markdown.check_artifacts(self.temporary_root, self.temporary_artifacts)

    def test_orphan_artifact_is_rejected(self) -> None:
        orphan = (
            self.temporary_root
            / "app"
            / "src"
            / "main"
            / "res"
            / "raw-orphan"
            / "plugin_instruction.md"
        )
        orphan.parent.mkdir(parents=True, exist_ok=True)
        orphan.write_text("orphan\n", encoding="utf-8", newline="\n")

        with self.assertRaisesRegex(
            generate_markdown.MarkdownGenerationError,
            r"orphan: app.*raw-orphan.*plugin_instruction\.md",
        ):
            generate_markdown.check_artifacts(self.temporary_root, self.temporary_artifacts)


class PunctuationRuleTest(unittest.TestCase):
    def test_non_ascii_punctuation_is_rejected_in_every_language(self) -> None:
        # Arabic, CJK, fullwidth, dash, quote, Spanish, katakana middle dot and arrow samples.
        for code_point in (0x060C, 0x061B, 0x061F, 0x3001, 0xFF0C, 0x2013, 0x2019, 0x00AB, 0x00BF, 0x30FB, 0x2192):
            with self.subTest(code_point=f"U+{code_point:04X}"):
                with self.assertRaisesRegex(
                    generate_markdown.MarkdownGenerationError,
                    re.escape(f"U+{code_point:04X}"),
                ):
                    generate_markdown.validate_ascii_punctuation(Path("strings.xml"), "text" + chr(code_point))

    def test_value_separator_and_bidi_isolates_are_allowed(self) -> None:
        generate_markdown.validate_ascii_punctuation(
            Path("strings.xml"),
            "75% " + chr(0x00B7) + " " + chr(0x2068) + "3-Terra Player" + chr(0x2069) + " " + chr(0x200F) + "0:12",
        )

    def test_android_strings_are_checked(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "strings.xml"
            path.write_text(
                '<resources><string name="sample">a' + chr(0x060C) + " b</string></resources>",
                encoding="utf-8",
            )
            with self.assertRaisesRegex(generate_markdown.MarkdownGenerationError, re.escape("U+060C")):
                generate_markdown.read_android_strings(path)


if __name__ == "__main__":
    unittest.main()
