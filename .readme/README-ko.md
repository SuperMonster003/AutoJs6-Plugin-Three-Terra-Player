<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="three-terra-player-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>백그라운드 재생을 지원하는 오디오 플레이어 플러그인 겸 독립 실행 앱</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 언어 (Languages)

******

현재 README.md는 다음 언어를 지원합니다:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ja.md)
- 한국어 [ko] # 현재
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ar.md)

******

### 소개

******

3-Terra Player (구 Audio Player) 는 AutoJs6 파일 관리자의 오디오 재생 플러그인이자 단독으로도 쓸 수 있는 간결한 오디오 플레이어입니다. 설치하고 활성화하면 AutoJs6 파일 관리자에서 아무 오디오 파일이나 탭해 바로 재생할 수 있어 별도의 타사 플레이어가 필요 없습니다. 일반 앱처럼 런처에서 열어 여러 오디오 파일을 한 번에 선택해 연속으로 재생할 수도 있습니다.

플러그인은 설치만 하면 AutoJs6가 자동으로 인식하므로 별도의 설정이 필요 없습니다. 재생은 Android 공식 미디어 프레임워크인 Media3 (ExoPlayer) 기반이며 백그라운드 재생과 시스템 미디어 알림 제어를 지원합니다. 오디오 파일에는 항상 읽기 전용으로만 접근하고 저장소 권한을 요청하지 않으며 원본 파일을 수정하거나 삭제하는 일도 결코 없습니다.

******

### 주요 기능

******

- 로컬 재생 목록: M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL의 순서, 제목, 중복 항목을 유지. 호스트에서는 같은 폴더를 읽고, 독립 실행에서는 폴더를 선택해 상대 경로를 처리. 한 번에 한 목록, 최대 128개 항목. 네트워크 URL, HLS, 중첩 목록은 미지원.
- 파일 관리자에서 탭 한 번으로 재생: AutoJs6 파일 관리자에서 아무 오디오 파일이나 탭하면 바로 재생이 시작됩니다.
- 같은 폴더 자동 연속 재생: 한 곡을 열면 같은 폴더의 다른 오디오를 자동으로 찾아 파일 이름의 자연 정렬 순서로 이어서 재생합니다 (최대 128곡, 선택한 곡부터 시작).
- 다중 선택 대기열 재생: 파일 관리자에서 최대 128개의 오디오를 선택해 한 번에 재생하며 선택한 순서가 그대로 유지됩니다.
- 완성도 높은 재생 화면: 앨범 아트, 제목 / 아티스트 / 앨범 태그, 샘플링 레이트와 비트레이트 같은 기술 정보, 드래그 가능한 진행 막대를 제공합니다.
- 일상적인 제어 완비: 이전 곡 / 다음 곡, 5 / 10 / 15 / 30초 중 선택하는 되감기와 빨리 감기, 순차 / 셔플 / 한 곡 반복, 저장 가능한 0.5배부터 2배 기본 속도, 대기열 종료 동작을 지원합니다.
- 재생 대기열 패널: 언제든 대기 중인 곡을 확인하고 탭해서 이동하거나 삭제할 수 있으며 현재 곡이 한눈에 보입니다.
- 취침 타이머: 15 / 30 / 60분 프리셋 또는 사용자 지정 시간을 고를 수 있고 현재 곡을 마친 뒤 정지할 수 있으며 종료 전 5초 동안 볼륨이 서서히 줄어듭니다.
- A-B 구간 반복: 원하는 구간을 반복해서 들을 수 있어 듣기 연습과 악기 카피에 유용합니다.
- 백그라운드 재생: 화면을 벗어나거나 화면이 꺼져도 재생이 끊기지 않으며 시스템 미디어 알림과 잠금 화면에서 바로 제어할 수 있습니다.
- 세션 복원: 런처에서 독립 실행형 앱을 다시 열면 마지막 대기열, 현재 곡, 정지 위치, 반복 / 셔플 상태, 재생 속도를 복원하며 복원 후에는 일시 정지 상태를 유지합니다.
- 독립 실행 모드: AutoJs6가 설치되어 있지 않아도 사용할 수 있으며 시작 화면에서 최대 128개의 오디오 파일을 한 번에 선택해 재생합니다.
- 반응형 모양: 언어 / 야간 모드 / 기본 색상은 AutoJs6를 따르거나 직접 설정할 수 있습니다. 앨범 아트에서 읽기 쉬운 색상표를 생성해 엣지 투 엣지 플레이어에 적용하며 전환과 햅틱은 시스템 설정을 따릅니다.

