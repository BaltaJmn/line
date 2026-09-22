# Compras y RevenueCat, paso a paso

El código lo deja escrito la issue #23 (`docs/tecnico.md` 6.15): `Billing` es el de MoodTraker, busca
un derecho llamado **exactamente `pro`** y coge el **primer paquete de la oferta actual**. Si el
nombre no coincide o no hay oferta marcada como *Current*, el `ProDialog` sale sin precio y sin botón.
Aquí queda lo que no es código, en el orden en que se hace. Los menús de la Play Console con capturas
de dónde está cada cosa: `../../HabitTracker/store/revenuecat.md`.

---

## 0. Qué se vende, decidido

| | |
|---|---|
| Producto | Uno solo, compra única, **no consumible**. Nunca suscripción |
| Identificador | `pro_lifetime` en las dos tiendas. **Irreversible**: un id borrado no se reutiliza |
| Nombre visible | Purl Pro |
| Precio v1.0 | **5,99 EUR** de base en España, conversión automática al resto **con redondeo** |
| Precio desde v1.1 | **8,99 EUR**, el día que sale el libro en PDF (§7) |
| Descuento de lanzamiento | No |
| Países | Todos |
| Prueba gratuita | No. El plan gratis es la prueba |

Qué abre el pago en v1.0, y solo esto (SPEC §6):

- Foto a partir de la **cuarta entrada con foto** (`FREE_PHOTO_LIMIT = 3`, cuenta las que tienen foto
  ahora mismo).
- **Siete portadas**: todas menos `sage`.
- **Widget del año**, Android e iOS.
- **Widget de pantalla de bloqueo**, iOS.

Qué no se cobra nunca, porque lo prometen la ficha y la política: escribir cualquier día, leer todos
los años, la rejilla, la búsqueda, la racha, el recordatorio, el bloqueo, el widget de hoy, exportar e
importar, y la tarjeta para compartir.

> Si cambia cualquiera de las dos listas, se tocan en el **mismo commit** las claves `pro*` de
> `docs/textos.md`, la descripción larga de las dos fichas (`listings/`, `app-store/`) y las
> descripciones del producto de abajo. Vender algo que la versión gratis ya da es tergiversación, y
> las dos tiendas lo tratan como tal.

Quien pierde Pro (un reembolso) no pierde nada de lo que tiene: las fotos siguen, la portada elegida
se queda, los widgets Pro se pintan bloqueados. Solo deja de poder añadir lo que es de Pro.

## 1. El producto en Play

Precondiciones: **un AAB subido a algún canal** (`lanzamiento.md`, fase 3) y el perfil de pagos
verificado, que ya lo está desde Quilt.

1. Play Console, **dentro de Purl**: *Monetizar con Play > Productos > Productos integrados en la
   aplicación > Crear producto*.
2. Id `pro_lifetime`. Nombre y descripción por idioma, de la tabla.
3. Precio 5,99 EUR, *Convertir* al resto de países y **Redondear precios**: sin eso salen 6,43 zł y
   cifras que leen como un error de la tienda.
4. **Activarlo.** Un producto inactivo no sale por la API y el diálogo se queda sin precio.

Play deriva el id de la opción de compra quitando el guion bajo (`prolifetime`) y la marca
*Retrocompatible*. Es lo normal y lo que necesita RevenueCat.

| Idioma | Nombre | Descripción (tope 200) |
|---|---|---|
| en-US | Purl Pro | Photos on every entry, seven more covers and the year widget. One-time payment, not a subscription. Writing, every past year, export, the lock and the reminder stay free. |
| es-ES | Purl Pro | Foto en todas las entradas, siete portadas más y el widget del año. Pago único, no es una suscripción. Escribir, releer, exportar, el bloqueo y el recordatorio siguen gratis. |
| pt-BR | Purl Pro | Foto em todas as entradas, mais sete capas e o widget do ano. Pagamento único, não é assinatura. Escrever, reler, exportar, o bloqueio e o lembrete continuam grátis. |
| de-DE | Purl Pro | Foto bei jedem Eintrag, sieben weitere Umschlagfarben und das Jahres-Widget. Einmalzahlung, kein Abo. Schreiben, Nachlesen, Export, Sperre und Erinnerung bleiben kostenlos. |
| fr-FR | Purl Pro | Une photo sur chaque entrée, sept couvertures de plus et le widget de l'année. Paiement unique, pas d'abonnement. Écrire, relire, exporter, le verrou et le rappel restent gratuits. |

El widget de pantalla de bloqueo no se nombra en Play: en Android no existe.

## 2. La cuenta de servicio de RevenueCat en Google

Es la que deja a RevenueCat preguntarle a Google si una compra es real. **Solo lee pedidos, no
publica**, y nunca es la misma que la de `ci.md`.

**La de Quilt sirve**: es `revenuecat@<proyecto>.iam.gserviceaccount.com`, con las tres APIs ya
activadas (Android Developer, Developer Reporting, Pub/Sub) y sus roles de Cloud puestos. Dos pasos:

1. Play Console, nivel de cuenta, *Usuarios y permisos*: si sus permisos son por app, añadirle Purl
   con los mismos cuatro de Quilt (ver información de la app, ver datos financieros, gestionar pedidos,
   gestionar la presencia en la tienda). Ninguno de publicar.
2. Google Cloud, pantalla *Claves* de esa cuenta: clave JSON nueva, que se sube tal cual en el paso 3.
   No se pega en un chat ni se guarda en el repositorio.

Hasta 36 horas para que Google acepte las credenciales. Mientras tanto RevenueCat da errores de
validación y no significa que esté mal montado.

