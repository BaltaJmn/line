# Diario de una línea al día, spec de producto

App Compose Multiplatform (Android + iOS) que hereda el cuaderno de papel *One Line a Day: A
Five-Year Memory Book*: escribes una línea al día y, al abrir la app, lees lo que escribiste ese
mismo día en años anteriores.

Tercera de la familia. Hermana de **Quilt** (`../HabitTracker`, `com.baltajmn.habit`) y de
**MoodTraker** (`../MoodTraker`, `com.baltajmn.mood`): misma arquitectura, misma paleta, misma
promesa (sin cuenta, sin suscripción, tus datos son tuyos). Identificador previsto para las dos
tiendas: `com.baltajmn.line`.

La promesa en una frase: *treinta segundos al día hoy, cinco años de memoria mañana*.

---

## 1. Benchmark: qué copiar y dónde atacar

| App | Modelo | Lo que hace bien | Lo que le duele |
|---|---|---|---|
| **Day One** | Suscripción, sin pago único: Silver 49,99 $/año, Gold 74,99 $/año con IA | `On This Day` es su función más valorada; ecosistema completo iOS, Android, Mac y web | Precio alto y subiendo, y complejidad creciente con IA que este formato no necesita |
| **Diarium** | Pago único, 14,99 $ por plataforma (19,99 $ en Windows) | El único competidor grande sin suscripción: el modelo es exactamente el nuestro | Interfaz de utilidad, sin identidad de marca ni cuidado visual |
| **Journey** | Pago único 17,99 $ de una plataforma, o membresía 6,99 $/mes, 49,99 $/año, 199 $ de por vida | Vista de años anteriores y sincronización multiplataforma real | Cobra dos veces la misma promesa: el pago único no cubre lo que el usuario cree que compra |
| **Grid Diary** | Suscripción 2,99 $/mes o 22,99 $/año | Rejilla de preguntas bien diseñada | Migró de pago único a suscripción y se llevó las reseñas por delante |
| **Stoic** | Suscripción anual cara, con opción de por vida | Enfoque coherente, 4,8 en App Store | Precio de app de bienestar completa para un alcance estrecho; peor en Android (4,2) |
| **Daylio** (su parte de diario) | Suscripción 4,99 $/mes, 35,99 $/año, 59,99 $ de por vida | Registro de un toque, muy pegado al hábito | El texto es secundario y no hay vista de años anteriores. Y cobra la copia de seguridad |
| **Reflectly** | 9,99 $/mes o 59,99 $/año en iOS, 19,99 $/año en Android | Interfaz atractiva, onboarding cuidado | Precio distinto según tienda y quejas de cancelación: el peor historial de confianza de la categoría |
| **Everlog** | Suscripción 1,99 $/mes, 19,99 $/año o 49,99 $ de por vida | Diseño cuidado, 4,65 de nota | Solo iOS |
| **Dabble Me** | Suscripción 4 $/mes | Cero fricción: te llega un correo y la respuesta es la entrada | Sin app propia; depende de que mires el correo |
| **Penzu** | 4,99 $/mes, 19,99 $/año, Pro+ 49,99 $/año | Plataforma web madura | Precios inconsistentes entre web y tiendas, guardado poco fiable en móvil |
| **Journal de Apple** | Gratis, preinstalada desde iOS 17 | Cero fricción de adquisición, cifrada, sugerencias en el dispositivo | Solo iOS, sin vista de años anteriores, descrita en reseñas como muy básica |
| **Clones directos** (One Line A Day 365, DayGram, One Line Diary, OneLine, FiveYearJournal, One Line Life) | Mezcla: 365 Journal cobra 3,99 $/semana o 19,99 $/año; DayGram es pago único de 2,99 $ | Validan que el formato exacto tiene demanda | Apps de una sola persona, ejecución básica, ninguna domina la categoría |
| **One Line a Day - Simple Diary** (Google Play, el clon más visible en Android) | Gratis con anuncios, premium de pago | El formato exacto, en Android | **La copia de seguridad y la exportación son premium**, y el plan gratis lleva anuncios. Es la competencia directa en la plataforma donde Journal de Apple no existe, y cobra justo lo que nosotros regalamos |

### El cuaderno de papel

La serie de Chronicle Books lleva vendidos más de 3 millones de ejemplares según libreros y prensa
(consenso de fuentes secundarias, no hay comunicado oficial: trátese como estimación sólida). Funciona
porque el hueco de cinco líneas quita la presión de la página en blanco, y porque la recompensa
llega en el segundo año, cuando ya puedes comparar el mismo día.

- Lo que el papel hace mejor: es un objeto bonito en la mesilla, la cinta marcapáginas, y el gesto
  físico de pasar la página hacia atrás. Nada de eso se replica en digital, y conviene no fingirlo.
- Lo que le duele y una app arregla: no hay búsqueda, no hay copia de seguridad (si se moja, se
  acabó), no te avisa, y no admite fotos. Esos cuatro puntos son, no por casualidad, justo lo que
  cobran Diarium, Journey y Day One.

### La amenaza de Journal de Apple

Gratis y preinstalada, es una amenaza de adquisición: se lleva al usuario de iOS que nunca habría
pagado por un diario. No es una amenaza de retención en este nicho: no existe en Android, no tiene
la página de cinco años, y su idea (sugerirte qué escribir a partir de fotos y ubicación) es lo
contrario de la restricción de una sola línea, que es de donde sale el enganche. El hueco no es
permanente: Apple amplía esa app en cada versión.

### Nuestro ataque, en una línea cada uno

1. **Pago único.** En una categoría donde un año de Day One cuesta 49,99 $ y el clon más visible
   cobra 3,99 $ a la semana.
2. **Android e iOS con el mismo cuidado.** Journal de Apple no existe en Android, Everlog tampoco,
   y en Google Play la competencia directa es de un solo desarrollador.
3. **La página de cinco años es la pantalla principal**, no un aviso secundario tipo `On This Day`.
4. **La copia de seguridad y la exportación no se cobran nunca.** Es la queja número uno de la
   categoría entera, y en un diario íntimo pesa más que en un tracker de hábitos. Y la copia
   automática del sistema sale cifrada de extremo a extremo o no sale.
5. **Escribir cuesta cero toques extra**: la app abre con el foco puesto en el campo de hoy.
6. **Bloqueo con biometría gratis.** Cobrar por proteger un diario es la misma señal de desconfianza
   que cobrar por exportarlo.

---

## 2. Funcionalidad esencial

### MVP, lo mínimo para publicar

- [ ] Escribir la línea de hoy con el foco ya puesto y el teclado arriba. Sin botón de guardar:
      se guarda con rebote al dejar de teclear y al salir de la pantalla.
- [ ] Una entrada por día. Editable siempre, sin historial de versiones.
- [ ] Debajo del campo de hoy, **lo que escribiste este mismo día en años anteriores**, el más
      reciente arriba. Es la pantalla principal, no una pantalla aparte.
- [ ] Durante el primer año, ese bloque se rellena con **hace una semana** y **hace un mes**, que
      tienen dato desde el día 8 y el día 31.
- [ ] Escribir en cualquier día pasado, sin límite de cuánto atrás, desde la rejilla o la búsqueda.
- [ ] Rejilla del año de 12 columnas por 31 filas, binaria: celda llena si hay línea, hueco si no.
      Nunca en rojo. Tocar una celda abre ese día.
- [ ] Búsqueda de texto libre, como filtro dentro de la pantalla del año. No es una cuarta pantalla.
- [ ] Racha de días escritos, en sitio secundario. Solo cuenta el día real de escritura: rellenar
      un día atrasado ni la sube ni la rompe.
- [ ] Recordatorio diario local a la hora que elijas, apagado hasta que lo enciendas. Se calla si el
      día ya tiene línea, **en las dos plataformas** (ver §5).
- [ ] Bloqueo de la app con la biometría del sistema y respaldo al código del dispositivo. Gratis,
      apagado por defecto. Con el bloqueo puesto, la vista de multitarea no enseña la pantalla.
- [ ] Widget de hoy en las dos plataformas, **de solo lectura**: la fecha, si el día ya está escrito
      y si hay recuerdo disponible. Nunca el texto: lee un fichero de estado aparte y el diario no
      está a su alcance (ver §3).
