# Interfaz de Purl

Pantalla a pantalla, con todos sus estados. Los textos se citan por su clave (`todayPlaceholder`) y
su versión en español; la tabla de los cinco idiomas está en `docs/textos.md`. Las medidas son `dp`
en la app, `sp` en el texto y `px` en las tarjetas.

La base es MoodTraker: `ui/theme/Theme.kt`, `ui/Icons.kt`, la estructura de `HomeScreen.kt` y
`Pro.kt`. Lo que no se dice aquí se hace como allí.

---

## 1. Sistema de diseño

### 1.1 Color

Los `colorScheme` de la familia, sin cambiar un hex (`MoodTraker/shared/.../ui/theme/Theme.kt`).

| Token | Claro | Oscuro | Uso |
|---|---|---|---|
| `background` | `FBF8F3` | `17150F` | lienzo de todas las pantallas |
| `onBackground` | `39352E` | `ECE5D9` | texto principal y el del usuario |
| `surface` | `FFFFFF` | `201D16` | diálogos |
| `surfaceVariant` | `F0EBE2` | `2C2820` | fondo de avisos y del buscador |
| `onSurfaceVariant` | `8B8479` | `9C9486` | texto secundario, iconos, etiquetas |
| `outline` | `E3DCD1` | `3A352B` | bordes de 1 dp, huecos de la rejilla |
| `outlineVariant` | `EFE9DF` | `2C2820` | separadores |
| `primary` | `6FAE9B` | `8FC9B6` | acción de texto, interruptores encendidos |
| `onPrimary` | `FFFFFF` | `12271F` | texto sobre `primary` |

`error` no se usa en ninguna pantalla: ni un aviso ni un borrado se pintan en rojo ni en su pariente. Por si
un componente de Material lo busca solo, `error` vale lo mismo que `onSurfaceVariant`.

**Portada.** La portada elegida (`docs/tecnico.md` sección 5) tiñe, y solo tiñe: las celdas llenas de
la rejilla del año, el punto del hito en Hoy, el punto de la fecha en Hoy y en el día abierto, las dos
tarjetas y los widgets. El acento de las acciones sigue siendo `primary`. En oscuro, el mismo hex.

### 1.2 Tipografía

| Estilo | Fuente | Tamaño / interlineado | Peso | Color | Dónde |
|---|---|---|---|---|---|
| `UserLarge` | Literata | 22 / 32 sp | Regular | `onBackground` | el campo de Hoy y del día abierto |
| `UserMedium` | Literata | 18 / 27 sp | Regular | `onBackground` | bloques de años anteriores y ecos |
| `UserSmall` | Literata | 16 / 24 sp | Regular | `onBackground` | resultados de búsqueda |
| `Title` | sistema | 20 / 26 sp | Medium | `onBackground` | título de Ajustes y de diálogos |
| `DateLine` | sistema | 15 / 20 sp | Medium | `onSurfaceVariant` | la fecha de Hoy y del día abierto |
| `Body` | sistema | 15 / 22 sp | Normal | `onBackground` | filas de Ajustes, avisos, diálogos |
| `Secondary` | sistema | 13 / 18 sp | Normal | `onSurfaceVariant` | subtítulos, vuelta de la página, número de día |
| `Light` | sistema | 13 / 18 sp | Light | `onSurfaceVariant` | racha, contador de caracteres |
| `Eyebrow` | sistema | 11 / 14 sp | Medium, 1,4 sp de espaciado | `onSurfaceVariant` | etiquetas de bloque y de sección, en mayúsculas con `uppercase()` |
| `Action` | sistema | 15 / 20 sp | Medium | `primary` | botones de texto |

Literata Regular es la única fuente empaquetada. No hay cursiva ni negrita del usuario.

En código, `Styles` de `ui/theme/Theme.kt` con el nombre en minúscula inicial: `Styles.userLarge`,
`Styles.eyebrow`. Cada estilo lleva ya su color.

### 1.3 Medidas

| Qué | Valor |
|---|---|
| Espaciado | 4, 8, 12, 16, 24, 32, 48 |
| Margen lateral de pantalla | 24 |
| Columna de contenido | ancho máximo 600, centrada (iPad y horizontal) |
| Radios (`SoftShapes`) | 8, 12, 18, 24, 32 |
| Bordes | 1 dp, `outline`. Sin sombras |
| Zona táctil mínima | 48x48 (`GlyphButton` pasa de 40 a 48; el icono sigue a 20) |
| Cabecera | 56 de alto, iconos a la derecha, sin barra ni fondo propio |
| Botón de texto | 40 de alto, 16 de relleno lateral, sin fondo |
| Botón con borde | 44 de alto, radio 24, borde 1 dp `outline`, texto `Action` |
| Foto | ancho de la columna, proporción 4:3 recortada al centro, radio 18 |

### 1.4 Movimiento

Ninguna animación propia. Cambiar de pantalla es instantáneo, como en las hermanas. Solo las del
sistema: el teclado, los diálogos, el interruptor, el selector de hora y el diálogo de biometría.

---

