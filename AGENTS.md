# AutoJs6-Plugin-Three-Terra-Player engineering rules

These repository-specific rules apply the AutoJs6 new plugin repository reference dated 2026-09-13. Preserve deeper AGENTS.md constraints when working on vendored code.

- Start with git status, branch, recent commits and relevant diffs. Existing uncommitted work belongs to the user; never reset or silently include it in your commits.
- Inspect, validate and commit each complete change with Conventional Commits unless the user requests otherwise. Before each commit set VERSION_BUILD to the reachable HEAD commit count plus one. Verify equality after committing. Do not auto-increment it during assembly.
- VERSION_NAME follows semantic versioning. Update every current-version changelog JSON and generated document when changing behavior. Never claim an unexecuted device test or an unpublished candidate is released.
- Resolve the platform and native alignment plugins 1.8.3 from public repositories in root settings. The platform plugin must run before build-logic. Do not use Maven local, consumer gradle/data overrides or sibling build substitutions.
- Read Android/Kotlin plugin versions from platform system properties. Keep SDK and application versions in version.properties and preserve signing logic. Java/Kotlin source encoding is UTF-8.
- Builds must be self-contained. Keep local API artifacts with their source and checksum, or use controlled source modules. Do not read sibling project JAR/AAR files at build time.
- sign.properties, local.properties, keystores and migration backups are ignored local files. Never log or commit their secrets. appendDigestToReleasedFiles must assemble and validate the exact signed output set, actual versions and CRC32 before collecting a release.
- Preserve protected Wake metadata, NoDisplay activity, WAKE action and DEFAULT category. Activation does no model loading or networking. Test the Manifest contract and actual service discovery/Binder paths.
- PluginInfo uses installed package versions, localized description, stable identity and explicit true ABI capabilities. Preserve published AIDL order and negotiate additions. Bound inputs, resource ownership and cancellation remain part of the contract.
- Application titles stay English and nontranslatable. Keep all ten locales plus explicit English, sort strings by name, put plurals/arrays in separate files, and use ASCII punctuation and escaped Android quotes.
- Maintain the existing purpose-specific PNG at app/src/main/res/mipmap/ic_launcher.png. README references must resolve to actual assets and the correct repository.
- Edit .readme/.changelog JSON and templates, then run the generator and its true read-only --check mode. Root README is Simplified Chinese. Generated changelogs belong under app/src/main/assets/doc, not .changelog.
- Preserve settings/local release-history behavior, fallback languages, accessibility and failure recovery. Keep networking and data permissions accurate in user documentation.
- Native capabilities require ELF/ZIP alignment checks and real 16 KB execution evidence. Static checks do not substitute for device tests. Record unavailable OEM activation and device matrix coverage explicitly.

- The app has no ABI-specific native implementation. Its universal APK and explicit empty PluginInfo ABI list support every host ABI; redundant byte-identical ABI splits are not required.

## Launcher icon

- Preserve `.python/icons/three-terra-original.png` unchanged and derive all shapes from its alpha. `py .python/generate_launcher_icons.py` deterministically creates the launcher and Plugin Center resources; `--check` is read-only. UI/adaptive sizes are derived by Optical geometry v1 (see the section below). Validate every nonzero alpha pixel after scaling and offset against the 66 dp adaptive safe circle; also reject canvas clipping and legacy-disc overflow.
- `mipmap/ic_launcher.png` / `mipmap-night/ic_launcher.png` remain transparent application/README assets with exact glyph colors `#272727` / `#D8D8D8`, identical alpha, and no adaptive XML override. Plugin-center icons use the host UI configuration.
- Settings offers LIGHT, DARK, AUTO (default) and TRANSPARENT. Four stable `${applicationId}.launcher.*IconAlias` components target the existing real Activity; never rename the published application ID or disable that Activity. Preserve all non-launcher intents and protected plugin entry points.
- Fixed light uses `ic_launcher_system_light` with `#272727` on `#FAFAFA`; fixed dark uses `ic_launcher_system` with `#D8D8D8` on `#212121`. Both resolve to adaptive XML on API 26+ and circular PNG on older APIs. AUTO has its own resource ID: default-dark and notnight-light bitmap XML on legacy APIs, with matching default/notnight adaptive XML on API 26+. Do not use values aliases: PackageManager resolves those while installing and freezes the icon ID. TRANSPARENT uses `ic_launcher`.
- PackageManager component state is the persisted selection. Enable the new alias before disabling the old one, use DONT_KILL_APP, batch the final state on API 33+, and roll back failures. Keep mutable shortcut ownership valid. No polling or launcher-data clearing. Explain in the picker that AUTO depends on launcher refresh/configuration and TRANSPARENT may acquire a system background/mask; some home-screen shortcuts may need re-adding.
- `LauncherIconResourceTest` checks colors, transparency and API/config selection; `LauncherIconOptionsTest` checks all four aliases, one enabled launcher, stable target, persistence and exact state restoration. Device rendering still requires visual inspection. The workspace `AUTOJS6_PLUGIN_BLACK_N_WHITE_ADAPTIVE_ICON_AGENTS.md` defines the common convention; these explicit four choices supersede the previous fixed-dark-only/no-alias rule.

