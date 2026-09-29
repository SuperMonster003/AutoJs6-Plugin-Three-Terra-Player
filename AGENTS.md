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

- The source is `.python/icons/three-terra-original.png` (432 x 432, preserved unchanged from the existing transparent day icon). Use its alpha as the common shape for all variants; normalize glyph colors to `#272727` for light application themes and `#D8D8D8` for dark themes. Preserve the source artwork and regenerate, never hand-edit output PNGs.
- `py .python/generate_launcher_icons.py` deterministically generates five 432 x 432 PNGs, one adaptive XML, and one background color XML. `--check` is read-only and rejects stale outputs and obsolete colliding resources. UI glyph width is 66%, adaptive glyph width is 0.44; the script verifies the 66 dp safe circle using the actual source aspect ratio.
- `mipmap/ic_launcher.png` and `mipmap-night/ic_launcher.png` are transparent UI/README assets on every Android version. Never add adaptive XML with this resource name. In-app references use `R.mipmap.ic_launcher`; the host plugin center resolves this resource under its own UI configuration.
- The Manifest's `icon` and `roundIcon` both use `@mipmap/ic_launcher_system`. It is an adaptive icon on API 26+ with `ic_launcher_system_foreground` and a single black `ic_launcher_monochrome`, or a filled circular PNG on older APIs. All system-icon configurations use glyph `#D8D8D8` on background `#212121`; there are no night/notnight launcher variants. This stable scheme avoids day icons retained by launcher caches; application UI assets still follow the app theme. System themed icons may be recolored by the launcher.
- Do not put a filled background in the adaptive foreground, use inset drawables, change launcher components to refresh caches, or clear launcher data. Resource-only `LauncherIconResourceTest` checks bitmap transparency, exact glyph colors, adaptive/legacy selection, dark default fallback, and Manifest wiring without changing user preferences. Actual launcher rendering still requires device inspection.
- The workspace reference `AUTOJS6_PLUGIN_BLACK_N_WHITE_ADAPTIVE_ICON_AGENTS.md` documents the current scheme and the platform limitations; the former shared `ic_launcher` adaptive/transparent naming and backgrounds are obsolete.

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