******

### 사용 방법

******

1. [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) 페이지에서 최신 플러그인 APK를 내려받아 AutoJs6가 실행되는 기기에 설치합니다.
2. AutoJs6의 플러그인 센터를 열어 `3-Terra Player`가 인식되고 활성화되어 있는지 확인합니다.
3. AutoJs6 파일 관리자에서 오디오 파일을 탭한 뒤 `오디오 재생`을 선택하거나, 길게 눌러 여러 파일을 선택한 다음 도구 모음에서 `선택한 오디오 재생`을 선택합니다.
4. 또는 런처에서 `3-Terra Player`를 바로 열어 파일 선택 버튼으로 여러 오디오를 한 번에 골라 재생할 수도 있습니다.

> 플러그인 센터에 플러그인이 보이지 않으면 먼저 AutoJs6를 6.8.0 (버전 코드 5276) 이상으로 업데이트하세요. 플러그인 자체는 Android 7.0 (API 24) 이상 기기를 지원하며 독립 실행 모드는 AutoJs6에 의존하지 않습니다.

******

### 지원 오디오 형식

******

파일 관리자 진입점은 일반 오디오 유형 `audio/*`를 선언하고, 일부 기기의 불완전하거나 오래된 MIME 유형 표와의 호환을 위해 다음 19가지 확장자를 명시적으로 포함합니다:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

확장자가 지원 목록에 있다고 해서 디코딩이 보장되지는 않습니다. 실제 재생 여부는 Media3, Android 버전, 기기 코덱, 파일 내용에 따라 달라집니다. 재생할 수 없는 파일을 만나면 오류 패널에서 다른 앱으로 열도록 선택할 수 있습니다.

******

### 자주 묻는 질문

******

#### 파일 관리자에 `오디오 재생` 버튼이 보이지 않나요?

다음 세 가지를 차례로 확인하세요: 플러그인이 설치되어 있는지, AutoJs6 플러그인 센터에서 이 플러그인이 활성화되어 있는지, AutoJs6가 6.8.0 (버전 코드 5276) 이상인지. 세 가지가 모두 갖춰지면 파일 관리자의 오디오 파일에 재생 항목이 나타납니다.

#### 한 곡만 탭했는데 왜 같은 폴더의 다른 곡이 대기열에 들어 있나요?

같은 폴더 자동 연속 재생 기능 때문입니다. 오디오 파일 하나를 열면 플러그인이 호스트가 관리하는 세션을 통해 같은 폴더의 다른 오디오를 찾아 파일 이름의 자연 정렬 순서로 대기열에 넣어 이어서 듣기 편하게 해 줍니다. 검색 과정은 완전히 읽기 전용이며 하위 폴더에는 들어가지 않습니다. 호스트 버전이 이 기능을 지원하지 않으면 선택한 곡만 재생됩니다.

#### 다중 선택 재생은 어떤 순서로 재생되나요?

선택한 순서 그대로 재생됩니다. 특정 순서로 듣고 싶다면 원하는 순서대로 파일을 선택하면 됩니다. 플레이어에 들어간 뒤에도 대기열 패널에서 곡을 탭해 이동하거나 삭제할 수 있습니다.

#### 화면을 벗어나거나 화면이 꺼져도 계속 재생되나요?

네. 재생은 백그라운드 서비스가 담당하며 시스템 미디어 알림과 잠금 화면 컨트롤로 제어할 수 있습니다. Android 13 이상에서 알림 권한은 선택 사항입니다. 거부하면 알림 컨트롤만 숨겨질 뿐 재생 자체에는 영향이 없습니다.

#### 플러그인이 제 오디오 파일을 수정하거나 업로드하나요?

아니요. 플러그인은 저장소 권한을 요청하지 않고 오디오 파일에 읽기 전용으로만 접근합니다. 네트워크는 GitHub의 새 버전 확인에만 사용되며 (사용자가 직접 실행하거나 하루 최대 한 번) 어떤 파일이나 개인 데이터도 업로드하지 않습니다.

#### 왜 어떤 파일은 소리가 나지 않거나 디코딩 오류가 표시되나요?

