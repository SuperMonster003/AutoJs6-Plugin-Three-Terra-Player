<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="audio-player-ic-launcher" border="0" width="128" />
  </p>

  <p>파일 관리자 플러그인. 앱 내 및 백그라운드 제어로 오디오 파일 재생</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Audio-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 언어 (Languages)

******

현재 README.md는 다음 언어를 지원합니다:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ja.md)
- 한국어 [ko] # 현재
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ar.md)

******

### 소개

******

오디오 플레이어는 파일 관리자에서 연 오디오 파일을 위한 앱 내 컨트롤러와 비공개 백그라운드 재생 서비스를 제공합니다. 오디오 MIME 유형을 가진 content URI의 읽기 전용 Android ACTION_VIEW 요청도 받을 수 있습니다.

******

### 기능

******

- 호스트가 이미 인식하는 18개 오디오 파일 확장자에 대해 Explorer Action 프로토콜 v2를 통해 기본 읽기 전용 작업 `play-audio`를 등록합니다.
- Media3 ExoPlayer와 MediaSessionService로 오디오를 재생하며 오디오 포커스, 출력 연결 해제 처리, 로컬 깨우기 모드, 백그라운드 재생 및 시스템 미디어 제어를 지원합니다.
- 제목 메타데이터와 재생 제어, 선택적인 Android 13+ 알림 권한 안내 및 재생 오류 피드백을 포함한 컨트롤러 화면을 제공합니다.
- `audio/*`의 읽기 전용 `content` URI에 대한 독립 Android ACTION_VIEW 요청을 받고 호출자의 extras와 광범위한 URI 권한을 버립니다.
- 디코더 실패 시 다른 호환 앱으로 파일을 여는 대안을 제공하고 자체 반복을 방지하기 위해 이 플러그인을 제외합니다.

******

### 탐색기 파일 확장자

******

탐색기 카탈로그는 의도적으로 다음 확장자만 일치시키고 MIME 유형 목록을 비워 둡니다:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave
```

확장자 일치는 디코딩을 보장하지 않습니다. 실제 재생은 Media3, Android 플랫폼, 기기 코덱 및 파일 내용에 따라 달라집니다.

******

### 호스트 동작

******

플러그인이 설치되면 파일 관리자는 나열된 확장자에 기본 작업으로 오디오 재생을 표시합니다. 이를 선택하면 플러그인 컨트롤러가 열리고 선택한 파일에 대한 임시 읽기 전용 접근으로 비공개 재생 서비스가 시작됩니다.

플러그인이 없으면 이 작업이 표시되지 않습니다. 호스트는 오디오 파일에 대한 기존 읽기 전용 외부 ACTION_VIEW 흐름을 유지하므로 설치된 다른 오디오 앱이 파일을 처리할 수 있습니다. 호환되는 외부 앱이 없으면 호스트에 대체 재생 인터페이스가 추가되지 않습니다.

******

### 플러그인 인터페이스

******

호스트는 다음 식별자로 플러그인을 검색하고 실행합니다:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: audio-player
engine: explorer-action
variant: default
Explorer action id: play-audio
Explorer protocol version: 2
Explorer MIME types: []
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/*
required host build: 5269
```

버전 1은 기본 파일 관리자에서 단일 파일용 기본 읽기 전용 작업을 제공합니다. 카탈로그는 확장자만 사용하고 독립 Android 진입점은 계속 `audio/*`를 받습니다.

호스트 빌드 5269 이상이 필요합니다.

******

### 보안

******

플러그인은 저장소 또는 네트워크 권한을 요청하지 않습니다. 파일 관리자 진입점은 호스트 서명 권한으로 보호되며 프로토콜 v2, 대상 및 상위 content URI, ClipData, 원본 화면, 표시 이름, MIME 유형, 선언된 크기 및 읽기 전용 플래그를 엄격하게 검증합니다. 비공개 재생 구성 요소에는 대상 URI와 읽기 권한만 전달합니다. 공개 Android 진입점은 읽기 전용 content 오디오 요청만 받고 쓰기, 영구 및 접두사 권한을 거부하며 호출자의 임의 extras를 전달하지 않습니다.

******

### 안전 제한

******

- 탐색기 작업당 대상 파일 1개.
- 선언된 크기가 8 TiB를 넘는 탐색기 요청은 거부됩니다.
- 탐색기 작업은 파일 이름 확장자로만 선택되며 실행 시 오디오 MIME 유형도 검증합니다.
- 공개 Android 진입점에는 ACTION_VIEW, `content` URI, `audio/*` 및 읽기 권한이 필요합니다.
- 알림 권한은 선택 사항입니다. 거부하면 알림 창 제어가 숨겨지지만 재생은 차단되지 않습니다.
- 완료 또는 디코더 오류 시 재생을 안전하게 종료합니다. 외부 대안은 새 읽기 권한만 전달하고 이 플러그인을 제외합니다.

******

### 릴리스 기록

******

# v1.0.1

###### 2026/08/08

* `수정` 플러그인 센터에서 활성화할 때 서비스 바인딩이 null이 되는 문제
* `개선` 더 명확하고 간결한 플러그인 이름, 설명 및 사용자 문서

# v1.0.0

###### 2026/08/02

* `기능` 플러그인 ID `audio-player`, 작업 ID `play-audio`, 엔진 `explorer-action`, 변형 `default`를 사용하는 Audio Player 플러그인
* `기능` 호스트의 18개 오디오 확장자를 위한 파일 관리자 기본 읽기 전용 작업, 필수 호스트 빌드 5269 이상
* `기능` 오디오 포커스, 출력 연결 해제 처리, 로컬 깨우기 모드, 백그라운드 재생, 시스템 미디어 제어 및 비공개 컨트롤러를 포함한 Media3 ExoPlayer와 MediaSessionService 재생
* `기능` 권한을 거부해도 재생을 차단하지 않는 선택적 Android 13+ 알림 권한 안내
* `기능` `content` URI 오디오 요청을 위한 독립 읽기 전용 Android ACTION_VIEW 지원과 디코더 실패 시 다른 호환 앱으로 전달하는 자체 반복 방지
* `기능` 프로토콜, URI, ClipData, 원본, 이름, MIME, 크기 및 권한의 엄격한 검증, 저장소 또는 네트워크 권한 없음, 최소 읽기 권한만 전달
* `기능` 스페인어, 프랑스어, 러시아어, 아랍어, 일본어, 한국어, 영어, 중국어 간체, 홍콩 중국어 번체 및 대만 중국어 번체로 현지화된 메타데이터, UI 텍스트, 사용 안내, README 및 변경 기록
* `의존성` AndroidX Media3 ExoPlayer, Session 및 UI 버전 1.10.1 추가

##### 더 많은 릴리스

* [CHANGELOG-ko.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/assets/doc/CHANGELOG-ko.md)

******

### 빌드

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release 빌드:

```powershell
.\gradlew.bat :app:assembleRelease
```

빌드 매개변수는 `version.properties`에서 가져옵니다. 현재 최소 SDK는 24이고 대상 SDK는 36입니다.

******

### 리소스 구성

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml`은 플러그인 메타데이터와 UI 텍스트를 현지화합니다. `plugin_instruction.md`는 호스트에 표시되는 안내를 제공합니다. `.python/generate_markdown.py`는 JSON 원본에서 현지화된 README와 변경 기록을 생성합니다.

******

### 링크

******

- AutoJs6 문서: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- Android 보안 파일 공유: https://developer.android.com/training/secure-file-sharing
