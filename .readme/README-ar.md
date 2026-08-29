<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <h1>3-Terra Player</h1>

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

يوفر 3-Terra Player وحدة تحكم داخل التطبيق وخدمة خاصة للتشغيل في الخلفية للملفات الصوتية المفتوحة من مدير الملفات. ويمكنه ايضا استقبال طلبات Android ACTION_VIEW للقراءة فقط لعناوين content URI ذات نوع MIME صوتي.

******

### الميزات

******

- يسجل `play-audio` و`play-audio-selection` عبر Explorer Action v12 لكل انواع MIME الصوتية التي يعرفها المضيف و19 امتدادا معروفا.
- يشغل الصوت باستخدام Media3 ExoPlayer وMediaSessionService مع دعم تركيز الصوت ومعالجة فصل جهاز الاخراج ووضع التنبيه المحلي والتشغيل في الخلفية وعناصر تحكم الوسائط في النظام.
- يوفر شاشة كاملة مع غلاف الالبوم والوسوم والمعلومات التقنية والسحب وقفزات 10 ثوان واوضاع الترتيب / العشوائي / تكرار مقطع واحد وسرعة من 0.5x الى 2x.
- يشغل حتى 128 ملف صوت محددا صراحة بترتيب المضيف, مع السابق / التالي وقائمة انتظار للانتقال الى المقاطع او ازالتها.
- يوفر مؤقت نوم تديره الخدمة باعدادات مسبقة ومدة مخصصة وتوقف بعد المقطع الحالي وتلاشي اخر خمس ثوان وتكرار A-B.
- يتذكر اخر ملف مفتوح وموضعه فقط ويستبدل السجل فور فتح ملف اخر ويزامن البيانات الوصفية وعناصر التحكم الحالية مع النظام.
- يوفر صفحة لاختيار حتى 128 مستندا ويقبل عناوين `content` ACTION_VIEW للقراءة فقط بما فيها اسماء MIME القديمة لـ WMA.
- يوفر بديلا عند فشل فك الترميز يفتح الملف في تطبيق اخر متوافق ويستبعد هذا الملحق لمنع حلقة ذاتية.
- يكتشف تشغيل ملف واحد من Explorer قائمة انتظار محدودة ومرتبة طبيعيا من ملفات الصوت الشقيقة القابلة للقراءة عبر Host Session خاصة بالطلب؛ ويحافظ التحديد المتعدد الصريح على ترتيب المضيف.
- ينشئ لوحة واضحة من لون مصدر واحد ويتبع AutoJs6 ويوفر 19 لون Material 500 وRGB مخصصا؛ وتشمل الاعدادات اللغة والليل والمتابعة والتحديثات والسجل ومعلومات التطبيق.

******

### امتدادات المستكشف

******

يعلن كتالوج Explorer النوع `audio/*` ويستخدم هذه الامتدادات ايضا لجداول MIME القديمة او الناقصة:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

مطابقة الامتداد لا تضمن فك الترميز. يعتمد التشغيل الفعلي على Media3 ومنصة Android وبرامج الترميز في الجهاز ومحتوى الملف.

******

### سلوك المضيف

******