확장자가 지원된다고 기기가 반드시 디코딩할 수 있는 것은 아니며, 일부 희귀한 코덱이나 손상된 파일은 재생되지 않을 수 있습니다. 오류가 나면 오류 패널에 구체적인 오류 코드가 표시되고 다른 앱으로 열 수 있는 항목이 제공됩니다 (자기 반복을 피하기 위해 이 플러그인은 자동으로 제외됩니다).

#### 플러그인 화면이 AutoJs6의 언어와 테마를 따르게 하려면 어떻게 하나요?

기본값이 이미 따르기입니다. 언어, 야간 모드, 테마 색상은 AutoJs6의 공식 읽기 전용 설정 인터페이스를 통해 자동으로 동기화됩니다. 설정 페이지에서 고정 언어나 원하는 색상으로 바꿀 수도 있습니다. AutoJs6가 설치되어 있지 않으면 시스템 모양과 내장 기본값으로 자동 대체됩니다.

#### 취침 타이머와 A-B 반복은 어디에 있나요?

둘 다 재생 화면 하단 도구 모음에 있습니다. 타이머 아이콘에서 프리셋 또는 사용자 지정 시간을 고를 수 있고, A-B 버튼은 누를 때마다 'A 지점 설정, B 지점 설정, 해제' 순서로 순환하며 구간 반복을 설정하거나 취소할 수 있습니다.

******

### 권한과 보안

******

오디오 파일은 신뢰할 수 없는 출처에서 올 수 있으므로 플러그인은 설계 단계부터 재생 과정에 여러 겹의 방어선을 두었습니다:

- 저장소 권한 없음: 플러그인은 기기 저장소의 읽기 / 쓰기 권한을 요청하지 않고 얻을 수도 없으며, 호스트나 시스템이 명시적으로 허가한 개별 파일에만 접근할 수 있습니다.
- 쓰기 없음: 오디오 파일에는 읽기 전용으로만 접근하며 어떤 원본 파일도 수정, 이동, 삭제하지 않습니다.
- 엄격한 검증: 파일 관리자 진입점은 AutoJs6 서명 권한으로 보호되며, 모든 재생 요청의 프로토콜 버전, 대상 목록, 읽기 전용 허가를 항목별로 검증하고 규격에 맞지 않는 요청은 즉시 거부합니다.
- 제한된 검색: 같은 폴더 연속 재생은 호스트가 관리하는 요청 범위 세션을 통해서만 읽고, 하위 폴더로 재귀하거나 파일 위치를 추측하지 않으며, 세션은 재생이 끝나면 함께 닫힙니다.
- 로컬 세션만 저장: 이어서 재생을 켜면 앱 전용 저장소에는 가장 최근의 독립 파일 선택기 대기열과 재생 상태만 보관합니다. 영구 읽기 권한이 있는 시스템 선택기 URI만 허용하고 Host Session 경로는 저장하지 않으며, 설정을 끄면 기록을 즉시 지웁니다.
- 최소한의 네트워크: 인터넷 권한은 사용자가 실행하거나 하루 한 번 이루어지는 GitHub 버전 확인에만 사용되며 오디오 내용이나 사용 데이터와는 전혀 무관합니다.
- 선택적 알림: Android 13+ 기기에서 알림 권한은 선택 사항이며 거부하면 알림 컨트롤만 숨겨질 뿐 재생은 영향을 받지 않습니다.

플러그인 설치 파일은 공식 [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) 페이지나 그 밖의 신뢰할 수 있는 경로에서만 받으세요. 출처가 불분명한 설치 파일은 이름과 버전이 같아 보여도 변조되었을 수 있습니다.

******

### 플러그인 인터페이스

******

다음 정보는 AutoJs6 호스트와 플러그인 개발자를 위한 것으로, 호스트는 이 식별자로 플러그인을 발견하고 기능을 협상합니다:

```text
application id: io.github.supermonster003.autojs6.plugin.audioplayer
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: audio-player
engine: explorer-action
variant: default
Explorer action ids: play-audio (single) / play-audio-selection (multiple, up to 128)
Explorer protocol version: 12 (accepts compatible read-only v4-v12 requests)
Explorer MIME types: [audio/*]
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/* plus legacy WMA MIME aliases
required host build: 5276
```

