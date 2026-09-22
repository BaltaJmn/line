# Contrato técnico de Purl

Todo lo que el código tiene que respetar, fichero a fichero. El porqué de cada decisión está en
`SPEC.md`; la interfaz, en `docs/pantallas.md`; los textos, en `docs/textos.md`. Si el código necesita
algo que aquí no está, se decide, se escribe aquí en el mismo cambio y se sigue.

Las rutas de las hermanas son relativas a `/Users/baltajmn/AndroidStudioProjects/`: `MoodTraker/` y
`HabitTracker/` (Quilt).

---

## 1. Identificadores

| Qué | Valor |
|---|---|
| Nombre visible | `Purl` (los cinco idiomas) |
| `applicationId` y `namespace` de `androidApp` | `com.baltajmn.line` |
| `namespace` de `shared` | `com.baltajmn.line.shared` |
| Paquete Kotlin | `com.baltajmn.line` |
| Bundle id de la app iOS | `com.baltajmn.line` (`APP_BUNDLE_ID` en `Config.xcconfig`) |
| Bundle id del widget iOS | `com.baltajmn.line.widget` (`$(APP_BUNDLE_ID).widget`) |
| Target y producto del widget | `LineWidget`, `LineWidgetExtension` |
| App Group | `group.com.baltajmn.line` |
| Kinds de WidgetKit | `LineTodayWidget`, `LineYearWidget`; v1.1 `LineMemoryWidget` |
| Esquema de URL | `com.baltajmn.line` (`com.baltajmn.line://today`, `://year`, `://pro`) |
| Canal de notificación Android | `line-daily` |
| Entitlement RevenueCat | `pro` (producto `pro_lifetime`, offering `default`, solo en el panel) |
| Política de privacidad | `https://line.baltajmn.dev/` |
| Nombre del zip de copia | `purl-AAAA-MM-DD.zip` |
| Carpeta de fotos del sistema (Android) | `Pictures/Purl` |
| `versionCode` / `versionName` inicial | `1` / `1.0`; iOS `MARKETING_VERSION = 1.0`, `CURRENT_PROJECT_VERSION = 1` |

---

## 2. Versiones y dependencias

Las de la familia, sin tocar. La plantilla del asistente trae versiones más nuevas (Kotlin 2.4.20,
Compose Multiplatform 1.12.0, AGP 9.1.1, SDK 37): se descartan. Subir de versión es una tarea aparte
para las tres apps a la vez.

`gradle/libs.versions.toml` es el de `MoodTraker/gradle/libs.versions.toml` tal cual, más dos
entradas:

```toml
[versions]
androidx-biometric = "1.1.0"
androidx-fragment = "1.9.0"

[libraries]
androidx-biometric = { module = "androidx.biometric:biometric", version.ref = "androidx-biometric" }
androidx-fragment = { module = "androidx.fragment:fragment-ktx", version.ref = "androidx-fragment" }
```

Versiones heredadas, para comprobar que la copia es la buena: `agp 9.0.1`, `android-compileSdk 36`,
`android-targetSdk 36`, `android-minSdk 24`, `androidx-activity 1.13.0`, `androidx-appcompat 1.7.1`,
`androidx-core 1.17.0`, `androidx-lifecycle 2.11.0-beta01`, `composeMultiplatform 1.11.1`,
`glance 1.1.1`, `kotlin 2.4.10`, `kotlinx-datetime 0.8.0`, `kotlinx-serialization 1.11.0`,
`material3 1.11.0-alpha07`, `purchases-kmp 3.2.1`.

`androidx.biometric` 1.1.0 es la única versión estable (enero de 2021). `biometric-compose` y
`registerForAuthenticationResult` están en alpha: no se usan. `fragment-ktx` se declara explícito
porque `MainActivity` pasa a ser `FragmentActivity` y la versión que arrastra biometric 1.1.0 es de
2020.

`shared/build.gradle.kts`: copia de `MoodTraker/shared/build.gradle.kts`, incluido el apaño del
enlazador `-L<xcode>/usr/lib/swift/<sdk>` para los binarios de test de iOS que exige purchases-kmp
3.2.1. Cambios: `namespace = "com.baltajmn.line.shared"`; en `androidMain`,
`implementation(libs.androidx.biometric)`, `implementation(libs.androidx.fragment)` y
`implementation(libs.glance.appwidget)`; en `commonMain`, `compose.components.resources` para la
fuente Literata.

`androidApp/build.gradle.kts`: copia del de MoodTraker. Cambios: `applicationId`, `namespace`,
`versionCode = 1`, `versionName = "1.0"`, dependencia de `libs.androidx.fragment`. La firma lee
`storeFile`, `storePassword`, `keyAlias` y `keyPassword` de `keystore.properties`, y sin ese fichero
cae a la firma de debug, igual que la familia.

---

## 3. Árbol de ficheros

`C` copia casi literal de la hermana indicada, `A` adaptación, `N` nuevo.

### `shared/src/commonMain/kotlin/com/baltajmn/line`

| Fichero | Qué hace | Origen |
|---|---|---|
| `App.kt` | `enum class Screen { Today, Year, Settings }`, `when`, `BackHandler`, overlays, puerta de bloqueo, `LifecycleEventEffect` de `ON_STOP` y `ON_START`, apertura por URL | A `MoodTraker/.../App.kt` |
| `model/Entry.kt` | `LineEntry`, `Settings`, `JournalFile`, `ExportFile`, `Journal`, `JournalJson` | N |
| `model/DayClock.kt` | `logicalDate`, `pastYears`, `echoes`, `nextReturn`, `isoKey` | N |
| `model/Insights.kt` | `streak`, `dayNumber`, `milestone` | N, patrón de `MoodTraker/.../model/Insights.kt` |
| `model/Text.kt` | `codePointCount`, `clampCodePoints`, `limitEdit`, `fold` | N |
| `data/Storage.kt` | `expect object Storage` | A `MoodTraker/.../data/Storage.kt` |
| `data/LineRepository.kt` | `object LineRepository`: estado, edición, escritor único, fotos, Pro | A `MoodTraker/.../data/MoodRepository.kt` |
| `data/Search.kt` | `search(journal, query)` | N |
| `data/Merge.kt` | `merge(device, incoming)` y `MergeResult` | N |
| `data/Photos.kt` | `expect fun decodeImage`, `expect object PhotoPicker`, `object Photos` (caché) | C `MoodTraker/.../data/Photos.kt`, `PHOTO_MAX_PX = 1024` |
| `data/Lock.kt` | `expect object Lock` | N |
| `data/Reminder.kt` | `expect object Reminder` | A `MoodTraker/.../data/Reminder.kt` |
| `data/ReminderPlan.kt` | `nextFire`, `reminderPlan`, `memorySnippet` | N |
| `data/FilePicker.kt` | `expect object FilePicker`: elegir fichero para importar y destino para exportar | A `MoodTraker/.../data/FilePicker.kt` |
| `data/Export.kt` | `journalMarkdown`, `exportZip`, `readBackup` | N |
| `data/Zip.kt` | `ZipWriter`, `ZipReader`, `crc32` | N |
| `data/WidgetState.kt` | `WidgetState`, `WidgetJson`, `widgetState(...)`, `widgetView(...)` | N |
| `data/Widgets.kt` | `expect fun refreshWidgets()`, `expect fun writeWidgetState(json: String)`, `syncWidgets(...)` | A `MoodTraker/.../data/Widgets.kt` |
| `data/Route.kt` | `object Route`: la pantalla que pide un widget o un enlace | N |
| `data/AppInfo.kt` | `PRIVACY_URL`, `SIBLINGS`, `expect object AppInfo` | A `MoodTraker/.../data/AppInfo.kt` |
| `data/MoodTrakerImport.kt` | v1.1: leer la copia de MoodTraker | N |
| `billing/Billing.kt` | `expect val revenueCatApiKey`, `object Billing` | C `MoodTraker/.../billing/Billing.kt` |
| `i18n/Strings.kt` | `expect fun systemLanguage()`, `object S` con `t(en, es, pt, de, fr)` | A `MoodTraker/.../i18n/Strings.kt` |
| `ui/theme/Theme.kt` | `LineTheme`, colores, `enum class Cover`, `SoftShapes`, `Styles` (los diez estilos de `docs/pantallas.md` 1.2), `MAX_CONTENT_WIDTH` | A `MoodTraker/.../ui/theme/Theme.kt` |
| `ui/Icons.kt` | `Glyph`, `GlyphButton`, `GlyphIcon` | A `MoodTraker/.../ui/Icons.kt` |
| `ui/LineField.kt` | el campo de una línea: Literata, tope, contador, `ImeAction.Done` | N |
| `ui/TodayScreen.kt` | Hoy | N |
| `ui/YearScreen.kt` | Año con búsqueda | N |
| `ui/YearGrid.kt` | rejilla 12x31 en `Canvas` | A `HabitTracker/.../ui/YearGrid.kt` |
| `ui/DaySheet.kt` | un día abierto | A `MoodTraker/.../ui/DaySheet.kt` |
| `ui/SettingsScreen.kt` | Ajustes | A `SettingsSheet` de `MoodTraker/.../ui/Pro.kt` |
| `ui/LockScreen.kt` | overlay de bloqueo | N |
| `ui/Pro.kt` | `ProDialog` | A `MoodTraker/.../ui/Pro.kt` |
| `ui/ShareScreen.kt` | vista previa y botones | A `MoodTraker/.../ui/ShareScreen.kt` |
| `share/ShareCard.kt` | `renderYearCard`, `renderLineCard` | A `MoodTraker/.../share/ShareCard.kt` |
| `share/Sharing.kt` | `expect fun ImageBitmap.encodeToPng()`, `expect object Sharing` | C `MoodTraker/.../share/Sharing.kt` |

