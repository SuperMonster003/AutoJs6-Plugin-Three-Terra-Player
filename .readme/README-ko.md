<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <h1>3-Terra Player</h1>

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

- 호스트가 인식하는 모든 오디오 MIME과 알려진 확장자 19개에 Explorer Action v12 작업 `play-audio`와 `play-audio-selection`을 등록합니다.
- Media3 ExoPlayer와 MediaSessionService로 오디오를 재생하며 오디오 포커스, 출력 연결 해제 처리, 로컬 깨우기 모드, 백그라운드 재생 및 시스템 미디어 제어를 지원합니다.
- 앨범 아트, 태그, 기술 정보, 탐색, 10초 이동, 순차 / 셔플 / 한 곡 반복 모드, 0.5배부터 2배까지 배속 제어를 갖춘 재생 화면을 제공합니다.
- 호스트가 제공한 순서대로 명시적으로 선택한 오디오 파일을 최대 128개까지 재생하며 이전 / 다음 및 곡 이동과 삭제가 가능한 대기열을 제공합니다.
- 서비스가 유지하는 프리셋 / 사용자 지정 취침 타이머, 현재 곡 후 정지, 마지막 5초 페이드아웃 및 A-B 구간 반복을 지원합니다.
- 가장 최근에 연 파일 하나와 위치만 기억하고 다른 파일을 열면 즉시 교체하며 현재 메타데이터와 제어를 시스템에 반영합니다.
- 최대 128개 문서를 선택하는 런처를 제공하고 이전 WMA MIME을 포함한 읽기 전용 Android ACTION_VIEW `content` URI를 받습니다.
- 디코더 실패 시 다른 호환 앱으로 파일을 여는 대안을 제공하고 자체 반복을 방지하기 위해 이 플러그인을 제외합니다.
- 탐색기에서 한 곡을 열면 요청 범위 Host Session을 통해 읽을 수 있는 직접 형제 오디오를 자연 정렬한 제한된 재생목록으로 찾으며 명시적 다중 선택은 호스트 선택 순서를 유지합니다.
- 하나의 원본 색상으로 읽기 쉬운 팔레트를 만들어 AutoJs6를 따르고 Material 500 색상 19개와 사용자 RGB를 제공합니다. 설정에서 언어, 야간, 이어서 재생, 업데이트, 기록 및 앱 정보도 관리합니다.

******

### 탐색기 파일 확장자

******

탐색기 카탈로그는 `audio/*`를 광고하고 오래되거나 불완전한 MIME 표를 위해 다음 확장자도 사용합니다:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

확장자 일치는 디코딩을 보장하지 않습니다. 실제 재생은 Media3, Android 플랫폼, 기기 코덱 및 파일 내용에 따라 달라집니다.

******

### 호스트 동작

******

