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
| `background` | `FBF8F3` | `17150F` | lienzo de todas las pantallas; fondo del botón de foto; texto del botón principal |
| `onBackground` | `39352E` | `ECE5D9` | texto principal y el del usuario; fondo del botón principal |
| `surface` | `FFFFFF` | `201D16` | diálogos, filas de Ajustes |
| `surfaceVariant` | `F0EBE2` | `2C2820` | avisos, buscador, botón del año, botón tonal |
| `onSurfaceVariant` | `8B8479` | `9C9486` | texto secundario, iconos, etiquetas |
| `outline` | `E3DCD1` | `3A352B` | bordes de 1 dp, días sin escribir de la rejilla |
| `outlineVariant` | `EFE9DF` | `2C2820` | separadores, la línea entre filas de Ajustes |
| `primary` | `6FAE9B` | `8FC9B6` | acción de texto, interruptores encendidos, cursor, viñetas del `ProDialog` |
| `onPrimary` | `FFFFFF` | `12271F` | texto sobre `primary` |

`error` no se usa en ninguna pantalla: ni un aviso ni un borrado se pintan en rojo ni en su pariente. Por si
un componente de Material lo busca solo, `error` vale lo mismo que `onSurfaceVariant`.

**Portada.** La portada elegida (`docs/tecnico.md` sección 5) es el color del diario, y se ve: la
página de Hoy y del día abierto, los puntos (1.5) de la rejilla, de los años en Hoy y del hito, los
resultados de búsqueda, las dos tarjetas y los widgets. Así elegir portada cambia algo que se mira
cada día. El acento de las acciones sigue siendo `primary`, y la acción principal va en tinta (1.3).
En oscuro, el mismo hex.

- `coverWash(portada)`: la portada mezclada con `background`, al 30 % en claro y al 16 % en oscuro,
  donde un pastel entero brillaría. Es el fondo de la página y de los resultados.
- `OnCover` `39352E`: los glifos que van sobre un pastel (la marca de la portada elegida, el
  candado), en los dos temas. La tinta clara del oscuro se perdería encima.

### 1.2 Tipografía

| Estilo | Fuente | Tamaño / interlineado | Peso | Color | Dónde |
|---|---|---|---|---|---|
| `Display` | Literata | 44 / 48 sp | Regular | `onBackground` | el día del mes en Hoy y en el día abierto, el año en Año |
| `Heading` | Literata | 28 / 34 sp | Regular | `onBackground` | título de Ajustes y del `ProDialog`; a 32 / 40, "Purl" en el bloqueo |
| `UserLarge` | Literata | 22 / 32 sp | Regular | `onBackground` | el campo de Hoy y del día abierto |
| `UserMedium` | Literata | 18 / 27 sp | Regular | `onBackground` | bloques de años anteriores y ecos |
| `UserSmall` | Literata | 16 / 24 sp | Regular | `onBackground` | resultados de búsqueda |
| `Title` | sistema | 20 / 26 sp | Medium | `onBackground` | el mes en la cabecera del día, títulos de diálogos |
| `Body` | sistema | 15 / 22 sp | Normal | `onBackground` | filas de Ajustes, avisos, diálogos, hito |
| `Secondary` | sistema | 13 / 18 sp | Normal | `onSurfaceVariant` | subtítulos, racha, contador, cuánto hace de un recuerdo, vuelta de la página |
| `Eyebrow` | sistema | 11 / 14 sp | Medium, 1,4 sp de espaciado | `onSurfaceVariant` | día de la semana, etiquetas de sección, en mayúsculas con `uppercase()` |
| `Action` | sistema | 15 / 20 sp | Medium | `primary` | botones de texto; en `onBackground`, el año de un recuerdo y el del botón del año; en `background`, el botón principal |

Literata Regular es la única fuente empaquetada. No hay cursiva ni negrita del usuario. Literata es
para lo que escribe el usuario y para el número o el nombre que encabeza cada página (`Display`,
`Heading`); todo lo que lo rodea es la fuente del sistema, más pequeña.

En código, `Styles` de `ui/theme/Theme.kt` con el nombre en minúscula inicial: `Styles.userLarge`,
`Styles.eyebrow`. Cada estilo lleva ya su color.

### 1.3 Medidas

| Qué | Valor |
|---|---|
| Espaciado | 4, 8, 12, 16, 20, 24, 28, 32, 36, 48 |
| Margen lateral de pantalla | 24 |
| Columna de contenido | ancho máximo 600, centrada (iPad y horizontal) |
| Radios (`SoftShapes`) | 8, 12, 18, 24, 32 |
| Bordes | 1 dp, `outline`. Sin sombras |
| Zona táctil mínima | 48x48 (`GlyphButton` de 48 con el icono a 20). El botón del año y el de foto se ven más bajos y reservan 48 con `minimumInteractiveComponentSize` |
| Cabecera | Hoy y el día abierto: la cabecera del día (4.1). Año, día abierto, Ajustes y Compartir: 56 de alto con los glifos, sin barra ni fondo; el glifo de la izquierda se corre 12 para que su trazo caiga en el margen |
| Página (`Page`) | ancho de la columna, radio 24, fondo `coverWash`, relleno 20 a la izquierda, 12 a la derecha y 18 arriba. Con el teclado arriba, borde de 1,5 dp del color de la portada: dónde se escribe no ofrece duda |
| Botón principal (`PillAction`) | 48 de alto, cápsula, fondo `onBackground`, texto `Action` en `background`, relleno lateral 22. Uno por sitio: la acción que ese sitio pide. Desactivado, al 50 % |
| Botón tonal (`PillAction`, `tonal`) | igual, fondo `surfaceVariant` y texto `onBackground`. La segunda acción cuando las dos pesan |
| Botón de texto | 40 de alto, 16 de relleno lateral, sin fondo, `Action` |
| Tarjeta de Ajustes | radio 18; filas en `surface` separadas 1 dp, que deja ver `outlineVariant` y dibuja la línea |
| Foto | ancho de la columna (o de la página), proporción 4:3 recortada al centro, radio 18 |