현재 버전은 프로토콜 v12의 단일 파일 및 순서 있는 다중 선택 읽기 전용 동작, 요청 범위의 같은 폴더 검색 기능, 독립 문서 선택기, 공개 읽기 전용 Android 오디오 진입점을 제공합니다 (구형 WMA MIME 별칭 포함). 선택적 Host Session 기능이 없는 호스트에서는 계속 선택한 파일만 재생됩니다.

같은 폴더 검색에는 Explorer Action v12를 지원하는 AutoJs6 6.8.0 (버전 코드 5276) 이상이 필요하며, 이후의 플러그인 기능도 이 요구 사항을 높이지 않습니다. 제품은 v1.4.0부터 3-Terra Player로 이름이 바뀌었지만 애플리케이션 ID는 `io.github.supermonster003.autojs6.plugin.audioplayer` 그대로 유지되어 덮어쓰기 설치로 업그레이드할 수 있습니다.

******

### 개발 로드맵

******

플러그인의 기능 계획과 진행 상황은 Roadmap.md에 체크 가능한 목록으로 관리됩니다. 마일스톤별로 정리되고 수용 기준이 함께 붙어 있으며 가사, 이퀄라이저, 커버 기반 색상, 몰입형 화면, 엔지니어링 품질 같은 방향을 다룹니다. 체크되지 않은 항목은 현재 버전의 기능이 아니라 계획 의도이며, Issues를 통해 언제든 논의에 참여할 수 있습니다.

- [Roadmap.md 보기](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/Roadmap.md)

******

### 릴리스 기록

******

#### v1.5.0

_2026/09/12_

- `기능` 로컬 재생 목록: M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL의 순서, 제목, 중복 항목을 유지. 호스트에서는 같은 폴더를 읽고, 독립 실행에서는 폴더를 선택해 상대 경로를 처리. 한 번에 한 목록, 최대 128개 항목. 네트워크 URL, HLS, 중첩 목록은 미지원
- `기능` 앨범 아트에서 그라데이션, 도구 모음, 제어 버튼, 대기열에 적용할 읽기 쉬운 색상표를 실시간 생성합니다. 모든 화면은 상태 표시줄, 디스플레이 컷아웃, 제스처 영역을 고려한 엣지 투 엣지 방식으로 표시됩니다
- `기능` 재생 설정에 저장 가능한 기본 속도, 5 / 10 / 15 / 30초 되감기와 빨리 감기, 대기열 종료 시 정지 / 처음으로 돌아가 일시 정지 / 다시 재생 동작을 추가했습니다
- `기능` 재생 / 일시 정지와 앨범 아트에 절제된 전환을 추가하고 주요 재생 작업에 시스템 설정을 따르는 햅틱을 추가했습니다. 시스템 애니메이션을 끄면 상태를 즉시 전환합니다
- `수정` 언어 및 야간 모드 대화상자 항목이 지나치게 큰 플랫폼 목록 글자 대신 16sp Material Body1을 사용합니다
- `개선` 앨범 아트 디코딩과 최대 64x64 색상 샘플링을 메인 스레드 밖에서 처리합니다. 임의의 색상에도 본문 4.5:1과 윤곽선 3:1의 기존 대비 기준을 유지합니다
- `개선` 재생 시각화 연구와 RMS 버킷 프로토타입을 완료했습니다. 녹음 권한이 필요한 Visualizer는 계속 제외하고 권한 없는 Media3 PCM 탭을 향후 성능 기준 검증 경로로 문서화했습니다
- `개선` README 레이아웃과 Gradle 플랫폼 버전 관리 방식을 통일
- `개선` 플러그인 설명을 간결하게 다듬고 다국어 리소스의 문장 부호를 통일
- `개선` 외부 보기 진입점을 External Viewer로 변경해 뷰어 의미를 통일
- `개선` 업데이트 대화상자의 릴리스 기록 버튼에서 내장 릴리스 기록 페이지를 열도록 변경
- `개선` 빌드 시 의도하지 않은 네이티브 의존성을 거부하고 JSON 보고서 생성

#### v1.4.1

_2026/08/31_