## 2. Iconos

`ui/Icons.kt` de MoodTraker: coordenadas en fracciones del lado, trazo del 9 % del lado, extremos y
uniones redondeados, color `onSurfaceVariant`, 20 dp dentro de un botón de 48. Se añaden los que faltan.

| Glyph | Trazos (x, y en fracción del lado) | Descripción para accesibilidad |
|---|---|---|
| `BACK` | (0.62, 0.18) (0.34, 0.50) (0.62, 0.82) | `a11yBack` (Volver) |
| `FORWARD` | (0.40, 0.18) (0.68, 0.50) (0.40, 0.82) | `a11yNextYear` (Año siguiente) |
| `SHARE` | (0.50, 0.88) (0.50, 0.16); y (0.26, 0.40) (0.50, 0.16) (0.74, 0.40) | `a11yShare` (Compartir) |
| `SETTINGS` | dos líneas (0.14, y) (0.86, y) en y = 0.34 y 0.62, con un círculo lleno de radio 0.11 en x = 0.66 y x = 0.38 | `a11ySettings` (Ajustes) |
| `YEAR` | nueve círculos llenos de radio 0.07 en x, y de {0.25, 0.50, 0.75} | `a11yYear` (El año) |
| `CLOSE` | (0.24, 0.24) (0.76, 0.76); y (0.76, 0.24) (0.24, 0.76) | `a11yClose` (Cerrar) |
| `PHOTO` | rectángulo de (0.14, 0.24) a (0.86, 0.80) con radio 0.08; círculo lleno de radio 0.07 en (0.36, 0.43); línea (0.14, 0.72) (0.40, 0.52) (0.58, 0.66) (0.70, 0.57) (0.86, 0.70) | `a11yPhoto` (Añadir foto) |
| `TRASH` | tapa (0.18, 0.28) (0.82, 0.28); asa (0.40, 0.28) (0.40, 0.18) (0.60, 0.18) (0.60, 0.28); cuerpo (0.26, 0.28) (0.31, 0.84) (0.69, 0.84) (0.74, 0.28) | `a11yDelete` (Borrar este día) |
| `SEARCH` | círculo de radio 0.24 en (0.44, 0.44); línea (0.62, 0.62) (0.84, 0.84) | ninguno (decorativo, dentro del campo) |
| `CHECK` | (0.22, 0.52) (0.42, 0.72) (0.78, 0.30) | ninguno (el estado lo dice la fila) |

`BACK` en la selección de año se describe como `a11yPreviousYear` (Año anterior).

---

## 3. Estructura

```
App
+-- Today (raíz)
|   +-- Year          (GlyphButton YEAR)
|   +-- Settings      (GlyphButton SETTINGS)
+-- overlays, por encima de la pantalla actual:
    +-- DaySheet      (desde Hoy, Año o la búsqueda)
    +-- ShareScreen   (desde Año: tarjeta del año; desde DaySheet: tarjeta de una línea)
    +-- ProDialog     (desde cualquier choque)
    +-- LockScreen    (por encima de todo)
```

- Atrás del sistema y `BackHandler`: cierra el overlay abierto; si no hay, vuelve a Hoy; en Hoy, sale.
- Los widgets y la baldosa abren con `today`, `year` o `pro`: la pantalla correspondiente, y `pro`
  abre Hoy con el `ProDialog` encima.
- Con `LockScreen` puesto no se procesa ninguna URL hasta desbloquear; después se aplica.
- Horizontal e iPad: todo igual, dentro de la columna de 600 centrada. Sin diseños propios.

---

## 4. Hoy

### 4.1 Composición

```
+------------------------------------------------+
|  o Sábado, 17 de enero              [YEAR] [S] |  cabecera, 56
|                                                |
|  o Tu línea número 30.                         |  hito (si hay)
|                                                |
|  Lo que quieras recordar de hoy                |  campo, UserLarge
|                                                |
|                                                |
|  5 días seguidos             [PHOTO]   263/280 |  fila de apoyo
|                                                |
|  +------------------------------------------+  |  foto de hoy (si hay)
|  |                                          |  |
|  +------------------------------------------+  |
|                                                |
|  +------------------------------------------+  |  aviso (si hay)
|  | Hace un mes que escribes. ¿Guardas una   |  |
|  | copia?          Ahora no   Hacer copia   |  |
|  +------------------------------------------+  |
|                                                |
|  2026, HACE UN AÑO                             |  Eyebrow
|  Mismo día, sol. Cambio de piso confirmado.    |  UserMedium
|  +------------------------------------------+  |
|  |  foto 4:3                                |  |
|  +------------------------------------------+  |
|                                                |
|  2025, HACE 2 AÑOS                             |
|  Primer día de vacaciones, llovió todo...      |
+------------------------------------------------+
```

Todo en una `Column` con `verticalScroll` e `imePadding()`, margen lateral 24.

- **Cabecera**: a la izquierda, un círculo de 8 dp del color de la portada y, a 8, la fecha larga
  (`longDate`, estilo `DateLine`); a la derecha `GlyphButton(YEAR)` y `GlyphButton(SETTINGS)`.