- [ ] Exportar en **un solo `.zip`** con `entries.json` (reimportable), `diario.md` (legible sin la
      app) y la carpeta `photos/`. Importar la copia propia. Gratis para siempre.
- [ ] Aviso único a los 30 días para que hagas una copia, y fecha de la última copia en Ajustes.
- [ ] Tarjeta para compartir de 1080x1350: por defecto solo forma (días escritos, racha, año), y
      el texto de una entrada solo si el usuario la elige a mano.
- [ ] Foto del día: una por entrada, opcional. Tres entradas con foto gratis, el resto en Pro.
- [ ] Cinco idiomas: inglés, español, portugués, alemán y francés.
- [ ] Tres pantallas: **Hoy**, **Año**, **Ajustes**. Sin librería de navegación.

### v1.1, lo que la gente pedirá en las reseñas

- **El libro en PDF maquetado**: una página por día con los bloques de cada año, portada y
  tipografía. Es el gancho de marketing más fuerte que tiene el concepto.
- Widget de pantalla de bloqueo en iOS, widget del año, baldosa de Ajustes rápidos en Android.
- **Dictar la línea por Siri y Atajos** sin abrir la app: un `AppIntent` con un parámetro de texto
  (iOS 16+) que llama al repositorio de Kotlin, igual que `Shortcuts.swift` sobre `Shortcuts.ios.kt`
  en las hermanas. Si el día ya tiene línea, añade en una línea nueva y no trunca nunca: el tope de
  280 vive en la interfaz, no en el modelo. Con el bloqueo activo exige el dispositivo desbloqueado.
- **Widget del recuerdo** (Pro): la línea de hace un año, en la pantalla de inicio. Es la única
  superficie que enseña texto, y solo porque el usuario la coloca a propósito para eso: colocarla es
  el consentimiento. Con el bloqueo activo no enseña nada, en iOS se tacha en pantalla de bloqueo y
  StandBy con `privacySensitive()`, y la app escribe en el estado del widget esa única línea, nunca
  el diario (ver §3).
- **Importar las notas de MoodTraker**: su copia ya lleva fecha y nota por día. Quien viene de la
  hermana con un año de notas tiene el eco del pasado el primer día, que es exactamente el riesgo
  número uno de este concepto (§11). Coste pequeño: el formato de copia de MoodTraker es nuestro.
  Nunca pisa una línea ya escrita.
- Etiquetas libres por entrada, como en MoodTraker: sin catálogo que mantener, la hoja del día
  sugiere las diez más usadas.
- Pulsación larga sobre una celda: las primeras palabras sin salir de la rejilla.
- Banco fijo de preguntas para el día en blanco, escrito a mano en los cinco idiomas, opcional y
  apagable. Sin IA y sin red.

### v1.2, retención

- Recapitulaciones de mes y de año, y el aviso de diciembre que las dispara.
- Ánimo opcional por entrada, reutilizando la paleta de MoodTraker, como filtro de la rejilla.
- Varias fotos por entrada.
- Sincronización sobre iCloud y Drive del propio usuario, si y solo si antes hay una política de
  fusión escrita (ver §9).

### Descartado a propósito

| Qué | Por qué |
|---|---|
| Editor de texto rico y párrafos largos | El límite es la función. Con párrafos somos un Day One peor, en el terreno donde Day One ya gana |
| Varias entradas por día | Rompe la rejilla de una entrada por (fecha, año), que es de lo que dependen la vista de cinco años y el grid |
| Varios diarios o libros en paralelo | Una línea al día es un solo hilo por definición. Duplicaría ajustes, exportación y paywall |
| Racha que castiga rellenar días pasados | Empuja a falsear fechas o a desinstalar. Mismo criterio que las hermanas: nada de rojo, nada de culpa |
| Ubicación automática por GPS | Permiso invasivo para algo que la propia línea ya dice |
| Efemérides y citas como puente del primer año | No hay fuente offline sin licencia ni peso, las citas tienen derechos y habría que traducirlas a cinco idiomas |
| Etiqueta de lugar como campo propio | Un campo se paga tres veces: modelo, Swift del widget, exportación y búsqueda |
| Importar desde Day One | Una jornada larga para convertir justo al usuario que menos se va a convertir. Cuando alguien lo pida por escrito |
| Sugerencias de diario de Apple (Journaling Suggestions, iOS 17.2+, entitlement `com.apple.developer.journal.allow`) | Day One ya lo usa, y sirve para lo contrario de este formato: te propone momentos (fotos, entrenos, sitios) para escribir largo. Es solo iOS, rompe la promesa de las dos plataformas iguales y exige un puente de SwiftUI. Revisar solo si elegir la foto del día se convierte en queja |
| `FLAG_SECURE` en Android | Bloquea también las capturas que el propio usuario quiere hacer, incluida la de su tarjeta. `setRecentsScreenshotEnabled(false)` (Android 13+) oculta la multitarea sin prohibir nada |
| Insignias, niveles, notificación de hito | El número redondo dentro de la pantalla y nada más. Una notificación de felicitación es la primera que se desactiva, y arrastra al recordatorio con ella |
| Cuarta pantalla de búsqueda | La familia cabe en tres pantallas |

### Explícitamente fuera de alcance

Cuentas de usuario, nada social, IA generativa sobre el contenido (resúmenes, sugerencias, chat),
audio o vídeo, anuncios, analítica. Cada uno añade coste recurrente, permiso invasivo o una pantalla
más de las tres que la app necesita.

---

## 3. Decisiones que se toman aquí, no en el código

Son las preguntas que aparecen en la primera hora de programar y que, sin escribir, se descubren en
forma de fallo tres meses después.

| Pregunta | Decisión |
|---|---|
| ¿A qué hora deja de ser hoy? | El día termina a las **03:00 locales**. Constante fija, no ajuste. Quien escribe a la una de la madrugada está contando el día que acaba de vivir |
| Zona horaria | Claves ISO **locales**, nunca UTC. Cambiar de huso no reescribe ninguna entrada |
| 29 de febrero | Fecha propia. En años no bisiestos no aparece en la vista de años anteriores, y el 28 no la absorbe |
| El límite de 280 caracteres | Vive **en la interfaz, no en el modelo**. El almacén acepta cualquier longitud y la lectura muestra el texto entero. Si el límite vive en el modelo, importar una copia con entradas largas trunca el diario del usuario en silencio, que es la peor clase de fallo posible aquí |
| Qué es un carácter | Se cuentan puntos de código, no unidades UTF-16: un emoji cuenta uno y no dos. Y el corte al pegar texto largo **nunca parte una pareja suplente**, la misma lección que `habitIcon()` en Quilt (`e8ef697`): media pareja no es texto válido y el lado Swift la rechaza |
| Dónde vive el diario | `entries.json` en el almacenamiento privado de la app (`filesDir` en Android, Application Support en iOS), **nunca en el contenedor del App Group**. Allí solo va `widget.json` con tres campos |
| Editar y borrar el pasado | Editable siempre, sin versiones. Borrar pide confirmación. Una entrada que queda vacía se elimina del mapa, para que la rejilla y la búsqueda no mientan |
| Qué escribe el widget | **Nada, y no puede.** A diferencia de Quilt y MoodTraker, el widget es de solo lectura, su intent solo abre la app en Hoy, y el fichero del diario ni siquiera está en su contenedor |
| Qué se ve con el bloqueo activo | Con `lockEnabled`, ni el widget ni la notificación imprimen texto de ninguna entrada, y la vista de multitarea sale en blanco. Solo estado |
| Copia automática del sistema | Android: a la nube **solo si va cifrada de extremo a extremo** (`disableIfNoEncryptionCapabilities="true"`), sin fotos; el traspaso entre dispositivos sí lleva fotos. iOS: la copia de iCloud del dispositivo, que ya incluye los datos de la app |
| Pérdida del dispositivo | Sin sincronización, la defensa es la copia cifrada del sistema, el traspaso al móvil nuevo, el aviso de los 30 días y la fecha de la última copia siempre visible |
| Nombre, `applicationId`, bundle id y App Group | Se fijan **antes de la primera línea de código**. Son irreversibles tras publicar |