`shared/src/commonMain/composeResources/font/literata_regular.ttf` (Literata Regular, SIL Open Font
License 1.1, de `github.com/googlefonts/literata`, commit `0c2761b`) y su licencia en
`composeResources/files/OFL.txt`: en `font/` el generador la tomaría por una fuente más. Se borra
`composeResources/drawable/compose-multiplatform.xml` de la plantilla.

### `shared/src/androidMain/kotlin/com/baltajmn/line`

| Fichero | Origen |
|---|---|
| `data/AndroidContext.kt` | C MoodTraker |
| `data/AppInfo.android.kt` | C MoodTraker |
| `data/FilePicker.android.kt` | A MoodTraker: `OpenDocument` para importar, `CreateDocument("application/zip")` para exportar |
| `data/Photos.android.kt` | C MoodTraker, 1024 px y calidad 80 |
| `data/Storage.android.kt` | A MoodTraker: `entries.json`, `entries.bak.json`, `entries.tmp.json`, `photos/` en `filesDir` |
| `data/Lock.android.kt` | N |
| `data/Reminder.android.kt` y `data/ReminderReceiver.kt` (con `BootReceiver`) | A MoodTraker |
| `data/Widgets.android.kt` | A MoodTraker: `widget.json` en `filesDir` y `updateAll` de los dos widgets |
| `widget/TodayWidget.kt` | A `MoodTraker/.../widget/MoodWidget.kt`, sin acción de escritura |
| `widget/YearWidget.kt` | A `HabitTracker/.../widget/YearWidget.kt`: `yearBitmap`, sin configuración |
| `i18n/Strings.android.kt`, `billing/Billing.android.kt`, `share/Sharing.android.kt` | C MoodTraker (carpeta `Pictures/Purl`) |
| `res/drawable/ic_notification.xml` | N, ver `docs/pantallas.md` |

### `shared/src/iosMain/kotlin/com/baltajmn/line`

| Fichero | Origen |
|---|---|
| `MainViewController.kt` | C MoodTraker |
| `LineBridge.kt` | N: lo que Swift llama de Kotlin (sección 7) |
| `data/AppInfo.ios.kt`, `data/FilePicker.ios.kt`, `data/Photos.ios.kt` | A MoodTraker (`UIDocumentPickerViewController` para importar y exportar) |
| `data/Storage.ios.kt` | A MoodTraker: Application Support, **no** el App Group |
| `data/Lock.ios.kt` | N |
| `data/Reminder.ios.kt` | N sobre `ReminderPlan` |
| `data/Widgets.ios.kt` | A MoodTraker: `widget.json` en el App Group y `WidgetCenter` vía `LineBridge` |
| `i18n/Strings.ios.kt`, `billing/Billing.ios.kt`, `share/Sharing.ios.kt` | C MoodTraker |

### Tests

| Fichero | Qué cubre |
|---|---|
| `shared/src/commonTest/kotlin/com/baltajmn/line/ModelTest.kt` | tests 1, 3, 4, 5, 8, 10, 11, 12, 13 |
| `shared/src/commonTest/kotlin/com/baltajmn/line/DataTest.kt` | tests 6, 7, 9, 14, 15 |
| `shared/src/androidHostTest/kotlin/com/baltajmn/line/StorageTest.kt` | test 2 |

### Android e iOS

| Ruta | Qué es |
|---|---|
| `androidApp/src/main/AndroidManifest.xml` | sección 8 |
| `androidApp/src/main/kotlin/com/baltajmn/line/MainActivity.kt` | `FragmentActivity`; lanzadores de fotos y ficheros; `setRecentsScreenshotEnabled`; extra `screen` |
| `androidApp/src/main/res/xml/` | `data_extraction_rules.xml`, `backup_rules.xml`, `locales_config.xml`, `file_paths.xml`, `today_widget_info.xml`, `year_widget_info.xml` |
| `androidApp/src/main/res/layout/today_widget_preview.xml`, `year_widget_preview.xml` | previsualización del selector de widgets, solo vistas admitidas por `RemoteViews` |
| `androidApp/src/main/res/values*/strings.xml` | `app_name` = `Purl` en los cinco idiomas |
| `iosApp/iosApp/iOSApp.swift` | la vista crema de multitarea, `onOpenURL`, `LineBridge` |
| `iosApp/iosApp/Info.plist`, `iosApp.entitlements`, `PrivacyInfo.xcprivacy` | sección 8 |
| `iosApp/iosApp/<lang>.lproj/InfoPlist.strings` | `docs/textos.md` |
| `iosApp/LineWidget/LineWidget.swift` | widget de hoy (sistema y accesorios) y el espejo `L` |
| `iosApp/LineWidget/LineYearWidget.swift` | A `HabitTracker/iosApp/HabitWidget/HabitYearWidget.swift`, sin `AppIntentConfiguration` |
| `iosApp/LineWidget/LineStore.swift` | decodifica `widget.json`, nada más |
| `iosApp/LineWidget/Info.plist`, `LineWidget.entitlements` | sección 8 |
| `iosApp/iosApp.xcodeproj/xcshareddata/xcschemes/iosApp.xcscheme` | esquema compartido, sin él el CI no archiva |
| `tools/play-listing/subir.py` | C MoodTraker, `PACKAGE_NAME = "com.baltajmn.line"` |
| `tools/store/capturas.py`, `tools/store/cabecera.py` | C MoodTraker |
| `tools/demo/generar.py` | N: el diario de ejemplo de las capturas (`store/capturas.md`) |
| `tools/generate_icons.py` | A `HabitTracker/tools/generate_icons.py` con la geometría de `docs/pantallas.md` |

---

## 4. Formatos de datos

### 4.1 `entries.json`

Vive en `filesDir` (Android) y en `Application Support/` (iOS). Nunca en el App Group.

```kotlin
@Serializable
data class LineEntry(
    val text: String = "",
    val photo: String? = null,
    val late: Boolean = false,
    val tags: List<String> = emptyList(),       // v1.1
    val mood: String? = null,                    // v1.2, ids de MoodTraker: m1..m5
)

@Serializable
data class Settings(
    val reminderOn: Boolean = false,
    val reminderHour: Int = 21,
    val reminderMinute: Int = 0,
    val reminderOffered: Boolean = false,
    val lockOn: Boolean = false,
    val cover: String = "sage",
    val lastBackup: String? = null,              // fecha ISO local
    val backupNoticeDone: Boolean = false,
    val pro: Boolean = false,                    // caché de RevenueCat, como isPro de MoodTraker
    val questionsOn: Boolean = false,            // v1.1
    val moodOn: Boolean = false,                 // v1.2
)

@Serializable
data class JournalFile(
    @EncodeDefault val version: Int = 1,
    @EncodeDefault val entries: Map<String, LineEntry> = emptyMap(),
    val settings: Settings = Settings(),
)

@Serializable
data class ExportFile(
    @EncodeDefault val version: Int = 1,
    @EncodeDefault val entries: Map<String, LineEntry> = emptyMap(),
)

typealias Journal = Map<String, LineEntry>

val JournalJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = false
    explicitNulls = false
}
```

- Claves `yyyy-MM-dd` de la fecha lógica local (`LocalDate.toString()`).
- `photo` es el nombre del fichero dentro de `photos/`, con extensión: `"p-3f9a1c2e.jpg"`. En el zip
  va en `photos/<photo>` y en `journal.md` como `![](photos/<photo>)`.
- `encodeDefaults = false`: `late: false`, `photo: null`, `tags: []` y los ajustes por defecto no se
  escriben. `version` y `entries` se escriben siempre (`@EncodeDefault`, que exige
  `@OptIn(ExperimentalSerializationApi::class)`).
- El mapa se guarda ordenado por clave (`toSortedMap()` antes de codificar), para que el fichero sea
  legible y los diffs de las copias tengan sentido.
- `version` sube a 2 solo en v1.2 (`photos` en lugar de `photo`); la lectura de la versión 1 sigue
  funcionando siempre.

### 4.2 `widget.json`

En el App Group (iOS) y en `filesDir` (Android). Se escribe con `encodeDefaults = true`, porque Swift
decodifica todos los campos, y con `explicitNulls = false`, así que en v1.0 `line` y `lineNext` no
llegan a aparecer en el fichero: no hay ni una clave de texto que leer. Los opcionales de Swift
decodifican igual si faltan.

```kotlin
@Serializable
data class WidgetState(
    val date: String,          // fecha lógica de hoy
    val written: Boolean,      // hoy tiene entrada
    val memory: Boolean,       // hoy tiene años anteriores
    val memoryNext: Boolean,   // mañana tiene años anteriores
    val year: Int,             // año de date
    val days: String,          // 366 caracteres '0'/'1', índice dayOfYear - 1 del año `year`
    val cover: String,         // id de portada
    val pro: Boolean,
    val line: String? = null,      // v1.1, widget del recuerdo
    val lineNext: String? = null,  // v1.1
)
```

```swift
struct LineState: Decodable {
    let date: String; let written: Bool; let memory: Bool; let memoryNext: Bool
    let year: Int; let days: String; let cover: String; let pro: Bool
    let line: String?; let lineNext: String?
}
```

`widget.json` no lleva **nunca** texto en v1.0. `line` y `lineNext` solo existen en v1.1 y solo si el
widget del recuerdo está colocado, `pro` y `!lockOn`.