- **Hito**: 8 debajo de la cabecera. Círculo de 8 del color de la portada y el texto (`Body`). Solo
  uno (`docs/tecnico.md` 6.3). Claves: `milestoneFirst` (Tu primera línea.), sin plazo porque la vuelta ya la dice `returnsOn` debajo, también un 29 de febrero;
  `milestoneThirty` (Tu línea número 30.), `milestoneHundred` (Cien líneas.), `milestoneAnniversary`
  (Hoy hace un año que empezaste este diario.), `milestoneThreeYears` (Hoy tienes tres años en la
  misma página.).
- **Campo** (`LineField`): 16 debajo. Sin borde, sin fondo, `UserLarge`, cursor `primary`, altura
  mínima 96. Texto de ayuda `todayPlaceholder` (Lo que quieras recordar de hoy) en
  `onSurfaceVariant`. Teclado con `ImeAction.Done` (cierra el teclado), mayúscula inicial de frase.
- **Fila de apoyo**: 8 debajo del campo, alto 48. Izquierda, la racha `streakDays(n)` (5 días
  seguidos) en `Light`, solo si `n >= 2`. Derecha, `GlyphButton(PHOTO)` si hoy no tiene foto, y el
  contador `counter(n, 280)` (263/280) en `Light` desde 250 puntos de código.
- **Foto de hoy**: 12 debajo; tocarla abre el día de hoy en `DaySheet`.
- **Aviso**: 24 debajo. Caja con fondo `surfaceVariant`, radio 18, relleno 16; texto `Body`; acciones
  `Action` alineadas a la derecha, separadas 8. Uno solo, por prioridad (4.3).
- **Años anteriores**: 32 debajo. Por cada año, del más reciente al más antiguo, separados 24: la
  etiqueta `pastYearLabel(year, n)` (2026, hace un año) en `Eyebrow`, 8, el texto en `UserMedium`
  completo (sin recortar), y la foto 12 debajo si la tiene. Tocar el bloque abre ese día en
  `DaySheet`.
- **Sin años anteriores**: en su lugar, los ecos y la vuelta (4.2, estados D y E).

### 4.2 Estados

| Estado | Qué cambia |
|---|---|
| A. Primera vez (diario vacío) | Foco en el campo y teclado arriba. Debajo del campo, en `Secondary`: `firstHelp` (Una línea al día. Dentro de un año, este mismo día, volverás a leerla.). Sin racha, sin años |
| B. Hoy sin escribir | Foco en el campo y teclado arriba al abrir |
| C. Escribiendo | Contador desde 250. Al llegar a 280, el campo no admite más (`docs/tecnico.md` 6.4) |
| D. Hoy escrito, con años anteriores | Sin foco ni teclado: se abre leyendo. Tocar el texto lo edita |
| E. Sin años anteriores | Bloques de eco, cada uno solo si existe: `echoWeek` (Hace una semana) y `echoMonth` (Hace un mes) como etiqueta `Eyebrow` con la fecha corta detrás (`echoLabel(label, date)`: Hace una semana, 10 de enero), el texto en `UserMedium`. Debajo, en `Secondary`: `dayNumber(n)` (Día 12 de tu diario.) y `returnsOn(fecha)` (Esta página volverá el 17 de enero de 2028.) |
| F. Con hito | La línea del hito (4.1) |
| G. Guardado fallido | Aviso `noticeSaveFailed` (No se ha podido guardar. Lo intento otra vez con tu próximo cambio.), sin acciones; desaparece con el primer guardado bueno |
| H. Diario dañado | Aviso `noticeCorrupt` (No se ha podido leer el diario. Los ficheros se han guardado aparte y no se ha borrado nada.) con acción `ok` (Vale) |
| I. Oferta de recordatorio | Tras guardar la primera línea, una vez: `offerReminder` (¿Te lo recuerdo cada día a las 21:00?) con `notNow` (Ahora no) y `yes` (Sí). `yes` enciende el recordatorio y pide el permiso |
| J. Aviso de copia | `noticeBackup` (Hace un mes que escribes. ¿Guardas una copia fuera del teléfono?) con `notNow` y `makeBackup` (Hacer copia) |
| K. Recapitulación (v1.2) | Del 26 de diciembre al 7 de enero, línea `recapYearOffer(año)` (Tu 2027 en Purl) en `Action`, bajo la fila de apoyo |

### 4.3 Prioridad de avisos

H, G, I, J. Se enseña el primero que aplique; el siguiente aparece cuando el anterior se va.

---

## 5. Año

