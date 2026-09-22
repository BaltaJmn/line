# Purl

Diario de una línea al día que se relee el mismo día de otros años. Android + iOS, Compose
Multiplatform sobre Kotlin Multiplatform. Nombre de producto **Purl**. Identificador en las dos
tiendas: `com.baltajmn.line`. Repositorio `BaltaJmn/line`, **público**.

Este fichero lo carga Codex solo en cualquier sesión abierta sobre este repositorio, desde
cualquier cuenta. Es el contexto permanente del proyecto: si algo hay que saber siempre, va aquí,
no en el chat.

Hermana de Quilt (`../HabitTracker`, `com.baltajmn.habit`) y de MoodTraker (`../MoodTraker`,
`com.baltajmn.mood`): misma arquitectura, misma paleta, misma promesa. Casi todos los ficheros de
plataforma salen de MoodTraker; `docs/tecnico.md` 3 dice de cuál sale cada uno.

## Antes de escribir código

**No queda nada por decidir.** Todo está escrito en cuatro documentos, y el código los sigue:

| Documento | Qué manda |
|---|---|
| `SPEC.md` | El porqué de cada decisión de producto |
| `docs/tecnico.md` | El contrato: identificadores, versiones, árbol de ficheros, formatos, algoritmos, configuración de plataforma, tests y qué secciones gobiernan cada issue (sección 11) |
| `docs/pantallas.md` | La interfaz: tokens, medidas, cada pantalla, estados, widgets, tarjetas, icono |
| `docs/textos.md` | Todos los textos, en los cinco idiomas |

Se trabaja por issues de GitHub, en el orden de `SPEC.md` 10. Cada issue enlaza sus secciones.

Si el código necesita algo que los documentos no dicen, **se decide, se escribe en el documento que
toca en el mismo commit, y se sigue**. Un documento que se queda atrás del código deja de servir el
día que se abre la siguiente sesión.

## Dónde vive cada cosa

| Ruta | Qué es |
|---|---|
| `shared/src/commonMain` | Toda la interfaz y toda la lógica. Es donde se trabaja por defecto |
| `shared/src/androidMain`, `shared/src/iosMain` | Solo los `actual` que el sistema obliga: almacenamiento, fotos, bloqueo, recordatorio, compartir, widgets, compras |
| `androidApp` | `MainActivity` (`FragmentActivity`), manifiesto, recursos, icono. Nada de lógica |
| `iosApp/iosApp` | `iOSApp.swift`, `Info.plist`, `PrivacyInfo.xcprivacy`, `<lang>.lproj`. Grupo sincronizado |
| `iosApp/LineWidget` | Widgets de WidgetKit. Leen `widget.json` y nada más |
| `iosApp/Configuration/Config.xcconfig` | Versión, identificador y Team ID de iOS. No se editan en el `.pbxproj` |
| `docs/` | Contrato técnico, interfaz y textos |
| `store/` | Fichas, novedades, formularios, política, compras, CI, capturas y el checklist de lanzamiento |
| `tools/` | Scripts de ficha, capturas, diario de demostración e icono |
| `.github/workflows` | Tests en cada push, publicación por etiqueta |

Inventario completo: `MAPA.md`.

## Contratos que no se rompen

- **El diario no sale del teléfono.** Ni analítica, ni informes de fallos, ni servidor. Lo único que
  sale es lo de RevenueCat. Cualquier cambio a eso toca en el mismo commit `store/privacy/index.html`,
  `store/formularios.md` y `PrivacyInfo.xcprivacy`.
- `entries.json` vive en `filesDir` (Android) y `Application Support` (iOS), **nunca** en el App Group.
  Se escribe de forma atómica, con `entries.bak.json`, y un fichero ilegible se pone en cuarentena, no
  se sobrescribe (`docs/tecnico.md` 6.12).
- `widget.json` es el único contrato con los widgets: en v1.0 lleva estructura, **nunca texto del
  diario**. Un campo nuevo se añade en `WidgetState` y en `LineStore.swift` en el mismo cambio.
- El día lógico acaba a las **03:00** locales. El tope de **280** se cuenta en puntos de código y vive
  en la interfaz, no en el almacén.
- **Importar nunca borra**: fusiona, y ningún texto del dispositivo se pierde (`docs/tecnico.md` 6.6).
- Lo que nunca se cobra: exportar e importar, compartir, escribir y leer todos los años, el
  recordatorio y el bloqueo. Cambiar qué es Pro toca en el mismo commit los textos `pro*`, las dos
  fichas y `store/revenuecat.md`.
