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
   categoría entera, y en un diario íntimo pesa más que en un tracker de hábitos.
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
- [ ] Recordatorio diario local a la hora que elijas, apagado hasta que lo enciendas.
- [ ] Bloqueo de la app con la biometría del sistema y respaldo al código del dispositivo. Gratis,
      apagado por defecto.
- [ ] Widget de hoy en las dos plataformas, **de solo lectura**: la fecha, si el día ya está escrito
      y si hay recuerdo disponible. Nunca el texto.
- [ ] Exportar a JSON (reimportable) y a Markdown. Importar la copia propia. Gratis para siempre.
- [ ] Aviso único a los 30 días para que hagas una copia, y fecha de la última copia en Ajustes.
- [ ] Tarjeta para compartir de 1080x1350: por defecto solo forma (días escritos, racha, año), y
      el texto de una entrada solo si el usuario la elige a mano.
- [ ] Foto del día: una por entrada, opcional. Tres entradas con foto gratis, el resto en Pro.
- [ ] Cinco idiomas: inglés, español, portugués, alemán y francés.
- [ ] Tres pantallas: **Hoy**, **Año**, **Ajustes**. Sin librería de navegación.

### v1.1, lo que la gente pedirá en las reseñas

- **El libro en PDF maquetado**: una página por día con los bloques de cada año, portada y
  tipografía. Es el gancho de marketing más fuerte que tiene el concepto.
- Widget de pantalla de bloqueo en iOS, widget del año, Siri y Atajos, baldosa de Ajustes rápidos
  en Android.
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
| Editar y borrar el pasado | Editable siempre, sin versiones. Borrar pide confirmación. Una entrada que queda vacía se elimina del mapa, para que la rejilla y la búsqueda no mientan |
| Qué escribe el widget | **Nada.** A diferencia de Quilt y MoodTraker, el widget es de solo lectura y su intent solo abre la app en Hoy |
| Qué se ve con el bloqueo activo | Con `lockEnabled`, ni el widget ni la notificación imprimen texto de ninguna entrada. Solo estado |
| Pérdida del dispositivo | Sin sincronización, la defensa es la copia del sistema más el aviso de los 30 días y la fecha de la última copia siempre visible |
| Nombre, `applicationId`, bundle id y App Group | Se fijan **antes de la primera línea de código**. Son irreversibles tras publicar |

El widget de solo lectura merece un párrafo propio: elimina de raíz la clase de fallo más cara de la
familia, la de un widget de Swift que reescribe el fichero entero y borra los campos que su `struct`
no declara. En Quilt y en MoodTraker ese contrato existe porque el widget marca un hábito o un ánimo
de un toque. Aquí no hay acción de un toque que justifique escribir: escribir es teclear. Si algún
día entra dictado por Siri, vuelve el contrato de paridad de campos y hay que anotarlo en `CLAUDE.md`
el mismo día.

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

En Android el receptor comprueba si el día ya tiene línea y se calla. En iOS el
`UNCalendarNotificationTrigger` repetitivo no puede comprobarlo, y la alternativa (disparadores
sueltos renovados al abrir la app) choca con el tope de 64 pendientes y falla peor. Se asume, y el
texto se escribe para que sirva en los dos casos: *un momento para tu línea de hoy*.

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

### Widgets: privacidad contra enganche

El widget de un tracker de hábitos puede enseñar el dato entero porque el dato es abstracto. Aquí el
dato es una frase íntima en una pantalla que ve cualquiera que mire de reojo.

**El widget muestra estructura, nunca contenido**: la fecha, si el día está escrito, y si hay
recuerdo disponible. Tocarlo abre la app en Hoy. Esto vale para Glance, para WidgetKit y para el
widget de pantalla de bloqueo de iOS. Al no imprimir texto nunca, el widget no depende de
`redacted(reason:)` para ser seguro, aunque se respete cuando el usuario lo activa.

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
| Rejilla del año y búsqueda de texto | Widget de pantalla de bloqueo y widget del año |
| Recordatorio diario | Siri, Atajos y baldosa de Ajustes rápidos |
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

| App | Escaparate | Lanzamiento |
|---|---|---|
| Quilt | 9,99 EUR | 6,99 EUR |
| **Este diario** | **8,99 EUR** | **6,49 EUR las primeras 4 a 6 semanas** |
| MoodTraker | 7,99 EUR | 5,49 EUR |

Queda entre las dos hermanas y muy por debajo de un solo año de cualquier competidor de peso: 49,99 $
de Day One Silver, 49,99 $ de Journey, 19,99 $ del clon más visible en App Store. Dentro del pago
único la horquilla de la categoría va de 2,99 $ (DayGram, muy básico) a 54,99 $, así que 8,99 EUR es
cómodo. **Precios regionales activados en las dos tiendas**: es lo único que hace que "asequible" no
sea solo una palabra, y no cuesta una línea de código.

### Lo que llega al bolsillo

```
8,99 EUR escaparate
/ 1,21 (IVA 21%)  = 7,43 EUR
- 15 % comisión   = 6,32 EUR netos
```