```
+------------------------------------------------+
|  [<]                                  [SHARE]  |  cabecera
|                                                |
|            [<]    2027    [>]                  |  selector de año
|                                                |
|  +------------------------------------------+  |
|  | (o) Buscar en el diario              [x] |  |  buscador
|  +------------------------------------------+  |
|                                                |
|     E  F  M  A  M  J  J  A  S  O  N  D         |
|   1 #  #  .  #  #  .  #  #  #  .  .  .         |
|     #  #  #  #  .  #  #  #  #  .  .  .         |
|     ...                                        |
|  10 ...                                        |
|  20 ...                                        |
|  30 #     #  .  #     #  .     .     .         |
|     #     .     #     #  #     .     .         |
|                                                |
|  212 líneas en 2027                            |
|                                                |
|  17 DE ENERO DE 2027                           |  resultados (con búsqueda)
|  Mismo día, sol. Cambio de piso confirmado.    |
+------------------------------------------------+
```

- **Cabecera**: `GlyphButton(BACK)` a Hoy; `GlyphButton(SHARE)` abre la tarjeta del año (si el año
  tiene al menos una línea; si no, no se pinta).
- **Selector de año**: `GlyphButton(BACK)` y `GlyphButton(FORWARD)` con el año en medio (`Title`).
  Del año de la entrada más antigua al actual; en los extremos el botón se pinta al 30 % y no
  responde. Se abre en el año actual.
- **Buscador**: 48 de alto, radio 24, fondo `surfaceVariant`, `SEARCH` a la izquierda, texto `Body`,
  ayuda `searchPlaceholder` (Buscar en el diario), `CLOSE` a la derecha cuando hay texto (vacía la
  búsqueda). Buscar no cambia de año.
- **Rejilla** (`YearGrid`, en `Canvas`): 12 columnas (meses) por 31 filas (días). Encima, las
  iniciales de los meses (`monthInitials`) en `Eyebrow`; a la izquierda, 20 de ancho, los números 1,
  10, 20 y 30 en `Eyebrow`. Celda cuadrada: lado = mínimo de 24 y `(ancho - 20 - 11 x 4) / 12`;
  hueco entre celdas 4; radio 4.
  - Día con línea: relleno del color de la portada.
  - Día sin línea: sin relleno, borde 1 dp `outline`.
  - Día futuro: borde `outline` al 40 %, no responde.
  - Día que no existe (30 y 31 de febrero, 29 en año no bisiesto, 31 de abril, junio, septiembre y
    noviembre): no se pinta.
  - Hoy: anillo de 1,5 dp `onBackground` alrededor, a 2 de la celda.
  - Con búsqueda: las celdas llenas que no casan bajan al 25 %.
  - Tocar un día pasado o de hoy abre `DaySheet`. Pulsación larga (v1.1): 6.2.
- **Recuento**: 16 debajo, `yearCount(n, año)` (212 líneas en 2027) en `Secondary`.
- **Resultados** (con búsqueda): 24 debajo, `resultsCount(n)` (12 resultados) en `Eyebrow` y la lista
  de todos los años, más reciente primero: la fecha larga con año en `Eyebrow`, 4, el texto en
  `UserSmall` hasta 3 líneas con `...`; 16 entre resultados. Tocar abre `DaySheet`.

| Estado | Qué se ve |
|---|---|
| Diario vacío | La rejilla con todos los huecos y `yearEmpty` (Tu año se irá llenando línea a línea.) en lugar del recuento |
| Búsqueda sin resultados | `noResults` (Nada con esas palabras.) en `Secondary` |
| Año pasado | Sin anillo de hoy, sin días futuros |

---

## 6. Día abierto (`DaySheet`)

```
+------------------------------------------------+
|  [x]                          [SHARE] [TRASH]  |
|                                                |
|  o Martes, 17 de enero de 2026                 |  DateLine, con año
|                                                |
|  Mismo día, sol. Cambio de piso confirmado.    |  LineField
|                                                |
|                              [PHOTO]   263/280 |
|                                                |
|  +------------------------------------------+  |
|  |  foto 4:3                                |  |
|  +------------------------------------------+  |
|            Cambiar foto   Quitar foto          |
+------------------------------------------------+
```

Pantalla completa sobre la actual, fondo `background`, mismo `verticalScroll` más `imePadding()`.

- `CLOSE` guarda (`flush`) y cierra.
- `SHARE` solo si el día tiene texto: abre la tarjeta de una línea.
- `TRASH` solo si el día tiene entrada: confirmación (9.1).
- Fecha: `longDateWithYear`. El campo es el mismo `LineField` de Hoy.
- Sin texto al abrir: foco y teclado arriba. Con texto: sin foco.
- Foto: `PHOTO` si no tiene; con foto, la imagen y debajo `changePhoto` (Cambiar foto) y
  `removePhoto` (Quitar foto) centrados. Cuarta entrada con foto sin Pro: `PHOTO` abre el `ProDialog`.

### 6.2 Pulsación larga en la rejilla (v1.1)

Burbuja sobre la celda: fondo `surface`, borde 1 dp, radio 12, relleno 12, ancho máximo 240; la
fecha corta en `Eyebrow` y los primeros 60 puntos de código en `UserSmall` con `...` si se cortan.
Se va al soltar.

---

## 7. Ajustes

