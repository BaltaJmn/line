# Lanzamiento, paso a paso

Checklist de Purl. **[tú]** es lo que solo puedes hacer tú (cuentas, contraseñas, formularios,
subidas por la Console) y **[yo]** lo que queda hecho desde el repositorio. Cada paso lleva la issue
que lo cierra.

Regla de la familia: **el código no marca la fecha de salida, la marcan los trámites.** La prueba
cerrada de 14 días y la verificación de Apple son las dos latencias largas; todo lo que es código
cabe dentro de ellas. Por eso la fase 1 va el primer día, no el último.

Pasos de cuenta que ya se hicieron para Quilt y no se repiten (perfil de pagos, papeleo fiscal de
Play, cuenta de servicio de RevenueCat): contados largo en
`../../HabitTracker/store/{lanzamiento,revenuecat}.md`. Aquí solo va lo que cambia de una app a otra.

---

## Identificadores, que son irreversibles

| Qué | Valor |
|---|---|
| `applicationId` de Android | `com.baltajmn.line` |
| Bundle id de iOS | `com.baltajmn.line` (`APP_BUNDLE_ID` en `iosApp/Configuration/Config.xcconfig`) |
| Bundle id del widget | `com.baltajmn.line.widget` |
| App Group | `group.com.baltajmn.line` |
| Producto de compra | `pro_lifetime`, no consumible, en las dos tiendas |
| Derecho de RevenueCat | `pro` |
| Nombre visible | Purl |
| Título de ficha | `Purl: 5-Year Line Diary` (y su traducción por idioma, `listings/`) |

En cuanto la primera build entra en cualquiera de las dos tiendas, los cinco primeros no cambian
nunca. Un identificador de producto borrado tampoco se reutiliza. El nombre visible sí se puede
cambiar después, pero cambiarlo con reseñas tira la búsqueda por nombre.

## Fase 0. Decisiones

Todas tomadas. El porqué de cada una, en `SPEC.md`.

- [x] Nombre: **Purl** (SPEC §7). Reserva si App Store Connect lo rechaza al crear la app:
      **Daythread**. Cambiarlo es buscar `Purl` en `docs/textos.md` (`app_name`,
      `CFBundleDisplayName` y los textos que lo nombran), `PRODUCT_NAME` del xcconfig,
      `EXPORT_PREFIX`, los ficheros de `listings/` y `app-store/` y la política. Los identificadores
      de la tabla no cambian.
- [x] Precio: **5,99 EUR** en v1.0, **8,99 EUR** desde v1.1, sin descuento de lanzamiento, precios
      regionales activados (SPEC §6, `revenuecat.md`).
- [x] Qué es Pro en v1.0: foto a partir de la cuarta entrada con foto, siete portadas, widget del
      año y widget de pantalla de bloqueo de iOS (SPEC §6).
- [x] iPad: sí, universal como las hermanas (SPEC §8). Obliga a capturas de iPad (`capturas.md`).
- [x] Política de privacidad en `https://line.baltajmn.dev/` (`privacy/README.md`).

## Fase 1. El primer día: lo que tiene latencia

Arrancar todo esto antes de escribir una línea de código. Ninguno depende del código.