El widget de solo lectura merece un párrafo propio: elimina de raíz la clase de fallo más cara de la
familia, la de un widget de Swift que reescribe el fichero entero y borra los campos que su `struct`
no declara. En Quilt y en MoodTraker ese contrato existe porque el widget marca un hábito o un ánimo
de un toque. Aquí no hay acción de un toque que justifique escribir: escribir es teclear.

Y aquí la garantía la da la arquitectura, no la disciplina. El diario no está en el App Group, así
que ni el widget de iOS ni ninguna extensión pueden leerlo ni escribirlo aunque alguien se equivoque
en el código. El widget lee `widget.json`: fecha, si el día está escrito y si hay recuerdo. Sin
texto que filtrar y sin modelo que reimplementar en Swift.

El dictado por Siri (v1.1) **no** reabre el contrato de paridad: el `AppIntent` corre en el proceso
de la app y escribe a través del repositorio de Kotlin, nunca con un modelo propio en Swift. El
contrato solo vuelve el día que código Swift escriba `entries.json`, y eso hay que anotarlo en
`CLAUDE.md` el mismo día.

---

## 4. Diseño

Misma paleta que las hermanas, para que las tres se lean como una familia.

- Fondo crema `#FBF8F3` en claro y `#17150F` en oscuro. Nunca blanco puro ni negro puro.
- Acento salvia `#6FAE9B`. Ocho pasteles para portadas y rejilla: rosa, melocotón, mantequilla,
  salvia, menta, cielo, lavanda, lila.
- Radios de 18 a 32 dp, bordes de 1 dp en vez de sombras. Sin tarjetas: el crema es el lienzo y cada
  bloque lo nombra una etiqueta pequeña en versalitas, como en MoodTraker.
- **Sin rojo en ninguna parte.** Un día sin escribir es un hueco, no un suspenso.
- Iconos dibujados con `Canvas`, no glifos de texto: en iOS un `‹` o un engranaje se pintan como
  emoji de color y rompen la escala de grises.
- La tipografía del texto del usuario es la protagonista de la pantalla Hoy. Todo lo demás (racha,
  fecha, contador) va en un peso ligero y un tamaño claramente menor.

Principios:

1. La app abre directamente sobre el teclado el día que no has escrito. Cero toques antes de escribir.
2. El día que ya has escrito, la app abre sobre tus años anteriores. Cero toques antes de leer.
3. Nada de números grandes de progreso: esto es un diario, no un cuadro de mandos.

---

## 5. Enganche y retención

Un diario diario muere el día 4 por dos motivos a la vez: hay que acordarse de abrir, y el día 4
todavía no tiene nada que consultar. El bucle se sostiene sobre dos piezas y ninguna necesita
servidor.

### El recordatorio

Una sola notificación al día, a la hora que elige el usuario cuando la enciende. El permiso se pide
en ese momento, nunca al arrancar. El texto no asume ni reprende: nunca "no has escrito hoy", nunca
"vas a perder tu racha".

En Android el receptor comprueba si el día ya tiene línea y se calla.

En iOS **también se calla**, y aquí el diario difiere de las hermanas. Un
`UNCalendarNotificationTrigger` repetitivo no puede comprobar nada, y Quilt descartó los
disparadores sueltos porque el tope de 64 avisos pendientes por app, repartido entre cinco hábitos y
sus días, daba 12 días de margen. Con un solo aviso diario el mismo tope da **64 días**:

- Se programan hasta 60 avisos sueltos, uno por día, cada uno con su propio identificador de fecha.
- Al guardar la línea del día se retira el aviso de hoy.
- Se rellena la ventana en cada arranque y en cada guardado, así que en uso normal nunca baja.
- Quien no abre la app en dos meses deja de recibir avisos. Es el comportamiento correcto: seguir
  avisando a quien ya lo dejó es lo que acaba en desinstalación.

De regalo, cada aviso suelto lleva su propio texto, así que en iOS también puede citar el fragmento
de hace un año (solo con el bloqueo apagado, ver abajo). El recuerdo de una fecha futura ya está
escrito hoy, y al editarlo se reprograma la ventana entera, que son 60 peticiones y cuesta nada.

El texto base se escribe para que sirva siempre: *un momento para tu línea de hoy*.

Los datos de industria sobre frecuencia (con un solo aviso semanal ya hay un 10 % que desactiva las
notificaciones si el contenido no aporta; en la franja de 6 a 10 por semana el abandono ronda el
30 %) confirman la regla: **una notificación al día, bien escrita, y ninguna más**. Nada de avisos de
hito, de recuerdo ni de racha.

### El eco del pasado

Es el mecanismo de retención más fuerte de la categoría porque no lo fabrica el diseño, lo fabrica el
propio usuario: cuanto más lleva escribiendo, más fuerte se vuelve, sin una línea de código
adicional. Es lo que ha vendido tres millones de cuadernos de papel.

- Se entrega **al abrir la app**, no como notificación aparte. El recuerdo es contenido que se
  consulta, no una interrupción más.
- El recordatorio diario puede citar el fragmento de hace un año dentro de su propio texto, pero
  solo si el bloqueo está apagado.
- Los primeros 365 días ese eco no existe, y es justo el periodo crítico. El puente sale del propio
  fichero: **hace una semana**, **hace un mes**, el número de día del diario, y la fecha exacta en la
  que esa página empezará a tener recuerdo. Honestidad sobre que lo mejor está por llegar; nada de
  recuerdos fabricados.
- El único atajo honesto es traer un pasado que ya existe: **las notas de MoodTraker** (v1.1). Son
  del propio usuario, con su fecha, y convierten el año uno en año dos para quien viene de la hermana.

### Widgets: privacidad contra enganche

El widget de un tracker de hábitos puede enseñar el dato entero porque el dato es abstracto. Aquí el
dato es una frase íntima en una pantalla que ve cualquiera que mire de reojo.

**El widget muestra estructura, nunca contenido**: la fecha, si el día está escrito, y si hay
recuerdo disponible. Tocarlo abre la app en Hoy. Esto vale para Glance, para WidgetKit y para el
widget de pantalla de bloqueo de iOS. Al no imprimir texto nunca, el widget no depende de
`redacted(reason:)` para ser seguro, aunque se respete cuando el usuario lo activa. Y no puede
imprimirlo aunque quisiera: solo tiene acceso a `widget.json`.

La excepción deliberada es el **widget del recuerdo** de v1.1, que existe para enseñar la línea de
hace un año. Ahí el consentimiento es colocarlo, y la app copia en `widget.json` esa única línea
cuando el widget está puesto y el bloqueo apagado. En iOS lleva `privacySensitive()`, así que en
pantalla de bloqueo y en StandBy sale tachado. Es el enganche más fuerte que tiene un widget en esta
categoría, y la regla de "nunca el diario" sigue intacta: sale una línea, elegida por la fecha, a
petición.

### Rachas, sin castigo

Contador de días seguidos, en un sitio secundario, nunca compitiendo con la línea del día. Sin
animación de pérdida, sin aviso previo, sin nada que se desbloquee por llegar a un número.

Aquí hay que ser más estricto que en las hermanas. En un hábito, la presión de racha empuja como
mucho a marcar una casilla vacía. En un diario empuja a escribir relleno, y el relleno contamina
justo el archivo del que sale todo el valor: si lo que recuperas dentro de un año no significa nada,
el eco del pasado deja de funcionar.

### Primera sesión

Una sola pantalla: la fecha de hoy, el campo con el foco puesto y un texto de ayuda corto. Sin
tutorial, sin elegir tema, sin configurar nada antes de escribir. El permiso de notificaciones se
ofrece **después** de guardar la primera línea.

### Qué se comparte de un diario privado

La tarjeta compartible por defecto **no lleva texto**: días escritos, racha, año y paleta. Si el
usuario quiere compartir una línea concreta, la elige él, entrada por entrada. La app no sugiere ni
selecciona nunca qué fragmento enseñar. Es la única forma de mantener el canal de crecimiento sin
tocar la honestidad con la que la gente escribe, que es la base del producto.