```
+------------------------------------------------+
|  [<]  Ajustes                                  |
|                                                |
|  RECORDATORIO                                  |
|  Recordatorio diario                    [ON]   |
|  A las 21:00                                   |
|                                                |
|  PRIVACIDAD                                    |
|  Bloquear el diario                    [OFF]   |
|  Pide tu cara, tu huella o el código           |
|                                                |
|  PORTADA                                       |
|  (o) (o) (o) (v) (o) (o) (o) (o)               |
|  Salvia es gratis; las demás, con Purl Pro.    |
|                                                |
|  COPIA                                         |
|  Exportar copia                                |
|  Última copia: 17 ene 2027                     |
|  Importar copia                                |
|  Se junta con tu diario, sin borrar nada       |
|                                                |
|  PURL PRO                                      |
|  Purl Pro                                      |
|  Fotos, portadas y widgets. Pago único         |
|  Restaurar compra                              |
|                                                |
|  MÁS APPS                                      |
|  Quilt                                         |
|  Tus hábitos, un año a la vista                |
|                                                |
|  ACERCA DE                                     |
|  Política de privacidad                        |
|  Versión 1.0                                   |
+------------------------------------------------+
```

Cabecera con `BACK` y el título `settingsTitle` (Ajustes) en `Title`. Secciones con etiqueta
`Eyebrow`, 32 entre secciones, 12 bajo la etiqueta. Cada fila: 56 de alto mínimo, título en `Body`,
subtítulo en `Secondary`, interruptor a la derecha (`Switch` de Material3 con `primary`); toda la
fila responde.

| Fila | Estados |
|---|---|
| `reminderRow` (Recordatorio diario) | Apagado: subtítulo `reminderOff` (Apagado). Encendido: `reminderAt(hora)` (A las 21:00); tocar la fila abre el `TimePicker` de Material3 en un diálogo con `ok` y `cancel`. Permiso denegado: el interruptor vuelve a apagado y el subtítulo pasa a `reminderDenied` (Las notificaciones de Purl están desactivadas en el sistema.) con la acción `openSystemSettings` (Abrir ajustes) |
| `lockRow` (Bloquear el diario) | Disponible: subtítulo `lockSubtitle` (Pide tu cara, tu huella o el código del teléfono). No disponible: interruptor desactivado y `lockUnavailable` (Pon un bloqueo de pantalla en el teléfono para usarlo.). Encender pide autenticar; si falla, sigue apagado |
| Portada | Ocho círculos de 32, separados 12 (con salto de línea si no caben). La elegida lleva `CHECK` en `onBackground` al 70 %. Sin Pro, debajo `coverProHint` (Salvia es gratis; las demás, con Purl Pro.); tocar una Pro abre el `ProDialog`. Cada círculo se describe con su nombre (`coverName(id)`) y "elegida" (`a11ySelected`) |
| `exportRow` (Exportar copia) | Con entradas: `lastBackup(fecha)` (Última copia: 17 ene 2027) o `lastBackupNever` (Todavía ninguna copia). Sin entradas: fila al 40 %, no responde, `exportNothing` (Aún no hay nada que copiar.) |
| `importRow` (Importar copia) | Subtítulo `importSubtitle` (Se junta con tu diario, sin borrar nada) |
| `proRow` (Purl Pro) | Sin Pro: `proSubtitle` (Fotos, portadas y widgets. Pago único); abre el `ProDialog`. Con Pro: `proOwned` (Comprado. Gracias.), no responde |
| `restoreRow` (Restaurar compra) | Siempre. Al terminar, `restoreDone` (Compra restaurada.) o `restoreNothing` (No hay ninguna compra que restaurar.) en un diálogo de un botón |
| Más apps | Solo si hay filas (`docs/tecnico.md`, `SIBLINGS`). Título el nombre de la app, subtítulo su lema (`siblingQuilt`, `siblingMood`); abre la tienda |
| `privacyRow` (Política de privacidad) | Abre `PRIVACY_URL` |
| Versión | `version(v)` (Versión 1.0), no responde |

v1.1 añade en COPIA la fila `importMoodRow` (Importar de MoodTraker) y una sección `sectionWriting`
(ESCRITURA) con el interruptor `questionsRow` (Una pregunta cuando el día está en blanco). v1.2 añade
ahí `moodRow` (Anotar el ánimo).

---

## 8. Bloqueo

```
+------------------------------------------------+
|                                                |
|                                                |
|                     Purl                       |  Literata, 32 sp
|                                                |
|               [  Desbloquear  ]                |  botón con borde
|                                                |
+------------------------------------------------+
```

Fondo `background`, centrado, sin nada más. Al aparecer quita el foco del campo de hoy: si no, el
teclado vuelve a subir encima del bloqueo al volver de segundo plano. El diálogo del sistema salta al
aparecer; si se cancela, queda el botón `unlock` (Desbloquear). Textos del diálogo del sistema: `lockPromptTitle` (Abrir Purl)
y `lockPromptSubtitle` (Tu diario está bloqueado).

La vista de multitarea en iOS es solo el color `background`, sin texto.

---

## 9. Diálogos

`AlertDialog` de Material3, fondo `surface`, radio 24, título `Title`, texto `Body`, botones `Action`.
Ningún botón destructivo en rojo.

