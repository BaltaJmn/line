# Mapa del repositorio

Inventario de todo lo que hay y dónde está. Los otros documentos de la raíz tienen otro trabajo:
[`CLAUDE.md`](CLAUDE.md) y [`AGENTS.md`](AGENTS.md) son las reglas de trabajo que se cargan solas en
cada sesión, y [`SPEC.md`](SPEC.md) explica el porqué de cada decisión de producto.

El árbol completo del código, fichero a fichero y con el origen de cada uno, está en
[`docs/tecnico.md`](docs/tecnico.md) 3. Cada issue que añade ficheros los añade también aquí.

## Raíz

| Fichero | Qué es |
|---|---|
| `README.md` | Qué es Purl y cómo se compila |
| `SPEC.md` | Spec de producto: benchmark, alcance por versión, diseño, retención, monetización, nombre, cumplimiento, plan y riesgos |
| `CLAUDE.md`, `AGENTS.md` | Contexto permanente y contratos que no se rompen |
| `MAPA.md` | Este fichero |
| `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, `gradlew`, `gradle/` | Gradle, con las versiones de las hermanas (`docs/tecnico.md` 2) |
| `.gitignore` | Firma, claves, cuentas de servicio y la salida del diario de demostración fuera del repositorio |
| `keystore.properties`, `local.properties` | Locales, ignorados por git, nunca se suben |

## Código

| Ruta | Qué hay |
|---|---|
| `shared/build.gradle.kts` | Módulo común: objetivos Android e iOS, framework estático `Shared`, el `-L` de Swift para los tests de iOS |
| `shared/src/commonMain/kotlin/com/baltajmn/line` | `App.kt` |
| `shared/src/commonMain/.../line/model` | `Entry.kt` (formato de `entries.json`), `DayClock.kt` (día lógico, años anteriores, ecos), `Insights.kt` (racha, hitos), `Text.kt` (tope de 280, plegado) |
| `shared/src/commonMain/.../line/data` | `Storage.kt` (expect del almacén), `LineRepository.kt` (fuente única, escritor único con `Mutex` y rebote de 800 ms) |
| `shared/src/androidMain/.../line/data` | `AndroidContext.kt`, `Storage.android.kt` (`filesDir`, temporal con `fsync` y renombrado) |
| `shared/src/iosMain/.../line/data` | `Storage.ios.kt` (Application Support, escritura atómica con protección hasta el primer desbloqueo) |
| `shared/src/commonMain/.../line/ui/theme` | `Theme.kt`: `LineTheme`, paleta de la familia sin rojo, `Cover`, `Styles` con Literata para el texto del usuario |
| `shared/src/commonMain/.../line/ui` | `Icons.kt` (los diez `Glyph` pintados con `Canvas`), `LineField.kt`, `Photo.kt` (foto 4:3 y boton del selector), `TodayScreen.kt`, `YearScreen.kt`, `YearGrid.kt`, `DaySheet.kt`, `SettingsScreen.kt`, `LockScreen.kt`, `Pro.kt` (el unico paywall) |
| `shared/src/commonMain/composeResources` | `font/literata_regular.ttf`, `files/OFL.txt` (su licencia) |
| `shared/src/commonMain/.../line/billing` | `Billing.kt`: RevenueCat, derecho `pro`, y las claves publicas por plataforma |
| `shared/src/commonMain/.../line/i18n` | `Strings.kt`: tabla `S` con los cinco idiomas, fechas escritas a mano y plurales |
| `shared/src/androidMain/.../line/i18n`, `iosMain/.../i18n` | `Strings.android.kt`, `Strings.ios.kt`: idioma del sistema |
| `shared/src/commonTest/.../line` | `ModelTest.kt`: tests 1, 3, 4, 5, 8, 11, 12 y el plegado del 13; `DataTest.kt`: crear y editar entradas; `i18n/StringsTest.kt`: los ejemplos de `docs/textos.md` |
| `shared/src/androidHostTest/.../line` | `StorageTest.kt`: test 2, carga, reparación, cuarentena y barrido de fotos |
| `shared/src/iosMain/kotlin/com/baltajmn/line` | `MainViewController.kt` |
| `androidApp/build.gradle.kts` | `com.baltajmn.line`, firma de release desde `keystore.properties` |
| `androidApp/src/main` | `MainActivity.kt` (`FragmentActivity`), manifiesto, tema `Theme.Purl`, `locales_config.xml` |
| `iosApp/iosApp` | `iOSApp.swift`, `ContentView.swift`, `Info.plist`, `iosApp.entitlements` |
| `iosApp/LineWidget` | Extensión de widgets: `Info.plist`, `LineWidget.entitlements`, `LineWidget.swift` (provisional hasta #21) |
| `iosApp/Configuration/Config.xcconfig` | Versión, identificador y Team ID de iOS |
| `iosApp/iosApp.xcodeproj/xcshareddata/xcschemes` | El esquema `iosApp`, compartido: sin él el CI no puede archivar |
| `.github/workflows` | `tests.yml`, `release.yml` (Play), `release-ios.yml` (TestFlight), `listings.yml` (ficha) |
| `tools/play-listing/subir.py` | Comprueba los topes de la ficha de Play y la sube |

## Documentos para programar

| Ruta | Qué hay |
|---|---|
| `docs/tecnico.md` | Identificadores, versiones, árbol de ficheros, formatos de datos, constantes, algoritmos, almacén, bloqueo, compras, puentes con Swift, manifiesto, `Info.plist`, `PrivacyInfo.xcprivacy`, CI, los 15 tests y qué secciones gobiernan cada issue |
| `docs/pantallas.md` | Tokens de color, tipografía, medidas, glifos, cada pantalla con sus estados, diálogos, tarjetas, widgets, notificación, icono y accesibilidad |
| `docs/textos.md` | Cada texto de la app en en, es, pt, de y fr, con su clave; `InfoPlist.strings`, `strings.xml`, Siri y las 60 preguntas de v1.1 |

## Tienda

| Ruta | Qué hay |
|---|---|
| `store/lanzamiento.md` | Checklist de las dos tiendas por fases, con los identificadores irreversibles y la issue de cada paso |
| `store/ci.md` | Secretos de GitHub, firma y cómo publican los cuatro workflows |
| `store/revenuecat.md` | Qué es Pro, el producto `pro_lifetime` en las dos tiendas, RevenueCat y la subida a 8,99 EUR con v1.1 |
| `store/formularios.md` | Seguridad de los datos, IARC, público, App Privacy, clasificación de Apple, notas para el revisor y el acceso a producción |
| `store/capturas.md` | Las seis escenas, titulares, tamaños, el diario de demostración y el gráfico de cabecera |
| `store/listings/<idioma>/` | Ficha de Play: `title.txt` (30), `short.txt` (80), `full.txt` (4000) |
| `store/app-store/<idioma>/` | Ficha de App Store: `name.txt` (30), `subtitle.txt` (30), `keywords.txt` (100), `promo.txt` (170), `description.txt` (4000) |
| `store/whatsnew/whatsnew-<idioma>` | Novedades de la versión, tope 500, las sube `release.yml` |
| `store/privacy/index.html` | La política de privacidad, inglés y español. Es el original: lo publicado en `BaltaJmn/line-privacy` es una copia |
| `store/privacy/README.md` | Dónde se publica, cómo se monta el DNS y sobre qué hechos del código está escrita |

Idiomas de las dos fichas: `en-US` (el de por defecto), `es-ES`, `pt-BR`, `de-DE`, `fr-FR`.

## Lo que llega con el código

Resumen de `docs/tecnico.md` 3, para saber dónde buscar cuando exista:

| Ruta | Qué habrá |
|---|---|
| `shared/src/commonMain/kotlin/com/baltajmn/line` | `App.kt`, `model/`, `data/`, `billing/`, `i18n/`, `ui/`, `ui/theme/`, `share/` |
| `shared/src/androidMain/.../line` | `data/` (almacén, fotos, bloqueo, recordatorio, widgets), `widget/` (Glance: `TodayWidget.kt`, `YearWidget.kt`), `i18n/`, `billing/`, `share/` |
| `shared/src/iosMain/.../line` | `MainViewController.kt`, `LineBridge.kt`, `data/`, `i18n/`, `billing/`, `share/` |
| `shared/src/commonTest`, `shared/src/androidHostTest` | `ModelTest.kt`, `DataTest.kt`, `StringsTest.kt`, `StorageTest.kt` |
| `androidApp/` | `MainActivity.kt`, manifiesto, `res/xml`, previsualizaciones de widgets, `strings.xml`, icono |
| `iosApp/iosApp`, `iosApp/LineWidget`, `iosApp/Configuration` | App, widgets de WidgetKit y `Config.xcconfig` |
| `tools/` | `play-listing/subir.py`, `store/capturas.py`, `store/cabecera.py`, `demo/generar.py`, `generate_icons.py`, `check-linestore.swift` |
| `.github/workflows/` | `tests.yml`, `release.yml`, `release-ios.yml`, `listings.yml` |

Los pasos de cuenta que son idénticos en las tres apps (Play Console, cuenta de Apple, Google Cloud,
RevenueCat) están contados largo en `../HabitTracker/store/`.