### 1.4 Movimiento

Una sola animación propia: un fundido de 180 ms (`Crossfade`) al cambiar entre Hoy, Año y Ajustes, y
al abrir o cerrar el día y la pantalla de compartir. Nunca un deslizamiento: un fundido es lo que
pide la opción de reducir movimiento, así que sirve a todo el mundo sin preguntar. El bloqueo sale
sin fundido, porque tiene que tapar en el acto. Del sistema: el teclado, los diálogos, el
interruptor, el selector de hora y el diálogo de biometría.

### 1.5 El punto

La firma de Purl es el punto del icono (13): una cápsula el doble de ancha que de alta, girada 8
grados. Donde hay filas, las filas se alternan como las vueltas del revés del icono: la primera gira
en sentido contrario al reloj y la siguiente en el del reloj (`stitchTilt(fila)`). Cada fila es una
vuelta de algo: los días en la rejilla del año, los meses en la tarjeta y en el widget, los años en
Hoy.

- Lleno del color de la portada: un día escrito, un recuerdo, el hito.
- Contorno: un día sin escribir (1 dp `outline`; al 40 % si es futuro). En Hoy sin años anteriores,
  un punto vacío en `onSurfaceVariant` es la página que todavía no ha vuelto.
- `StitchMark`: un punto suelto en una caja de 20x12, cápsula de 17,6 de ancho y 0,52 de alto.
- `Swatch`: filas de puntos con las proporciones del icono: alto 0,545 del ancho, hueco 0,18 del
  ancho, paso de fila 0,91 del ancho. Un nulo es un punto sin hacer, en contorno de 1,5.
- Código: `stitch()`, `stitchTilt()`, `StitchMark` y `Swatch` en `ui/Icons.kt`; las tarjetas y los
  widgets dibujan la misma cápsula con sus propias herramientas.

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
| `YEAR` | una muestra de la rejilla: tres filas de dos puntos (1.5) llenos de 0.34 x 0.17, centros en x de {0.31, 0.69} e y de {0.26, 0.50, 0.74}, girados según su fila | `a11yYear` (El año), en el botón del año |
| `CLOSE` | (0.24, 0.24) (0.76, 0.76); y (0.76, 0.24) (0.24, 0.76) | `a11yClose` (Cerrar) |
| `PHOTO` | rectángulo de (0.14, 0.24) a (0.86, 0.80) con radio 0.08; círculo lleno de radio 0.07 en (0.36, 0.43); línea (0.14, 0.72) (0.40, 0.52) (0.58, 0.66) (0.70, 0.57) (0.86, 0.70) | `a11yPhoto` (Añadir foto) |
| `TRASH` | tapa (0.18, 0.28) (0.82, 0.28); asa (0.40, 0.28) (0.40, 0.18) (0.60, 0.18) (0.60, 0.28); cuerpo (0.26, 0.28) (0.31, 0.84) (0.69, 0.84) (0.74, 0.28) | `a11yDelete` (Borrar este día) |
| `SEARCH` | círculo de radio 0.24 en (0.44, 0.44); línea (0.62, 0.62) (0.84, 0.84) | ninguno (decorativo, dentro del campo) |
| `CHECK` | (0.22, 0.52) (0.42, 0.72) (0.78, 0.30) | ninguno (el estado lo dice la fila) |
| `LOCK` | cuerpo: rectángulo de (0.22, 0.44) a (0.78, 0.86) con radio 0.08; asa: (0.34, 0.44) (0.34, 0.32), medio círculo por arriba de radio 0.16 con centro en (0.50, 0.32), y (0.66, 0.32) (0.66, 0.44) | ninguno (lo dicen la portada o el botón) |

`BACK` en la selección de año se describe como `a11yPreviousYear` (Año anterior).

---

## 3. Estructura