### 4.3 La copia: `purl-AAAA-MM-DD.zip`

Zip STORED (método 0), bit 11 de propósito general puesto (nombres UTF-8), sin cifrado, sin zip64, sin
descriptor de datos: tamaño y CRC van en la cabecera local de cada entrada, así que el lector puede
recorrerlo de principio a fin sin leer el directorio central. Orden fijo:

1. `entries.json`: `ExportFile` (version y entries; sin `settings`).
2. `journal.md`.
3. `photos/<photo>`, una por foto referenciada, en el orden de las fechas.

Hora DOS de cada entrada: la del momento de exportar. El directorio central y el registro de fin
(EOCD) se escriben completos, para que cualquier descompresor lo abra.

### 4.4 `journal.md`

```
# Purl

## 2026-01-17
Primer día de vacaciones, llovió todo el rato.

## 2027-01-17
Mismo día, sol.

![](photos/p-3f9a1c2e.jpg)

## 2027-02-03
Cena con los de la facultad.
#amigos #cena
```

Fechas ISO en orden ascendente, una línea en blanco entre bloques, el texto tal cual (con sus saltos
de línea), la foto como imagen relativa y, desde v1.1, las etiquetas en una línea con `#`. Sin
traducir: es un fichero para dentro de 50 años.

### 4.5 Copia de MoodTraker (v1.1)

JSON plano: `{ "app": "mood", "store": { "entries": [ { "date", "moodId", "note", "tags" } ] }, "photos": {...} }`
(`MoodTraker/shared/.../data/MoodRepository.kt`, `model/Mood.kt`). Se lee como `JsonObject`, sin
clases propias: se exige `app == "mood"`, se toman las entradas con `note` no vacía, `note` pasa a
`text`, `tags` a `tags`, `moodId` a `mood` (v1.2; en v1.1 se ignora) y las fotos se ignoran. Nunca
pisa una fecha que ya tenga entrada.

### 4.6 Validación de una importación

Se rechaza, con el diario intacto y la clave de texto indicada, si:

| Caso | Clave |
|---|---|
| No es zip ni JSON | `importNotBackup` |
| Zip truncado, CRC distinto, método distinto de 0 o entrada cifrada | `importDamaged` |
| Zip sin `entries.json` | `importNotBackup` |
| JSON sin `version` entero o sin objeto `entries` | `importNotBackup` |
| `version` mayor que la que entiende esta app | `importTooNew` |
| Una clave de `entries` que no es fecha ISO válida | `importDamaged` |
| Un `photo` que no es un nombre de fichero suelto (`../`, `/`, `.`, vacío) | `importDamaged` |
| Copia sin ninguna entrada | `importEmpty` |

Una copia de MoodTraker metida por el botón de importar normal se reconoce (`app == "mood"`) y se
responde con `importIsMoodTraker` (en v1.0) o se desvía a su importación (v1.1).

---

## 5. Constantes

| Nombre | Valor | Fichero |
|---|---|---|
| `DAY_CUTOFF_HOUR` | `3` | `model/DayClock.kt` |
| `LINE_LIMIT` | `280` | `model/Text.kt` |
| `COUNTER_FROM` | `250` | `model/Text.kt` |
| `SAVE_DEBOUNCE_MS` | `800` | `data/LineRepository.kt` |
| `RELOCK_AFTER` | `60.seconds` | `App.kt` |
| `FREE_PHOTO_LIMIT` | `3` | `data/LineRepository.kt` |
| `PHOTO_MAX_PX` | `1024` | `data/Photos.kt` |
| `PHOTO_JPEG_QUALITY` | `80` (iOS `0.8`) | `data/Photos.*.kt` |
| `REMINDER_WINDOW` | `60` | `data/ReminderPlan.kt` |
| `REMINDER_DEFAULT_HOUR` / `_MINUTE` | `21` / `0` | `model/Entry.kt` (defaults de `Settings`) |
| `MEMORY_SNIPPET_MAX` | `120` | `data/ReminderPlan.kt` |
| `BACKUP_NOTICE_AFTER_DAYS` | `30` | `data/LineRepository.kt` |
| `STREAK_SHOWN_FROM` | `2` | `model/Insights.kt` |
| `MAX_CONTENT_WIDTH` | `600.dp` | `ui/theme/Theme.kt` |
| `REMINDER_CHANNEL` | `"line-daily"` | `data/Reminder.android.kt` |
| `REMINDER_ID_PREFIX` | `"reminder-"` | `data/Reminder.ios.kt` |
| `APP_GROUP` | `"group.com.baltajmn.line"` | `data/Widgets.ios.kt` |
| `URL_SCHEME` | `"com.baltajmn.line"` | `App.kt` |
| `PRIVACY_URL` | `"https://line.baltajmn.dev/"` | `data/AppInfo.kt` |
| `EXPORT_PREFIX` | `"purl"` | `data/Export.kt` |
| `BACKUP_VERSION` | `1` | `model/Entry.kt` |
| `PREVIEW_CODE_POINTS` | `60` (v1.1) | `ui/YearGrid.kt` |
| `QUESTION_COUNT` | `60` (v1.1) | `i18n/Strings.kt` |
| `TAG_MAX`, `TAGS_PER_ENTRY`, `TAG_SUGGESTIONS` | `24`, `5`, `10` (v1.1) | `model/Text.kt` |
| `PHOTOS_PER_ENTRY` | `4` (v1.2) | `data/LineRepository.kt` |
| `updatePeriodMillis` de los dos widgets | `10800000` | `res/xml/*_widget_info.xml` |

Portadas, en este orden (el de la paleta de la familia), con `sage` gratis y por defecto:

| id | hex | id | hex |
|---|---|---|---|
| `rose` | `F0AFBE` | `mint` | `9CD3C7` |
| `peach` | `F5C39B` | `sky` | `A2C3E9` |
| `butter` | `EDDC98` | `periwinkle` | `B4B8EC` |
| `sage` | `B6D6AB` | `lilac` | `D9AFE6` |

Un id de portada desconocido se lee como `sage`.

---

## 6. Algoritmos

Todas las funciones de `model/` y de los ficheros puros de `data/` reciben la fecha o el instante
como parámetro. Nada de `Clock.System` dentro: se prueban con fechas fijas, como en la familia.

### 6.1 Fecha lógica

```kotlin
fun logicalDate(now: Instant, tz: TimeZone): LocalDate {
    val local = now.toLocalDateTime(tz)
    return if (local.hour < DAY_CUTOFF_HOUR) local.date.minus(1, DateTimeUnit.DAY) else local.date
}
fun logicalDate(local: LocalDateTime): LocalDate = ...misma regla sobre una hora local dada
```

Se compara la hora local, no se restan tres horas a un instante: el cambio de hora no la mueve. "Hoy"
en toda la app es `logicalDate(Clock.System.now(), TimeZone.currentSystemDefault())`, recalculado al
volver a primer plano y cada vez que se pinta Hoy.

Donde el reloj retrasa cruzando las 03:00 (hoy solo `Pacific/Chatham`, que pasa de 03:45 a 02:45), el
día anterior vuelve durante 15 minutos una vez al año. Se acepta: no se guarda nada contra ello.

### 6.2 Años anteriores, ecos y vuelta de la página

```kotlin
fun pastYears(j: Journal, d: LocalDate): List<Pair<LocalDate, LineEntry>> =
    j.mapNotNull { (k, e) -> LocalDate.parse(k).takeIf {
        it.monthNumber == d.monthNumber && it.dayOfMonth == d.dayOfMonth && it.year < d.year
    }?.let { it to e } }.sortedByDescending { it.first }

data class Echoes(val week: Pair<LocalDate, LineEntry>?, val month: Pair<LocalDate, LineEntry>?)

fun echoes(j: Journal, d: LocalDate): Echoes? {
    if (pastYears(j, d).isNotEmpty()) return null
    val w = d.minus(7, DateTimeUnit.DAY)
    val m = d.minus(1, DateTimeUnit.MONTH)          // 31 de marzo: último día de febrero
    return Echoes(j[w.toString()]?.let { w to it }, j[m.toString()]?.let { m to it })
}

fun nextReturn(d: LocalDate): LocalDate =
    if (d.monthNumber == 2 && d.dayOfMonth == 29)
        generateSequence(d.year + 1) { it + 1 }.first { isLeap(it) }.let { LocalDate(it, 2, 29) }
    else d.plus(1, DateTimeUnit.YEAR)
```

Hoy enseña `nextReturn(hoy)` solo cuando `pastYears` está vacío.

### 6.3 Racha, número de día e hitos

