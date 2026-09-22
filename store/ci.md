# Publicar desde GitHub Actions

Los cuatro workflows de MoodTraker, copiados a `.github/workflows/` con los cambios de
`docs/tecnico.md` 9. La secuencia de Android vive en `BaltaJmn/ci`, igual que en las hermanas: aquí
solo está la llamada con el nombre de paquete, las tareas de Gradle y la carpeta de notas.

## Cómo se dispara

**Por etiqueta**, no en cada push a `main`. Cada subida quema un `versionCode` y le llega a los
probadores.

```bash
git tag v1.0 && git push origin v1.0
```

La misma etiqueta dispara `release.yml` (Play, canal `alpha`) y `release-ios.yml` (TestFlight).

Disparo manual para otro canal:

```bash
gh workflow run release.yml --ref main -f track=internal
```

**`internal` y `alpha` no son el mismo sitio.** La prueba interna se activa en minutos y es donde se
comprueba que lo que entrega Play funciona, pero los 14 días con 12 probadores solo corren en la
prueba **cerrada** (`alpha`). Un `versionCode` gastado en un canal no vale en otro.

**El `versionCode` lo sube quien etiqueta**, en `androidApp/build.gradle.kts`, antes de etiquetar. Si
se olvida, Play rechaza la subida con "Version code N has already been used".

## La firma

`release.yml` **no** lleva `signer-cn`. El workflow compartido comprueba que el certificado del AAB
contenga el texto de ese parámetro, y su valor por defecto es `CN=Baltasar` (comprobado en
`BaltaJmn/ci`, `android-play-release.yml`). El almacén de Purl se genera con ese mismo CN
(`lanzamiento.md`, fase 2), así que el valor por defecto acierta. MoodTraker lo pasa porque su
certificado dice `CN=MoodTraker`; si algún día se regenera el de Purl con otro CN, la línea hay que
añadirla.

Sin `keystore.properties` el build de release cae a la clave de debug en silencio. El workflow lo
detecta antes de subir: es la comprobación del CN.

## Notas de versión

Automáticas: el workflow lee `store/whatsnew/whatsnew-<idioma>`, un fichero por idioma con el nombre
que espera Play, tope 500 caracteres. Se reescriben en cada versión que cambie algo visible.

## La ficha de tienda

`listings.yml` sube los cinco idiomas de golpe con la API de Android Publisher, a mano desde
*Actions*. Textos en `store/listings/<idioma>/` con los topes de Play: título 30, corta 80, larga
4000. Un push que toque `store/listings/**` solo comprueba los topes.

```bash
python3 tools/play-listing/subir.py                        # comprueba los topes en local
gh workflow run listings.yml --ref main -f accion=estado   # lee en qué canal está cada versionCode
gh workflow run listings.yml --ref main -f accion=subir    # escribe la ficha
```

## Secretos que hay que crear

En el repositorio: *Settings > Secrets and variables > Actions > New repository secret*.

| Secreto | Qué es |
|---|---|
| `KEYSTORE_BASE64` | El `.jks` de subida, en base64 |
| `KEYSTORE_PASSWORD` | La del almacén |
| `KEY_ALIAS` | `upload` |
| `KEY_PASSWORD` | La de la clave |
| `PLAY_SERVICE_ACCOUNT_JSON` | El JSON de la cuenta de servicio **de publicar**, nunca el de RevenueCat |

```bash
base64 -i ~/keys/purl-upload.jks | pbcopy
```

Se pega en el formulario de GitHub. Ni en un fichero del repositorio ni en un chat: el repositorio es
público y todo lo que entra en su historia se queda.

### La cuenta de servicio para publicar

**La que ya publica Quilt y MoodTraker sirve**, y es lo que se hace: mismo poder sobre la misma
consola, ya vive en Google Cloud con las APIs activadas. Dos pasos:

1. Play Console, *Usuarios y permisos*: darle sobre Purl **publicar en canales de prueba** y
   **gestionar la presencia en la tienda** (lo que necesita `listings.yml`). Si sus permisos son de
   cuenta y no de app, ya los tiene.
2. Google Cloud, pantalla *Claves* de esa cuenta: una clave JSON nueva, porque la original solo se
   descarga una vez. El secreto de GitHub es por repositorio, así que hay que pegarlo en `line`
   igualmente.

**La de RevenueCat no se reutiliza nunca para esto, ni al revés.** Se creó sin poder publicar a
propósito: si se filtra su JSON, la diferencia es entre que te lean los pedidos y que te suban un
binario. Son dos cuentas y siguen siendo dos.

## Los otros dos workflows

`tests.yml` corre en cada push a `main` y en cada pull request: `:shared:testAndroidHostTest` en
Ubuntu y `:shared:iosSimulatorArm64Test` en `macos-26`, que es lo único que demuestra que `iosMain`
compila y enlaza. `macos-26` y no `macos-15`: Compose enlaza clases de UIKit que solo existen desde el
SDK de iOS 26.

`release-ios.yml` archiva `Purl.xcarchive` con el esquema `iosApp`, exporta y sube a TestFlight en un
solo `xcodebuild` (`destination: upload` en el `ExportOptions.plist`). El número de build lo pone el
workflow desde `github.run_number`. Mientras no exista `APPSTORE_KEY_ID`, el trabajo se salta solo y
deja un aviso en vez de salir rojo.

### Secretos de Apple

| Secreto | Qué es |
|---|---|
| `APPSTORE_KEY_ID` | El Key ID de la clave de la App Store Connect API |
| `APPSTORE_ISSUER_ID` | El Issuer ID, el mismo para todas las claves de la cuenta |
| `APPSTORE_PRIVATE_KEY` | El contenido del `.p8`, entero, con sus líneas `BEGIN`/`END` |
| `APPLE_TEAM_ID` | El Team ID de la cuenta de desarrollador |

La clave `.p8` se descarga **una sola vez**. Rol *App Manager* o superior, para que
`-allowProvisioningUpdates` cree el certificado y los perfiles por su cuenta, incluidos los del widget
y el App Group, sin meter un `.p12` en un secreto. Si ya existe una para las hermanas, sirve la misma:
es de cuenta, no de app.