### 9.1 Borrar un día

`deleteTitle` (¿Borrar este día?), `deleteText` (Se borran la línea y su foto. No se puede
deshacer.), `delete` (Borrar), `cancel` (Cancelar).

### 9.2 Importar

- Confirmación: `importTitle` (Importar copia) y `importSummary(added, joined, same)` (La copia trae
  12 días nuevos, 3 que se juntan con los tuyos y 40 iguales. No se borra nada.), con `importAction`
  (Importar) y `cancel`. Durante la importación, el botón pasa a `working` (Un momento...).
- Hecho: `importDone(n)` (Diario al día: 15 días actualizados.) con `ok`.
- Error: `importFailedTitle` (No se ha podido importar) y el texto de `docs/tecnico.md` 4.6
  (`importNotBackup`, `importDamaged`, `importTooNew`, `importEmpty`, `importIsMoodTraker`), con `ok`.

### 9.3 Exportar

Sin diálogo propio: el selector del sistema. Si falla, `exportFailed` (No se ha podido guardar la
copia.) con `ok`.

### 9.4 `ProDialog`

```
Purl Pro
- Fotos en todas tus entradas
- Siete portadas más
- El widget del año
- El widget de la pantalla de bloqueo      (solo iOS)
Pago único, sin suscripción.

        Restaurar     Ahora no     Comprar por 5,99 EUR
```

- Título `proTitle` (Purl Pro). Líneas `proPhotos`, `proCovers`, `proYearWidget` y, en iOS,
  `proLockWidget`, cada una empezando por un guion; debajo `proOnce` (Pago único, sin suscripción.).
- Botones: `restore` (Restaurar), `notNow` (Ahora no), `buy(price)` (Comprar por 5,99 EUR) con el
  precio que devuelve la tienda.
- Sin tienda: texto extra `storeUnavailable` (La tienda no está disponible ahora.) y `buy` desactivado.
- Comprando: `buy` pasa a `working`. Éxito: se cierra. Cancelado: no pasa nada. Error: `buyFailed` (No
  se ha podido completar la compra.) bajo las líneas.
- v1.1 añade `proBook`, `proMemoryWidget`, `proSiri` (iOS) y `proTile` (Android); v1.2 `proRecaps` y
  `proManyPhotos`.

---

## 10. Compartir

```
+------------------------------------------------+
|  [x]                                           |
|                                                |
|        +----------------------------+          |
|        |                            |          |  la tarjeta a escala,
|        |        vista previa        |          |  radio 18, borde 1 dp
|        |                            |          |
|        +----------------------------+          |
|                                                |
|     [ Compartir ]     [ Guardar en fotos ]     |  botones con borde
+------------------------------------------------+
```

`share` (Compartir) abre la hoja del sistema; `saveToPhotos` (Guardar en fotos) guarda y responde con
`saved` (Guardada en tus fotos.) o `saveFailed` (No se ha podido guardar.). En Android 9 o anterior,
`saveToPhotos` no aparece.

### 10.1 Tarjeta del año (1080x1350 px)

Siempre en claro, como las hermanas: fondo `Cream FBF8F3`, tinta `Ink 39352E`, secundario
`Muted 8B8479`, hueco `Empty EDE7DC`.

| Elemento | Posición y tamaño |
|---|---|
| Año | x 108, línea base y 250; Literata 140 px, `Ink` |
| Líneas escritas | x 108, base y 330; sistema 42 px, `Muted`: `cardLines(n)` (212 líneas) |
| Racha más larga del año | x 108, base y 390; sistema 42 px, `Muted`: `cardLongestStreak(n)` (Racha más larga: 34 días) |
| Rejilla | 12 filas (meses) por 31 columnas (días); celda 22 px, hueco 6 px, radio 5; bloque de 862x330 que empieza en x 109, y 520. Llena: portada. Hueco: `Empty`. Día futuro: `Empty` al 50 %. Día inexistente: nada |
| Iniciales de meses | no se pintan |
| Pie | "Purl" en Literata 52 px `Ink`, x 108, base y 1210; `cardTagline` (una línea al día) en sistema 34 px `Muted`, x 108, base y 1262 |
| Punto de portada | círculo de 36 px de diámetro, portada, centrado en x 954, y 1196 |

La racha más larga del año es `longestStreak(j, año)`: la racha de días a tiempo más larga dentro del
año (`model/Insights.kt`).

### 10.2 Tarjeta de una línea (1080x1350 px)

| Elemento | Posición y tamaño |
|---|---|
| Punto de portada | círculo de 36 px, centro x 126, y 150 |
| Fecha | x 168, base y 162; sistema 34 px Medium, `Muted`, mayúsculas, 4 px de espaciado: `longDateWithYear` |
| Texto | Literata en `Ink`, caja de x 108 a 972 (864 de ancho) y de y 260 a 1080. Empieza en 64 px con interlineado 1,4; si no cabe, baja de 4 en 4 hasta 36 px; si a 36 no cabe, se corta con `...` |
| Foto | no se pinta: la tarjeta es de la línea |
| Pie | el mismo de la tarjeta del año |