```kotlin
fun streak(j: Journal, today: LocalDate): Int {
    fun onTime(d: LocalDate) = j[d.toString()]?.let { !it.late } == true
    var d = if (onTime(today)) today else today.minus(1, DateTimeUnit.DAY)
    var n = 0
    while (onTime(d)) { n++; d = d.minus(1, DateTimeUnit.DAY) }
    return n
}   // se pinta solo si n >= STREAK_SHOWN_FROM

// La racha a tiempo más larga dentro de un año: la tarjeta del año y, en v1.2, la recapitulación.
fun longestStreak(j: Journal, year: Int): Int {
    var best = 0; var run = 0
    var d = LocalDate(year, 1, 1)
    while (d.year == year) {
        run = if (j[d.toString()]?.let { !it.late } == true) run + 1 else 0
        best = maxOf(best, run); d = d.plus(1, DateTimeUnit.DAY)
    }
    return best
}

fun dayNumber(j: Journal, today: LocalDate): Int {
    val first = j.keys.minOrNull()?.let(LocalDate::parse) ?: return 1
    return if (first > today) 1 else first.daysUntil(today) + 1
}

enum class Milestone { FirstLine, Thirty, Hundred, Anniversary, ThreeYears }

fun milestone(j: Journal, today: LocalDate): Milestone? {
    val written = today.toString() in j
    val first = j.keys.minOrNull()?.let(LocalDate::parse) ?: return null
    if (today == first.plus(1, DateTimeUnit.YEAR)) return Milestone.Anniversary
    if (written && threeYearsDate(j) == today) return Milestone.ThreeYears
    if (written && j.size == 100) return Milestone.Hundred
    if (written && j.size == 30) return Milestone.Thirty
    if (written && j.size == 1) return Milestone.FirstLine
    return null
}

// La primera fecha, en orden, cuyo mm-dd acumula entradas de tres años distintos.
fun threeYearsDate(j: Journal): LocalDate? {
    val years = mutableMapOf<String, Int>()
    for (k in j.keys.sorted()) {
        val md = k.substring(5)
        val n = (years[md] ?: 0) + 1
        years[md] = n
        if (n == 3) return LocalDate.parse(k)
    }
    return null
}
```

Una entrada `late` sí cuenta para los hitos de recuento: son líneas del diario. No cuenta para la
racha.

Un diario empezado un 29 de febrero cumple el año el 28 de febrero siguiente: 365 días después, que es
lo que da `plus` al no existir el 29. A propósito, y con test (11).

### 6.4 Texto: puntos de código, tope y plegado

```kotlin
fun String.codePointCount(): Int  // cuenta pares suplentes como uno

// Los primeros n puntos de código, sin partir nunca una pareja suplente ni parar justo antes de un
// punto que prolonga el anterior: marca combinante U+0300 a U+036F, VS16, ZWJ o tono de piel.
fun String.clampCodePoints(n: Int): String

data class Edit(val text: String, val cursor: Int)

// La edición que el campo acepta. old es el texto antes de la pulsación, new el que propone el
// teclado o el pegado y cursor dónde deja el teclado el cursor en new (selection.end).
fun limitEdit(old: String, new: String, cursor: Int, limit: Int = LINE_LIMIT): Edit {
    val allowed = maxOf(limit, old.codePointCount())
    if (new.codePointCount() <= allowed) return Edit(new, cursor)
    // lo insertado acaba en el cursor: lo que va detrás ya estaba
    if (old.endsWith(new.substring(cursor))) {
        q = new.length - cursor
        p = minOf(new.commonPrefixWith(old).length, cursor, old.length - q)
    } else {                                        // cursor que no cuadra: el prefijo más largo
        p = new.commonPrefixWith(old).length
        q = minOf(new.commonSuffixWith(old).length, new.length - p, old.length - p)
    }
    // no partir parejas en los bordes
    if (p > 0 && new[p - 1].isHighSurrogate()) p--
    if (q > 0 && new[new.length - q].isLowSurrogate()) q--
    val room = (allowed - (new[0, p) + new[new.length - q, end)).codePointCount()).coerceAtLeast(0)
    val kept = new.substring(p, new.length - q).clampCodePoints(room)
    return Edit(new[0, p) + kept + new[new.length - q, end), p + kept.length)
}
```

Con `limitEdit` solo se recorta lo que se acaba de insertar, nunca el texto que ya había: pegar en
medio de una línea llena no le corta el final. El cursor se ancla porque sin él no hay regla buena:
adivinar lo insertado por el prefijo común se come la letra siguiente cuando lo pegado empieza por
ella. El cursor queda al final de lo insertado. Un texto que ya pasaba de 280 (importado o dictado)
se puede acortar pero no alargar.

Un emoji de un solo punto de código cuenta uno. Los compuestos (corazón con VS16, tono de piel,
familias, banderas) cuentan sus partes; el corte no los deja a medias, salvo una bandera partida justo
en el borde, que pierde su segunda letra.

```kotlin
fun fold(s: String): String  // minúsculas y sin diacríticos
```

Tabla de plegado, tras `lowercase()`:

| Letras | Queda |
|---|---|
| á à â ã ä å | a |
| é è ê ë | e |
| í ì î ï | i |
| ó ò ô õ ö | o |
| ú ù û ü | u |
| ý ÿ | y |
| ç | c |
| ñ | n |
| ß | ss |
| œ | oe |
| æ | ae |
| marcas combinantes U+0300 a U+036F (acentos descompuestos, NFD) | se quitan |

### 6.5 Búsqueda

```kotlin
fun search(j: Journal, query: String): List<Pair<LocalDate, LineEntry>> {
    val q = fold(query.trim())
    if (q.isEmpty()) return emptyList()
    return j.filter { (_, e) -> fold(e.text).contains(q) || e.tags.any { fold(it).contains(q) } }
        .map { (k, e) -> LocalDate.parse(k) to e }
        .sortedByDescending { it.first }
}
```

La rejilla atenúa en el año visible las celdas cuya fecha no está en el resultado; la lista enseña
todos los años.

### 6.6 Fusión

```kotlin
data class MergeResult(
    val journal: Journal,
    val added: Int,        // fechas que solo tenía la copia
    val joined: Int,       // fechas de los dos lados que cambian
    val same: Int,         // fechas de los dos lados que no cambian
    val photosFromIncoming: Set<String>,   // nombres de foto de la copia que hay que traer
)

fun merge(device: Journal, incoming: Journal): MergeResult
```

Por cada fecha de `incoming`:

1. No está en `device`: se añade tal cual. `added++`. Si trae foto, entra en `photosFromIncoming`.
2. Está en los dos. Sea `a = device.text.trim()`, `b = incoming.text.trim()`:
   - texto: si `a == b` o `b` está vacío, `device.text`; si `a` está vacío, `incoming.text`; si `a`
     contiene a `b`, `device.text`; si `b` contiene a `a`, `incoming.text`; si no,
     `device.text + "\n" + incoming.text`.
   - foto: la de `device` si tiene; si no, la de `incoming` (y entra en `photosFromIncoming`).
   - `late`: el de `device`. `tags`: unión sin repetir, orden de `device` primero. `mood`: el de
     `device` si tiene.
   - Si el resultado es igual a `device`, `same++`; si no, `joined++`.

Las fechas que solo tiene `device` no se tocan. `merge` es pura; el repositorio copia las fotos y
guarda. Una foto de la copia cuyo nombre ya existe en `photos/` se copia con un nombre nuevo
(`p-` más 8 hex) y la entrada se actualiza con ese nombre.

Importar de MoodTraker (v1.1) no usa `merge`: solo añade fechas que no existen.

### 6.7 Importar, paso a paso

1. `FilePicker` entrega un flujo de bytes del fichero elegido.
2. Si empieza por los bytes `50 4B 03 04` (`PK`), `ZipReader` lo recorre entrada a entrada: `entries.json` a
   memoria, cada foto a `import/` (carpeta temporal en el almacenamiento privado), comprobando el CRC
   de cada una. Si empieza por `{`, se lee como `entries.json` suelto (sin fotos).
3. Validación (4.6). Si falla, se borra `import/` y se enseña el error.
4. `merge` en seco: se enseña la confirmación con `added`, `joined` y `same`.
5. Si confirma: se mueven las fotos de `photosFromIncoming` de `import/` a `photos/`, se aplica el
   diario fusionado, se guarda con `flush()` y se borra `import/`. Si cancela, se borra `import/`.

Solo se adoptan las fotos que la copia ha traído de verdad. Una copia que nombra una foto que no
entrega (un `entries.json` suelto, por ejemplo) deja la línea y pierde el nombre de la foto: adoptar
por nombre permitiría que se quedara con lo que una importación anterior hubiera dejado en `import/`.
Las fotos se mueven fuera del hilo principal, y el botón de importar dice `working` mientras tanto.

`import/` se vacía también al arrancar, por si la app murió a mitad.

### 6.8 Exportar

1. `FilePicker.createZip(suggestedName = "purl-" + hoy + ".zip")` devuelve un destino (Android:
   `OutputStream` del documento creado con SAF; iOS: fichero temporal en `tmp/` que luego se entrega
   a `UIDocumentPickerViewController(forExporting:)`).
2. `flush()` y se toma una instantánea del diario.
3. `ZipWriter` escribe `entries.json`, `journal.md` y cada foto, leyendo cada una entera (unos 200 KB)
   para calcular su CRC antes de la cabecera: nunca más de una foto en memoria.
4. Solo cuando el sistema confirma el guardado: `lastBackup = hoy`, `backupNoticeDone = true`. Cancelar
   el selector no es fallar: no se enseña nada y no se toca nada. `exportFailed` es solo para el error
   de verdad.

El aviso de la copia (`docs/pantallas.md` 4.2 J) sale cuando hay entradas, han pasado
`BACKUP_NOTICE_AFTER_DAYS` días **desde la primera entrada del diario** (`first.daysUntil(hoy) >= 30`),
`lastBackup` es nulo y `backupNoticeDone` es falso. `Ahora no` también pone `backupNoticeDone = true`:
es un aviso único, y repetirlo no hace mejor la copia. `backupNoticeDone` vive en `Settings`, que no
viaja en la copia, así que importar un diario ajeno nunca arrastra el aviso ya contestado.

### 6.9 Zip