Riesgo asumido y anotado: una tarjeta sin texto es menos atractiva que el mosaico de MoodTraker. Si
el canal no tira, se itera el diseño de la tarjeta agregada antes de tocar la regla del texto.

### Hitos

Una línea discreta dentro de la pantalla, ese día y solo ese día: primera entrada, 30 días, 100 días,
el primer aniversario del diario, y el día en que por primera vez hay tres años a la vista en la
misma página. Ligados al valor real (más historia, eco más fuerte), nunca a puntos ni a trofeos.

---

## 6. Monetización

### El dato que decide el modelo

Igual que en las dos hermanas: **un usuario cuesta 0 EUR al mes**. Sin backend, sin cuentas, sin IA,
sin almacenamiento nuestro. Una suscripción no tendría nada que sostener, y en esta categoría la
suscripción es justo lo que la gente reprocha a la líder (Day One ha eliminado el pago único y ha
subido a 49,99 $ el plan básico).

**Decisión: pago único como único producto de pago.**

### El eje del paywall

Esta es la decisión más difícil de la app y hay que dejarla escrita porque los tres ejes obvios son
malos:

- Limitar **entradas** castiga el hábito diario que queremos enganchar. Reseñas de una estrella.
- Limitar **los años visibles** ataca la razón de ser del formato. Es vender un cuaderno de cinco
  años y cobrar por pasar las páginas de atrás.
- No limitar **nada** deja la app sin producto que vender.

**Se cobran las superficies y los adornos. Nunca el contenido ni la memoria.**

| Gratis para siempre | Pro, pago único |
|---|---|
| Escribir cualquier día, incluidos los pasados | Foto en todas las entradas |
| Leer todo el histórico y la vista completa de años anteriores | Portadas y paletas del libro |
| Rejilla del año y búsqueda de texto | Widgets de pantalla de bloqueo, del año y del recuerdo |
| Recordatorio diario | Dictado por Siri y Atajos, baldosa de Ajustes rápidos |
| Bloqueo con biometría | Recapitulaciones de mes y de año |
| Widget de hoy | Libro en PDF maquetado (v1.1) |
| Exportar e importar (JSON y Markdown) | |
| Tarjeta para compartir | |
| Tres entradas con foto | |

La distinción que hay que escribir para que la regla no se coma a sí misma: **exportar tus datos no
se cobra nunca; el libro en PDF maquetado no es una exportación de datos, es un producto**. Quien
solo quiere sus datos los tiene gratis y reimportables en dos formatos.

Las tres entradas con foto son el equivalente exacto del mood con foto gratis de MoodTraker: están
para que todo el mundo vea el efecto antes de pagar. Igual que `FREE_HABIT_LIMIT` en Quilt, el número
es una perilla para medir, no un dogma.

### Las cuatro cosas que no se tocan nunca

1. **Exportar e importar.** Es la queja número uno de la categoría (el caso Daylio) y aquí pesa más:
   son datos más íntimos que un hábito o un color.
2. **Compartir.** Cada tarjeta publicada es marketing gratis.
3. **El libro base completo, escribir y leer.** Es la promesa del producto.
4. **El recordatorio y el bloqueo.** Sin empujón no hay hábito, y cobrar por proteger un diario es la
   misma señal de desconfianza que cobrar por la copia de seguridad.

### Precio

La escalera real de la familia, leída de sus propios repositorios y no de sus specs, que se quedaron
atrás:

| App | Escaparate | Fuente |
|---|---|---|
| Quilt | **4,99 EUR**, sin descuento de lanzamiento | `HabitTracker/store/lanzamiento.md`, producto `pro_lifetime` |
| MoodTraker | 7,99 EUR, 5,49 EUR de lanzamiento | `MoodTraker/store/revenuecat.md` |
| **Este diario** | **5,99 EUR en v1.0, 8,99 EUR desde v1.1** | Esta sección |

A 8,99 EUR desde el primer día el diario sería **la más cara de las tres**, no la del medio, y con el
paquete de pago más fino de la familia en v1.0 (§11, riesgo 2): foto, portadas y dos superficies.
La propuesta es escalonar por valor y no por calendario:

- **v1.0 a 5,99 EUR.** Lo que se vende es foto en todas las entradas, portadas y widgets extra.
- **v1.1 a 8,99 EUR**, el día que sale el libro en PDF maquetado, que es el producto que justifica el
  precio. Quien compró a 5,99 conserva Pro para siempre: subir el precio cuando crece lo que se da es
  la dirección honesta, y en RevenueCat es tocar el panel, no el código.

Las dos cifras quedan muy por debajo de un solo año de cualquier competidor de peso: 49,99 $ de Day
One Silver, 49,99 $ de Journey, 19,99 $ del clon más visible en App Store. Dentro del pago único la
horquilla de la categoría va de 2,99 $ (DayGram, muy básico) a 54,99 $. **Precios regionales
activados en las dos tiendas**: es lo único que hace que "asequible" no sea solo una palabra, y no
cuesta una línea de código.

Es una decisión del autor. Si prefiere 8,99 desde el principio, la alternativa de la primera versión
de este spec sigue siendo defendible: 6,49 EUR de lanzamiento las primeras 4 a 6 semanas.

### Lo que llega al bolsillo

```
5,99 EUR escaparate           8,99 EUR escaparate
/ 1,21 (IVA 21%)  = 4,95 EUR  / 1,21 (IVA 21%)  = 7,43 EUR
- 15 % comisión   = 4,21 EUR  - 15 % comisión   = 6,32 EUR netos
```

| Objetivo | Ventas/mes a 5,99 | Descargas/mes a 5,99 | Ventas/mes a 8,99 | Descargas/mes a 8,99 |
|---|---|---|---|---|
| 500 EUR | ~119 | ~4.760 | ~80 | ~3.200 |
| 1.000 EUR | ~238 | ~9.520 | ~159 | ~6.360 |
| 3.000 EUR | ~713 | ~28.500 | ~475 | ~19.000 |

Descargas calculadas al 2,5 % de conversión, que es la hipótesis de trabajo de la familia, no un
dato medido de esta categoría. Si el precio más bajo convierte mejor, la diferencia se come sola; eso
se mide en la beta y en las primeras semanas, no aquí.

### Reglas

1. **El plan gratis es la prueba.** No existen pruebas gratuitas para compras únicas.
2. **El paywall aparece al chocar**: al poner la cuarta foto, al tocar una portada de Pro o al
   colocar el widget del año o el del recuerdo. Nunca al arrancar.
3. **"Restaurar compra" visible en Ajustes.** Requisito de Apple.
4. **Nada de anuncios.**
5. **RevenueCat KMP**, un producto no consumible, las dos tiendas de una vez.
6. **Small Business Program de Apple el primer día**: 70 % en vez de 55 %. Si la cuenta no está ya
   inscrita, la comisión reducida tarda en aplicarse y las primeras ventas se cobran al 30 %.
7. **Venta cruzada discreta**: una sección en Ajustes con una fila por app hermana y una mención
   descartable al final del onboarding. Sin banners, sin notificaciones, sin cruzar datos entre apps.

### Cuándo tocaría una suscripción

Solo si aparece un coste recurrente real: sincronización en nube propia, IA, o impresión física del
libro. Y aun entonces el reparto sería pago único para la app y cobro aparte solo para el servicio
con coste, nunca convertir en suscripción el núcleo de escribir y leer tu diario.

---

## 7. Identidad y ficha

### El nombre

El repositorio se llama `line` y el identificador será `com.baltajmn.line`, pero el nombre visible no
tiene por qué coincidir, igual que Quilt vive en `com.baltajmn.habit`. Y **no puede** coincidir:
"Line" a secas choca con LINE, la mensajería, que ocupa esa búsqueda en las dos tiendas y es marca
registrada. El identificador no se ve y no importa; el nombre de la ficha sí.

Descartado de entrada el nombre literal: **One Line a Day**, **5 Year Journal** y variantes ya los
usan al menos cinco competidores directos. Hunde la búsqueda por nombre exacto y rompe la convención
de la familia, que nombra con metáfora.

