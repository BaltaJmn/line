# Formularios de las dos tiendas, respuesta a respuesta

Todo lo que las consolas preguntan y no tiene API, con la respuesta cerrada y el hecho del código que
la sostiene. Se pegan a mano. Si el código cambia algo de lo que aquí se afirma (un permiso, un SDK,
un dato que sale del teléfono), se cambian en el mismo commit este fichero, `privacy/index.html` y
`iosApp/iosApp/PrivacyInfo.xcprivacy`.

Hechos de partida, todos de `docs/tecnico.md`:

- El diario (texto, fechas, fotos, ajustes) vive en `filesDir` en Android y en `Application Support`
  en iOS. No hay servidor, ni cuenta, ni analítica, ni publicidad, ni informes de fallos.
- Lo único que sale del teléfono es lo de **RevenueCat**: un identificador anónimo de instalación
  (se configura sin `appUserID`), el historial de compras y datos técnicos del dispositivo.
- Los widgets leen `widget.json`, que en v1.0 lleva solo la estructura (qué días hay escritos, si hoy
  lo está, la portada), nunca el texto. Desde v1.1 lleva además la línea de hace un año, solo mientras
  el widget del recuerdo está colocado, con Pro y sin bloqueo (`docs/tecnico.md` 12.3). Sigue siendo
  un fichero del dispositivo (App Group en iOS, `filesDir` en Android): no cambia ninguna respuesta de
  estos formularios, porque nada sale del teléfono.
- Siri (v1.1, iOS): la voz la procesa Apple; la app recibe texto y lo escribe en local. No añade
  ningún dato recogido.
- La notificación puede llevar un trozo de 120 puntos de código de una entrada de otro año, generado
  en el teléfono. Con el bloqueo encendido no lleva cuerpo.

---

## 1. Play: seguridad de los datos

*Política > Contenido de la aplicación > Seguridad de los datos.*

| Pregunta | Respuesta |
|---|---|
| ¿Tu app recoge o comparte alguno de los tipos de datos obligatorios? | Sí |
| ¿Se cifran en tránsito todos los datos recogidos? | Sí (HTTPS del SDK de RevenueCat) |
| ¿Qué métodos de creación de cuenta admite? | *Mi aplicación no permite que los usuarios creen una cuenta*. Por eso no hace falta URL de borrado de cuenta |
| ¿Se puede iniciar sesión con cuentas creadas fuera de la app? | No |
| ¿Ofreces una forma de pedir que se borren los datos? | Sí. URL de eliminación: `https://line.baltajmn.dev/`, que nombra Purl y explica los pasos: borrar una entrada, desinstalar, y para lo de RevenueCat, un correo con el número de pedido |

Tipos de datos, los únicos dos que se marcan:

| Tipo | Recogido | Compartido | Efímero | Obligatorio | Finalidad |
|---|---|---|---|---|---|
| Información financiera > Historial de compras | Sí | No | No | Sí | Funcionalidad de la app |
| IDs de dispositivo o de otro tipo | Sí | No | No | Sí | Funcionalidad de la app |

RevenueCat recibe los datos como encargado del tratamiento, así que no cuenta como "compartido".
Play no deja enviar este cuestionario hasta que *Público objetivo* (§3) está hecho: mientras, se
guarda en borrador con todo lo de arriba.

Lo que **no** se marca, y por qué:

| Tipo | Por qué no |
|---|---|
| Fotos | La foto elegida se copia, reducida, al almacenamiento privado de la app y no sale de ahí |
| Otro contenido generado por el usuario (el texto del diario) | No sale del dispositivo |
| Mensajes, contactos, ubicación, salud, actividad | La app no los toca |
| Registros de fallos, diagnóstico | No hay SDK que los mande |