```kotlin
fun crc32(bytes: ByteArray, start: Int = 0, end: Int = bytes.size, crc: Int = 0): Int  // tabla de 256

class ZipWriter(private val sink: (ByteArray) -> Unit) {
    fun add(name: String, bytes: ByteArray)
    fun finish()          // directorio central y EOCD
}

class ZipReader(private val source: (Int) -> ByteArray?) {   // lee hasta n bytes; null al final
    // Recorre las cabeceras locales en orden. Lanza ZipDamaged si falta una firma, se acaba el
    // fichero antes del directorio central, el CRC no coincide, method != 0 o el bit 0 está puesto.
    fun forEach(block: (name: String, bytes: ByteArray) -> Unit)
}
```

Unas 150 líneas. Firmas: cabecera local `0x04034b50`, directorio central `0x02014b50`, EOCD
`0x06054b50`. Todos los enteros en little endian.

### 6.10 Recordatorio: el siguiente disparo y el plan de iOS

```kotlin
// El siguiente instante de disparo estrictamente posterior a now cuyo día lógico no esté escrito.
fun nextFire(now: LocalDateTime, hour: Int, minute: Int, written: (LocalDate) -> Boolean): LocalDateTime {
    var c = LocalDateTime(now.date, LocalTime(hour, minute))
    if (c <= now) c = c.plusDays(1)
    while (written(logicalDate(c))) c = c.plusDays(1)
    return c
}

data class Planned(val id: String, val at: LocalDateTime, val title: String, val body: String?)

fun reminderPlan(j: Journal, s: Settings, now: LocalDateTime): List<Planned> {
    if (!s.reminderOn) return emptyList()
    val first = nextFire(now, s.reminderHour, s.reminderMinute) { it.toString() in j }
    return (0 until REMINDER_WINDOW).map { i ->
        val at = first.plusDays(i)
        val day = logicalDate(at)
        val memory = if (s.lockOn) null else memorySnippet(j, day)
        Planned("reminder-$day", at,
            if (memory == null) S.reminderTitle else S.reminderMemoryTitle, memory)
    }
}

// La línea de day - 1 año exacto, o null. El 29 de febrero no tiene: la resta recorta al 28 y la
// comprobación de mes y día lo descarta.
fun memorySnippet(j: Journal, day: LocalDate): String? {
    val y = day.minus(1, DateTimeUnit.YEAR)
    if (y.dayOfMonth != day.dayOfMonth) return null
    val text = j[y.toString()]?.text?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    if (text.codePointCount() <= MEMORY_SNIPPET_MAX) return text.replace('\n', ' ')
    val cut = text.clampCodePoints(MEMORY_SNIPPET_MAX)
    val space = cut.lastIndexOf(' ')
    return (if (space > 0) cut.substring(0, space) else cut).replace('\n', ' ') + "..."
}
```

`plusDays` mantiene la hora local: los avisos siguen a las 21:00 tras un cambio de hora. Con una hora
entre las 00:00 y las 02:59, el día lógico del aviso es el anterior, y eso es lo que se comprueba.

`expect object Reminder { fun sync(askPermission: Boolean) }`. Las dos plataformas leen los ajustes y
el diario por su cuenta, así que quien llama solo dice que algo ha cambiado. `askPermission` solo es
cierto en el momento en que se enciende el recordatorio: preguntar al arrancar es de mala educación y
en Android no se puede desde la composición. `LineRepository` lo llama tras cada guardado bueno, no en
cada pulsación: el diario guardado es el único con el que el recordatorio tiene que estar de acuerdo.

- **iOS**: `Reminder.sync()` borra todas las peticiones pendientes con prefijo `reminder-` y programa
  `reminderPlan(...)` con `UNCalendarNotificationTrigger(dateMatching = año, mes, día, hora, minuto,
  repeats = false)` y sonido por defecto. Se llama al arrancar, tras cada guardado, al cambiar la hora,
  al encender o apagar el recordatorio y al cambiar el bloqueo.
- **Android**: `Reminder.sync()` programa una sola alarma con `setAndAllowWhileIdle(RTC_WAKEUP)` en
  `nextFire(...)`. `ReminderReceiver` al disparar: calcula el día lógico de ahora; si está escrito, no
  notifica; si no, notifica con el texto de `memorySnippet` (salvo `lockOn`); y en los dos casos vuelve
  a llamar a `sync()`. `BootReceiver` escucha `BOOT_COMPLETED`, `MY_PACKAGE_REPLACED`, `TIME_SET` y
  `TIMEZONE_CHANGED` y llama a `sync()`. Guardar la línea de hoy también llama a `sync()`, así que la
  alarma de hoy se mueve a mañana sin esperar al receptor.

### 6.11 Estado de los widgets

```kotlin
fun widgetState(j: Journal, s: Settings, today: LocalDate): WidgetState {
    val tomorrow = today.plus(1, DateTimeUnit.DAY)
    val days = CharArray(366) { '0' }
    j.keys.forEach { k -> LocalDate.parse(k).takeIf { it.year == today.year }?.let { days[it.dayOfYear - 1] = '1' } }
    return WidgetState(today.toString(), today.toString() in j, pastYears(j, today).isNotEmpty(),
        pastYears(j, tomorrow).isNotEmpty(), today.year, String(days), s.cover, s.pro)
}

// Lo que pinta el widget ahora, sin la app. La misma regla en Swift.
fun widgetView(st: WidgetState, today: LocalDate): WidgetState = when (st.date) {
    today.toString() -> st
    today.minus(1, DateTimeUnit.DAY).toString() ->
        st.copy(date = today.toString(), written = false, memory = st.memoryNext, line = st.lineNext)
    else -> st.copy(date = today.toString(), written = false, memory = false, line = null)
}.let { if (it.year != today.year) it.copy(year = today.year, days = "0".repeat(366)) else it }
```

Se escribe tras cada guardado, al cambiar portada, bloqueo o Pro, al arrancar y en cada `ON_START`,
que es donde se recoge el cambio de día de las 03:00. Después, `refreshWidgets()`. Un fallo al
escribirlo no puede tumbar un guardado: `syncWidgets` se traga el error y el widget se corrige en el
siguiente.

- Android: `updatePeriodMillis = 10800000` y `widgetView` en `provideGlance`: tras las 03:00 el widget
  se corrige solo antes de las 06:00.
- iOS: `getTimeline` devuelve dos entradas, ahora y las próximas 03:00 (con `widgetView` aplicado a
  cada una), y política `.after(próximas 03:00)`.

### 6.12 Almacén: cargar, reparar, escribir

```kotlin
expect object Storage {
    fun read(): String?                 // entries.json
    fun readPrevious(): String?         // entries.bak.json
    fun write(text: String)             // atómico, rota la .bak
    fun restoreMain(text: String)       // reescribe entries.json sin rotar la .bak
    fun quarantine()                    // mueve las dos a corrupt/entries-<yyyyMMdd-HHmmss>.json y .bak.json
    fun writePhoto(name: String, bytes: ByteArray)
    fun readPhoto(name: String): ByteArray?
    fun deletePhoto(name: String)
    fun listPhotos(): List<String>
    fun importDir(): String             // ruta de import/, creada vacía
}
```

Carga:

1. `read()` decodifica: se usa.
2. Si no, `readPrevious()` decodifica: se usa y `restoreMain()` repara el principal.
3. Si no hay ninguno de los dos: diario nuevo.
4. Si existen y ninguno decodifica: `quarantine()`, diario vacío, y Hoy enseña `noticeCorrupt` hasta
   que se descarte. Nunca se sobrescribe un fichero que no se ha podido leer.
5. Solo tras 1 o 2: barrido de fotos huérfanas (`listPhotos()` menos las referenciadas) y vaciado de
   `import/`.

Escritura, en `LineRepository`:

```kotlin
private val writeLock = Mutex()
private var saveJob: Job? = null
private var written: JournalFile? = null

fun edit(change: (JournalFile) -> JournalFile) {
    file = change(file)                                 // el estado de Compose cambia al momento
    saveJob?.cancel()
    saveJob = scope.launch { delay(SAVE_DEBOUNCE_MS); persist() }
}

suspend fun flush() { saveJob?.cancel(); persist() }

private suspend fun persist() = writeLock.withLock {
    val snapshot = file
    if (snapshot === written) return@withLock
    val ok = withContext(Dispatchers.IO) { runCatching { Storage.write(encode(snapshot)) }.isSuccess }
    if (ok) { written = snapshot; saveFailed = false; afterSave(snapshot) } else saveFailed = true
}
```

Un solo `Mutex`: dos guardados nunca se cruzan ni rotan la `.bak` dos veces, y siempre se escribe la
última instantánea. `saveFailed` pinta `noticeSaveFailed` en Hoy; el siguiente cambio reintenta.
`afterSave` escribe `widget.json`, refresca widgets y sincroniza el recordatorio.

`App.kt` llama a `flush()` en `ON_STOP` (`LifecycleEventEffect`), y los editores al salir.

- Android: temporal, renombrar el actual a `.bak`, renombrar el temporal (como MoodTraker).
- iOS: copiar el actual a `.bak`, `writeToFile(atomically = true)`, y fijar
  `NSFileProtectionCompleteUntilFirstUserAuthentication` en los dos ficheros y en `photos/`.

### 6.13 Crear y editar entradas

```kotlin
fun setText(date: LocalDate, text: String, today: LocalDate)
fun setPhoto(date: LocalDate, bytes: ByteArray?)   // null quita la foto
fun delete(date: LocalDate)                        // tras la confirmación de la UI
```

- Crear (no había entrada y el texto deja de estar en blanco, o se pone una foto): `late = date < today`.
- Editar: conserva `late`.
- Si queda sin texto (en blanco) y sin foto: la entrada sale del mapa (y su foto, si la tenía, se borra
  al guardar).