| Candidato | Metáfora del icono | Nota |
|---|---|---|
| **Fivefold** (recomendado) | Una hoja pastel plegada en cinco capas que al abrirse enseña los cinco años | Lleva "five" dentro, que es exactamente lo que se busca en la tienda. Sin colisión en la categoría. La búsqueda exacta sí devuelve dos vecinos que no compiten: *FiveFoldFury*, un juego en App Store, y *The Five Fold Network*, una televisión religiosa en Play |
| Braid | Cinco hebras pastel que se trenzan en una sola línea | Sigue la familia textil de Quilt, pero **BRAID es marca registrada de software en Estados Unidos** (el videojuego) y contamina la búsqueda. Solo si la comprobación de marca sale limpia |
| Skein | Una madeja que se enrolla un día más cada día | Libre y bonito, pero es palabra rara y difícil de pronunciar fuera del inglés |
| Strand | Una hebra suelta | Demasiado genérico, se diluye en la búsqueda |

Antes de comprar dominio: buscar en EUIPO, en USPTO, en las dos tiendas y en Google, y tener dos
alternativas de la misma familia. **El nombre y los identificadores se fijan antes de escribir
código.**

### El icono

El mismo criterio que en Quilt: el icono es la propia metáfora del producto sobre fondo oscuro
(`#2C2820` a `#17150F`), porque en la comparativa a 48 px la versión crema desaparece sobre un
lanzador claro. Los pasteles son los protagonistas.

Sale todo de un script como el `tools/generate_icons.py` de Quilt: PNG de 1024 para iOS, adaptativo
de Android con su capa `monochrome` de un solo color, PNG heredados en cinco densidades e icono de
notificación en blanco sobre transparente.

### ASO

**Inglés**

| Campo | Contenido |
|---|---|
| Título | `Fivefold: 5-Year Line Diary` |
| Subtítulo (App Store) | `One line a day, five years` |
| Palabras clave | diary, journal, 5 year, one line, memory, daily, gratitude, notebook, reflection, on this day |
| Descripción corta (Play, 80) | `Write one line a day. See what you wrote on this date in past years.` |

**Español**

| Campo | Contenido |
|---|---|
| Título | `Fivefold: diario de una línea` |
| Subtítulo (App Store) | `Una línea al día, cinco años` |
| Palabras clave | diario, agenda, una línea, diario de 5 años, memoria, recuerdos, gratitud, reflexión, en este día |
| Descripción corta (Play, 80) | `Escribe una línea al día y lee qué escribiste este mismo día otros años.` |

Ángulo de la descripción larga, igual en los cinco idiomas: treinta segundos al día, no un ejercicio
de escritura; el gancho es releer; pago único sin suscripción; todo se queda en el teléfono y se
puede exportar siempre. **Ni una promesa de salud mental**: convierte la app en producto de bienestar
a ojos de la revisión y no mueve la conversión.

---

## 8. Cumplimiento de tienda

Bloque de trabajo del día 1, no del día 20: son los trámites que bloquean la publicación **después**
de que el código esté listo.

| Qué | Detalle |
|---|---|
| **Trader status del DSA** | Obligatorio en la UE desde el 17/02/2025. Apple retira las apps que no lo declaran y publica la dirección del trader en la ficha. Si la cuenta ya lo declaró por Quilt, esto se hereda |
| **Manifiesto de privacidad de iOS** | `PrivacyInfo.xcprivacy` con las razones de las APIs de motivo obligatorio que llame **nuestro** código: `NSUserDefaults` (CA92.1) si se usa, y `FileTimestamp` (C617.1) solo si leemos fechas o tamaños de ficheros; escribir no la exige. RevenueCat trae su propio manifiesto. La fuente de verdad no es esta lista, es el informe de privacidad que genera Xcode sobre el archivo. Sin manifiesto, rechazo automático `ITMS-91053` / `ITMS-91061` en la primera subida |
| **App Privacy y Data Safety** | Con RevenueCat dentro se declaran historial de compras e identificadores, igual que en Quilt: obligatorio, no compartido, sin seguimiento. El texto de las entradas no sale del dispositivo, y eso se dice explícitamente en los dos formularios y en la política |
| **Clasificación por edad** | Cuestionario nuevo de Apple. El usuario escribe lo que quiere pero no lo comparte con nadie: no es contenido generado por usuarios a efectos de moderación. En Play, público objetivo a partir de 13 para no entrar en la política de Familias |
| **Copia automática del sistema** | Android: Auto Backup está activo por defecto y sube hasta 25 MB por app al Drive del usuario; si se pasa, **no sube nada** y lo reintenta cuando baje. Por eso las fotos se excluyen de `<cloud-backup>` y el texto (menos de 1 MB) entra. Y la nube se configura con `disableIfNoEncryptionCapabilities="true"`: solo sube si va cifrada de extremo a extremo, que en Android 9+ exige que el usuario tenga bloqueo de pantalla. El traspaso entre dispositivos (`<device-transfer>`) no cuenta contra los 25 MB y **sí lleva las fotos**. Por debajo de Android 12 se usa `fullBackupContent` con `requireFlags="clientSideEncryption"`. iOS: la copia de iCloud del dispositivo incluye los datos de la app y los del App Group, según el soporte técnico de Apple; comprobarlo igualmente en dispositivo antes de prometerlo en la ficha |
| **Data Safety y la copia del sistema** | La definición de Google: se declara lo que "sale del dispositivo", salvo lo que va cifrado de extremo a extremo y nadie más que emisor y receptor puede leer. Con la nube solo cifrada, la copia del sistema entra en esa excepción. **Es una inferencia sobre la definición**, porque la ayuda de Play no menciona la copia del sistema: se deja escrita en `store/` con su fuente y se revisa si Google publica algo concreto |
| **Prueba cerrada de Google** | 12 probadores durante 14 días continuos, **por app**, más hasta 7 días de revisión del acceso a producción. Si el número baja de 12 hay que recuperarlo y volver a sostenerlo. Es el camino crítico del calendario. Los probadores de Quilt primero: ya dijeron que sí una vez, y un diario pide abrirlo a diario, que es justo el uso que Google mira. El intercambio recíproco entre desarrolladores, Test4Test incluido, arrastra el riesgo de asociación de cuentas que documenta `HabitTracker/store/testers.md` |
| **La corrección de honestidad** | Con RevenueCat dentro, "sin red" deja de ser cierto. El mensaje es "sin cuenta y sin analítica; la única conexión que hace la app es la de la compra", igual que ya corrigió Quilt |

---

## 9. Arquitectura prevista

El repositorio está vacío: esto es el plan, no el estado. Se clona el andamiaje de MoodTraker
(Gradle, CI, workflows de release, firma, scripts de ficha, política de privacidad) y se renombra.

### Árbol de `shared/src/commonMain/kotlin/com/baltajmn/line`

```
App.kt                  enum Screen (Today, Year, Settings) + puerta de bloqueo
model/Entry.kt          LineEntry + Journal = Map<String, LineEntry>, racha y recuento (con tests)
model/DayClock.kt       corte del día a las 03:00, fecha local, vecinos del mismo día (con tests)
data/Storage.kt         expect: JSON atómico con copia .bak (filesDir / Application Support)
data/LineRepository.kt  fuente única de verdad, estado Compose, límite de fotos del plan gratis
data/Search.kt          filtro lineal en memoria sobre el Journal ya cargado
data/Photos.kt          expect: selector del sistema, reescalado a 512 px, caché en memoria
data/Lock.kt            expect: biometría con respaldo al código, multitarea oculta
data/Reminder.kt        expect: un aviso diario que se calla si el día ya tiene línea
data/ReminderPlan.kt    las fechas y textos de los próximos 60 avisos, puro común (con tests)
data/Backup.kt          expect: guardar y elegir fichero para exportar e importar
data/Export.kt          entries.json, diario.md y el zip que los junta, puro común
data/Zip.kt             zip STORED de escritura y lectura con CRC32, puro común (con tests)
data/WidgetState.kt     deriva widget.json (fecha, escrito, recuerdo) y lo escribe en el App Group
data/Widgets.kt         expect: refrescar el widget tras cada guardado
billing/Billing.kt      expect: clave de RevenueCat + comprar / restaurar / refrescar
i18n/Strings.kt         los cinco idiomas en una tabla, obligados por firma
ui/theme/Theme.kt       paleta pastel claro/oscuro
ui/TodayScreen.kt       el campo de hoy y los años anteriores del mismo día
ui/YearScreen.kt        rejilla binaria, lista y búsqueda como filtro
ui/DaySheet.kt          un día concreto: texto y foto
ui/SettingsScreen.kt    recordatorio, bloqueo, copia, idioma, restaurar compra
ui/LockScreen.kt        overlay, se pinta antes que cualquier otra pantalla
ui/Pro.kt               paywall
ui/ShareScreen.kt       vista previa de la tarjeta
share/ShareCard.kt      dibuja la tarjeta 1080x1350
share/Sharing.kt        expect: PNG + hoja de compartir + guardar en fotos
```