عند تثبيت الملحق وتفعيله في AutoJs6 يعرض مدير الملفات تشغيل الصوت لملف واحد وتشغيل الصوت المحدد في شريط التحديد المتعدد. يبدأ تشغيل الملف الواحد من المقطع المحدد ويمكنه اكتشاف ملفات الصوت الشقيقة المباشرة القابلة للقراءة بترتيب طبيعي؛ ويحافظ التحديد المتعدد الصريح على ترتيب المضيف.

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
Explorer action ids: play-audio (single) / play-audio-selection (multiple, up to 128)
Explorer protocol version: 12 (accepts compatible read-only v4–v12 requests)
Explorer MIME types: [audio/*]
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/* plus legacy WMA MIME aliases
required host build: 5276
```

يوفر الاصدار 1.3.0 اجراءات v12 للقراءة فقط ووصولا محدودا للشقائق واختيار مستندات مستقلا ومداخل Android للصوت وWMA. ومن دون Host Session اختيارية يشغل الملف المحدد فقط.

يتطلب اكتشاف المجلد نفسه AutoJs6 6.8.0 build 5276 او احدث وExplorer Action v12؛ ولن يرتفع هذا المتطلب مع قدرات الملحق اللاحقة.

******

### الامان

******

لا يطلب الملحق اذن التخزين ولا يكتب الملفات المصدر. يستخدم الانترنت فقط لفحص اصدارات GitHub يدويا او يوميا. يتحقق المدخل الموقع بدقة من v12 وTARGETS وClipData والبيانات الوصفية والقراءة؛ وتبقى Host Session مرتبطة بالـ UID وغير متكررة والمداخل العامة للقراءة فقط.

******

### حدود الامان

******

- يبدأ الاجراء الفردي من ملف محدد واحد بالضبط ويمكنه تكوين قائمة انتظار محدودة من ملفات الصوت الشقيقة المباشرة القابلة للقراءة؛ يقبل اجراء التحديد من 1 الى 128 ملفا فريدا ويحافظ على ترتيبها.
- ترفض طلبات المستكشف التي يزيد حجمها المعلن على 8 TiB.
- تطابق الاجراءات نوع MIME صوتيا معروفا او قائمة الامتدادات الصريحة وتطبع كل هدف وتتحقق منه.
- اكتشاف الملفات الشقيقة غير متكرر ومتاح فقط عبر جلسة يملكها المضيف وخاصة بالطلب؛ ولا يخمن الملحق URI شقيقا ولا يتلقى مسار نظام ملفات.
- يتطلب مدخل Android العام ACTION_VIEW وURI بنظام `content` ونوع MIME صوتيا او WMA مدعوما واذن قراءة.
- اذن الاشعارات اختياري. يؤدي الرفض الى اخفاء عناصر التحكم من لوحة الاشعارات لكنه لا يمنع التشغيل.
- ينتهي التشغيل بامان عند الاكتمال او خطا فك الترميز. يمرر البديل الخارجي اذن قراءة جديدا فقط ويستبعد هذا الملحق.

******

### سجل الاصدارات

******

# v1.3.0

###### 2026/08/29

* `ميزة` اضيفت صفحة تشغيل ومشغل مستقل لملفات متعددة مع صفحة اعدادات للغة والوضع الليلي ولون السمة ومتابعة التشغيل والتحديثات وسجل الاصدارات ومعلومات التطبيق والمطور
* `ميزة` تتبع اللغة والوضع الليلي ولون المصدر AutoJs6 افتراضيا عبر عقد الاعدادات الرسمي للقراءة فقط؛ وتظل خيارات المضيف غير المتاح ظاهرة لكنها معطلة مع الرجوع الى قيم التطبيق الافتراضية
* `ميزة` اضيف التحقق اليدوي واليومي التلقائي من التحديثات وادارة الاصدارات المتجاهلة وسجل اصدارات مدمج ومترجم
* `إصلاح` اصلح ظهور Host color unavailable دائما عبر كشف مدخل خدمة معلومات الملحق المحمي الذي يتطلبه موفر اعدادات المضيف
* `إصلاح` تعلن اجراءات Explorer انواع MIME الصوتية الى جانب 19 امتدادا معروفا بما فيها WMA كي يفتح كل صوت يتعرف عليه المضيف مباشرة في الملحق
* `تحسين` تتذكر متابعة التشغيل اخر ملف مفتوح فقط وتحذف السجل السابق فور فتح ملف اخر ولا تحفظ موضع ملف اكتمل تشغيله
* `تحسين` حجزت منطقة بيانات وصفية ثابتة من ثلاثة اسطر وتستكمل معدل العينة ومعدل البت من المسار المحدد بترتيب 44.1 kHz · MP3 · 128 kbps
* `تحسين` اصبح A-B دورة واضحة من ثلاث ضغطات لتعيين A ثم B ثم المسح؛ واضيفت مسافة لعناصر التحكم السفلية مع توحيد حجم الايقونات وتوسيطها عموديا

# v1.2.2

###### 2026/08/27

* `إصلاح` لم يعد التشغيل من المستكشف يفشل بعد ترقية الملحق عندما يواصل مدير ملفات AutoJs6 المفتوح إرسال إجراء بروتوكول v4 المخزّن مؤقتًا؛ تقبل البوابة طلبات القراءة فقط المتوافقة من v4 إلى v12 مع استمرار الإعلان عن v12
* `إصلاح` لم تعد امتدادات الصوت المعلنة تُرفض عندما يعيد جدول MIME في Android أو لدى الشركة المصنّعة نوعًا عامًا أو من فئة application؛ توفر قائمة الامتدادات المسموح بها الآن نوع MIME صوتيًا قياسيًا وثابتًا
* `تحسين` تسجل طلبات Explorer المرفوضة الآن رمز سبب يحافظ على الخصوصية من دون أسماء ملفات أو مسارات معروضة أو عناوين URI، مما يتيح تشخيص اختلافات العقد مستقبلًا مباشرة

# v1.2.1

###### 2026/08/27

* `ميزة` يمكن لإجراء تشغيل الصوت لملف واحد الآن اكتشاف ما يصل إلى 128 ملفا صوتيا قابلا للقراءة في المجلد نفسه عبر Explorer Action v12 وإنشاء قائمة مرتبة طبيعيا تبدأ بالمسار المحدد
* `إصلاح` لم يعد API 24 يرفض طلب Explorer صالحا عندما يضيف Android من بيان Activity البوابة علامة FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS غير المرتبطة بالأذونات
* `إصلاح` لم يعد الانتقال التلقائي إلى مسار شقيق يتعطل عند إعادة إنشاء Intent الرجوع لـ MediaSession؛ يبقى الهدف الممنوح أصلا مرساة Host Session بشكل مستقل عن عنصر القائمة النشط
* `تحسين` يحتفظ المسار المحدد بـ content URI الأصلي بينما تبث المسارات الشقيقة فقط عبر واصفات ملفات Host Session المقيدة بالطلب؛ لا يتم تخمين URI شقيق ولا تضاف صلاحيات متكررة أو كتابة أو تخزين أو وصول دائم
* `تحسين` تستبعد تصفية امتدادات الصوت ملفات الفيديو .mp4 التي تحمل اسم ملفات الصوت .m4a نفسه من القائمة، بينما تبقى قائمة التحديد المتعدد الصريحة الحالية بلا تغيير
* `تحسين` تنتقل ملكية Host Session إلى خدمة التشغيل في الخلفية وتغلق عند استبدال القائمة أو فشل البدء أو اكتمال التشغيل أو تدمير الخدمة
* `تبعية` ترقية Explorer Action API المضمنة من البروتوكول v4 إلى امتداد قراءة الملفات الشقيقة v12 المتوافق مع الإصدارات السابقة مع الإبقاء على الحد الأدنى لبناء المضيف 5276

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