- Foto: se reescala en la plataforma, se guarda como `p-` más 8 hex aleatorios `.jpg`; la vieja se
  borra tras guardar. Antes de añadir foto a una fecha que no tiene: si `!pro` y ya hay
  `FREE_PHOTO_LIMIT` entradas con foto, se abre el `ProDialog` y no se hace nada más.
- No se puede escribir en fechas posteriores a hoy.

### 6.14 Bloqueo

```kotlin
expect object Lock {
    fun isAvailable(): Boolean
    fun authenticate(onResult: (Boolean) -> Unit)
    fun setHidesPreview(on: Boolean)    // Android 13+; en iOS no hace nada (lo pinta Swift)
}
```

- Android, en `Lock.android.kt` sobre `BiometricPrompt` de `androidx.biometric` 1.1.0, alojado en la
  `FragmentActivity` que `MainActivity` registra al crearse (`Lock.host = WeakReference(this)`):
  - API 30+: `setAllowedAuthenticators(BIOMETRIC_WEAK or DEVICE_CREDENTIAL)`, sin botón negativo.
    `isAvailable() = canAuthenticate(BIOMETRIC_WEAK or DEVICE_CREDENTIAL) == BIOMETRIC_SUCCESS`.
  - API 24-29: `setAllowedAuthenticators(BIOMETRIC_WEAK)` y `setDeviceCredentialAllowed(true)`.
    `isAvailable() = KeyguardManager.isDeviceSecure`.
  - Título y subtítulo del diálogo: `lockPromptTitle`, `lockPromptSubtitle`.
  - `setHidesPreview(on)`: en API 33+, `activity.setRecentsScreenshotEnabled(!on)`.
- iOS: `LAContext().canEvaluatePolicy(.deviceOwnerAuthentication)` y `evaluatePolicy` con
  `localizedReason = lockPromptSubtitle`; el resultado vuelve al hilo principal.
- En `App.kt`: `locked = settings.lockOn` al arrancar. En `ON_STOP`, `backgroundAt =
  TimeSource.Monotonic.markNow()`. En `ON_START`, si `lockOn` y `backgroundAt.elapsedNow() >=
  RELOCK_AFTER`, `locked = true`. Con `locked`, `LockScreen` tapa todo y llama a `authenticate` al
  aparecer.
- Encender el bloqueo: `authenticate`; solo si responde `true` se guarda `lockOn = true`. Apagar: sin
  autenticar.

### 6.15 Compras

`Billing` es el de MoodTraker, con `LineRepository.updatePro(active)` que escribe `settings.pro` y
dispara `widget.json`. `Billing.configure()` al arrancar y `Billing.refresh()` en cada `ON_START`. Un
fallo de red nunca quita el Pro guardado. Claves públicas como literal en `Billing.android.kt`
(`goog_...`) y `Billing.ios.kt` (`appl_...`), `null` hasta que exista el proyecto.

### 6.16 Apps hermanas

```kotlin
data class Sibling(val name: String, val tagline: String, val androidUrl: String?, val iosUrl: String?)
val SIBLINGS: List<Sibling>   // en data/AppInfo.kt; tagline por clave de docs/textos.md
```