Tres pantallas en el enum. `Lock`, `Pro` y `ShareScreen` son overlays, igual que ya lo son en las
hermanas.

### `entries.json`

```json
{
  "version": 1,
  "entries": {
    "2026-01-17": { "text": "Primer día de vacaciones, llovió todo el rato." },
    "2027-01-17": { "text": "Mismo día, sol. Cambio de piso confirmado.", "photo": "p-8f21.jpg" }
  },
  "reminderHour": 21,
  "reminderMinute": 0,
  "lockEnabled": false
}
```

Mapa plano por fecha ISO local, no un bloque fijo de cinco huecos. La vista de años anteriores se
calcula filtrando las claves por su sufijo `mm-dd`. El formato no impone techo de cinco años: el
número de años es una decisión de producto, no del fichero. Las fotos van en `photos/` referenciadas
por nombre, nunca incrustadas, y cambiar una foto genera nombre nuevo para que la caché no sirva la
vieja.

`entries.json` y `photos/` viven en el almacenamiento privado de la app, **fuera del App Group**. En
el contenedor compartido solo hay esto:

```json
{ "date": "2027-01-17", "written": true, "memory": true }
```

Más, en v1.1 y solo con el widget del recuerdo puesto y el bloqueo apagado, un campo `"line"` con la
línea de hace un año. `LineStore.swift` decodifica esos tres o cuatro campos y nada más: no hay
modelo del diario en Swift que mantener en paridad, y no hay forma de que el widget lea ni borre una
entrada.

### La copia de seguridad es un zip

MoodTraker mete las fotos en base64 dentro del JSON de la copia. Aquí eso no escala: una foto por día
a 512 px son unos 40 a 80 KB, así que un año con foto diaria son 15 a 30 MB, un tercio más en base64,
y cinco años pasan de 100 MB **en una sola cadena en memoria**. En un Android de gama baja eso es un
cierre por falta de memoria justo al hacer la copia, que es el peor momento posible. Y a ese tamaño
tampoco cabe en un correo, que era el argumento del fichero único.

La copia es **un solo fichero `.zip`**, que sigue siendo un fichero:

```
<nombre>-2027-01-17.zip
├── entries.json    lo que reimporta la app
├── journal.md      todas las líneas por fecha, legible con cualquier editor dentro de 50 años
└── photos/         los JPEG tal cual
```

Los nombres de dentro son fijos y en inglés en los cinco idiomas, para que la estructura sea la misma
venga de donde venga la copia.

- **STORED, sin compresión.** Los JPEG ya van comprimidos y el texto pesa poco: comprimir no ahorra
  nada y obliga a implementar DEFLATE.
- Se escribe **entrada a entrada** al destino, sin montar el archivo en memoria.
- iOS (Archivos), los gestores de ficheros de Android y cualquier escritorio abren un zip con doble
  toque. El usuario puede comprobar por sí mismo que sus datos son suyos, que es el argumento de venta.
- `data/Zip.kt` en común: escritor y lector STORED con CRC32 escrito a mano, unas 150 líneas con sus
  tests. Okio no sirve: su soporte de zip es de solo lectura y solo JVM. Si algún día hay que leer zips
  ajenos (la copia de Day One), `kmp-zip` (`no.synth:kmp-zip`, lee y escribe, con targets de iOS,
  licencia MPL-2.0) es la opción, pero para leer lo que escribimos nosotros no hace falta una
  dependencia.
- La importación acepta el zip y, por compatibilidad, un `entries.json` suelto.

### El problema que las hermanas no tienen: el fichero crece

Un hábito guarda un booleano y un ánimo guarda un id. Aquí se guarda texto libre.

Cinco años de líneas de 200 caracteres son unos 365.000 caracteres, del orden de 400 a 500 KB con la
sobrecarga del JSON. Veinte años, 1,5 a 2 MB. Es una estimación, no una medida, y sigue siendo un
fichero pequeño.

Lo que deja de valer no es el tamaño, es **cuántas veces se escribe**:

- **Autoguardado con rebote de 700 a 1.000 ms** tras la última pulsación, más guardado forzado al ir
  a segundo plano o al salir de la pantalla. Guardar por tecla reescribiría el diario entero una vez
  por carácter, y también la copia `.bak`.
- **Un único escritor, fuera del hilo principal.** El guardado del rebote y el de ir a segundo plano
  pueden coincidir; dos escrituras cruzadas rotan la `.bak` dos veces y la copia buena se pierde.
  Una sola cola serializada lo evita. Es además la deuda que MoodTraker ya tiene anotada: su `save()`
  reescribe el fichero entero en el hilo principal en cada toque.
- **iOS, protección `CompleteUntilFirstUserAuthentication`**, no `Complete`, aunque el widget ya no lea
  el diario. Con `Complete` el fichero deja de poder abrirse unos segundos después de bloquear el
  teléfono, y el guardado del rebote que salta justo después de bloquear fallaría: la última línea
  escrita se perdería sin aviso.
- La señal para partir el fichero **no es cuántos KB pesa**, es medir en el dispositivo más antiguo
  soportado el tiempo de serializar y escribir. Si el percentil 95 se acerca al intervalo de rebote
  (referencia: 50 a 100 ms, donde un guardado síncrono ya se nota como tirón), se parte por años
  (`entries-2030.json`) y se mantiene en memoria solo el año activo. El widget no cambia: nunca leyó
  el diario.

### Búsqueda sin base de datos

Cargar el Journal entero al arrancar y filtrar con `contains()` insensible a mayúsculas. A estos
tamaños, recorrer unos miles de cadenas cortas es submilisegundo y no necesita índice.

Deja de bastar cuando cambie la forma de lo que se pide: ranking, coincidencia difusa, por raíz.
Aviso importante, comprobado: **FTS5 no está disponible en la SQLite del sistema de la mayoría de
dispositivos Android**, así que "SQLDelight y ya" es falso; haría falta empaquetar una SQLite propia
(requery, o Room 2.7 con `BundledSQLiteDriver`), o sea una dependencia nueva y un segundo almacén que
rompe el contrato de "un solo fichero es la fuente de verdad" del que vive el widget. Plan B de
verdad, no alternativa cómoda.

### Bloqueo

`data/Lock.kt` como `expect object` mínimo: `isAvailable()` y `authenticate(onResult)`.

- Android: `BiometricPrompt` con `DEVICE_CREDENTIAL`, comprobando con `BiometricManager.canAuthenticate()`.
- iOS: `LAContext.evaluatePolicy(.deviceOwnerAuthentication)`, que ya cae solo al código del
  dispositivo.

**Sin PIN propio.** Sin cifrado del fichero, un PIN propio solo añade una pantalla de recuperación
que mantener y el caso "he olvidado el código y he perdido mi diario".

El agujero real no está en la API: está en lo que se ve **fuera** de la app sin pedir biometría.
Tres sitios, tres respuestas:

| Dónde | Con el bloqueo activo |
|---|---|
| Widget | No imprime texto. Además no puede: solo tiene `widget.json` |
| Notificación | No imprime texto: ni el fragmento de hace un año ni nada del diario |
| Vista de multitarea | Android 13+: `setRecentsScreenshotEnabled(false)`, que oculta la miniatura sin impedir las capturas del usuario. iOS: una vista de color crema encima en cuanto la escena deja de estar activa, puesta desde `iOSApp.swift` y no desde Compose, que no llega a repintar antes de la foto del sistema. Por debajo de Android 13 no hay forma limpia y se acepta |

