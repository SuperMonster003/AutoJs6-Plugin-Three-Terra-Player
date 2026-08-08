<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="audio-player-ic-launcher" border="0" width="128" />
  </p>

  <p>ملحق مدير الملفات. يشغل الملفات الصوتية مع عناصر تحكم داخل التطبيق وفي الخلفية</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Audio-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### اللغات (Languages)

******

يدعم ملف README.md الحالي اللغات التالية:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ru.md)
- العربية [ar] # الحالي

******

### مقدمة

******

يوفر Audio Player وحدة تحكم داخل التطبيق وخدمة خاصة للتشغيل في الخلفية للملفات الصوتية المفتوحة من مدير الملفات. ويمكنه ايضا استقبال طلبات Android ACTION_VIEW للقراءة فقط لعناوين content URI ذات نوع MIME صوتي.

******

### الميزات

******

- يسجل اجراء المستكشف الرئيسي للقراءة فقط `play-audio` عبر بروتوكول Explorer Action v2 لامتدادات ملفات الصوت الثمانية عشر التي يتعرف عليها المضيف.
- يشغل الصوت باستخدام Media3 ExoPlayer وMediaSessionService مع دعم تركيز الصوت ومعالجة فصل جهاز الاخراج ووضع التنبيه المحلي والتشغيل في الخلفية وعناصر تحكم الوسائط في النظام.
- يوفر شاشة تحكم تتضمن بيانات العنوان وعناصر تشغيل وشرحا اختياريا لاذن الاشعارات في Android 13+ وملاحظات عن اخطاء التشغيل.
- يقبل طلبات Android ACTION_VIEW المستقلة لعناوين URI بنظام `content` للقراءة فقط مع `audio/*` ويتجاهل extras الخاصة بالمتصل واذونات URI الواسعة.
- يوفر بديلا عند فشل فك الترميز يفتح الملف في تطبيق اخر متوافق ويستبعد هذا الملحق لمنع حلقة ذاتية.

******

### امتدادات المستكشف

******