Quilt (`siblingQuilt`) y MoodTraker (`siblingMood`), con la URL de cada tienda **solo si la app está
en producción** en ella el día que se publica Purl; si no, `null`. Se rellena en el paso de salida
(#30, `store/lanzamiento.md`). Ajustes enseña las filas con URL para la plataforma actual y oculta la
sección si no queda ninguna.

### 6.17 Portadas

`Cover` en `Theme.kt` con los ocho de la tabla de la sección 5 (`Cover.of(id)`). Elegir una portada que no es `sage`
sin `pro` abre el `ProDialog` y no cambia nada. Con `pro` falso, una portada Pro ya elegida se
mantiene (D-B6).

---

## 7. Puentes con cada plataforma

### Kotlin que llama Swift (`iosMain/.../LineBridge.kt`)

```kotlin
object LineBridge {
    fun isLockOn(): Boolean                       // para la vista crema de multitarea
    fun open(url: String)                         // onOpenURL: today, year o pro
    var reloadWidgets: (() -> Unit)? = null       // lo asigna iOSApp.swift: WidgetCenter.shared.reloadAllTimelines()
    fun setMemoryWidgetPlaced(placed: Boolean)    // v1.1, desde WidgetCenter.getCurrentConfigurations
    fun dictate(text: String): DictateResult      // v1.1, Siri: Saved, NeedsPro
}
```

### Swift en `iOSApp.swift`

- `ZStack { ComposeView(); if scenePhase != .active && LineBridge.shared.isLockOn() { Color(cream) } }`,
  con el crema `#FBF8F3` o `#17150F` según el esquema del sistema.
- `.onOpenURL { LineBridge.shared.open(url: $0.absoluteString) }`.
- Al arrancar: `LineBridge.shared.reloadWidgets = { WidgetCenter.shared.reloadAllTimelines() }`.

### Android

- `MainActivity` (`FragmentActivity`): `enableEdgeToEdge()` antes de `super.onCreate`,
  `AndroidContext.init`, `Lock.host`, lanzadores de `PickVisualMedia`, `OpenDocument` y
  `CreateDocument`, y el extra `screen` (`today`, `year`, `pro`) que traen los widgets. Maneja
  `onNewIntent` igual.
- `Widgets.android.kt`: `writeWidgetState` a `filesDir/widget.json`; `refreshWidgets` con
  `TodayWidget().updateAll(context)` y `YearWidget().updateAll(context)` en una corrutina.

---

## 8. Configuración de plataforma

### 8.1 `AndroidManifest.xml`

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.USE_BIOMETRIC" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:label="@string/app_name"
        android:localeConfig="@xml/locales_config"
        android:supportsRtl="true"
        android:theme="@style/Theme.Purl">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:launchMode="singleTask"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <receiver android:name="com.baltajmn.line.data.ReminderReceiver" android:exported="false" />

        <!-- Broadcasts protegidos del sistema: exported es obligatorio para recibirlos. -->
        <receiver android:name="com.baltajmn.line.data.BootReceiver" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
                <action android:name="android.intent.action.MY_PACKAGE_REPLACED" />
                <action android:name="android.intent.action.TIME_SET" />
                <action android:name="android.intent.action.TIMEZONE_CHANGED" />
            </intent-filter>
        </receiver>

        <receiver android:name="com.baltajmn.line.widget.TodayWidgetReceiver" android:exported="true">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>
            <meta-data android:name="android.appwidget.provider" android:resource="@xml/today_widget_info" />
        </receiver>

        <receiver android:name="com.baltajmn.line.widget.YearWidgetReceiver" android:exported="true">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>
            <meta-data android:name="android.appwidget.provider" android:resource="@xml/year_widget_info" />
        </receiver>

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data android:name="android.support.FILE_PROVIDER_PATHS" android:resource="@xml/file_paths" />
        </provider>
    </application>
</manifest>
```

`@style/Theme.Purl` en `res/values/themes.xml` hereda de `android:Theme.Material.Light.NoActionBar`
con `android:windowBackground` crema `#FBF8F3`, y en `values-night` de
`android:Theme.Material.NoActionBar` con `#17150F`: el arranque no destella en blanco.

### 8.2 `res/xml`

`data_extraction_rules.xml` (Android 12+):

```xml
<?xml version="1.0" encoding="utf-8"?>
<data-extraction-rules>
    <cloud-backup disableIfNoEncryptionCapabilities="true">
        <include domain="file" path="entries.json" />
        <include domain="file" path="entries.bak.json" />
    </cloud-backup>
    <device-transfer>
        <include domain="file" path="entries.json" />
        <include domain="file" path="entries.bak.json" />
        <include domain="file" path="photos/" />
    </device-transfer>
</data-extraction-rules>
```

`backup_rules.xml` (Android 11 y anteriores):

```xml
<?xml version="1.0" encoding="utf-8"?>
<full-backup-content>
    <include domain="file" path="entries.json" requireFlags="clientSideEncryption" />
    <include domain="file" path="entries.bak.json" requireFlags="clientSideEncryption" />
</full-backup-content>
```

Con un `include`, solo se copia lo incluido: `widget.json`, `import/`, `corrupt/` y las preferencias de
RevenueCat se quedan fuera. Las fotos solo viajan en el traspaso entre dispositivos (sin tope de 25 MB).
`settings.pro` viaja dentro de `entries.json` y `Billing.refresh()` lo corrige en el primer arranque.

`locales_config.xml`: el de la familia (`en`, `es`, `pt`, `de`, `fr`).

`values/colors.xml` y `values-night/colors.xml`: `widget_background`, `widget_on_background`,
`widget_muted` y `widget_outline`, los mismos hex que `Light` y `Dark`. Solo los usa la
previsualización del selector, que es `RemoteViews` y no puede leer el tema de Compose.

`layout/today_widget_preview.xml` y `drawable/widget_circle.xml`: la previsualización, con la fecha
de muestra y el estado sin escribir (`widget_preview_date`, `widget_preview_state`).

`file_paths.xml`:

```xml
<paths><cache-path name="share" path="share/" /></paths>
```

`today_widget_info.xml`:

```xml
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="110dp" android:minHeight="110dp"
    android:targetCellWidth="2" android:targetCellHeight="2"
    android:resizeMode="horizontal|vertical"
    android:updatePeriodMillis="10800000"
    android:previewLayout="@layout/today_widget_preview"
    android:initialLayout="@layout/today_widget_preview"
    android:description="@string/widget_today_description"
    android:widgetCategory="home_screen" />
```

`year_widget_info.xml`: igual con `minWidth="180dp"`, `minHeight="110dp"`, `targetCellWidth="4"`,
`targetCellHeight="2"`, `year_widget_preview` y `widget_year_description`. Sin `configure`.

### 8.3 iOS

`Config.xcconfig`:

```
TEAM_ID=
APP_BUNDLE_ID=com.baltajmn.line
PRODUCT_NAME=Purl
PRODUCT_BUNDLE_IDENTIFIER=$(APP_BUNDLE_ID)
CURRENT_PROJECT_VERSION=1
MARKETING_VERSION=1.0
```

Proyecto: `IPHONEOS_DEPLOYMENT_TARGET = 17.0` en los dos targets, `TARGETED_DEVICE_FAMILY = 1,2`,
orientaciones como la familia (iPhone vertical y horizontal; iPad las cuatro).

`iosApp/iosApp/Info.plist`, claves además de las del proyecto:

| Clave | Valor |
|---|---|
| `CADisableMinimumFrameDurationOnPhone` | `true` |
| `CFBundleDisplayName` | `Purl` |
| `CFBundleLocalizations` | `en`, `es`, `pt`, `de`, `fr` |
| `NSFaceIDUsageDescription` | texto en inglés; traducciones en `InfoPlist.strings` |
| `NSPhotoLibraryAddUsageDescription` | texto en inglés; traducciones en `InfoPlist.strings` |
| `ITSAppUsesNonExemptEncryption` | `false` |
| `CFBundleURLTypes` | un tipo con `CFBundleURLSchemes = [com.baltajmn.line]` |

`iosApp/LineWidget/Info.plist`: `NSExtension/NSExtensionPointIdentifier =
com.apple.widgetkit-extension` y el mismo `CFBundleLocalizations`.

`iosApp.entitlements` y `LineWidget.entitlements`: solo `com.apple.security.application-groups =
[group.com.baltajmn.line]`.

`iosApp/iosApp/PrivacyInfo.xcprivacy`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>NSPrivacyTracking</key>
    <false/>
    <key>NSPrivacyTrackingDomains</key>
    <array/>
    <key>NSPrivacyCollectedDataTypes</key>
    <array>
        <dict>
            <key>NSPrivacyCollectedDataType</key>
            <string>NSPrivacyCollectedDataTypePurchaseHistory</string>
            <key>NSPrivacyCollectedDataTypeLinked</key>
            <false/>
            <key>NSPrivacyCollectedDataTypeTracking</key>
            <false/>
            <key>NSPrivacyCollectedDataTypePurposes</key>
            <array><string>NSPrivacyCollectedDataTypePurposeAppFunctionality</string></array>
        </dict>
        <dict>
            <key>NSPrivacyCollectedDataType</key>
            <string>NSPrivacyCollectedDataTypeUserID</string>
            <key>NSPrivacyCollectedDataTypeLinked</key>
            <false/>
            <key>NSPrivacyCollectedDataTypeTracking</key>
            <false/>
            <key>NSPrivacyCollectedDataTypePurposes</key>
            <array><string>NSPrivacyCollectedDataTypePurposeAppFunctionality</string></array>
        </dict>
    </array>
    <key>NSPrivacyAccessedAPITypes</key>
    <array>
        <dict>
            <key>NSPrivacyAccessedAPIType</key>
            <string>NSPrivacyAccessedAPICategoryFileTimestamp</string>
            <key>NSPrivacyAccessedAPITypeReasons</key>
            <array><string>0A2A.1</string></array>
        </dict>
        <dict>
            <key>NSPrivacyAccessedAPIType</key>
            <string>NSPrivacyAccessedAPICategorySystemBootTime</string>
            <key>NSPrivacyAccessedAPITypeReasons</key>
            <array><string>35F9.1</string></array>
        </dict>
    </array>
</dict>
</plist>
```

Fuentes y porqué:

- `FileTimestamp 0A2A.1` y `SystemBootTime 35F9.1`: la documentación de JetBrains dice que Compose
  Multiplatform puede dejar `fstat`, `stat` y `mach_absolute_time` en el binario y que se declaren con
  esas razones ([Privacy manifest for iOS apps](https://kotlinlang.org/docs/multiplatform/multiplatform-privacy-manifest.html)).
  El framework `Shared` es estático, así que el manifiesto de la app es el que cuenta. `35F9.1` cubre
  además el reloj monótono del relock (medir tiempo entre eventos de la app).
- Tipos de datos: los de RevenueCat, que coinciden con lo que se declara en App Privacy
  (`store/formularios.md`). RevenueCat trae además su propio manifiesto.
- Sin `UserDefaults`: nuestro código no lo usa; todo va en `entries.json`.
- La comprobación final es el informe de privacidad de Xcode sobre el primer archivo (Product,
  Archive, Generate Privacy Report). Lo que añada, se añade aquí y ahí.

---

## 9. CI

Se copian los cuatro workflows de MoodTraker a `.github/workflows/`:

| Workflow | Cambios |
|---|---|
| `tests.yml` | ninguno (JVM en `ubuntu-latest`, iOS en `macos-26`) |
| `release.yml` | `package-name: com.baltajmn.line`; `whatsnew-dir: store/whatsnew`; sin `signer-cn`: la clave de subida se genera con `CN=Baltasar`, el valor por defecto del workflow compartido (`store/ci.md`) |
| `release-ios.yml` | archivo `Purl.xcarchive`; esquema `iosApp`; se salta solo sin `APPSTORE_KEY_ID` |
| `listings.yml` | ninguno |

Publicar: `git tag v1.0 && git push origin v1.0`. Secretos en `store/ci.md`.

---

## 10. Tests

Fechas siempre fijas y pasadas como parámetro.

1. **Serialización**: ida y vuelta de un `JournalFile` con comillas, `\n`, un emoji (escrito en el
   test con el escape `\uD83D\uDE42`), acentos y `late`; los defaults no aparecen en el JSON; `version`
   y `entries` sí.
2. **Escritura atómica** (`androidHostTest`): se deja un `entries.tmp.json` a medias y un `entries.json`
   ilegible; la carga cae a `entries.bak.json` y repara el principal. Con los dos ilegibles, se ponen
   en cuarentena y no se sobrescriben.
3. **Años anteriores**: 2026-01-17 y 2028-01-17 aparecen para 2029-01-17 en ese orden; 2028-02-29 no
   aparece para 2029-02-28 ni para 2030-03-01; sí para 2032-02-29.
4. **Fecha lógica y racha**: 2027-01-17 02:59 es el 16; 03:00 es el 17; un cambio de hora (2027-03-28
   en Europe/Madrid) no mueve el corte. Racha: 15, 16 y 17 escritos a tiempo dan 3 el 17; si el 16 es
   `late`, dan 1; el 18 por la mañana sin escribir sigue dando 3.
5. **Migración**: un JSON sin `settings`, sin `late` y con un campo desconocido se lee; un `photo` de
   la versión 1 se lee en v1.2 como lista de uno.
6. **Zip**: ida y vuelta con `entries.json`, `journal.md` y dos fotos binarias; `crc32("123456789")`
   es `0xCBF43926`; un zip cortado en la mitad se rechaza (`ZipDamaged`) sin tocar el diario.
7. **Plan de iOS**: con recordatorio a las 21:00, a las 10:00 y hoy sin escribir, la primera es hoy; a
   las 10:00 con hoy escrito, mañana; a las 22:00, mañana; son 60; con `lockOn` ninguna lleva cuerpo;
   el 2028-02-29 no lleva recuerdo; con la hora a las 01:30, el día lógico del aviso es el anterior.
8. **Tope de 280**: un emoji (`\uD83D\uDE42`) cuenta 1; pegar 300 caracteres en un campo vacío deja 280; pegar en medio de
   una línea de 279 inserta 1 punto de código y conserva el final; pegar "happy " tras "went " en
   "went home" al borde conserva la "h" de "home"; nunca queda media pareja suplente ni un corazón sin
   su VS16; un texto de 300 se puede acortar y no alargar.
9. **Estado de los widgets**: `widgetState` con recuerdos hoy y mañana; `widgetView` con la fecha de
   ayer da `written = false` y `memory = memoryNext`; con la de anteayer, `memory = false`; al cambiar
   de año, `days` vacío. En v1.1: con `lockOn`, `line` es nulo aunque el widget esté colocado.
10. **Fusión**: fecha nueva se añade; mismo texto no cambia; "Sol" contra "Sol y playa" deja el largo;
    textos distintos quedan los dos con `\n`; foto del dispositivo gana; contadores `added`, `joined`
    y `same` correctos; ninguna fecha del dispositivo se pierde.
11. **Hitos**: primera línea, 30 y 100 solo con hoy escrito y el recuento exacto; aniversario aunque
    hoy no esté escrito, y el de un diario empezado el 29 de febrero cae el 28; tres años el primer día que un mm-dd junta tres años, y no el siguiente;
    prioridad cuando coinciden.
12. **Ecos y vuelta**: sin años anteriores, hace una semana y hace un mes (2027-03-31 da 2027-02-28);
    con años anteriores, sin ecos; `nextReturn(2028-02-29)` es 2032-02-29.
13. **Plegado y búsqueda**: `"cafe"` encuentra `"Café"`, `"ano"` encuentra `"año"`, `"strasse"` encuentra
    `"Straße"` y `"cafe"` encuentra un `"Café"` descompuesto; resultados de todos los años, más reciente primero.
14. **Siguiente disparo de Android**: `nextFire` a las 20:59 con hoy sin escribir es hoy a las 21:00; con
    hoy escrito, mañana; a las 21:00 en punto, mañana.
15. **Importación**: un JSON sin `entries` se rechaza con `importNotBackup`; `version: 99` con
    `importTooNew`; una copia de MoodTraker se reconoce.

---

## 11. Qué gobierna cada issue

| Issue | Secciones de este documento | Otros |
|---|---|---|
| #1 Nombre e identificadores | 1 | SPEC §7, `store/lanzamiento.md` |
| #2 Precio | 6.15 | SPEC §6, `store/revenuecat.md` |
| #3 Altas y cuentas | 9 | `store/lanzamiento.md`, `store/ci.md`, `store/revenuecat.md` |
| #4 Andamiaje | 1, 2, 3, 8, 9 | |
| #5 Modelo | 4.1, 5, 6.1, 6.2, 6.3, 6.4 | tests 1, 3, 4, 11, 12 |
| #6 Almacén | 4.1, 6.12, 6.13 | tests 1, 2, 5 |
| #7 Strings | | `docs/textos.md` |
| #8 Tema | 3, 5 | `docs/pantallas.md` |
| #9 Hoy | 6.1, 6.4, 6.13 | `docs/pantallas.md`, test 8 |
| #10 Años anteriores, ecos, hitos | 6.2, 6.3 | tests 3, 11, 12 |
| #11 Año | 6.5 | `docs/pantallas.md`, test 13 |
| #12 Ajustes | 6.14, 6.15, 6.16, 6.17 | `docs/pantallas.md` |
| #13 Recordatorio Android | 6.10, 8.1 | test 14 |
| #14 Recordatorio iOS | 6.10 | test 7 |
| #15 Zip | 4.3, 6.9 | test 6 |
| #16 Copia | 4.3, 4.4, 4.6, 6.6, 6.7, 6.8 | tests 10, 15 |
| #17 Copia del sistema | 8.2 | |
| #18 Bloqueo | 6.14, 7 | |
| #19 `widget.json` | 4.2, 6.11 | test 9 |
| #20 Widget de hoy Android | 6.11, 7, 8.2 | `docs/pantallas.md` |
| #21 Widget de hoy iOS | 4.2, 6.11, 8.3 | `docs/pantallas.md` |
| #22 Foto | 6.13, 5 | |
| #23 Compras | 6.15 | `store/revenuecat.md` |
| #24 Portadas | 5, 6.17 | `docs/pantallas.md` |
| #25 Tarjeta | 3 | `docs/pantallas.md` |
| #26 Icono | 3 | `docs/pantallas.md` |
| #27 Privacidad y formularios | 8.3 | `store/formularios.md`, `store/privacy/` |
| #28 Ficha | | `store/listings/`, `store/app-store/`, `store/capturas.md` |
| #29 Prueba cerrada | 9 | `store/lanzamiento.md` |
| #30 Salida | | `store/lanzamiento.md` |
| #31 Libro PDF (v1.1) | 12.1 | `docs/pantallas.md` |
| #32 Siri (v1.1) | 7, 12.2 | `docs/textos.md` |
| #33 Widget del recuerdo (v1.1) | 4.2, 6.11, 12.3 | |
| #34 Widget de bloqueo iOS (**v1.0**) | 4.2, 6.11 | `docs/pantallas.md` |
| #35 Widget del año (**v1.0**) | 4.2, 6.11, 8.2 | `docs/pantallas.md` |
| #36 Baldosa (v1.1) | 12.4 | |
| #37 Importar MoodTraker (v1.1) | 4.5, 12.5 | |
| #38 Etiquetas (v1.1) | 4.1, 5, 12.6 | |
| #39 Pulsación larga (v1.1) | 5 | `docs/pantallas.md` |
| #40 Preguntas (v1.1) | 12.7 | `docs/textos.md` |
| #41 Recaps (v1.2) | 12.8 | `docs/pantallas.md` |
| #42 Ánimo (v1.2) | 4.1, 12.9 | |
| #43 Varias fotos (v1.2) | 12.10 | |
| #44 Sincronización (v1.2) | 12.11 | SPEC §9 |

---

## 12. v1.1 y v1.2

### 12.1 Libro en PDF

`book/Book.kt` (común) calcula las páginas; `expect object BookRenderer` las dibuja: Android con
`PdfDocument` y un `Typeface` cargado de la misma Literata; iOS con `UIGraphicsPDFRenderer` y la fuente
registrada con `CTFontManagerRegisterFontsForURL`. A5 de 420x595 puntos. Páginas: portada, y una por
fecha del calendario (1 de enero a 31 de diciembre, 29 de febrero incluido si existe) con al menos una
entrada, con un bloque por año en orden **ascendente**. Se dibuja página a página, sin montar el libro
en memoria, en un fichero temporal que luego se entrega al selector del sistema. Nombre
`purl-book-AAAA-MM-DD.pdf`. Cancelable entre páginas. Maqueta en `docs/pantallas.md`.

### 12.2 Siri

`iosApp/iosApp/Shortcuts.swift`: `struct WriteLineIntent: AppIntent` con `@Parameter var line: String`,
`static var authenticationPolicy: IntentAuthenticationPolicy = .requiresAuthentication`, título y
descripción literales en inglés, y `perform()` que llama a `LineBridge.shared.dictate(text:)`:

- Sin Pro: devuelve `NeedsPro` y el intent responde con el diálogo `siriNeedsPro`.
- Con Pro: si hoy no tiene entrada, la crea con ese texto; si la tiene, añade `"\n" + text`. Nunca
  recorta. Guarda con `flush()`. Responde `siriSaved`.

`AppShortcutsProvider` con las frases de `docs/textos.md`, siempre con `\(.applicationName)`.

### 12.3 Widget del recuerdo

Kind `LineMemoryWidget` (iOS, `systemSmall` y `systemMedium`, `.privacySensitive()` sobre el texto) y
`MemoryWidget` en Glance (receptor `MemoryWidgetReceiver`, mínimo 180x110 dp). La app sabe si está
colocado: Android con `GlanceAppWidgetManager(context).getGlanceIds(MemoryWidget::class.java)`; iOS con
`WidgetCenter.shared.getCurrentConfigurations`, que `iOSApp.swift` consulta al arrancar y pasa a
`LineBridge.setMemoryWidgetPlaced`. Solo entonces, con `pro` y sin `lockOn`, `widgetState` rellena
`line = memorySnippet(j, today)` y `lineNext = memorySnippet(j, tomorrow)`, sin el recorte de 120 (el
widget recorta al pintar). Sin Pro: estado bloqueado, toque al paywall.

### 12.4 Baldosa

`QuickTileService` (A `HabitTracker/.../QuickToggleTileService.kt`): etiqueta `tileLabel`, subtítulo
`tileWritten` o `tileNotWritten` (API 29+), estado `STATE_ACTIVE` si hoy está escrito. Al tocar:
`startActivityAndCollapse` con el extra `screen=today` y `focus=true` (en API 34+, la variante con
`PendingIntent`). Sin Pro, `screen=pro`.

### 12.5 Importar de MoodTraker

Botón en Ajustes, sección de copia. Lee 4.5, añade solo fechas nuevas con `late = false`, confirma con
`importMoodCount(nuevas, saltadas)` y guarda.

### 12.6 Etiquetas

Normalización: `trim()`, minúsculas, espacios internos a `-`, se quita un `#` inicial, recorte a
`TAG_MAX` puntos de código; vacía o repetida, se descarta; como mucho `TAGS_PER_ENTRY`. Sugerencias: las
`TAG_SUGGESTIONS` más usadas del diario, excluidas las que ya tiene la entrada.

### 12.7 Preguntas del día

`S.question(i)` con `i = today.toEpochDays().mod(QUESTION_COUNT)`. Solo en el campo vacío de Hoy y solo
con `questionsOn`.

### 12.8 Recapitulaciones

`recapMonth(j, year, month)` y `recapYear(j, year)` en `model/Insights.kt`: días escritos, fotos, racha
más larga del periodo, y la lista de entradas en orden. Sin notificación. Del 26 de diciembre al 7 de
enero, Hoy enseña `recapYearOffer(año)`: hasta el 31, el año en curso; desde el 1, el anterior.

### 12.9 Ánimo

`mood` con los ids de MoodTraker `m1` a `m5` y sus colores (`m5 EDDC98`, `m4 B6D6AB`, `m3 DCD3C4`,
`m2 B4B8EC`, `m1 97A9CE`). Solo con `moodOn`. La importación de MoodTraker trae `moodId` desde esta
versión.

### 12.10 Varias fotos

`version: 2`, `photos: List<String>` en lugar de `photo` (la lectura de la versión 1 convierte `photo`
en lista de uno), hasta `PHOTOS_PER_ENTRY` con Pro. El plan gratis sigue en tres entradas con una foto.
El zip, `journal.md` y el libro pintan todas.

### 12.11 Sincronización

Política de SPEC §9. Cada dispositivo guarda `sync/base.json`, la última versión sincronizada. Por
fecha: si solo cambió un lado respecto a `base`, gana ese lado; si cambiaron los dos, `merge` (6.6);
borrado contra edición, gana la edición. iOS sobre el contenedor de iCloud Drive con `NSMetadataQuery`;
Android sobre una carpeta elegida con `OpenDocumentTree`. La carpeta tiene la estructura del zip
desempaquetado.