- `Strings.kt` obliga a los cinco idiomas (en, es, pt, de, fr) por firma de función, y se copia de
  `docs/textos.md`. Los textos **no** salen de Compose Resources: parte se pinta fuera de un
  `@Composable` (receptor, Glance, `Canvas` de las tarjetas, notificación).
- **El `versionCode` no se reutiliza nunca**, ni entre canales de Play.
- La descripción larga de Play **conserva los saltos de línea tal cual**: cada párrafo de
  `store/listings/` va en una sola línea.

## Superficies del sistema

Todo lo que se ve fuera de la app lee y escribe por `LineRepository`, nunca por su cuenta.

| Superficie | Dónde | Nota |
|---|---|---|
| Widget de hoy | `widget/TodayWidget.kt` (Glance), `LineWidget.swift` | Gratis. Estado de hoy y si hay recuerdo, sin texto |
| Widget del año | `widget/YearWidget.kt`, `LineYearWidget.swift` | Pro. Se pinta bloqueado sin Pro |
| Widget de pantalla de bloqueo | `LineWidget.swift` | Pro. Solo iOS |
| Recordatorio | `Reminder.android.kt` + receptores, `Reminder.ios.kt` | Android se calla si hoy está escrito; iOS programa 60 avisos sueltos (`docs/tecnico.md` 6.10) |

Reglas que cuestan una tarde si se olvidan:

- La vista crema que tapa la multitarea en iOS se pone desde `iOSApp.swift`, no desde Compose, que no
  llega a repintar antes de la foto del sistema.
- `purchases-kmp` 3.2.1 necesita el `-L` al toolchain de Swift en los binarios de test de iOS
  (`shared/build.gradle.kts`, copiado de MoodTraker).
- Glance no tiene borde ni lienzo: la rejilla del widget del año es un `Bitmap`.
- La capa `<monochrome>` del icono adaptativo es una máscara de un solo color.
- Los textos de `AppIntents` (v1.1) tienen que ser literales; las traducciones, en
  `<lang>.lproj/Localizable.strings`.

## Seguridad, sin excepciones

El repositorio es público: todo lo que entra en su historia se queda.

- El `.jks` de firma, `keystore.properties` y `local.properties` nunca se suben. Están en
  `.gitignore`.
- Los secretos viven solo en GitHub repository secrets.
- La clave secreta de RevenueCat (`sk_...`) nunca entra en el repositorio. Solo las públicas
  (`goog_`, `appl_`), que ya viajan dentro del binario.
- La cuenta de servicio que publica en Play es distinta de la de RevenueCat, que es de solo lectura a
  propósito. No se juntan ni se usan una para el trabajo de la otra.

## Cómo se marcan los cambios entre las dos cuentas

El registro es `git log`, no un fichero paralelo que se desincroniza al segundo día.

Cada commit hecho con Codex lleva dos trailers:

```
Co-Authored-By: Codex <noreply@openai.com>
Codex-Session: <enlace de la sesión>
```

El cuerpo del commit explica **por qué**, no qué. El diff ya dice qué.

## Estilo

- Documentación y commits en español, salvo el material de tienda en otros idiomas.
- Sin em dash y sin emoji en nada que escriba Codex. En texto, en comentarios y en commits. En los
  tests, un emoji se escribe con su escape (`\uD83D\uDE42`).
- Comentarios: solo los que explican una decisión que el código no puede explicar solo.
- Código, comentarios y nombres en inglés, como en las hermanas.

## Comandos

```bash
./gradlew :shared:testAndroidHostTest          # tests comunes sobre JVM, el rápido
./gradlew :shared:iosSimulatorArm64Test        # tests comunes sobre Kotlin/Native
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:bundleRelease            # necesita keystore.properties
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -destination 'generic/platform=iOS Simulator' build CODE_SIGNING_ALLOWED=NO
```

Iterar con el objetivo mínimo del módulo tocado; el build de todo, una vez al final.

Publicar: `git tag v1.0 && git push origin v1.0` dispara Play (`alpha`) y TestFlight. El de iOS se
salta solo mientras no existan los secretos de Apple (`store/ci.md`).

Ficha de tienda:

```bash
python3 tools/play-listing/subir.py                        # comprueba los topes, no toca Play
gh workflow run listings.yml --ref main -f accion=estado   # lee en qué canal está cada versión
gh workflow run listings.yml --ref main -f accion=subir    # escribe la ficha en Play
```