| Objetivo | Ventas/mes | Descargas/mes (2,5 % de conversión) |
|---|---|---|
| 500 EUR | ~80 | ~3.200 |
| 1.000 EUR | ~159 | ~6.360 |
| 3.000 EUR | ~475 | ~19.000 |

A precio de lanzamiento (4,56 EUR netos) los mismos objetivos piden ~110, ~220 y ~659 ventas. Por eso
el descuento es temporal y no la base del cálculo. El 2,5 % de conversión es la hipótesis de trabajo
de la familia, no un dato medido de esta categoría.

### Reglas

1. **El plan gratis es la prueba.** No existen pruebas gratuitas para compras únicas.
2. **El paywall aparece al chocar**: al poner la cuarta foto, al tocar una portada de Pro o al
   colocar el widget del año. Nunca al arrancar.
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
tiene por qué coincidir, igual que Quilt vive en `com.baltajmn.habit`.

Descartado de entrada el nombre literal: **One Line a Day**, **5 Year Journal** y variantes ya los
usan al menos cinco competidores directos. Hunde la búsqueda por nombre exacto y rompe la convención
de la familia, que nombra con metáfora.

| Candidato | Metáfora del icono | Nota |
|---|---|---|
| **Fivefold** (recomendado) | Una hoja pastel plegada en cinco capas que al abrirse enseña los cinco años | Lleva "five" dentro, que es exactamente lo que se busca en la tienda. Sin colisión encontrada |
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
| **Manifiesto de privacidad de iOS** | `PrivacyInfo.xcprivacy` con `FileTimestamp` (C617.1) y `NSUserDefaults` (CA92.1). Una app que escribe un fichero en cada guardado entra de lleno. Sin él, rechazo automático `ITMS-91053` / `ITMS-91061` en la primera subida |
| **App Privacy y Data Safety** | Con RevenueCat dentro se declaran historial de compras e identificadores. El texto de las entradas no sale del dispositivo, y eso se dice explícitamente en los dos formularios y en la política |
| **Clasificación por edad** | Cuestionario nuevo de Apple. El usuario escribe lo que quiere pero no lo comparte con nadie: no es contenido generado por usuarios a efectos de moderación. En Play, público objetivo a partir de 13 para no entrar en la política de Familias |
| **Copia automática del sistema** | En Android, Auto Backup sube `entries.json` al Drive del usuario (tope de 25 MB por app; el texto no llega a 1 MB, las fotos sí pueden). Se deja activada, porque perder el diario es peor, y se declara en la política. Las fotos se excluyen de `dataExtractionRules`. En iOS hay que comprobar en dispositivo que el contenedor del App Group entra en la copia de iCloud |
| **Prueba cerrada de Google** | 12 probadores durante 14 días continuos, **por app**, más hasta 7 días de revisión del acceso a producción. Si el número baja de 12 hay que recuperarlo y volver a sostenerlo. Es el camino crítico del calendario |
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
data/Storage.kt         expect: JSON atómico con copia .bak (filesDir / App Group)
data/LineRepository.kt  fuente única de verdad, estado Compose, límite de fotos del plan gratis
data/Search.kt          filtro lineal en memoria sobre el Journal ya cargado
data/Photos.kt          expect: selector del sistema, reescalado a 512 px, caché en memoria
data/Lock.kt            expect: biometría con respaldo al código del dispositivo
data/Reminder.kt        expect: un aviso diario, se calla si el día ya tiene línea (con tests)
data/Backup.kt          expect: guardar y elegir fichero para exportar e importar
data/Export.kt          JSON y Markdown, puro común, sin API de plataforma
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
vieja. En la copia de seguridad sí viajan en base64 dentro del propio JSON, como en MoodTraker: un
fichero es algo que una persona se puede mandar por correo, un zip es algo que pierde.

`LineStore.swift` reimplementa el modelo para el widget, **en solo lectura**. Mientras siga siendo de
solo lectura, el contrato de paridad de campos de Quilt y MoodTraker no aplica: el widget no puede
borrar un campo que no declara porque no escribe. El día que escriba, vuelve.

### El problema que las hermanas no tienen: el fichero crece

Un hábito guarda un booleano y un ánimo guarda un id. Aquí se guarda texto libre.

Cinco años de líneas de 200 caracteres son unos 365.000 caracteres, del orden de 400 a 500 KB con la
sobrecarga del JSON. Veinte años, 1,5 a 2 MB. Es una estimación, no una medida, y sigue siendo un
fichero pequeño.

Lo que deja de valer no es el tamaño, es **cuántas veces se escribe**:

- **Autoguardado con rebote de 700 a 1.000 ms** tras la última pulsación, más guardado forzado al ir
  a segundo plano o al salir de la pantalla. Guardar por tecla reescribiría el diario entero una vez
  por carácter, y también la copia `.bak`.
- La señal para partir el fichero **no es cuántos KB pesa**, es medir en el dispositivo más antiguo
  soportado el tiempo de serializar y escribir. Si el percentil 95 se acerca al intervalo de rebote
  (referencia: 50 a 100 ms, donde un guardado síncrono ya se nota como tirón), se parte por años
  (`entries-2030.json`), se mantiene en memoria solo el año activo y el widget lee solo el del año en
  curso.

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