- [ ] **[tú] Crear la app en Play Console** con el nombre Purl, idioma por defecto `en-US`, app
      gratuita. El paquete se fija con el primer AAB, no aquí. (#3)
- [ ] **[tú] Abrir o comprobar la cuenta de Apple Developer**, 99 USD al año. La verificación tarda
      días y no se acelera. Contratos, fiscalidad y datos bancarios en App Store Connect, y el
      **Small Business Program** pedido el mismo día: si llega tarde, las primeras ventas se cobran
      al 30 %. (#3)
- [ ] **[tú] Crear la app en App Store Connect** en cuanto exista la cuenta, con el bundle id de la
      tabla (hay que registrarlo antes en *Certificates, Identifiers & Profiles*, con App Groups
      activado, y lo mismo para `com.baltajmn.line.widget`). **Es la comprobación de que el nombre
      Purl está libre**: si App Store Connect dice que el nombre ya está en uso, se aplica la reserva
      de la fase 0 ese mismo día. (#1)
- [ ] **[tú] Declarar la condición de comerciante (DSA)** en las dos consolas si no se hizo ya con
      Quilt. Es de cuenta, no de app: si ya está, se hereda. Apple retira de la UE las apps sin ella.
      (#27)
- [ ] **[tú] Registro DNS de la política**: en **Cloudflare**, que sirve la zona `baltajmn.dev`
      aunque el dominio se registre en Porkbun, un `CNAME` con host `line` y destino
      `baltajmn.github.io`. Pasos completos en `privacy/README.md`. (#27)
- [ ] **[tú] Reclutar 16 probadores**, no 12. Empieza por los de Quilt y MoodTraker: ya dijeron que
      sí una vez. El requisito de Play es **por app** (comprobado en la ayuda de Play el 22/09/2026:
      "must run a closed test for their app"), así que Purl hace su propia prueba cerrada aunque
      Quilt ya tenga acceso a producción. Cómo reclutar y qué no hacer:
      `../../HabitTracker/store/testers.md`. (#29)

## Fase 2. Infraestructura

- [x] Repositorio `BaltaJmn/line`, público. Los minutos de Actions no se facturan.
- [ ] **[yo] Andamiaje y CI**: los cuatro workflows de MoodTraker con los cambios de
      `docs/tecnico.md` 9. (#4)
- [ ] **[tú] Crear el almacén de subida.** Un comando, y el `CN=Baltasar` no es opcional: es lo que
      comprueba el workflow compartido (`ci.md`).

      ```bash
      keytool -genkeypair -v -keystore ~/keys/purl-upload.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Baltasar, O=BaltaJmn, C=ES"
      ```

      Después, `keystore.properties` en la raíz del repositorio (git-ignorado) con `storeFile`,
      `storePassword`, `keyAlias=upload` y `keyPassword`. Apunta aquí la huella SHA-256 que
      imprime `keytool -list -v -keystore ~/keys/purl-upload.jks -J-Duser.language=en
      -J-Duser.country=US`, para contrastarla con la que enseñe Play al subir el primer AAB. Copia
      del `.jks` fuera de este Mac. (#3)
- [ ] **[tú] Los cinco secretos de firma y publicación** en GitHub (`ci.md`). (#3)
- [ ] **[tú] Publicar la política**: repositorio público `BaltaJmn/line-privacy` con
      `privacy/index.html` y GitHub Pages (`privacy/README.md`). (#27)

## Fase 3. Play

**El orden importa.** La API de Android Publisher no responde hasta que la app tiene un binario
subido a mano, así que `release.yml` y `listings.yml` fallan si se ejecutan antes.

1. **[tú] Contenido de la aplicación** en la Console: política, seguridad de los datos,
   clasificación, público objetivo, declaraciones. Respuestas una a una en `formularios.md`. (#27)
2. **[tú] La primera subida, a mano.** `./gradlew :androidApp:bundleRelease` y subir
   `androidApp/build/outputs/bundle/release/androidApp-release.aab` en *Probar y publicar > Pruebas
   internas > Crear versión*. Comprobar que la huella del certificado de subida coincide con la
   apuntada en la fase 2. (#29)
3. **[yo] Subir el `versionCode` a 2 y commitearlo.** El 1 queda gastado en la prueba interna y Play
   no lo acepta en ningún otro canal.
4. **[yo] Ficha**, ya con la API viva: `gh workflow run listings.yml --ref main -f accion=subir`.
   Textos en `listings/`, con los topes comprobados. (#28)
5. **[tú] Imágenes**: icono de 512, gráfico de 1024x500 y capturas (`capturas.md`). La API de
   listings solo escribe texto. (#28)
6. **[tú] Producto `pro_lifetime`** a 5,99 EUR (`revenuecat.md` §1). (#23)
7. **[tú] Abrir la prueba cerrada** (canal `alpha`) con la lista de probadores como Grupo de Google, y
   **[yo]** etiquetar: `git tag v1.0 && git push origin v1.0`, que publica en `alpha`. (#29)

## Fase 4. RevenueCat

Paso a paso en `revenuecat.md`. Resumen: proyecto Purl, las dos tiendas, derecho `pro`, oferta
`default` como *Current* con un paquete *Lifetime*, y las dos claves públicas pegadas en
`Billing.android.kt` y `Billing.ios.kt`. Mientras sean `null`, la app funciona entera en modo gratis
y no revienta. (#23)

**[tú] Probar una compra real** en un móvil con la app instalada desde la prueba interna y la cuenta
en *Licencia para testing*, y *Restaurar compra* tras desinstalar. En el emulador no se puede: no
trae Play Billing.

## Fase 5. La prueba cerrada, el camino crítico

- [ ] **[tú] Sostener 12 aceptaciones durante 14 días seguidos.** El contador arranca cuando la
      versión está aprobada **y** hay 12 aceptaciones a la vez, y vuelve a cero si un solo día baja
      de 12. La Console no enseña contador: el requisito se tacha solo al cumplirse. Subir versiones
      nuevas al canal durante la ventana es normal y no reinicia nada. (#29)
- [ ] **[tú] Pedir a los probadores lo que Google mira**: que escriban de verdad, a diario. Un
      diario de una línea es justo el uso diario que la revisión quiere ver.
- [ ] **[yo] Apuntar lo que salga de la prueba** (fallos, arreglos, mensajes) con el commit que lo
      prueba: es la materia del formulario de acceso a producción (`formularios.md` §7).
- [ ] **[tú] Solicitar acceso a producción** al terminar. Hasta 7 días de revisión. (#30)

## Fase 6. Apple

- [ ] **[tú] Clave de la App Store Connect API** (rol *App Manager*) y los cuatro secretos de Apple
      (`ci.md`). Con ellos, la misma etiqueta `v*` archiva y sube a TestFlight. (#3)
- [ ] **[tú] `TEAM_ID`** en `Config.xcconfig`. **[yo]** lo commiteo: no es secreto.
- [ ] **[tú] Producto `pro_lifetime`** en App Store Connect (`revenuecat.md` §6) con su captura de
      revisión. (#23)
- [ ] **[tú] App Privacy, clasificación por edad, cumplimiento de exportación** y notas para el
      revisor: `formularios.md`. (#27)
- [ ] **[tú] Ficha**: los cinco ficheros por idioma de `app-store/`, capturas de iPhone de 6,9" y de
      iPad de 13" (`capturas.md`), URL de la política y de soporte. (#28)
- [ ] **[yo] Informe de privacidad de Xcode** sobre el primer archivo (*Product > Archive > Generate
      Privacy Report*), contrastado con `PrivacyInfo.xcprivacy`. Lo que salga de más se añade al
      manifiesto y a `formularios.md` en el mismo commit. (#27)
- [ ] **[tú] Probar la compra en el sandbox** de Apple y *Restaurar compra*, que el revisor mira.
- [ ] **[tú] Enviar a revisión** con la compra adjunta a la versión: la primera compra de una app
      solo se revisa junto a una versión.

## Fase 7. Salida (#30)

- [ ] **[yo] Rellenar `SIBLINGS`** en `data/AppInfo.kt` (`docs/tecnico.md` 6.16) con la URL de tienda
      de Quilt y de MoodTraker **solo donde estén en producción ese día**. Comprobarlo abriendo la URL
      pública sin sesión iniciada:
      `https://play.google.com/store/apps/details?id=com.baltajmn.habit`,
      `https://play.google.com/store/apps/details?id=com.baltajmn.mood`, y la búsqueda de cada nombre
      en App Store. Lo que no cargue, `null`.
- [ ] **[yo] Versión final**: `versionCode` siguiente, `versionName` `1.0`, notas en `whatsnew/`.
- [ ] **[tú] Producción en Play** con despliegue al 100 % (una app nueva no tiene usuarios que
      proteger con un despliegue escalonado) y **publicación manual en App Store** (*Manually release
      this version*), para que las dos salgan el mismo día.
- [ ] **[tú] Comprobar tras publicar**: la ficha carga sin sesión en las dos tiendas, la política
      abre desde Ajustes, una compra real con tarjeta propia se reembolsa en la Console.

## Después de la salida

- **v1.1 sube el precio a 8,99 EUR** el día que sale el libro en PDF, no antes (`revenuecat.md` §7).
  Las notas de versión y la descripción larga dicen entonces "libro en PDF" en la lista de Pro, en el
  mismo commit.
- **Actualizar la política y los formularios** en el mismo commit que añada cualquier cosa que salga
  del dispositivo o cualquier permiso nuevo. La v1.1 no lo hace: el widget del recuerdo y Siri se
  quedan en el teléfono.

---

## Qué issue cierra cada fase

| Issue | Dónde |
|---|---|
| #1 Nombre | Fase 0 y fase 1 (App Store Connect confirma el nombre) |
| #2 Precio | Fase 0: decidido, se cierra |
| #3 Altas y cuentas | Fases 1, 2 y 6 |
| #23 Compras | Fase 4, `revenuecat.md` |
| #27 Privacidad y formularios | Fases 1, 3 y 6, `formularios.md`, `privacy/` |
| #28 Ficha | Fases 3 y 6, `listings/`, `app-store/`, `capturas.md` |
| #29 Prueba cerrada | Fases 1, 3 y 5 |
| #30 Salida | Fases 5 y 7 |