**Vuelve a pedirse a los 60 segundos en segundo plano**, constante fija como el corte de las 03:00.
Pedirla cada vez que se sale un instante a copiar algo es la forma más rápida de que alguien apague el
bloqueo, y un bloqueo apagado no protege nada.

Es una decisión de producto tanto como técnica y se toma antes de publicar, no después de la primera
reseña.

### Cifrado en reposo: teatro

El fichero vive en el sandbox de la app, los dos sistemas lo cifran en reposo, y en iOS bajo
`NSFileProtectionCompleteUntilFirstUserAuthentication` por la razón del guardado tras bloquear que
se cuenta arriba. Cifrar por encima exigiría guardar una clave que el propio proceso pueda leer sin
intervención del usuario: no protege de nadie que no pudiera ya leer el fichero. Ese esfuerzo rinde
más en lo que se ve fuera de la app, que es el agujero de verdad. La copia que sale del teléfono ya
va cifrada de extremo a extremo por el sistema, o no sale (§8).

### Tests mínimos

1. Ida y vuelta de serialización con comillas, saltos de línea, emoji y acentos. Más crítico que en
   las hermanas porque el valor no está acotado a un conjunto cerrado.
2. Escritura atómica: interrumpir a mitad y comprobar que la lectura cae a `.bak` sin perder el día.
3. Vista de años anteriores: agrupar por sufijo `mm-dd`, con el 29 de febrero como caso propio.
4. Corte del día a las 03:00, racha y "hoy ya tiene línea", con medianoche y cambio de huso.
5. Migración: un fichero de la versión anterior se sigue leyendo tras añadir un campo opcional.
6. Zip: ida y vuelta con texto, `journal.md` y fotos binarias; CRC32 contra un vector conocido
   (`"123456789"` da `0xCBF43926`); un zip truncado se rechaza sin tocar el diario.
7. Plan de avisos de iOS: 60 fechas desde mañana si hoy ya está escrito y desde hoy si no, texto del
   recuerdo solo con el bloqueo apagado, y el 29 de febrero.
8. Tope de 280: contar un emoji como uno y no partir nunca una pareja suplente al cortar lo pegado.
9. `widget.json`: con el bloqueo activo no lleva `line` aunque el widget del recuerdo esté puesto.

### Sincronización, y por qué no ahora

En iOS es viable y barato de activar (contenedor de iCloud Drive del usuario y `NSMetadataQuery`).
Lo caro no es activarlo: es decidir qué pasa cuando dos dispositivos escriben el mismo día sin verse.
Fusionar por clave de día funciona en el caso común y falla justo en el que importa.

En Android no hay equivalente del sistema: la ruta realista es el Storage Access Framework apuntando
a una carpeta de Drive elegida a mano, que es exportar e importar con un paso menos, no sincronizar.

Fuera del MVP y de v1.1. Es el único cambio de toda esta lista que rompe la garantía de la familia:
un fichero, una escritura atómica, un dispositivo.

### Trampas heredadas que siguen aplicando

- Textos de `AppIntents` (`LocalizedStringResource`, `TypeDisplayRepresentation`) tienen que ser
  literales: un valor de la tabla de idiomas rompe el build con `No AppIntents metadata have been exported`.
- `CADisableMinimumFrameDurationOnPhone` tiene que estar en el `Info.plist` y valer `true`, o Compose
  Multiplatform aborta el proceso al arrancar y parece que la app ni se lanza.
- `plutil -extract` reescribe el fichero de entrada si no le pasas `-o -`.
- Carpeta sincronizada de `iosApp/iosApp`: un fichero nuevo entra en el target sin tocar el `.pbxproj`.
- Glance no tiene lienzo. Aquí no hace falta bitmap porque el widget es texto, pero vuelve a aplicar
  el día que se dibuje la rejilla del año en un widget.
- **El `versionCode` no se reutiliza nunca**, ni entre canales de Play.
- **Todo lo que convive con el teclado scrollea con `imePadding()`.** Hoy abre con el teclado arriba y
  los años anteriores debajo: sin `verticalScroll` más `imePadding`, la hoja se encoge y corta lo de
  abajo. Es el fallo que Quilt arrastró hasta la 1.7 en su formulario, con el botón de crear visto
  como una pastilla vacía (`e8ef697`).

---

## 10. Plan de ataque

1. Nombre definitivo, comprobación de marca, `applicationId`, bundle id y App Group.
2. Clonar el andamiaje de MoodTraker: Gradle, CI, workflows, firma, iconos, scripts de ficha,
   política de privacidad.
3. Alta en las dos consolas con ese identificador, trader status, Small Business Program, ficha en
   borrador.
4. Primera build instalable y **alta de la prueba cerrada de Google con 12 probadores**. El reloj de
   los 14 días arranca aquí, no al final.
5. Modelo y almacén: JSON atómico, copia `.bak`, número de versión.
6. Pantalla Hoy: escribir, autoguardado con rebote, corte de día a las 03:00.
7. La vista de años anteriores dentro de Hoy, con los ecos de hace una semana y hace un mes.
8. Pantalla Año: rejilla binaria y búsqueda como filtro de la propia pantalla.
9. Ajustes: recordatorio, bloqueo, exportar, importar, idioma, restaurar compra.
10. `Strings.kt` en los cinco idiomas.
11. Recordatorio local en las dos plataformas, permiso al encenderlo. En iOS, la ventana de 60 avisos
    sueltos.
12. Exportar el zip, importar la copia propia, aviso de copia a los 30 días, reglas de copia del
    sistema (`dataExtractionRules` y `fullBackupContent`).
13. Bloqueo con biometría y multitarea oculta.
14. `widget.json` y los widgets de solo lectura: Glance y WidgetKit.
15. Foto del día y paywall: RevenueCat, producto no consumible, restaurar, compra probada de verdad.
16. Tarjeta para compartir en `Canvas`.
17. Formularios: manifiesto de privacidad, App Privacy, Data Safety, clasificación por edad.
18. Capturas y textos de ficha, cinco idiomas, dos tiendas.
19. Beta corta con gente real.
20. Envío a revisión de Apple y solicitud de acceso a producción en Play al cumplirse los 14 días.

### Estimación

**Producto, unas 18,5 jornadas**

| Tarea | Jornadas |
|---|---|
| Andamiaje copiado y renombrado | 1 |
| Modelo, almacén atómico, versión del fichero | 1 |
| Pantalla Hoy, autoguardado, corte de día | 2 |
| Vista de años anteriores y ecos del primer año | 1,5 |
| Pantalla Año: rejilla y búsqueda | 1,5 |
| Ajustes completos | 1 |
| `Strings.kt`, cinco idiomas | 1 |
| Recordatorio en las dos plataformas, con la ventana de avisos de iOS | 1,5 |
| Exportar el zip, importar, aviso de copia y reglas de copia del sistema | 1,5 |
| Bloqueo biométrico y multitarea oculta | 1 |
| Widget de Android (Glance) | 1 |
| Widget de iOS (WidgetKit), que solo decodifica `widget.json` | 1 |
| Foto del día | 1 |
| Paywall y RevenueCat | 1 |
| Tarjeta para compartir | 1 |
| Pulido en dispositivo real, claro y oscuro, texto grande | 1,5 |

**Tienda, unas 4 jornadas**: altas y trámites 0,5; RevenueCat 0,5; probar compras de verdad 1;
política y formularios 0,5; capturas y textos en cinco idiomas 1,5.

**Riesgo, 3 jornadas**: 2 de beta con gente real, 1 de colchón por rechazo.

**Total: unas 25,5 jornadas. De 5 a 7 semanas de calendario**, mandadas por los 14 días de prueba
cerrada de Google más hasta 7 de revisión del acceso a producción. Si la prueba cerrada no arranca en
la primera semana, el calendario se va a 9 semanas aunque el código esté terminado.

---

## 11. Riesgos abiertos