플러그인을 설치하고 AutoJs6에서 활성화하면 파일 관리자는 단일 파일에 오디오 재생을, 다중 선택 도구 모음에 선택한 오디오 재생을 표시합니다. 단일 파일 진입점은 선택한 곡에서 시작해 읽을 수 있는 직접 형제 오디오를 자연 순서로 찾을 수 있으며 명시적 다중 선택은 호스트 선택 순서를 유지합니다.

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
Explorer action ids: play-audio (single) / play-audio-selection (multiple, up to 128)
Explorer protocol version: 12 (accepts compatible read-only v4–v12 requests)
Explorer MIME types: [audio/*]
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/* plus legacy WMA MIME aliases
required host build: 5276
```

버전 1.3.0은 v12 읽기 전용 작업, 제한된 형제 접근, 독립 문서 선택기 및 Android 오디오 / WMA 진입점을 제공합니다. 선택적 Host Session이 없으면 선택 파일만 재생합니다.

같은 폴더 검색에는 AutoJs6 6.8.0 build 5276 이상과 Explorer Action v12가 필요하며 이후 플러그인 기능에서도 이 요구 사항을 높이지 않습니다.

******

### 보안

******

저장소 권한을 요청하지 않고 원본 파일을 쓰지 않습니다. 인터넷은 수동 또는 일일 GitHub 릴리스 확인에만 사용합니다. 서명 진입점은 v12, TARGETS, ClipData, 메타데이터와 읽기를 엄격히 검증하고 Host Session은 UID 고정 및 비재귀이며 공개 진입점은 읽기 전용입니다.

******

### 안전 제한

******

- 단일 작업은 정확히 하나의 선택 파일에서 시작해 읽을 수 있는 직접 형제 오디오의 제한된 재생목록을 만들 수 있으며 선택 작업은 순서를 유지한 중복 없는 파일 1개에서 128개를 받습니다.
- 선언된 크기가 8 TiB를 넘는 탐색기 요청은 거부됩니다.
- 탐색기 작업은 인식된 오디오 MIME 또는 명시된 확장자와 일치하고 각 대상을 정규화하고 검증합니다.
- 형제 검색은 호스트가 관리하는 요청 범위 세션에서만 가능하고 재귀하지 않습니다. 플러그인은 형제 URI를 추측하거나 파일 시스템 경로를 받지 않습니다.
- 공개 Android 진입점에는 ACTION_VIEW, `content` URI, 지원되는 오디오 또는 WMA MIME과 읽기 권한이 필요합니다.
- 알림 권한은 선택 사항입니다. 거부하면 알림 창 제어가 숨겨지지만 재생은 차단되지 않습니다.
- 완료 또는 디코더 오류 시 재생을 안전하게 종료합니다. 외부 대안은 새 읽기 권한만 전달하고 이 플러그인을 제외합니다.

******

### 릴리스 기록

******

# v1.3.0

###### 2026/08/29

* `기능` 런처와 다중 파일 독립 플레이어를 추가하고 언어, 야간 모드, 테마 색상, 이어서 재생, 업데이트, 릴리스 기록, 앱 및 개발자 정보 전용 설정 화면을 제공합니다
* `기능` 언어, 야간 모드 및 원본 색상은 공식 읽기 전용 설정 계약을 통해 기본적으로 AutoJs6를 따릅니다. 호스트를 사용할 수 없어도 선택지는 비활성 상태로 표시되고 앱 기본값으로 대체됩니다
* `기능` 수동 및 일일 자동 업데이트 확인, 무시한 버전 관리, 현지화된 내장 릴리스 기록을 추가했습니다
* `수정` 호스트 설정 공급자가 요구하는 보호된 플러그인 정보 서비스 진입점을 공개하여 항상 Host color unavailable로 표시되던 문제를 수정했습니다
* `수정` 탐색기 작업이 19개 알려진 확장자와 함께 오디오 MIME 및 WMA를 광고하여 호스트가 인식한 모든 오디오를 플러그인으로 바로 전달합니다
* `개선` 이어서 재생은 가장 최근 파일 하나만 기억하고 다른 파일을 열면 이전 기록을 즉시 삭제하며 재생을 마친 위치는 저장하지 않습니다
* `개선` 메타데이터 영역을 안정적인 세 줄로 고정하고 선택된 오디오 트랙의 샘플링 레이트와 비트레이트를 44.1 kHz · MP3 · 128 kbps 순서로 보완합니다
* `개선` A-B는 A 설정, B 설정, 해제의 명확한 세 번 누르기로 바뀌었으며 하단 제어 간격과 아이콘 크기 및 수직 중앙을 조정했습니다

# v1.2.2

###### 2026/08/27

* `수정` 플러그인을 덮어쓴 뒤 실행 중인 AutoJs6 파일 관리자가 캐시된 프로토콜 v4 작업을 계속 보내 모든 오디오 요청이 즉시 거부되던 문제를 수정했습니다. 게이트웨이는 읽기 전용 v4–v12 요청을 호환 처리하면서 계속 v12를 알립니다
* `수정` Android 또는 제조사 MIME 표가 와일드카드나 application 형식을 반환할 때 지원 대상으로 알린 오디오 확장자가 잘못 거부되던 문제를 수정했습니다. 이제 확장자 허용 목록이 안정적인 표준 오디오 MIME 형식을 제공합니다
* `개선` 거부된 Explorer 요청은 파일 이름, 표시 경로, URI를 포함하지 않는 개인정보 보호형 사유 코드를 기록하여 이후 계약 불일치를 바로 진단할 수 있습니다

# v1.2.1

###### 2026/08/27

* `기능` 단일 파일 오디오 재생 작업이 Explorer Action v12를 통해 같은 폴더의 읽을 수 있는 오디오 파일을 최대 128개 검색하고 선택한 트랙부터 자연 정렬 재생목록을 구성
* `수정` API 24에서 Android가 게이트웨이 Activity 매니페스트의 비권한 FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS 플래그를 추가할 때 정상 Explorer 요청을 잘못 거부하던 문제 수정
* `수정` 인접 트랙으로 자동 전환하면서 MediaSession 복귀 Intent를 다시 만들 때 발생하던 충돌 수정; 원래 권한이 부여된 대상은 현재 재생 항목과 독립적으로 Host Session 기준점으로 유지
* `개선` 선택한 트랙은 원래 content URI를 유지하고 인접 트랙은 요청 범위 Host Session 파일 설명자로만 스트리밍하며 인접 URI 추측이나 재귀, 쓰기, 저장소, 영구 접근을 추가하지 않음
* `개선` 오디오 확장자 필터가 .m4a 오디오와 이름이 같은 .mp4 동영상을 재생목록에서 제외하며 기존 명시적 다중 선택 재생목록 동작은 그대로 유지
* `개선` Host Session 소유권을 백그라운드 재생 서비스로 넘기고 재생목록 교체, 시작 실패, 재생 완료 또는 서비스 제거 시 닫음
* `의존성` 포함된 Explorer Action API를 프로토콜 v4에서 하위 호환 v12 인접 파일 읽기 확장으로 업그레이드하고 최소 호스트 빌드 5276 유지

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