- `기능` 독립 실행형 앱을 런처에서 다시 열면 마지막 대기열, 현재 곡, 정지 위치, 반복 모드, 셔플 상태, 재생 속도를 복원합니다; 복원된 세션은 일시 정지 상태를 유지하며 영구 읽기 권한이 있는 시스템 파일 선택기 대기열만 저장합니다
- `수정` 일부 테마에서 플레이어 오른쪽 위 더보기 메뉴가 흰색 배경에 흰색 글자로 표시되어 설정 항목을 알아볼 수 없던 문제를 수정했습니다
- `개선` 테마 색상 설정의 AutoJs6 소스 문구를 `AutoJs6 따르기`로 통일하고, 색상 선택 패널에는 호스트 색상의 HEX 값을 바로 표시합니다
- `개선` 10개 언어의 README, CHANGELOG, 플러그인 안내를 기본적으로 수동 검토하고 프로젝트 및 앱 내 업데이트 링크를 3-Terra Player 공식 저장소로 이전했습니다

#### v1.4.0

_2026/08/29_

- `기능` 시스템 미디어 알림 전면 개선: 이전 곡 / 다음 곡 / 셔플 전환 / 종료 버튼을 새로 갖추고 앱 전용 단색 아이콘을 사용하며 셔플 상태는 플레이어와 실시간으로 동기화
- `수정` AutoJs6 플러그인 센터에서 플러그인이 오류로 자동 표시되고 비활성화될 수 있던 문제 수정: 플러그인 정보와 파일 관리자 동작이 이제 각각 독립된 서비스 엔드포인트를 사용
- `수정` 재생 대기열을 비운 뒤 오래된 정보가 남던 문제 수정: 화면이 명확한 빈 상태로 전환되고 재생, 탐색, 배속, 타이머, A-B 등 컨트롤이 함께 비활성화
- `수정` 재생 버튼 그림자가 하단 영역에서 잘리던 문제 수정; 설정과 업데이트 대화 상자의 라디오 버튼, 체크박스, 진행 막대, 버튼이 일관되게 테마 색상을 따르도록 개선
- `개선` 앱과 플러그인 이름을 3-Terra Player로 공식 변경: 애플리케이션 ID는 그대로이므로 덮어쓰기 설치로 바로 업그레이드할 수 있고 기존 설정도 그대로 유지
- `개선` 플레이어 오른쪽 위 메뉴를 설정 항목만 남기고 간소화하고 설정 페이지와 중복되던 팔레트 버튼을 제거

##### 더 많은 릴리스 기록은 다음에서 확인할 수 있습니다

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/assets/doc/CHANGELOG-ko.md)

******

### 빌드

******

이 섹션은 소스 코드에서 플러그인을 직접 빌드하려는 개발자를 위한 내용입니다.

debug APK 빌드:

```powershell
.\gradlew.bat :app:assembleDebug
```

release APK 빌드 (버전 관리에서 제외된 `sign.properties`에 서명 정보를 설정하면 자동으로 서명됩니다):

```powershell
.\gradlew.bat :app:assembleRelease
```

릴리스 보관용으로는 `:app:appendDigestToReleasedFiles` 작업을 실행하면 서명된 APK가 `releases/`에 복사되고 파일 이름에 버전과 CRC32 다이제스트가 덧붙습니다.

빌드 매개변수는 `version.properties`에 모여 있습니다: 최소 SDK 24 (Android 7.0), 대상 SDK 36, 현재 버전 1.5.0.

******

### 현지화와 문서 생성

******

```text
.readme/common.json
.readme/lang_*.json
.readme/template_readme.md
.readme/template_plugin_instruction.md
.changelog/lang_*.json
.changelog/template_changelog.md
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml`은 플러그인 메타데이터와 UI 텍스트의 현지화를 담당하고, `plugin_instruction.md`는 호스트 플러그인 센터에 표시되는 사용 안내를 제공합니다. README, 변경 기록, 사용 안내는 모두 JSON 원본에서 생성됩니다. `.readme/`와 `.changelog/` 아래의 원본 파일을 수정한 뒤 `py .python/generate_markdown.py`를 실행해 모든 산출물을 다시 생성하며, 생성된 파일은 직접 편집하지 않습니다. `py .python/generate_markdown.py --check`를 실행하면 원본과 산출물이 동기화되어 있는지 검사할 수 있습니다.

******

### 라이선스

******

프로젝트 코드는 [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/LICENSE) 라이선스로 공개되어 있습니다. 오디오 재생 기능은 [AndroidX Media3](https://developer.android.com/media/media3) (Apache License 2.0) 기반입니다.

******

### 관련 링크

******

- AutoJs6 문서: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- Android 보안 파일 공유: https://developer.android.com/training/secure-file-sharing


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/docs/16kb.md)