**La copia automática del sistema.** Android puede subir `entries.json` al Drive del usuario con su
copia de seguridad. La configuración (`docs/tecnico.md` 8.2) solo lo permite **cifrado de extremo a
extremo** (`disableIfNoEncryptionCapabilities="true"` y `clientSideEncryption`), y la definición de
Google excluye de "recogido" lo que va cifrado de extremo a extremo y solo pueden leer emisor y
receptor ([Seguridad de los datos, ayuda de Play Console](https://support.google.com/googleplay/android-developer/answer/10787469)).
Es una **inferencia**: la ayuda no nombra la copia del sistema. Si Google publica algo concreto, se
revisa aquí.

## 2. Play: clasificación de contenido (IARC)

| Pregunta | Respuesta |
|---|---|
| Categoría | Utilidad, productividad, comunicación u otras |
| Violencia, sexo, lenguaje soez, drogas, apuestas, miedo | No a todo |
| ¿Los usuarios pueden interactuar o intercambiar contenido? | No. La tarjeta se comparte con la hoja del sistema, fuera de la app |
| ¿Comparte la ubicación del usuario? | No |
| ¿Permite comprar bienes digitales? | Sí |
| ¿Contiene anuncios? | No |
| ¿Acceso sin restricciones a internet? | No |

Resultado esperado: PEGI 3, ESRB Everyone, USK 0, el más bajo de cada sistema.

## 3. Play: público objetivo y declaraciones

| Campo | Valor |
|---|---|
| Grupos de edad | 13-15, 16-17, 18 y más |
| ¿Atrae a menores de 13? | No |
| Anuncios | No contiene anuncios |
| Datos de inicio de sesión (antes "Acceso a la app") | **Sí**, aunque no haya cuentas: la redacción de 2026 cuenta como restringido cualquier pago, y el revisor no compra con cuentas personales ni usa pruebas gratis. Se le deja un código promocional de `pro_lifetime` con las instrucciones de abajo. Play no deja empezar *Público objetivo* sin esta sección |
| App de noticias | No |
| Salud | No tiene funciones de salud. Es un diario personal |
| Funciones financieras | No |
| App de gobierno | No |

13+ deja la app fuera del programa Familias, que trae requisitos que no aplican.

*Datos de inicio de sesión > Añade detalles*: nombre `Purl Pro for review`, usuario y contraseña
vacíos, y en *Cualquier otra información* (tope 500, en inglés), con el código de la promoción en
lugar de `CODE`:

```
Purl has no account and no login: everything stays on the device.

Paid content: Purl Pro, a one-time purchase (pro_lifetime). Redeem this promo code in the Play Store app (profile icon > Payments & subscriptions > Redeem code): CODE. Then open Purl > Settings > Restore purchase, or Settings > Purl Pro.

The lock (Settings > Privacy) is optional, off by default, and uses the device's own screen lock or biometrics.
```

La casilla de acceso completo, contenido de pago incluido, solo se marca con el código puesto. Los
códigos salen de una promoción de `pro_lifetime` de seis meses (*Monetizar con Play > Códigos
promocionales*); el CSV se guarda en `~/keys/`, fuera del repositorio, y al caducar se crea otra y
se cambia el código aquí. Orden que impone Play: esta sección, luego *Público objetivo*, y solo
entonces se puede enviar *Seguridad de los datos*.

Permisos del manifiesto fusionado, los mismos que Quilt más `USE_BIOMETRIC`: ninguno pide
declaración. No se usa `SCHEDULE_EXACT_ALARM` (el recordatorio va con `setAndAllowWhileIdle`) ni
`READ_MEDIA_IMAGES` (el selector de fotos del sistema no pide permiso). `FOREGROUND_SERVICE` y
`WAKE_LOCK` los trae WorkManager a través de Glance, igual que en Quilt, que pasó la revisión sin
declaración. Comprobar la lista sobre el AAB:

```bash
grep -oE '<uses-permission[^>]*android:name="[^"]*"' \
  androidApp/build/intermediates/merged_manifest/release/*/AndroidManifest.xml | sort -u
```

`com.android.vending.BILLING` está en el binario desde la primera subida, así que "¿Tiene compras en
la aplicación?" es **sí** desde la primera subida aunque las claves sean `null`.

## 4. Play: ficha, categoría y contacto

| Campo | Valor |
|---|---|
| Tipo | Aplicación |
| Categoría | **Estilo de vida** |
| Etiquetas | *Bloc de notas*, *Estilo de vida* y *Productividad*. La lista cerrada de Play no tiene diario; *Autoayuda* se descarta por lo mismo que la categoría de salud |
| Correo de contacto | `baltajmn@gmail.com`, el mismo de la política |
| Sitio web | `https://line.baltajmn.dev/` |
| Teléfono | Vacío |
| Política de privacidad | `https://line.baltajmn.dev/` |

Estilo de vida y no Salud y bienestar: la ficha no hace ni una promesa de salud mental (SPEC §7), y la
categoría de salud trae la declaración de salud y más escrutinio a cambio de nada.

## 5. App Store: privacidad de la app

*App Store Connect > Purl > Privacidad de la app.*

| Pregunta | Respuesta |
|---|---|
| ¿Recoges datos de esta app? | Sí |
| Compras > Historial de compras | Recogido. Finalidad: funcionalidad de la app. **No** vinculado a la identidad. **No** usado para rastreo |
| Identificadores > ID de usuario | Recogido. Funcionalidad de la app. No vinculado. No rastreo |
| El resto de tipos | No recogidos |

Es exactamente lo que dice `PrivacyInfo.xcprivacy` (`docs/tecnico.md` 8.3). Si el informe de
privacidad de Xcode sobre el primer archivo añade algo, se añade en los dos sitios.

URL de la política: `https://line.baltajmn.dev/`.

## 6. App Store: el resto de la ficha

| Campo | Valor |
|---|---|
| Categoría principal | **Estilo de vida** |
| Categoría secundaria | Productividad |
| Clasificación por edad | Cuestionario de 2025: **ninguno** en todos los contenidos; **no** en contenido generado por usuarios, mensajería, publicidad, acceso web sin restricciones, temas médicos o de bienestar, concursos y apuestas. Resultado esperado **4+** |
| Derechos de contenido | No contiene ni accede a contenido de terceros |
| Cumplimiento de exportación | No pregunta: `ITSAppUsesNonExemptEncryption = false` en el `Info.plist`. La única criptografía es el HTTPS del sistema |
| Copyright | `2026 Baltasar Jiménez` |
| URL de soporte | `https://line.baltajmn.dev/` (lleva el correo de contacto) |
| URL de marketing | Vacía |
| Inicio de sesión para la revisión | No hace falta: la app no tiene cuentas |
| Publicación | Manual, para salir el mismo día que Play |

El usuario escribe lo que quiere, pero nadie más lo ve ni la app lo publica: no es contenido generado
por usuarios a efectos de moderación, y por eso la respuesta es no.

Notas para el revisor, en inglés:

```
Purl has no account and no server. Everything is stored on the device, so no demo account is needed.

To see the main feature (reading what you wrote on this date in past years), write a line today; past years appear once they exist. The year grid and search are behind the grid icon at the top.

The optional lock (Settings) uses Face ID or the device passcode through LocalAuthentication. It is off by default.

Purl Pro is a one-time non-consumable purchase (pro_lifetime). It opens from Settings > Purl Pro, from choosing a non-default cover, or from adding a photo to a fourth entry. Restore Purchase is in Settings and in the purchase dialog.
```

Datos de contacto de la revisión: nombre, teléfono y correo, **a mano**.

## 7. Play: solicitud de acceso a producción

Se envía al terminar los 14 días de la prueba cerrada. Ocho respuestas libres de 300 caracteres y dos
desplegables; si Google la rechaza hay que rellenarla entera otra vez, por eso vive aquí. En inglés:
el revisor no tiene garantizado el español. Google contrasta cada afirmación con las estadísticas de
la prueba, así que **todo lo que dependa de la prueba se escribe al final con datos reales**, con el
commit o el mensaje que lo prueba, como hizo Quilt en `../../HabitTracker/store/produccion.md`.

Fijas desde hoy:

**¿A qué audiencia va dirigida?**

```
Adults and teenagers from 13 up who want to remember their days without the effort of a journal: one line a day, reread on the same date in later years. It suits people who keep five-year paper diaries and privacy minded users, since there is no account and the diary never leaves the phone.
```

**Describe cómo aporta valor**

```
Journaling apps ask for paragraphs and get abandoned. Purl asks for one line and gives back what you wrote on this date in past years, which is what brings people back. Writing, reading, search, the lock, the reminder and export are free. No account, no ads, no analytics.
```

**Descargas esperadas el primer año**: entre 0 y 10.000.

Al final de la prueba, con los apuntes de `lanzamiento.md` fase 5:

| Pregunta | Qué contar |
|---|---|
| ¿Cómo reclutaste usuarios? | De dónde salió cada grupo, sin servicios de pago ni intercambios de testers |
| ¿Cómo de fácil te ha resultado? | Lo que fue |
| Interacciones de los testers | Qué usaron de verdad: escribir a diario, la rejilla, el recordatorio, la copia |
| Comentarios y cómo los recogiste | Canal y los dos o tres comentarios concretos |
| Cambios a partir de la prueba | Cada arreglo con su commit |
| ¿Cómo decidiste que estaba lista? | La última versión sin informes nuevos, los tests en cada push, la publicación por CI |
