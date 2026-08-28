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

- 호스트가 인식하는 18개 오디오 확장자에 대해 Explorer Action 프로토콜 v12로 단일 파일 작업 `play-audio`와 순서가 있는 다중 선택 작업 `play-audio-selection`을 등록합니다.
- Media3 ExoPlayer와 MediaSessionService로 오디오를 재생하며 오디오 포커스, 출력 연결 해제 처리, 로컬 깨우기 모드, 백그라운드 재생 및 시스템 미디어 제어를 지원합니다.
- 앨범 아트, 태그, 기술 정보, 탐색, 10초 이동, 순차 / 셔플 / 한 곡 반복 모드, 0.5배부터 2배까지 배속 제어를 갖춘 재생 화면을 제공합니다.
- 호스트가 제공한 순서대로 명시적으로 선택한 오디오 파일을 최대 128개까지 재생하며 이전 / 다음 및 곡 이동과 삭제가 가능한 대기열을 제공합니다.
- 서비스가 유지하는 프리셋 / 사용자 지정 취침 타이머, 현재 곡 후 정지, 마지막 5초 페이드아웃 및 A-B 구간 반복을 지원합니다.
- 곡별 재생 위치를 기억하고 현재 제목, 커버, 이전 / 다음 및 10초 이동을 시스템 미디어 화면에 반영합니다.
- `audio/*`의 읽기 전용 `content` URI에 대한 독립 Android ACTION_VIEW 요청을 받고 호출자의 extras와 광범위한 URI 권한을 버립니다.
- 디코더 실패 시 다른 호환 앱으로 파일을 여는 대안을 제공하고 자체 반복을 방지하기 위해 이 플러그인을 제외합니다.
- 탐색기에서 한 곡을 열면 요청 범위 Host Session을 통해 읽을 수 있는 직접 형제 오디오를 자연 정렬한 제한된 재생목록으로 찾으며 명시적 다중 선택은 호스트 선택 순서를 유지합니다.

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

플러그인이 설치되면 파일 관리자는 단일 파일에 오디오 재생을, 다중 선택 도구 모음에 선택한 오디오 재생을 표시합니다. 단일 파일 진입점은 선택한 곡에서 시작해 읽을 수 있는 직접 형제 오디오를 자연 순서로 찾을 수 있으며 명시적 다중 선택은 호스트 선택 순서를 유지합니다.

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
Explorer protocol version: 4
Explorer MIME types: []
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/*
required host build: 5276
```

버전 1.2.1은 요청 범위의 직접 형제 읽기 기능을 받을 수 있는 프로토콜 v12 단일 파일 읽기 전용 기본 작업과 순서가 있는 다중 선택 작업을 제공합니다. 독립 Android 진입점은 단일 파일로 유지되며 `audio/*`를 받습니다. 선택적 Host Session이 없는 호스트에서는 선택 파일만 재생합니다.

같은 폴더 검색에는 AutoJs6 6.8.0 build 5276 이상과 Explorer Action v12가 필요하며 이후 플러그인 기능에서도 이 요구 사항을 높이지 않습니다.

******

### 보안

******

플러그인은 저장소 또는 인터넷 접근 권한을 요청하지 않습니다. 서명으로 보호된 진입점은 프로토콜 v12, 순서가 있는 TARGETS와 ClipData, 식별자, 상위 관계, 메타데이터 및 읽기 전용 플래그를 엄격히 검증합니다. 선택적 Host Session은 호스트가 플러그인 UID에 고정하며 선택 파일의 직접 상위 폴더 나열과 선택 파일 또는 읽을 수 있는 직접 형제 파일 열기만 허용합니다. 재생 구성 요소는 불투명 합성 경로만 받고 파일 시스템 경로는 받지 않습니다. 공개 Android 진입점은 읽기 전용 단일 파일로 유지됩니다.

******

### 안전 제한

******

- 단일 작업은 정확히 하나의 선택 파일에서 시작해 읽을 수 있는 직접 형제 오디오의 제한된 재생목록을 만들 수 있으며 선택 작업은 순서를 유지한 중복 없는 파일 1개에서 128개를 받습니다.
- 선언된 크기가 8 TiB를 넘는 탐색기 요청은 거부됩니다.
- 탐색기 작업은 파일 이름 확장자로만 선택되며 실행 시 오디오 MIME 유형도 검증합니다.
- 형제 검색은 호스트가 관리하는 요청 범위 세션에서만 가능하고 재귀하지 않습니다. 플러그인은 형제 URI를 추측하거나 파일 시스템 경로를 받지 않습니다.
- 공개 Android 진입점에는 ACTION_VIEW, `content` URI, `audio/*` 및 읽기 권한이 필요합니다.
- 알림 권한은 선택 사항입니다. 거부하면 알림 창 제어가 숨겨지지만 재생은 차단되지 않습니다.
- 완료 또는 디코더 오류 시 재생을 안전하게 종료합니다. 외부 대안은 새 읽기 권한만 전달하고 이 플러그인을 제외합니다.

******

### 릴리스 기록

******

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

# v1.2.0

###### 2026/08/27

* `기능` Explorer Action 프로토콜 v4 지원과 명시적으로 선택한 오디오 파일 최대 128개로 대기열을 만드는 순서가 있는 다중 선택 작업 추가
* `기능` 이전 / 다음, 곡 이동과 삭제가 가능한 대기열, 순차 / 셔플 / 한 곡 반복 모드를 갖춘 네이티브 Media3 재생 목록
* `기능` 15 / 30 / 60분, 사용자 지정 시간, 현재 곡 후 정지, 마지막 5초 페이드아웃을 갖춘 서비스 관리 취침 타이머
* `기능` 선택 구간을 반복하는 A-B 구간 반복
* `개선` 시스템 미디어 제어에 이전, 다음, 10초 뒤로 / 앞으로가 표시되며 메타데이터와 이어듣기 위치가 현재 대기열 곡을 따름
* `개선` Explorer 요청 검증을 순서가 있는 TARGETS와 ClipData, 고유 식별자, 호스트 세션 및 모든 선택 파일로 확장하면서 읽기 권한은 넓히지 않음
* `개선` 상위 FileProvider URI로 자식을 열거할 수 없음을 문서화하고 형제 파일 자동 검색과 URI 추측을 비활성 상태로 유지하며 명시적 다중 선택을 안전한 경로로 제공
* `의존성` 번들 Explorer Action API를 프로토콜 v2에서 v4로 업그레이드하고 최소 호스트 빌드를 5276으로 변경
* `의존성` AndroidX RecyclerView 1.4.0 추가

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