## 3. El proyecto en RevenueCat

Un proyecto propio, **Purl**, separado de los de Quilt y MoodTraker: cada uno tiene su derecho y su
producto, y mezclarlos haría que comprar una desbloqueara otra.

1. *Create new project*, nombre Purl.
2. *Apps > + Play Store*: package `com.baltajmn.line`, sube el JSON del paso 2.
3. *Products > + New*: Play Store, `pro_lifetime`.
4. *Entitlements > + New*: identificador **`pro`**, en minúsculas. *Attach* el producto.
5. *Offerings > + New*: identificador `default`, **márcala como Current**. Dentro, *+ Package* de
   tipo *Lifetime* (`$rc_lifetime`) con el producto.

Comprobación: si no hay oferta *Current* o esa oferta no tiene paquete, la app enseña el diálogo sin
precio.

## 4. Las claves

*Project settings > API keys*. Se copian las **públicas**:

| Plataforma | Prefijo | Dónde va |
|---|---|---|
| Android | `goog_` | `revenueCatApiKey` en `shared/src/androidMain/.../data/Billing.android.kt` |
| iOS | `appl_` | `revenueCatApiKey` en `shared/src/iosMain/.../data/Billing.ios.kt` |

Son públicas: viajan dentro del binario y cualquiera puede sacarlas. Van como literal en el código.
**La clave secreta `sk_...` no sale nunca del panel de RevenueCat**: ni en el código, ni en un
secreto de GitHub, ni en un chat. La app no la necesita.

Mientras la clave sea `null`, `Billing.configure()` no hace nada y la app funciona entera en modo
gratis. Así se puede trabajar y probar todo lo demás antes de tener el panel montado.

## 5. Probar una compra de verdad en Android

1. Play Console, nivel de cuenta, *Ajustes > Monetización > Licencia para testing*: el correo de
   Google del móvil de pruebas. Compra con el diálogo real y sin cargo.
2. Instalar **desde la prueba interna**, no por `adb`: una compra solo funciona si el binario viene de
   Play. En el emulador no hay Play Billing.
3. Comprar, y ver en RevenueCat, *Customer History*, el evento y el derecho `pro` activo.
4. Desinstalar, reinstalar, *Restaurar compra* desde Ajustes. Es el camino que más se rompe.
5. Reembolsar desde la Console y comprobar que, al volver a abrir la app, los widgets Pro se pintan
   bloqueados y las fotos siguen ahí.

## 6. iOS

En App Store Connect, dentro de Purl, *Monetización > Compras dentro de la app > +*:

| Campo | Valor |
|---|---|
| Tipo | No consumible |
| Nombre de referencia | Purl Pro |
| Id de producto | `pro_lifetime` |
| Precio | País base **España**, 5,99 EUR; el resto, por la equivalencia automática de Apple |
| Disponibilidad | Todos los países |
| Captura para la revisión | El `ProDialog` abierto en un iPhone, desde el simulador |
| Nota para la revisión | `Unlocks photos beyond the third entry with a photo, seven more cover colors, the year widget and the lock screen widget. One-time purchase. Restore Purchase is in Settings and in this dialog.` |

Nombre visible (tope 30) y descripción (tope 45) por idioma:

| Idioma | Nombre | Descripción |
|---|---|---|
| en-US | Purl Pro | Photos, covers and widgets. Pay once. |
| es-ES | Purl Pro | Fotos, portadas y widgets. Pago único. |
| pt-BR | Purl Pro | Fotos, capas e widgets. Pagamento único. |
| de-DE | Purl Pro | Fotos, Umschläge, Widgets. Einmalzahlung. |
| fr-FR | Purl Pro | Photos, couvertures, widgets. Achat unique. |

**La primera compra solo se revisa junto a una versión**: se adjunta en la página de la versión, en
*Compras dentro de la app*, antes de enviar a revisión.

En RevenueCat, mismo proyecto Purl:

1. *Apps > + App Store*, bundle id `com.baltajmn.line`, y la **In-App Purchase Key** (`.p8` de App
   Store Connect, *Usuarios y acceso > Integraciones > Compra dentro de la app*). Si el panel pide
   además el *App-Specific Shared Secret*, se genera en la página de la app y se pega también.
2. *Products*: `pro_lifetime` de App Store.
3. Adjuntarlo al **mismo derecho `pro`** y al **mismo paquete** de la oferta `default`. Así quien
   compró en una plataforma restaura en la otra si usa el mismo identificador, y el código no
   distingue tiendas.
4. Copiar la `appl_...` al código (§4).

Probar en el sandbox: cuenta de sandbox en *Usuarios y acceso > Sandbox*, sesión iniciada en el
iPhone en *Ajustes > App Store > Cuenta de sandbox*, comprar, borrar la app, reinstalar y restaurar.

## 7. Subir a 8,99 EUR con la v1.1

Se hace **el día que la v1.1 se publica**, no antes: el precio sube porque crece lo que se da.

- **Play**: *Productos integrados*, `pro_lifetime`, precio 8,99 EUR, *Convertir* y *Redondear*.
  Cambia en unas horas.
- **App Store**: *Programación de precios* del producto, *Añadir cambio de precio* con fecha de
  inicio el día de publicación, país base España, 8,99 EUR.
- **Descripciones**: se añade el libro en PDF a la lista de Pro en la tabla del §1, en `pro*` de
  `docs/textos.md` y en las dos fichas, en el mismo commit que etiqueta la v1.1.
- Quien compró a 5,99 conserva Pro para siempre: es un no consumible y el derecho no caduca.
