<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <h1>{{ product_name }}</h1>

  <p>
    <img src="{{ repo_url }}/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="audio-player-ic-launcher" border="0" width="128" />
  </p>

  <p>{{ text_plugin_synopsis }}</p>

  <p>
    <a href="{{ repo_url }}/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/{{ repo_slug }}?label=Release"/></a>
    <a href="{{ repo_url }}/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/{{ repo_slug }}?color=A24232&label=Issues"/></a>
    <a href="{{ repo_url }}/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/{{ repo_slug }}?color=534BAE&label=License"/></a>
  </p>
</div>

******

### {{ h3_languages_with_ascii }}

******

{{ p_languages_all_supported_for_readme }}:

{{ placeholder_ul_languages_all_supported }}

******

### {{ h3_introduction }}

******

{{ p_introduction }}

******

### {{ h3_functions }}

******

{{ placeholder_features }}

******

### {{ h3_supported_formats }}

******

{{ p_supported_formats }}:

```text
{{ supported_extensions }}
```

{{ p_decoder_support }}.

******

### {{ h3_host_behavior }}

******

{{ p_host_installed }}

{{ p_host_missing }}

******

### {{ h3_plugin_interface }}

******

{{ p_plugin_interface }}:

```text
service action: {{ plugin_action }}
execute action: {{ plugin_execute_action }}
plugin id: {{ plugin_id }}
engine: {{ plugin_engine }}
variant: {{ plugin_variant }}
Explorer action ids: {{ explorer_action_ids }}
Explorer protocol version: {{ explorer_protocol_version }}
Explorer MIME types: {{ explorer_mime_types }}
Android VIEW action: {{ android_view_action }}
Android VIEW MIME type: {{ android_view_mime_type }}
required host build: {{ required_host_build }}
```

{{ p_plugin_scope }}

{{ p_plugin_packaging }}

******

### {{ h3_security }}

******

{{ p_security }}

******

### {{ h3_security_limits }}

******

{{ placeholder_security_limits }}

******

### {{ h3_release_history }}

******

{{ placeholder_latest_release_history }}

##### {{ h5_for_more_release_history }}

* {{ placeholder_read_more_in_changelog_md }}

******

### {{ h3_build }}

******

```powershell
.\gradlew.bat :app:assembleDebug
```

{{ text_release_build }}:

```powershell
.\gradlew.bat :app:assembleRelease
```

{{ p_build_params }}.

******

### {{ h3_resource_layout }}

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

{{ p_resource_layout }}.

******

### {{ h3_links }}

******

- {{ text_link_autojs6_docs }}: {{ docs_autojs6_url }}
- {{ text_link_android_media3 }}: https://developer.android.com/media/media3
- {{ text_link_android_secure_file_sharing }}: https://developer.android.com/training/secure-file-sharing