El agujero real no está en la API: está en que el widget y la notificación leen el fichero fuera del
proceso de la app y no pueden pedir biometría antes de pintar. Por eso, con el bloqueo activo, ni uno
ni otra imprimen texto. Es una decisión de producto tanto como técnica y se toma antes de publicar,
no después de la primera reseña.

### Cifrado en reposo: teatro

El fichero vive en el sandbox de la app y, en iOS, ya bajo
`NSFileProtectionCompleteUntilFirstUserAuthentication`, que es justo lo que permite al widget
funcionar con el teléfono bloqueado. Cifrar por encima exigiría guardar una clave que el propio
proceso (y el del widget) pueda leer sin intervención del usuario: no protege de nadie que no pudiera
ya leer el fichero. Ese esfuerzo rinde más en ocultar el contenido en widget y notificación, que es
el agujero de verdad.

### Tests mínimos

1. Ida y vuelta de serialización con comillas, saltos de línea, emoji y acentos. Más crítico que en
   las hermanas porque el valor no está acotado a un conjunto cerrado.
2. Escritura atómica: interrumpir a mitad y comprobar que la lectura cae a `.bak` sin perder el día.
3. Vista de años anteriores: agrupar por sufijo `mm-dd`, con el 29 de febrero como caso propio.
4. Corte del día a las 03:00, racha y "hoy ya tiene línea", con medianoche y cambio de huso.
5. Migración: un fichero de la versión anterior se sigue leyendo tras añadir un campo opcional.

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
11. Recordatorio local en las dos plataformas, permiso al encenderlo.
12. Exportar JSON y Markdown, importar la copia propia, aviso de copia a los 30 días.
13. Bloqueo con biometría.
14. Widgets de solo lectura: Glance y WidgetKit.
15. Foto del día y paywall: RevenueCat, producto no consumible, restaurar, compra probada de verdad.
16. Tarjeta para compartir en `Canvas`.
17. Formularios: manifiesto de privacidad, App Privacy, Data Safety, clasificación por edad.
18. Capturas y textos de ficha, cinco idiomas, dos tiendas.
19. Beta corta con gente real.
20. Envío a revisión de Apple y solicitud de acceso a producción en Play al cumplirse los 14 días.

### Estimación

**Producto, unas 17,5 jornadas**

| Tarea | Jornadas |
|---|---|
| Andamiaje copiado y renombrado | 1 |
| Modelo, almacén atómico, versión del fichero | 1 |
| Pantalla Hoy, autoguardado, corte de día | 2 |
| Vista de años anteriores y ecos del primer año | 1,5 |
| Pantalla Año: rejilla y búsqueda | 1,5 |
| Ajustes completos | 1 |
| `Strings.kt`, cinco idiomas | 1 |
| Recordatorio en las dos plataformas | 1 |
| Exportar, importar y aviso de copia | 1 |
| Bloqueo biométrico | 0,5 |
| Widget de Android (Glance) | 1 |
| Widget de iOS (WidgetKit) | 1,5 |
| Foto del día | 1 |
| Paywall y RevenueCat | 1 |
| Tarjeta para compartir | 1 |
| Pulido en dispositivo real, claro y oscuro, texto grande | 1,5 |

**Tienda, unas 4 jornadas**: altas y trámites 0,5; RevenueCat 0,5; probar compras de verdad 1;
política y formularios 0,5; capturas y textos en cinco idiomas 1,5.

**Riesgo, 3 jornadas**: 2 de beta con gente real, 1 de colchón por rechazo.

**Total: unas 24,5 jornadas. De 5 a 7 semanas de calendario**, mandadas por los 14 días de prueba
cerrada de Google más hasta 7 de revisión del acceso a producción. Si la prueba cerrada no arranca en
la primera semana, el calendario se va a 9 semanas aunque el código esté terminado.

---

## 11. Riesgos abiertos

1. **El valor central no existe hasta el año dos.** La retención se gana o se pierde entre el día 4 y
   el día 30, y ahí solo hay el recordatorio, el eco corto y la rejilla. Es el riesgo número uno del
   concepto y no lo arregla ninguna función: lo arregla que la primera semana sea muy buena.
2. **El paquete de pago puede seguir pareciendo fino** aunque entre la foto. Se mide en la beta, no
   aquí: si en dos semanas no compra nadie, el problema es el paquete, no el precio.
3. **"He cambiado de móvil y he perdido mi diario"** es la reseña de una estrella previsible. El
   aviso de copia y la copia automática del sistema son la única defensa antes de v1.2.
4. **El nombre.** Braid colisiona con marca registrada de software conocida; Fivefold no tiene
   colisión encontrada, pero la comprobación se ha hecho por búsqueda web, no en EUIPO ni dentro de
   las consolas.
5. **La tarjeta sin texto puede compartirse menos** que el mosaico de MoodTraker, y con ella el canal
   orgánico.
6. **Auto Backup sube el diario al Drive del usuario por defecto.** Es la defensa contra la pérdida y
   a la vez algo que hay que declarar en la política de una app que se vende como íntima.
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