---

## 11. Widgets

Fondo `background` del sistema claro u oscuro, radio el del sistema, relleno 16. Sin texto del
diario, nunca (el del recuerdo es la excepción de v1.1).

En Android el relleno propio es 8 y no 16: desde Android 12 el escritorio ya mete su margen, y los
dos juntos parten en dos la etiqueta más corta dentro de un 2x2. Ahí `widgetMemory` puede ocupar dos
líneas; el punto queda centrado con ellas. Por debajo de 150 de alto el widget se queda sin la línea
del recuerdo antes que pintarla a medias.

### 11.1 Hoy

| Tamaño | Contenido |
|---|---|
| Android 2x2 (mínimo 110x110) e iOS `systemSmall` | Arriba, la fecha corta (`widgetDate`: SÁB 17 ENE) en 12 sp Medium `onSurfaceVariant`. En el centro, un círculo de 28: lleno del color de la portada si está escrita, anillo de 2 `outline` si no. Debajo, `widgetWritten` (Escrita) o `widgetNotWritten` (Por escribir) en 15 sp Medium. Abajo, si hay recuerdo, un punto de 6 de la portada y `widgetMemory` (Hay recuerdo) en 12 sp |
| iOS `systemMedium` | La fecha larga a la izquierda (`longDate`, 17 sp Medium) y, a la derecha, el círculo, el estado y el recuerdo en columna |
| iOS `accessoryCircular` (Pro) | Círculo lleno si está escrita, anillo si no; un punto en el centro si hay recuerdo. Sin Pro: `Image(systemName: "lock")` |
| iOS `accessoryRectangular` (Pro) | Tres líneas: la fecha corta, el estado y, si hay, `widgetMemory`. Sin Pro: `proTitle` y `widgetUnlock` (Toca para activarlo) |

Tocar: Hoy. Accesorios sin Pro: el `ProDialog`.

### 11.2 Año (Pro)

| Tamaño | Contenido |
|---|---|
| Android 4x2 (mínimo 180x110) e iOS `systemMedium` | La rejilla de 12 filas por 31 columnas (como la tarjeta), llenando el ancho; encima, el año en 13 sp Medium y `yearCount` a la derecha en 12 sp |
| iOS `systemLarge` | Lo mismo con celdas mayores y el año en 20 sp |
| Sin Pro | La rejilla en `Empty` y, centrado encima, `proTitle` y `widgetUnlock` |

Android pinta la rejilla en un `Bitmap` (`yearBitmap` de Quilt); iOS con `Canvas`. Tocar: Año, o el
`ProDialog` sin Pro.

### 11.3 Recuerdo (v1.1, Pro)

`systemSmall` y `systemMedium` (iOS), 3x2 (Android). `widgetMemoryLabel(year)` (Hace un año, 2026)
en `Eyebrow` y la línea en Literata 15 sp (iOS con la fuente registrada en la extensión; Glance con la
del sistema en serif), hasta 5 líneas con `...`, con `.privacySensitive()` en iOS. Sin recuerdo:
`widgetNoMemory` (Hoy no hay recuerdo de hace un año.). Con el bloqueo puesto: `widgetLocked` (Diario
bloqueado.). Sin Pro: `proTitle` y `widgetUnlock`. Tocar: el día de hace un año.

### 11.4 Baldosa (v1.1, Android)

`tileLabel` (Línea de hoy); subtítulo `tileWritten` (Escrita) o `tileNotWritten` (Por escribir);
icono `ic_notification`.

---

## 12. Notificación

- Android: icono pequeño `ic_notification`, color `6FAE9B` (`setColor`), título y texto de
  `docs/tecnico.md` 6.10 (`reminderTitle`: Un momento para tu línea de hoy; `reminderMemoryTitle`:
  Hace un año, hoy; cuerpo: el fragmento), sin icono grande, `autoCancel`, abre `MainActivity`.
- iOS: el icono de la app y los mismos textos; sonido por defecto.

---

## 13. Icono de la app

**Metáfora**: el punto del revés. Cinco vueltas de puntos, una por año, cada una de un pastel; el
último punto, el de hoy, en crema.

Lienzo de 1024x1024:

- Fondo: degradado lineal vertical de `2C2820` (arriba) a `17150F` (abajo). En iOS, sin esquinas (las
  pone el sistema).
- Cinco filas de cuatro cápsulas (rectángulos de 110x60 con radio 30), separadas 20 en horizontal y
  40 en vertical. Bloque de 500x460 centrado: x de 262 a 762; centros de fila en y = 302, 402, 502,
  602 y 702, desplazados 10 hacia abajo para centrar ópticamente: 312, 412, 512, 612, 712.
- Colores por fila, de arriba abajo: `rose F0AFBE`, `butter EDDC98`, `sage B6D6AB`, `sky A2C3E9`,
  `lilac D9AFE6`. La cuarta cápsula de la última fila, en crema `FBF8F3`.