يطابق كتالوج المستكشف هذه الامتدادات فقط بشكل مقصود ويعلن قائمة انواع MIME فارغة:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave
```

مطابقة الامتداد لا تضمن فك الترميز. يعتمد التشغيل الفعلي على Media3 ومنصة Android وبرامج الترميز في الجهاز ومحتوى الملف.

******

### سلوك المضيف

******

عند تثبيت الملحق يعرض مدير الملفات تشغيل الصوت كاجراء رئيسي للامتدادات المدرجة. يؤدي اختياره الى فتح وحدة تحكم الملحق وبدء خدمة التشغيل الخاصة مع وصول مؤقت للقراءة فقط الى الملف المحدد.

عند غياب الملحق لا يظهر هذا الاجراء. يحتفظ المضيف بمسار ACTION_VIEW الخارجي الحالي للقراءة فقط لملفات الصوت, لذلك يمكن لتطبيق صوت اخر مثبت معالجة الملف. اذا لم يتوفر تطبيق خارجي متوافق فلن يحصل المضيف على واجهة تشغيل بديلة.

******

### واجهة الملحق

******

يكتشف المضيف الملحق وينفذه بالمعرفات التالية:

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

يوفر الاصدار 1 اجراء رئيسيا للقراءة فقط لملف واحد في مدير الملفات الرئيسي. يستخدم الكتالوج الامتدادات فقط بينما تواصل نقطة دخول Android المستقلة قبول `audio/*`.

يتطلب بناء المضيف رقم 5269 او احدث.

******

### الامان

******

لا يطلب الملحق اذن التخزين او الشبكة. تحمي صلاحية توقيع المضيف نقطة دخول مدير الملفات التي تتحقق بشكل صارم من البروتوكول v2 وعناوين content URI الهدف والاصل وClipData وسطح المصدر واسم العرض ونوع MIME والحجم المعلن وعلامات القراءة فقط. لا يتم تمرير سوى URI الهدف واذن القراءة الى مكونات التشغيل الخاصة. تقبل نقطة دخول Android العامة طلبات content audio للقراءة فقط وترفض اذونات الكتابة والاذونات الدائمة واذونات البادئة ولا تمرر ابدا extras عشوائية من المتصل.

******

### حدود الامان

******

- ملف هدف واحد لكل اجراء مستكشف.
- ترفض طلبات المستكشف التي يزيد حجمها المعلن على 8 TiB.
- يتم اختيار اجراء المستكشف حسب امتداد اسم الملف فقط مع التحقق من نوع MIME صوتي عند التنفيذ.
- تتطلب نقطة دخول Android العامة ACTION_VIEW وURI بنظام `content` و`audio/*` واذن قراءة.
- اذن الاشعارات اختياري. يؤدي الرفض الى اخفاء عناصر التحكم من لوحة الاشعارات لكنه لا يمنع التشغيل.
- ينتهي التشغيل بامان عند الاكتمال او خطا فك الترميز. يمرر البديل الخارجي اذن قراءة جديدا فقط ويستبعد هذا الملحق.

******

### سجل الاصدارات

******

# v1.0.1

###### 2026/08/08

* `إصلاح` ربط خدمة فارغ عند تفعيل الملحق في مركز الملحقات
* `تحسين` اسم ووصف ووثائق مستخدم اوضح واكثر ايجازا

# v1.0.0

###### 2026/08/02

* `ميزة` ملحق Audio Player بمعرف الملحق `audio-player` ومعرف الاجراء `play-audio` والمحرك `explorer-action` والمتغير `default`
* `ميزة` اجراء رئيسي للقراءة فقط في مدير الملفات لامتدادات الصوت الثمانية عشر للمضيف مع اشتراط بناء المضيف رقم 5269 او احدث
* `ميزة` تشغيل Media3 ExoPlayer وMediaSessionService مع تركيز الصوت ومعالجة فصل جهاز الاخراج ووضع التنبيه المحلي والتشغيل في الخلفية وعناصر تحكم النظام وواجهة تحكم خاصة
* `ميزة` شرح اختياري لاذن الاشعارات في Android 13+ من دون منع التشغيل عند رفض الاذن
* `ميزة` دعم Android ACTION_VIEW مستقل للقراءة فقط لطلبات الصوت عبر URI بنظام `content` والتحويل الى تطبيق اخر متوافق عند فشل فك الترميز مع منع الحلقة الذاتية
* `ميزة` تحقق صارم من البروتوكول وURI وClipData والمصدر والاسم وMIME والحجم والاذونات من دون اذن تخزين او شبكة ومع تمرير الحد الادنى من اذن القراءة
* `ميزة` بيانات الملحق ونصوص الواجهة وتعليمات الاستخدام وملفات README وسجلات التغييرات المترجمة الى الاسبانية والفرنسية والروسية والعربية واليابانية والكورية والانجليزية والصينية المبسطة والصينية التقليدية لهونغ كونغ والصينية التقليدية لتايوان
* `تبعية` إضافة AndroidX Media3 ExoPlayer وSession وUI الإصدار 1.10.1

##### لمزيد من الاصدارات

* [CHANGELOG-ar.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/assets/doc/CHANGELOG-ar.md)

******

### البناء

******

```powershell
.\gradlew.bat :app:assembleDebug
```

بناء Release:

```powershell
.\gradlew.bat :app:assembleRelease
```

تاتي معاملات البناء من `version.properties`. الحد الادنى الحالي لاصدار SDK هو 24 واصدار SDK المستهدف هو 36.

******

### تخطيط الموارد

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

يوفر `strings.xml` ترجمة بيانات الملحق ونصوص الواجهة. يوفر `plugin_instruction.md` التعليمات التي يعرضها المضيف. ينشئ `.python/generate_markdown.py` ملفات README وسجل التغييرات المترجمة من مصادر JSON.

******

### الروابط

******

- وثائق AutoJs6: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- مشاركة الملفات الامنة في Android: https://developer.android.com/training/secure-file-sharing