```
App
+-- Today (raíz)
|   +-- Year          (botón del año)
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
|                                                |
|  17  SÁBADO                   [YEAR 2027] [S]  |  cabecera del día
|      Enero                                     |
|                                                |
|  (=) Tu línea número 30.                       |  hito (si hay)
|                                                |
|  +------------------------------------------+  |  página, en el color
|  | Lo que quieras recordar de hoy           |  |  de la portada
|  |                                          |  |  campo, UserLarge
|  | +--------------------------------------+ |  |
|  | |  foto de hoy 4:3 (si hay)            | |  |
|  | +--------------------------------------+ |  |
|  | 5 días seguidos     [PHOTO Añadir foto]  |  |  fila de apoyo
|  +------------------------------------------+  |
|                                                |
|  +------------------------------------------+  |  aviso (si hay)
|  | Hace un mes que escribes. ¿Guardas una   |  |
|  | copia?        Ahora no  ( Hacer copia )  |  |
|  +------------------------------------------+  |
|                                                |
|  (=) 2026  hace un año                         |  punto, año, cuánto hace
|      Mismo día, sol. Cambio de piso            |  UserMedium, sangría 30
|      confirmado.                               |
|      +-------------------------------------+   |
|      |  foto 4:3                           |   |
|      +-------------------------------------+   |
|                                                |
|  (=) 2025  hace 2 años                         |  el punto gira al revés
|      Primer día de vacaciones, llovió...       |
+------------------------------------------------+
```

`(=)` es un punto (1.5). Todo en una `Column` con `verticalScroll` e `imePadding()`, margen lateral 24.

- **Cabecera del día** (`DayHeader`): 16 bajo la barra de estado. El día del mes en `Display`, que
  es lo único que comparten todos los recuerdos de debajo; a 12, en columna, el día de la semana en
  `Eyebrow` y el mes (`monthNames`, con mayúscula inicial) en `Title`. A la derecha, el botón del
  año y `GlyphButton(SETTINGS)`.
- **Botón del año**: cápsula `surfaceVariant`, relleno 12 a la izquierda, 14 a la derecha y 8
  arriba y abajo; `YEAR` de 18 y, a 6, el año en curso en `Action` color `onBackground`. Dice
  adónde lleva mejor que un glifo solo.
- **Hito**: 16 debajo. `StitchMark` de la portada y, a 10, el texto (`Body`). Solo uno
  (`docs/tecnico.md` 6.3). Claves: `milestoneFirst` (Tu primera línea.), sin plazo porque la vuelta ya la dice `returnsOn` debajo, también un 29 de febrero;
  `milestoneThirty` (Tu línea número 30.), `milestoneHundred` (Cien líneas.), `milestoneAnniversary`
  (Hoy hace un año que empezaste este diario.), `milestoneThreeYears` (Hoy tienes tres años en la
  misma página.).
- **Página** (`Page`, 1.3): 20 debajo. Dentro, el campo (`LineField`): sin borde ni fondo propios,
  `UserLarge`, cursor `primary`, altura mínima 96. Texto de ayuda `todayPlaceholder` (Lo que
  quieras recordar de hoy) en `onSurfaceVariant`. Teclado con `ImeAction.Done` (cierra el teclado),
  mayúscula inicial de frase.
- **Foto de hoy**: dentro de la página, 12 bajo el campo; tocarla abre el día de hoy en `DaySheet`.
- **Fila de apoyo**: al pie de la página, 56 de alto. A la izquierda, sin teclado, la racha
  `streakDays(n)` (5 días seguidos) en `Secondary`, solo si `n >= 2`; con el teclado arriba, en su
  lugar, el contador `counter(n, 280)` (263/280) en `Secondary` desde 250 puntos de código. No caben
  los dos, y escribiendo solo importa el contador. A la derecha, si hoy no tiene foto, el botón de
  foto: cápsula en `background`, relleno 10 a la izquierda, 14 a la derecha y 7 arriba y abajo;
  `PHOTO` de 18 y, a 6, `a11yPhoto` (Añadir foto) en `Secondary` color `onBackground`. Sin fotos
  libres (cuarta entrada con foto sin Pro), el glifo es `LOCK` y el toque abre el `ProDialog`.
- **Aviso**: 16 debajo. Caja `surfaceVariant`, radio 24, relleno 20; texto `Body`; 16 más abajo, las
  acciones a la derecha, separadas 8: la última, la que el aviso pide, es el botón principal; las
  otras, botones de texto. Uno solo, por prioridad (4.3).
- **Años anteriores**: 36 debajo. Por cada año, del más reciente al más antiguo, separados 28: un
  `StitchMark` de la portada girado según su fila (el año más reciente, fila 0), a 10 el año en
  `Action` color `onBackground` y a 8 `yearsAgo(n)` (hace un año) en `Secondary`; 6 debajo, con
  sangría de 30, el texto en `UserMedium` completo (sin recortar) y la foto 12 debajo si la tiene.
  Tocar el bloque abre ese día en `DaySheet`.
- **Sin años anteriores**: en su lugar, los ecos y la vuelta (4.2, estado E).

### 4.2 Estados