1. **El valor central no existe hasta el año dos.** La retención se gana o se pierde entre el día 4 y
   el día 30, y ahí solo hay el recordatorio, el eco corto y la rejilla. Es el riesgo número uno del
   concepto y no lo arregla ninguna función: lo arregla que la primera semana sea muy buena. La única
   excepción honesta es importar un pasado real, las notas de MoodTraker, y solo alcanza a quien
   viene de la hermana.
2. **El paquete de pago puede seguir pareciendo fino** aunque entre la foto. Se mide en la beta, no
   aquí: si en dos semanas no compra nadie, el problema es el paquete, no el precio. Por eso el
   precio escalonado de §6: 5,99 mientras el paquete es fino, 8,99 cuando llega el libro.
3. **"He cambiado de móvil y he perdido mi diario"** es la reseña de una estrella previsible. El
   aviso de copia y la copia automática del sistema son la única defensa antes de v1.2.
4. **El nombre.** Braid colisiona con marca registrada de software conocida; Fivefold no tiene
   colisión encontrada, pero la comprobación se ha hecho por búsqueda web, no en EUIPO ni dentro de
   las consolas.
5. **La tarjeta sin texto puede compartirse menos** que el mosaico de MoodTraker, y con ella el canal
   orgánico.
6. **Auto Backup sube el diario al Drive del usuario por defecto.** Resuelto a medias: con
   `disableIfNoEncryptionCapabilities` solo sube cifrado de extremo a extremo, y quien no tiene
   bloqueo de pantalla se queda sin copia en la nube. Se dice en la política, y el aviso de los 30
   días es para esa gente. Lo que queda abierto es que la exención de Data Safety es una inferencia
   sobre la definición de Google, no una frase suya.
7. **La prueba cerrada de Google es por app** y vuelve a aplicar aquí entera.
8. **Journal de Apple puede añadir la vista de años anteriores** en cualquier versión. El hueco en
   iOS no es permanente; en Android sí es estructural.

### Correcciones de datos hechas durante la investigación

Van aquí para que nadie las vuelva a buscar:

- Day One Silver cuesta **49,99 $/año**, no 34,99. Solo tiene plan anual, sin pago único.
- En la UE, un desarrollador del Small Business Program que use pago alternativo o enlaces externos
  paga **10 %**; el 13 % es la tarifa reducida de quien **no** está en el programa, y la Core
  Technology Commission del 5 % va aparte.
- Google no "reinicia el contador" si un probador se da de baja: hay que sostener 12 optados durante
  14 días, y si bajas de 12 tienes que recuperar el umbral y volver a sostenerlo.
- El PDF de pago de Daylio **sí** puede incluir fotos; lo que está capado en el CSV gratis son los
  iconos.
- El precio anual exacto de Stoic no es verificable en la ficha: las fuentes dan cifras distintas.
- **FTS5 no viene compilada en la SQLite del sistema de Android** en la mayoría de dispositivos.
- Quilt se vende a **4,99 EUR**, no a 9,99: su SPEC conserva el plan inicial, pero el producto real
  (`pro_lifetime`) y su checklist dicen 4,99. MoodTraker, 7,99 con 5,49 de lanzamiento.
- El tope de 64 avisos pendientes de iOS no descarta los avisos sueltos en esta app: se reparte entre
  un solo aviso diario, no entre varios hábitos y sus días. Da 64 días de margen, no 12.
- El traspaso entre dispositivos de Android no cuenta contra los 25 MB de la copia en la nube, así
  que las fotos pueden viajar al móvil nuevo aunque no suban a Drive.
- Okio lee zips pero no los escribe, y solo en JVM: no sirve para la copia en iOS.
- `setRecentsScreenshotEnabled(false)` existe desde Android 13 y oculta la multitarea sin bloquear
  las capturas, que es lo que hace `FLAG_SECURE`.

---

## Fuentes

- [Day One, planes y precios](https://dayoneapp.com/plans/)
- [Day One Gold, plan con IA, 9to5Mac](https://9to5mac.com/2026/04/08/day-one-journaling-app-introduces-gold-plan-with-ai-summaries-and-daily-chat/)
- [Diarium, precio y modelo](https://diariumapp.com/en)
- [Journey, membresía](https://journey.cloud/membership)
- [One Line A Day 365 Journal, App Store](https://apps.apple.com/us/app/one-line-a-day-365-journal/id6770859782)
- [DayGram, Google Play](https://play.google.com/store/apps/details?id=com.saltycrackers.daygram)
- [Daylio, reseña y precios, ChoosingTherapy](https://www.choosingtherapy.com/daylio-app-review/)
- [Reflectly, reseña y precios, ChoosingTherapy](https://www.choosingtherapy.com/reflectly-app-review/)
- [One Line a Day, cuaderno de Chronicle Books](https://www.chroniclebooks.com/products/one-line-a-day-a-five-year-memory-book)
- [Por qué funciona el diario de una línea, HuffPost](https://www.huffpost.com/entry/five-year-line-a-day-journal-ud_l_69400a60e4b0c1ae50e35951)
- [Trader status del DSA, Apple Developer](https://developer.apple.com/news/upcoming-requirements/?id=02172025a)
- [Comisiones y términos en la UE, Apple Developer](https://developer.apple.com/support/apps-in-the-eu/)
- [Small Business Program, Apple](https://developer.apple.com/app-store/small-business-program/)
- [12 probadores y 14 días, Play Console Help](https://support.google.com/googleplay/android-developer/answer/14151465)
- [Clasificación por edad nueva, Apple Developer](https://developer.apple.com/news/?id=ks775ehf)
- [Auto Backup, Android Developers](https://developer.android.com/identity/data/autobackup)
- [Data Safety con RevenueCat](https://www.revenuecat.com/docs/platform-resources/google-platform-resources/google-plays-data-safety)
- [FTS5 no disponible en Android, SQLDelight #1977](https://github.com/sqldelight/sqldelight/issues/1977)
- [Protección de ficheros hasta la primera autenticación, Apple](https://developer.apple.com/documentation/foundation/nsdata/writingoptions/completefileprotectionuntilfirstuserauthentication)
- [Ocultar datos sensibles en widgets, Swift Senpai](https://swiftsenpai.com/development/hide-sensitive-widget-data/)
- [Estadísticas de notificaciones push, Business of Apps](https://www.businessofapps.com/marketplace/push-notifications/research/push-notifications-statistics/)
- [One Line a Day - Simple Diary, Google Play](https://play.google.com/store/apps/details?id=net.unifar.mydiary)
- [Tope de 64 avisos pendientes en iOS, Apple Developer Forums](https://developer.apple.com/forums/thread/811171)
- [Los 64 más próximos se quedan, el resto se descarta, Apple Developer Forums](https://developer.apple.com/forums/thread/765490)
- [Data Safety: qué es recogida y la excepción del cifrado de extremo a extremo, Play Console Help](https://support.google.com/googleplay/android-developer/answer/10787469?hl=en)
- [Recomendaciones de seguridad para copias, Android Developers](https://developer.android.com/privacy-and-security/risks/backup-best-practices)
- [El App Group entra en la copia de iCloud, Apple Developer Forums](https://developer.apple.com/forums/thread/693766)
- [Qué incluye la copia de iCloud, Apple Support](https://support.apple.com/en-us/108770)
- [Multitarea sin capturas con `setRecentsScreenshotEnabled`, Tomáš Repčík](https://tomasrepcik.dev/blog/2023/2023-12-09-android-securing-screen/)
- [Pantalla de recientes, Android Developers](https://developer.android.com/guide/components/activities/recents)
- [Zip en Okio, solo lectura y JVM, changelog de Okio](https://square.github.io/okio/changelog/)
- [kmp-zip, zip multiplataforma de lectura y escritura](https://github.com/henrik242/kmp-zip)
- [Entitlement de Journaling Suggestions, Apple Developer](https://developer.apple.com/documentation/bundleresources/entitlements/com.apple.developer.journal.allow)
- [Day One adopta Journaling Suggestions, iDrop News](https://www.idropnews.com/news/day-one-gains-support-for-ios-172-journalling-suggestions/203880/)
- [FiveFoldFury, App Store](https://apps.apple.com/us/app/fivefoldfury/id6747382328)
- [The Five Fold Network, Google Play](https://play.google.com/store/apps/details?id=com.lightcast.thefivefoldnetwork)
