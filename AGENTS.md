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

- Preserve `.python/icons/three-terra-original.png` unchanged and derive all shapes from its alpha. `py .python/generate_launcher_icons.py` deterministically creates 14 resources; `--check` is read-only. UI width is .66; adaptive width is .44. Validate every nonzero alpha pixel after scaling and offset against the 66 dp adaptive safe circle; also reject canvas clipping and legacy-disc overflow.
- `mipmap/ic_launcher.png` / `mipmap-night/ic_launcher.png` remain transparent application/README assets with exact glyph colors `#272727` / `#D8D8D8`, identical alpha, and no adaptive XML override. Plugin-center icons use the host UI configuration.
- Settings offers LIGHT, DARK (default), AUTO and TRANSPARENT. Four stable `${applicationId}.launcher.*IconAlias` components target the existing real Activity; never rename the published application ID or disable that Activity. Preserve all non-launcher intents and protected plugin entry points.
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
