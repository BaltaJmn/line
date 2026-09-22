# Purl

Un diario de una línea al día para Android e iOS. Escribes una línea sobre hoy y debajo lees lo que
escribiste en esta misma fecha otros años. Sin cuenta y sin servidor: el diario no sale del
teléfono.

Compose Multiplatform sobre Kotlin Multiplatform, hermana de Quilt y MoodTraker: una sola interfaz
para las dos plataformas, con `expect`/`actual` solo donde el sistema obliga.

## Compilar

```bash
./gradlew :shared:testAndroidHostTest     # tests comunes sobre JVM
./gradlew :shared:iosSimulatorArm64Test   # tests comunes sobre Kotlin/Native
./gradlew :androidApp:assembleDebug
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -destination 'generic/platform=iOS Simulator' build CODE_SIGNING_ALLOWED=NO
```

## Documentos

- `SPEC.md`: qué hace la app y por qué.
- `docs/tecnico.md`, `docs/pantallas.md` y `docs/textos.md`: el contrato que sigue el código.
- `store/`: fichas, política de privacidad, formularios y el checklist de lanzamiento.
- `MAPA.md`: dónde vive cada cosa.