- Las filas pares (segunda y cuarta) giran cada cápsula 8 grados en sentido horario sobre su centro, y
  las impares 8 en sentido contrario: el zigzag del punto de media, sin dibujar hilo.

Android adaptativo (lienzo de 108 dp): capa de fondo de color `221E17`; capa frontal con las cápsulas
escaladas al 61 % y centradas (quedan dentro del círculo seguro de 66 dp). Capa `monochrome`: las mismas
cápsulas en un solo color blanco sobre transparente, sin la crema diferenciada.

Icono de notificación (`ic_notification.xml`, 24x24, blanco sobre transparente): dos filas de dos
cápsulas de 8x4 con radio 2, en (3, 8), (13, 8), (3, 13) y (13, 13), con el mismo giro alterno.

Se genera todo con `tools/generate_icons.py` (1024 para iOS, adaptativo, cinco densidades heredadas,
`monochrome` y notificación) y se compara a 48 px sobre lanzador claro y oscuro antes de darlo por
bueno.

**Arranque**: Android, `windowBackground` crema `FBF8F3` o `17150F` según el tema del sistema
(`docs/tecnico.md` 8.1); iOS, el de la plantilla de las hermanas.

---

## 14. Accesibilidad

- Cada `GlyphButton` lleva su descripción (sección 2). La fila de apoyo lee la racha y el contador
  como texto.
- Celdas de la rejilla: `a11yDay(fecha, estado)` (17 de enero, escrita / sin escribir), en el orden
  de las fechas. Los días futuros y los que no existen no son nodos.
- Bloques de años anteriores: un nodo por bloque que lee la etiqueta, el texto y `a11yOpenDay`
  (Abrir este día).
- Orden de lectura en Hoy: fecha, hito, campo, racha, contador, foto, aviso, años anteriores.
- Texto grande (200 %): todo el texto crece con `sp` y hace salto de línea; la cabecera parte la fecha
  en dos líneas; la rejilla no crece (medidas en `dp`) y la búsqueda y los resultados cubren la
  lectura. Probado en #9, #11 y #12.
- Contraste: `onSurfaceVariant` sobre `background` es el de las hermanas; el texto del usuario siempre
  en `onBackground`.

---

## 15. v1.1 y v1.2

### 15.1 Libro en PDF (v1.1)

A5, 420x595 pt, márgenes de 42 pt.

- Portada: fondo del color de la portada al 100 %; "Purl" en Literata 44 pt `Ink` centrado a 220 pt
  de arriba; debajo, a 30, el rango de años (`bookYears`: 2026 a 2030) en 16 pt `Ink` al 70 %.
- Página de día: la fecha (`longDate`, sin año) en sistema 11 pt Medium mayúsculas `Muted` arriba a la
  izquierda; una línea de 0,5 pt `Empty` debajo. Bloques por año, **de más antiguo a más reciente**:
  el año en 9 pt Medium `Muted`, 4, el texto en Literata 12 pt con interlineado 16, `Ink`; si tiene
  foto, una miniatura de 96x72 pt con radio 6 a la derecha del texto (el texto se estrecha). 14 pt
  entre bloques. Si un día no cabe en una página, sigue en la siguiente con la fecha repetida.
- Pie: número de página en 8 pt `Muted` centrado a 20 pt del borde inferior.
- Progreso: diálogo con `bookMaking(n, total)` (Maquetando el libro: 120 de 366) y `cancel`.

### 15.2 Etiquetas (v1.1)

Bajo la fila de apoyo, en Hoy y en el día abierto: chips de 32 de alto, radio 16, borde 1 dp, texto
13 sp con `#`. El último chip es `addTag` (+ etiqueta), que abre un campo en línea. Con el campo
abierto, debajo, las sugerencias como chips al 60 %.

### 15.3 Pregunta del día (v1.1)

Con `questionsOn` y el campo de hoy vacío, el texto de ayuda es la pregunta del día en lugar de
`todayPlaceholder`.

### 15.4 Recapitulaciones (v1.2)

- Mes: tocar la inicial de un mes en la rejilla abre una pantalla superpuesta: el mes y el año en
  `Title`, `recapMonthStats(días, fotos)` en `Secondary`, y la lista de sus líneas en orden (fecha en
  `Eyebrow`, texto en `UserSmall`).
- Año: tocar el año del selector abre `recapYearTitle(año)` (Tu 2027) con días escritos, racha más
  larga, fotos, la rejilla y la lista de meses; tocar un mes abre su recapitulación.
- Sin Pro, tocar abre el `ProDialog`.

### 15.5 Ánimo (v1.2)

Con `moodOn`, bajo la fila de apoyo: cinco círculos de 28 con los colores de MoodTraker, separados 12;
el elegido lleva anillo `onBackground`. En Año, encima de la rejilla, los mismos cinco como filtro:
con uno elegido, solo se llenan las celdas de ese ánimo y con su color.

### 15.6 Varias fotos (v1.2)

Carrusel horizontal de fotos 4:3 al 85 % del ancho, separadas 8, con `PHOTO` al final mientras haya
hueco (4 con Pro).