## Validation

```powershell
py .python/generate_markdown.py
py .python/generate_markdown.py --check
py .python/check_repository.py --pending-commit
.\gradlew.bat --no-daemon '-Djava.vendor=Eclipse Adoptium' '-Djava.vendor.version=Temurin-21.0.12.1+1' :app:assembleDebug :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebugAndroidTest :app:lintDebug
.\gradlew.bat :app:appendDigestToReleasedFiles
git diff --check
```

Run the relevant custom Python regression suites after changing their logic. After committing, run check_repository.py without --pending-commit and review git status. Install/activate/upgrade and Binder smoke tests on the exact signed release remain necessary evidence for an actual release.


## Standalone settings unification (2026-09-29)

- Follow the workspace `AUTOJS6_PLUGIN_STANDALONE_SETTINGS_AGENTS.md`. Keep the shared row order language, night mode, theme color, launcher icon; default appearance follows AutoJs6. Unavailable hosts fall back to system locale/night and the real host default color `#FFDEAD`.
- Use flat grouped rows with 16 sp titles, 14 sp summaries, min 72 dp height, 24 dp side padding, a 24 dp outline icon in a 40 dp slot, and text-aligned subtle dividers. Dialog/navigation rows end in a chevron; switch rows contain only a Material 3 switch and toggle once when the row is clicked.
- Choice dialogs are centered with 24 dp corners, at least 24 dp horizontal inset, max 560 dp width and bounded scrolling content above a fixed text-button footer. Language, night, theme and launcher drafts save only on OK; Cancel, Back and owner destruction discard the draft.
- `ThemeColorChooser` / `ThemeColorValue` provide the common 16 presets, one HEX/RGB input, local swatch/button preview and independent error semantics. Preserve old saved preset/custom colors even when absent from the new preset list. Surfaces, primary/secondary text, outlines and dividers use the shared neutral tokens; theme colors affect controls and emphasis, not page backgrounds.
- Launcher AUTO is the Manifest default. `LauncherIconStatePolicy` resolves explicit PM states first; normalize via the existing Activity and a non-exported MY_PACKAGE_REPLACED receiver. An ordinary upgrade preserves explicit choices and repairs duplicate/missing entries without adding a second preference source. Existing connected devices are switched to AUTO only under the user's separate instruction.
- Validate custom-color control tint, dialog scrolling, Cancel/OK semantics, API 24/modern launcher-state migration and device rendering. Keep about/developer rounded containers with transparent inner Three glyphs. Never infer release or device evidence from compilation alone.

## Optical icon standard (2026-10-03)

- Follow `../AUTOJS6_PLUGIN_BLACK_N_WHITE_ADAPTIVE_ICON_AGENTS.md` for every standalone plugin, including the Plugin Center. `.python/icon_geometry.py` v1 is a self-contained copy of the common geometry algorithm; keep its implementation identical across the standalone plugins. Never read sibling checkouts during a build.
- Derive size from the equal-weight combination of visible bounding-box area (alpha >= 16) and alpha-weighted ink area. Target visible size is 0.52 of the canvas, with only documented optical corrections in 0.94-1.06. The adaptive ratio is always the UI ratio multiplied by 72/108. This supersedes older hardcoded UI/adaptive widths in historical notes. Preserve aspect ratio, optical placement and final nonzero-alpha safety checks.
- Current derived widths: UI 0.7450, adaptive 0.4966 (rounded documentation values, not generation constants). Optical scale=1.00 and zero offsets.
- Generate `mipmap/ic_plugin_center.png` and its night counterpart from the same geometry as the transparent UI/launcher mode. They are transparent neutral artwork for installed and catalog entries, independent of the active launcher alias. Keep them through `raw/keep_plugin_center_icon.xml`. Existing separate brand assets retain their original purpose.
- Black, white and neutral grayscale are allowed for every plugin without per-plugin approval. Pure silhouettes default to #272727 / #D8D8D8; shaded artwork may preserve meaningful tonal details with R=G=B and matching day/night alpha. Stamp Mail is one example, not an exception. Keep light-theme artwork dark enough and dark-theme artwork light enough to remain legible. Do not introduce a filled background into the Plugin Center assets.
- Run the icon generator and its read-only `--check`, `.python/tests/test_icon_geometry.py`, existing icon regressions, and review the full set at 36/48/64 px in both themes and in launcher masks. `.github/workflows/icons.yml` verifies Windows/Linux reproducibility. Synthetic previews do not replace actual launcher verification.