| Estado | Qué cambia |
|---|---|
| A. Primera vez (diario vacío) | Foco en el campo y teclado arriba: la página con su borde. 12 bajo la página, en `Secondary`: `firstHelp` (Una línea al día. Dentro de un año, este mismo día, volverás a leerla.). Sin racha, sin años |
| B. Hoy sin escribir | Foco en el campo y teclado arriba al abrir |
| C. Escribiendo | Borde de la página. Contador desde 250 en lugar de la racha. Al llegar a 280, el campo no admite más (`docs/tecnico.md` 6.4) |
| D. Hoy escrito, con años anteriores | Sin foco ni teclado: se abre leyendo. Tocar el texto lo edita |
| E. Sin años anteriores | Bloques de eco, cada uno solo si existe, con la forma de los de años: `echoWeek` (Hace una semana) o `echoMonth` (Hace un mes) donde va el año, y la fecha corta (`shortDate`: 10 de enero) donde va `yearsAgo`. Debajo, a 28, un punto vacío en `onSurfaceVariant` y, a 10, en `Secondary`: `returnsOn(fecha)` (Esta página volverá el 17 de enero de 2028.) y `dayNumber(n)` (Día 12 de tu diario.) |
| F. Con hito | La línea del hito (4.1) |
| G. Guardado fallido | Aviso `noticeSaveFailed` (No se ha podido guardar. Lo intento otra vez con tu próximo cambio.), sin acciones; desaparece con el primer guardado bueno |
| H. Diario dañado | Aviso `noticeCorrupt` (No se ha podido leer el diario. Los ficheros se han guardado aparte y no se ha borrado nada.) con `ok` (Vale) como botón principal |
| I. Oferta de recordatorio | Tras guardar la primera línea, una vez: `offerReminder` (¿Te lo recuerdo cada día a las 21:00?) con `notNow` (Ahora no) y `yes` (Sí), el principal. `yes` enciende el recordatorio y pide el permiso |
| J. Aviso de copia | `noticeBackup` (Hace un mes que escribes. ¿Guardas una copia fuera del teléfono?) con `notNow` y `makeBackup` (Hacer copia), el principal |
| K. Recapitulación (v1.2) | Del 26 de diciembre al 7 de enero, línea `recapYearOffer(año)` (Tu 2027 en Purl) en `Action`, bajo la página |

### 4.3 Prioridad de avisos

H, G, I, J. Se enseña el primero que aplique; el siguiente aparece cuando el anterior se va.

---

## 5. Año

```
+------------------------------------------------+
|  [<]                                  [SHARE]  |  cabecera
|                                                |
|  2027                             [<]   [>]    |  Display y flechas
|  212 líneas                                    |  Secondary
|                                                |
|  +------------------------------------------+  |
|  | (o) Buscar en el diario              [x] |  |  buscador
|  +------------------------------------------+  |
|                                                |
|     E  F  M  A  M  J  J  A  S  O  N  D         |
|   1 /  /  /  /  /  /  /  /  /  .  .  .         |  un punto por día; cada
|     \  \  \  o  \  \  \  \  \  .  .  .         |  fila gira al revés que
|     ...                                        |  la anterior
|  30 /     /  o  /     /  o     .     .         |
|     \     o     \     \  \     .     .         |
|                                                |
|  12 RESULTADOS                                 |  con búsqueda
|  +------------------------------------------+  |
|  | 17 DE ENERO DE 2027                      |  |
|  | Mismo día, sol. Cambio de piso...        |  |
|  +------------------------------------------+  |
+------------------------------------------------+
```

