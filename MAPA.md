# Mapa del repositorio

Inventario de todo lo que hay y dónde está. Los otros documentos de la raíz tienen otro trabajo:
[`CLAUDE.md`](CLAUDE.md) y [`AGENTS.md`](AGENTS.md) son las reglas de trabajo que se cargan solas en
cada sesión, y [`SPEC.md`](SPEC.md) explica el porqué de cada decisión de producto.

Hoy el repositorio tiene los documentos y el material de tienda. El código llega con la issue #4
(andamiaje) y las siguientes; su árbol completo, fichero a fichero y con el origen de cada uno, ya
está en [`docs/tecnico.md`](docs/tecnico.md) 3. Cada issue que añade ficheros los añade también aquí.

## Raíz

| Fichero | Qué es |
|---|---|
| `SPEC.md` | Spec de producto: benchmark, alcance por versión, diseño, retención, monetización, nombre, cumplimiento, plan y riesgos |
| `CLAUDE.md`, `AGENTS.md` | Contexto permanente y contratos que no se rompen |
| `MAPA.md` | Este fichero |
| `.gitignore` | Firma, claves, cuentas de servicio y la salida del diario de demostración fuera del repositorio |
| `keystore.properties`, `local.properties` | Locales, ignorados por git, nunca se suben |

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
| `shared/src/androidMain/.../line` | `data/` (almacén, fotos, bloqueo, recordatorio, widgets), `widget/` (Glance), `i18n/`, `billing/`, `share/` |
| `shared/src/iosMain/.../line` | `MainViewController.kt`, `LineBridge.kt`, `data/`, `i18n/`, `billing/`, `share/` |
| `shared/src/commonTest`, `shared/src/androidHostTest` | `ModelTest.kt`, `DataTest.kt`, `StorageTest.kt` |
| `androidApp/` | `MainActivity.kt`, manifiesto, `res/xml`, previsualizaciones de widgets, `strings.xml`, icono |
| `iosApp/iosApp`, `iosApp/LineWidget`, `iosApp/Configuration` | App, widgets de WidgetKit y `Config.xcconfig` |
| `tools/` | `play-listing/subir.py`, `store/capturas.py`, `store/cabecera.py`, `demo/generar.py`, `generate_icons.py` |
| `.github/workflows/` | `tests.yml`, `release.yml`, `release-ios.yml`, `listings.yml` |

Los pasos de cuenta que son idénticos en las tres apps (Play Console, cuenta de Apple, Google Cloud,
RevenueCat) están contados largo en `../HabitTracker/store/`.