`/` y `\` son puntos llenos, `o` sin escribir y `.` futuros.

- **Cabecera**: `GlyphButton(BACK)` a Hoy; `GlyphButton(SHARE)` abre la tarjeta del año (si el año
  tiene al menos una línea; si no, no se pinta).
- **Año**: el año en `Display` y debajo `cardLines(n)` (212 líneas) en `Secondary`, o `yearEmpty`
  con el diario vacío; es un encabezado. A la derecha, `GlyphButton(BACK)` y `GlyphButton(FORWARD)`,
  del año de la entrada más antigua al actual; en los extremos la flecha se pinta al 30 % y no
  responde. Se abre en el año actual.
- **Buscador**: 20 debajo. 48 de alto, radio 24, fondo `surfaceVariant`, `SEARCH` a la izquierda,
  texto `Body`, ayuda `searchPlaceholder` (Buscar en el diario), `CLOSE` a la derecha cuando hay texto
  (vacía la búsqueda). Buscar no cambia de año. Una sola línea: los resultados salen mientras se
  escribe, y la tecla de buscar del teclado solo lo esconde, para ver la lista entera.
- **Rejilla** (`YearGrid`, en `Canvas`): 24 debajo. Una muestra de punto: 12 columnas (meses) por 31
  filas (días), un punto (1.5) por día. Encima, las iniciales de los meses (`monthInitials`) en
  `Eyebrow`; a la izquierda, 20 de ancho, los números 1, 10, 20 y 30 en `Eyebrow`, centrados en su
  fila. Ancho del punto: mínimo de 24 y `(ancho - 20 - 11 x 4) / 12`; alto, la mitad; 4 entre
  columnas; paso de fila, 0,8 del ancho, más apretado que las columnas como en un tejido. Cada fila
  gira al revés que la anterior (el día 1, fila 0). Con un teléfono normal el año entero cabe casi
  sin desplazar.
  - Día con línea: lleno del color de la portada.
  - Día sin línea: contorno de 1 dp `outline`.
  - Día futuro: contorno `outline` al 40 %, no responde.
  - Día que no existe (30 y 31 de febrero, 29 en año no bisiesto, 31 de abril, junio, septiembre y
    noviembre): no se pinta.
  - Hoy: anillo de 1,5 dp `onBackground`, un punto 2,5 más grande por cada lado y con el mismo giro.
  - Con búsqueda: los días llenos que no casan bajan al 25 %.
  - Zona táctil: el ancho del punto por el paso de fila, exacta. Aquí no se estira a 48 como
    cualquier objetivo pequeño: los días se tocan, y estirados cada uno taparía la mitad del de
    arriba, así que el lector de pantalla nombraría el día de debajo del dedo. Tocar un día pasado o
    de hoy abre `DaySheet`. Pulsación larga (v1.1): 6.2.
- **Resultados** (con búsqueda): 24 bajo la rejilla, `resultsCount(n)` (12 resultados) en `Eyebrow`
  y la lista de todos los años, más reciente primero, cada uno en una caja `coverWash` de radio 18 y
  relleno 16, separadas 12: la fecha larga con año en `Eyebrow`, 4, el texto en `UserSmall` hasta 3
  líneas con `...`. Tocar abre `DaySheet`.

| Estado | Qué se ve |
|---|---|
| Diario vacío | La rejilla sin un punto lleno y `yearEmpty` (Tu año se irá llenando línea a línea.) bajo el año, en lugar del recuento |
| Búsqueda sin resultados | `noResults` (Nada con esas palabras.) en `Secondary` |
| Año pasado | Sin anillo de hoy, sin días futuros |

---

## 6. Día abierto (`DaySheet`)

```
+------------------------------------------------+
|  [x]                          [SHARE] [TRASH]  |
|                                                |
|  17  MARTES                                    |  cabecera del día, con año
|      Enero de 2026                             |
|                                                |
|  +------------------------------------------+  |  página
|  | Mismo día, sol. Cambio de piso           |  |  LineField
|  | confirmado.                              |  |
|  | +--------------------------------------+ |  |
|  | |  foto 4:3                            | |  |
|  | +--------------------------------------+ |  |
|  |            Cambiar foto   Quitar foto    |  |
|  | 263/280                                  |  |  fila de apoyo
|  +------------------------------------------+  |
+------------------------------------------------+
```

Pantalla completa sobre la actual, fondo `background`, mismo `verticalScroll` más `imePadding()`.

- `CLOSE` guarda (`flush`) y cierra.
- `SHARE` solo si el día tiene texto: abre la tarjeta de una línea.
- `TRASH` solo si el día tiene entrada: confirmación (9.1).
- Cabecera: la de Hoy (`DayHeader`), 8 bajo la fila de glifos, con `monthYear` (Enero de 2026) en
  lugar del mes solo. La página y el campo son los de Hoy, borde incluido.
- Sin texto al abrir: foco y teclado arriba. Con texto: sin foco.
- Foto: dentro de la página, 12 bajo el campo; debajo, a la derecha, `changePhoto` (Cambiar foto) y
  `removePhoto` (Quitar foto). Sin foto, el botón de foto de Hoy en la fila de apoyo; cuarta entrada
  con foto sin Pro: `LOCK` y el `ProDialog`.
- Fila de apoyo: el contador desde 250 a la izquierda. Sin racha: la racha es de hoy.

### 6.2 Pulsación larga en la rejilla (v1.1)

Burbuja sobre el punto: fondo `surface`, borde 1 dp, radio 12, relleno 12, ancho máximo 240; la
fecha corta en `Eyebrow` y los primeros 60 puntos de código en `UserSmall` con `...` si se cortan.
Se va al soltar. Encima del punto, centrada sobre él y dentro del ancho de la rejilla; debajo de su
fila solo si arriba no cabe (las primeras filas). Solo en un día escrito: la pulsación larga en un hueco no hace
nada, y un día con foto y sin texto enseña solo la fecha.

---

## 7. Ajustes

```
+------------------------------------------------+
|  [<]                                           |
|  Ajustes                                       |  Heading
|                                                |
|  RECORDATORIO                                  |
|  +------------------------------------------+  |
|  | Recordatorio diario                 [ON] |  |  tarjeta de filas
|  | A las 21:00                              |  |
|  +------------------------------------------+  |
|                                                |
|  PRIVACIDAD                                    |
|  +------------------------------------------+  |
|  | Bloquear el diario                 [OFF] |  |
|  | Pide tu cara, tu huella o el código      |  |
|  +------------------------------------------+  |
|                                                |
|  PORTADA                                       |
|  +------------------------------------------+  |
|  |  [#]      [#]      [#]      [v]          |  |  cuadernos, 2 x 4
|  |  [#]      [#]      [#]      [#]          |  |
|  |  Salvia es gratis; las demás, con Pro.   |  |
|  +------------------------------------------+  |
|                                                |
|  COPIA                                         |
|  +------------------------------------------+  |
|  | Exportar copia                           |  |
|  | Última copia: 17 ene 2027                |  |
|  |------------------------------------------|  |
|  | Importar copia                           |  |
|  | Se junta con tu diario, sin borrar nada  |  |
|  +------------------------------------------+  |
|                                                |
|  PURL PRO, MÁS APPS, ACERCA DE: igual          |
+------------------------------------------------+
```

Cabecera con `BACK` y, debajo, el título `settingsTitle` (Ajustes) en `Heading`, que es un
encabezado. Cada sección: la etiqueta en `Eyebrow` con 4 de sangría, 28 entre secciones y 10 bajo
la etiqueta, y una tarjeta de Ajustes (1.3). Cada fila: 60 de alto mínimo, 16 de relleno lateral,
título en `Body`, subtítulo en `Secondary`, interruptor a la derecha (`Switch` de Material3 con
`primary`); toda la fila responde. Una fila que no responde baja su texto al 40 %, no la tarjeta.

| Fila | Estados |
|---|---|
| `reminderRow` (Recordatorio diario) | Apagado: subtítulo `reminderOff` (Apagado). Encendido: `reminderAt(hora)` (A las 21:00); tocar la fila abre el `TimePicker` de Material3 en un diálogo con `ok` y `cancel`. Permiso denegado: el interruptor vuelve a apagado y el subtítulo pasa a `reminderDenied` (Las notificaciones de Purl están desactivadas en el sistema.) con la acción `openSystemSettings` (Abrir ajustes) |
| `lockRow` (Bloquear el diario) | Disponible: subtítulo `lockSubtitle` (Pide tu cara, tu huella o el código del teléfono). No disponible: interruptor desactivado y `lockUnavailable` (Pon un bloqueo de pantalla en el teléfono para usarlo.); se vuelve a mirar cada vez que la app vuelve a primer plano, así que al volver con el bloqueo puesto la fila ya está disponible. Encender pide autenticar; si falla, sigue apagado |
| Portada | Los ocho pastels como los cuadernos que colorean: dos filas de cuatro repartidas a lo ancho, en la tarjeta con relleno 16. Ancho: mínimo de 72 y `(ancho - 3 x 12) / 4`; proporción 3:4; radio 4 en el lomo y 10 en el canto; lomo de 7 a la izquierda en `OnCover` al 8 %. La elegida lleva borde de 2 `onBackground` y `CHECK` de 22 en `OnCover`. Sin Pro, las siete de pago llevan `LOCK` de 18 en `OnCover` al 50 %, y debajo, a 12, `coverProHint` (Salvia es gratis; las demás, con Purl Pro.); tocar una Pro abre el `ProDialog`. Cada cuaderno se describe con su nombre (`coverName(id)`) y "elegida" (`a11ySelected`) |
| `exportRow` (Exportar copia) | Con entradas: `lastBackup(fecha)` (Última copia: 17 ene 2027) o `lastBackupNever` (Todavía ninguna copia). Sin entradas: fila al 40 %, no responde, `exportNothing` (Aún no hay nada que copiar.) |
| `importRow` (Importar copia) | Subtítulo `importSubtitle` (Se junta con tu diario, sin borrar nada) |
| `proRow` (Purl Pro) | Sin Pro: `proSubtitle` (Fotos, portadas y widgets. Pago único); abre el `ProDialog`. Con Pro: `proOwned` (Comprado. Gracias.), no responde |
| `restoreRow` (Restaurar compra) | Siempre. Al terminar, `restoreDone` (Compra restaurada.) o `restoreNothing` (No hay ninguna compra que restaurar.) en un diálogo de un botón |
| Más apps | Solo si hay filas (`docs/tecnico.md`, `SIBLINGS`). Título el nombre de la app, subtítulo su lema (`siblingQuilt`, `siblingMood`); abre la tienda |
| `privacyRow` (Política de privacidad) | Abre `PRIVACY_URL` |
| Versión | `version(v)` (Versión 1.0), no responde |

v1.1 añade en COPIA la fila `importMoodRow` (Importar de MoodTraker) y una sección `sectionWriting`
(ESCRITURA), entre RECORDATORIO y PRIVACIDAD, con el interruptor `questionsRow` (Una pregunta cuando el día está en blanco). v1.2 añade
ahí `moodRow` (Anotar el ánimo).

---

## 8. Bloqueo

```
+------------------------------------------------+
|                                                |
|                  (= = = =)                     |  el icono: cinco filas
|                  (= = = =)                     |  de cuatro puntos
|                  (= = = =)                     |
|                  (= = = =)                     |
|                  (= = = o)                     |
|                                                |
|                    Purl                        |  Heading a 32 / 40
|                                                |
|              ( Desbloquear )                   |  botón principal
|                                                |
+------------------------------------------------+
```

Fondo `background`, centrado, sin nada más. Arriba, el icono como `Swatch` de puntos de 22: filas
`rose`, `butter`, `sage`, `sky` y `lilac`, y el último punto sin hacer, el de hoy. 28 debajo,
"Purl" en `Heading` a 32 / 40; 32 debajo, `unlock` (Desbloquear) como botón principal. Al aparecer
quita el foco del campo de hoy: si no, el teclado vuelve a subir encima del bloqueo al volver de
segundo plano. El diálogo del sistema salta al aparecer; si se cancela, queda el botón. Textos del
diálogo del sistema: `lockPromptTitle` (Abrir Purl) y `lockPromptSubtitle` (Tu diario está
bloqueado).

La vista de multitarea en iOS es solo el color `background`, sin texto.

---

## 9. Diálogos

`AlertDialog` de Material3, fondo `surface`, radio 32, título `Title`, texto `Body`, botones `Action`.
Ningún botón destructivo en rojo. El `ProDialog` es el único propio (9.4).

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
              (= = = =)             las ocho portadas,
              (= = = =)             dos filas de puntos
               Purl Pro             Heading
      Pago único, sin suscripción.
 (=) Fotos en todas tus entradas
 (=) Siete portadas más
 (=) El widget del año
 (=) El widget de la pantalla de bloqueo    (solo iOS)

 (      Comprar por 5,99 EUR          )     botón principal, a lo ancho
         Restaurar     Ahora no
```

- `Dialog` propio, no `AlertDialog`: ancho máximo 400, fondo `surface`, radio 32, relleno 24,
  contenido centrado.
- Arriba, un `Swatch` de puntos de 28 con las ocho portadas en dos filas de cuatro: lo que se
  compra, antes de leerlo. 20 debajo, `proTitle` (Purl Pro) en `Heading`; 4, `proOnce` (Pago
  único, sin suscripción.) en `Secondary`; 20, las líneas `proPhotos`, `proCovers`,
  `proYearWidget` y, en iOS, `proLockWidget`, en `Body`, cada una tras un `StitchMark` en `primary`
  girado según su fila, separadas 12.
- 24 debajo, `buy(price)` (Comprar por 5,99 EUR) como botón principal a lo ancho, con el precio que
  devuelve la tienda; 8 debajo, `restore` (Restaurar) y `notNow` (Ahora no) como botones de texto.
- Sin tienda: `storeUnavailable` (La tienda no está disponible ahora.) bajo las líneas en
  `Secondary`, y `buy` no se pinta: sin precio no hay oferta que hacer.
- Comprando: `buy` pasa a `working` y al 50 %. Éxito: se cierra. Cancelado: no pasa nada. Error:
  `buyFailed` (No se ha podido completar la compra.) bajo las líneas.
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
|    ( Compartir )    ( Guardar en fotos )      |  principal y tonal
+------------------------------------------------+
```

`share` (Compartir), el botón principal, abre la hoja del sistema; el tonal `saveToPhotos` (Guardar en fotos) guarda y responde con
`saved` (Guardada en tus fotos.) o `saveFailed` (No se ha podido guardar.). En Android 9 o anterior,
`saveToPhotos` no aparece.

### 10.1 Tarjeta del año (1080x1350 px)

Siempre en claro, como las hermanas: fondo `Cream FBF8F3`, tinta `Ink 39352E`, secundario
`Muted 8B8479`, hueco `Empty EDE7DC`.

Todas las medidas de 10.1 y 10.2 son píxeles de la tarjeta, sea cual sea la pantalla: se dibuja y
se mide el texto con densidad 1. Un `TextMeasurer` sacado de `rememberTextMeasurer()` trae la
densidad de la pantalla y pinta cada texto al doble o al triple.

| Elemento | Posición y tamaño |
|---|---|
| Año | x 108, línea base y 250; Literata 140 px, `Ink` |
| Líneas escritas | x 108, base y 330; sistema 42 px, `Muted`: `cardLines(n)` (212 líneas) |
| Racha más larga del año | x 108, base y 390; sistema 42 px, `Muted`: `cardLongestStreak(n)` (Racha más larga: 34 días) |
| Rejilla | 12 filas (meses) por 31 columnas (días), un punto (1.5) por día: 22 px de ancho y 12,1 de alto, en celdas de 22 con 6 de hueco; bloque de 862x330 que empieza en x 109, y 520. Cada mes gira al revés que el anterior (enero, fila 0), como las vueltas del icono. Lleno: portada. Sin escribir: `Empty`. Día futuro: `Empty` al 50 %. Día inexistente: nada |
| Iniciales de meses | no se pintan |
| Pie | "Purl" en Literata 52 px `Ink`, x 108, base y 1210; `cardTagline` (una línea al día) en sistema 34 px `Muted`, x 108, base y 1262 |
| Punto de portada | un punto de 44x24 px de la portada, centrado en x 954, y 1196, con el giro de la fila 0 |

La racha más larga del año es `longestStreak(j, año)`: la racha de días a tiempo más larga dentro del
año (`model/Insights.kt`).

### 10.2 Tarjeta de una línea (1080x1350 px)

| Elemento | Posición y tamaño |
|---|---|
| Punto de portada | un punto de 44x24 px, centro x 126, y 150, con el giro de la fila 0 |
| Fecha | x 168, base y 162, hasta x 972; sistema 34 px Medium, `Muted`, mayúsculas, 4 px de espaciado: `longDateWithYear`. La que no cabe (la portuguesa, de 35 letras) parte en una segunda línea, que aún queda por encima del texto |
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
| Android 2x2 (mínimo 110x110) e iOS `systemSmall` | Arriba, la fecha corta (`widgetDate`: SÁB 17 ENE) en 12 sp Medium `onSurfaceVariant`. En el centro, un punto (1.5) de 40 de ancho: lleno del color de la portada si está escrita, contorno de 2 `outline` si no. Debajo, `widgetWritten` (Escrita) o `widgetNotWritten` (Por escribir) en 15 sp Medium. Abajo, si hay recuerdo, un punto de 12 de la portada y `widgetMemory` (Hay recuerdo) en 12 sp |
| iOS `systemMedium` | La fecha larga a la izquierda (`longDate`, 17 sp Medium) y, a la derecha, el punto, el estado y el recuerdo en columna |
| iOS `accessoryCircular` (Pro) | Círculo lleno si está escrita, anillo si no; un punto en el centro si hay recuerdo. Redondo a propósito: en una esfera, una cápsula girada se lee como una mancha. Sin Pro: `Image(systemName: "lock")` |
| iOS `accessoryRectangular` (Pro) | Tres líneas: la fecha corta, el estado y, si hay, `widgetMemory`. Sin Pro: `proTitle` y `widgetUnlock` (Toca para activarlo) |

Tocar: Hoy. Accesorios sin Pro: el `ProDialog`.

### 11.2 Año (Pro)

| Tamaño | Contenido |
|---|---|
| Android 4x2 (mínimo 180x110) e iOS `systemMedium` | La rejilla de 12 filas por 31 columnas como la de la tarjeta: un punto por día, alto 0,55 del ancho, cada mes girado al revés que el anterior; llenando el ancho; encima, el año en 13 sp Medium y `yearCount` a la derecha en 12 sp |
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

Android adaptativo (lienzo de 108 dp): capa de fondo de color `221E17`; capa frontal con el bloque de
cápsulas centrado y ajustado a la diagonal del círculo seguro de 66 dp, que son 48,5 x 44,7 dp (el
91 % de su tamaño natural). El 61 % que decía antes dejaba el bloque en 32 dp dentro de 108: seguro,
pero diminuto al lado del de las hermanas. Capa `monochrome`: las mismas
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

- Cada `GlyphButton` lleva su descripción (sección 2); el botón del año, `a11yYear`; el de foto se
  lee por su texto. La fila de apoyo lee la racha y el contador como texto.
- Cabecera del día: un solo nodo, encabezado, que lee la fecha entera (`longDate` en Hoy,
  `longDateWithYear` en el día abierto). El año de Año y el título de Ajustes también son
  encabezados.
- Celdas de la rejilla: `a11yDay(fecha, estado)` (17 de enero, escrita / sin escribir), en el orden
  de las fechas. Los días futuros y los que no existen no son nodos.
- Bloques de años anteriores: un nodo por bloque que lee el año, cuánto hace, el texto y
  `a11yOpenDay` (Abrir este día).
- Orden de lectura en Hoy: fecha, año, ajustes, hito, campo, foto, racha o contador, botón de foto,
  aviso, años anteriores.
- Texto grande (200 %): todo el texto crece con `sp` y hace salto de línea; en la cabecera del día
  el mes parte en dos líneas; la rejilla no crece (medidas en `dp`) y la búsqueda y los resultados cubren la
  lectura. Probado en #9, #11 y #12.
- Contraste: `onSurfaceVariant` sobre `background` es el de las hermanas; el texto del usuario siempre
  en `onBackground`.

---

## 15. v1.1 y v1.2

### 15.1 Libro en PDF (v1.1)

A5, 420x595 pt, márgenes de 42 pt.

- Portada: fondo del color de la portada al 100 %; "Purl" en Literata 44 pt `Ink` centrado a 220 pt
  de arriba; debajo, a 30, el rango de años (`bookYears`: 2026 a 2030) en 16 pt `Ink` al 70 %. Con un
  solo año, el año solo (2026), no "2026 a 2026".
- Página de día: la fecha (`shortDate`: sin año ni día de la semana, que cambia de un año a otro) en sistema 11 pt Medium mayúsculas `Muted` arriba a la
  izquierda; una línea de 0,5 pt `Empty` debajo. Bloques por año, **de más antiguo a más reciente**:
  el año en 9 pt Medium `Muted`, 4, el texto en Literata 12 pt con interlineado 16, `Ink`; si tiene
  foto, una miniatura de 96x72 pt con radio 6 a la derecha del texto (el texto se estrecha). 14 pt
  entre bloques. Si un día no cabe en una página, sigue en la siguiente con la fecha repetida: el año
  que no cabe en lo que queda pasa entero a la siguiente, y solo se parte un año más largo que una
  página entera, que repite su año arriba de cada trozo.
- Pie: número de página en 8 pt `Muted` centrado a 20 pt del borde inferior. La portada es la página 1
  sin número, así que el número impreso coincide con el que enseña el visor.
- Progreso: diálogo con `bookMaking(n, total)` (Maquetando el libro: 120 de 366) y `cancel`.

### 15.2 Etiquetas (v1.1)

Dentro de la página (1.3), en Hoy y en el día abierto, 12 bajo el campo o la foto y encima de la
fila de apoyo: chips de 32 de alto, radio 16, fondo `background` y sin borde, como el botón de foto;
texto `Secondary` con `#`, en `onBackground` la etiqueta y en `onSurfaceVariant` el resto. El
último chip es `addTag` (+ etiqueta), que abre un campo en línea con borde de 1 dp `primary`. Con el campo
abierto, debajo, las sugerencias como chips al 60 %. Tocar una etiqueta la quita (`a11yRemoveTag`);
tocar una sugerencia la añade; Hecho en el teclado añade lo escrito y deja el campo abierto para la
siguiente, y salir del campo añade lo que hubiera. Con cinco (`TAGS_PER_ENTRY`) desaparecen `addTag` y
las sugerencias. La fila solo sale en un día con entrada: una etiqueta describe una línea, no hace
escrito un día, así que un día que se queda sin texto ni foto se va con sus etiquetas.

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
