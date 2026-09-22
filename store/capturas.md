# Capturas y gráficos de las fichas

Seis escenas, las mismas en las dos tiendas, en **en-US** y **es-ES**. Portugués, alemán y francés
heredan las de en-US, que es lo que hacen Play y App Store cuando un idioma no trae las suyas. Se
sacan con un diario de demostración generado, nunca con uno real: un diario de verdad en una ficha
pública es justo lo que esta app promete no hacer.

Herramientas (`docs/tecnico.md` 3):

- `tools/demo/generar.py`: escribe el diario de demostración.
- `tools/store/capturas.py`: el de MoodTraker, con las seis escenas y los tres tamaños de abajo.
- `tools/store/cabecera.py`: el de MoodTraker, con el motivo del icono de Purl.

---

## 1. Tamaños

| Destino | Dispositivo | Captura cruda | Imagen final |
|---|---|---|---|
| `play` | Emulador Pixel 8, API 36 | 1080x2400 | 1200x2100 PNG |
| `iphone` | Simulador iPhone 17 Pro Max | 1320x2868 | 1320x2868 PNG (6,9") |
| `ipad` | Simulador iPad Pro 13" | 2064x2752 | 2064x2752 PNG (13") |

Play rechaza una captura cuyo lado largo pase del doble del corto, y por eso la de Play va en un
marco de 1200x2100. Las de Apple conservan el tamaño del dispositivo, que es el que exige App Store
Connect; el marco es el mismo, escalado. Con 6,9" y 13", App Store rellena solo los tamaños menores.

```bash
python3 tools/store/capturas.py <carpeta-de-crudas> <idioma> <destino>
```

Espera dentro `01_hoy.png` a `06_ajustes.png` y deja el resultado en
`store/screenshots/<destino>/<idioma>/`.

## 2. Las seis escenas

| Fichero | Pantalla | Qué tiene que verse |
|---|---|---|
| `01_hoy` | Hoy, estado D (`docs/pantallas.md` 4.2) | La línea de hoy escrita, la racha, y debajo los dos años anteriores, el de hace un año con foto. Sin teclado |
| `02_ano` | Año, el año en curso | La rejilla llena hasta hoy con huecos sueltos, y el recuento, que va debajo: desplazada hasta que se vean las 31 filas y el recuento a la vez |
| `03_buscar` | Año con la búsqueda abierta | `coffee` en en-US y `cafe` sin tilde en es-ES: salen resultados de tres años, que enseña de paso el plegado de acentos. Sin teclado: la tecla de buscar lo esconde |
| `04_widgets` | Pantalla de inicio del sistema | El widget de hoy (2x2) y el del año (4x2) sobre un fondo de pantalla liso del sistema |
| `05_tarjeta` | Compartir, tarjeta del año | La tarjeta de 1080x1350 en la vista previa, antes de la hoja del sistema |
| `06_ajustes` | Ajustes | Bloqueo encendido, recordatorio a las 21:00, la fila de portadas con salvia elegida |

La 06 necesita un bloqueo de dispositivo para poder encender el de Purl: en el emulador, un PIN en
los ajustes del sistema; en el simulador, *Features > Face ID > Enrolled* y *Matching Face* cuando lo
pida.

## 3. Titulares

Dos líneas por escena. Es lo único que se lee en la tira de la ficha.

| Escena | en-US | es-ES |
|---|---|---|
| 01 | One line today. / Read it again next year. | Una línea hoy. / Vuelve a leerla en un año. |
| 02 | Your whole year / on one screen | Tu año entero / en una pantalla |
| 03 | Find any day, / in any year | Encuentra cualquier día, / de cualquier año |
| 04 | On your home screen, / never your words | En tu pantalla de inicio, / sin tus palabras |
| 05 | Share the year, / not the diary | Comparte el año, / no el diario |
| 06 | Lock, reminder and backup. / All three free. | Bloqueo, recordatorio y copia. / Los tres, gratis. |

Fondos, uno por escena, los seis primeros pasteles de la paleta aclarados como en MoodTraker:
`#F9ECEF`, `#FBF0E6`, `#F8F4E2`, `#E9F1E5`, `#E4F0EC`, `#E6EDF7`.

## 4. El diario de demostración

```bash
python3 tools/demo/generar.py --idioma es-ES [--hoy AAAA-MM-DD]
```

`--hoy` es por defecto el día en que se ejecuta: el diario se construye alrededor de la fecha del
dispositivo, así que no hay que tocar el reloj del emulador ni del simulador. Deja
`tools/demo/salida/<idioma>/entries.json` y `tools/demo/salida/<idioma>/photos/`. `tools/demo/salida/`
va en `.gitignore`.

Reglas, todas fijas para que dos ejecuciones el mismo día den el mismo diario:

- Tres años: del 1 de enero de `hoy.year - 2` hasta `hoy`, ambos incluidos.
- Un día está escrito si `(fecha.toordinal() * 7919) % 10 < 8`, más estos, siempre: el 1 de enero de
  los tres años, los doce días anteriores a `hoy` (racha de 13 con hoy), `hoy`, los dos
  aniversarios de `hoy` y las cuatro fechas de búsqueda.
- El texto de un día de relleno es `RELLENO[idioma][fecha.toordinal() % 14]`.
- `late` siempre `false`. Ninguna etiqueta ni ánimo.
- El programa se niega con `--hoy` el 1 de enero o el 29 de febrero: el primero sacaría el hito del
  aniversario y el de tres años en la escena 01, y el segundo no tiene años anteriores. También se
  niega del 2 al 10 de enero: `hoy - 9 días` cae el año anterior y la búsqueda de la escena 03 se
  queda en dos años.
- `settings`: `{"reminderOn": true, "reminderOffered": true, "cover": "sage", "lastBackup": hoy,
  "backupNoticeDone": true, "pro": true}`. Así no sale ni la oferta del recordatorio ni el aviso de
  copia, y los widgets Pro se pintan. `pro` es la caché: con la clave de RevenueCat a `null` nadie la
  corrige.
- Fotos: tres, de `tools/demo/fotos/1.jpg`, `2.jpg` y `3.jpg`, que pones tú (fotos propias, sin
  personas ni marcas; la carpeta va en `.gitignore`). Se reescalan a 1024 px de lado largo y se
  guardan como `p-00000001.jpg` a `p-00000003.jpg`. La 1 va en `hoy - 1 año`; la 2 en `hoy - 30
  días`; la 3 en `hoy - 120 días`. Sin la carpeta, el diario sale sin fotos y avisa.

Textos fijos:

| Fecha | en-US | es-ES |
|---|---|---|
| `hoy` | Market with Leo. We bought far too many peaches. | Mercado con Leo. Compramos demasiados melocotones. |
| `hoy - 1 año` | Same street, new flat. First night among boxes. | Misma calle, piso nuevo. Primera noche entre cajas. |
| `hoy - 2 años` | Rained all day. Painted the hallway green, no regrets. | Llovió todo el día. Pintamos el pasillo de verde, sin arrepentimientos. |
| `hoy - 2 años - 1 día` | Coffee with Ana at the place by the river. Two hours gone. | Café con Ana en el sitio del río. Se nos fueron dos horas. |
| `hoy - 1 año - 1 día` | New coffee grinder. The kitchen smells like a café. | Molinillo de café nuevo. La cocina huele a cafetería. |
| `hoy - 45 días` | Coffee on the balcony before anyone was awake. | Café en el balcón antes de que nadie se despertara. |
| `hoy - 9 días` | Too much coffee, too little sleep. Still a good day. | Demasiado café, poco sueño. Aun así, buen día. |

`RELLENO`, catorce por idioma, en este orden:

| # | en-US | es-ES |
|---|---|---|
| 0 | Long walk after work. The light was orange the whole way home. | Paseo largo al salir del trabajo. Luz naranja todo el camino. |
| 1 | Soup, a blanket and two episodes. Exactly what the day needed. | Sopa, manta y dos capítulos. Justo lo que pedía el día. |
| 2 | Finished the book. The last chapter was worth the wait. | Terminé el libro. El último capítulo mereció la espera. |
| 3 | Rain all morning, sun by five. Ran anyway. | Lluvia toda la mañana, sol a las cinco. Salí a correr igual. |
| 4 | Lunch with Marta. We laughed about the same old story. | Comida con Marta. Nos reímos de la misma historia de siempre. |
| 5 | Fixed the kitchen tap myself. Small victory. | Arreglé el grifo de la cocina sin ayuda. Pequeña victoria. |
| 6 | Bought tomatoes that actually taste like tomatoes. | Compré tomates que saben a tomate. |
| 7 | Slow Sunday. Nothing planned, nothing missed. | Domingo lento. Nada planeado, nada echado de menos. |
| 8 | First swim of the year. Freezing and perfect. | Primer baño del año. Helada y perfecta. |
| 9 | Called grandma. She remembered my birthday before I did. | Llamé a la abuela. Se acordó de mi cumpleaños antes que yo. |
| 10 | The train was late; found a new bakery while waiting. | El tren llegó tarde; encontré una panadería nueva esperando. |
| 11 | Planted basil on the balcony. Fingers crossed. | Planté albahaca en el balcón. A ver si prende. |
| 12 | Long day, but the presentation went well. | Día largo, pero la presentación salió bien. |
| 13 | Watched the storm from the window with tea. | Vi la tormenta desde la ventana con un té. |

El check del programa: al terminar comprueba que ninguna fecha pasa de `hoy`, que `hoy` no dispara
ningún hito (`docs/tecnico.md` 6.3) y que hay exactamente tres entradas con foto.

## 5. Meter el diario en la app

Con la build de debug instalada y la app **cerrada**: si está abierta, al irse al fondo escribe su
propio diario encima.

Android:

```bash
adb shell am force-stop com.baltajmn.line
adb push tools/demo/salida/es-ES/. /data/local/tmp/demo/
adb shell run-as com.baltajmn.line sh -c 'rm -rf files/photos files/entries.bak.json; mkdir -p files/photos; cp /data/local/tmp/demo/entries.json files/; cp /data/local/tmp/demo/photos/* files/photos/'
adb shell cmd locale set-app-locales com.baltajmn.line --locales es-ES
```

iOS (cada simulador tiene su contenedor: se repite en el iPhone y en el iPad):

```bash
xcrun simctl terminate booted com.baltajmn.line
D="$(xcrun simctl get_app_container booted com.baltajmn.line data)/Library/Application Support"
rm -rf "$D/photos" "$D/entries.bak.json" && mkdir -p "$D/photos"
cp tools/demo/salida/es-ES/entries.json "$D/" && cp tools/demo/salida/es-ES/photos/* "$D/photos/"
xcrun simctl launch booted com.baltajmn.line -AppleLanguages "(es)" -AppleLocale es_ES
```

Abrir la app una vez tras copiar: al arrancar escribe `widget.json`, que es lo que pintan los
widgets de la escena 04.

## 6. Barra de estado limpia

Android, modo demo de SystemUI:

```bash
adb shell settings put global sysui_demo_allowed 1
adb shell am broadcast -a com.android.systemui.demo -e command enter
adb shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 0941
adb shell am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged false
adb shell am broadcast -a com.android.systemui.demo -e command network -e wifi show -e level 4
adb shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false
```

iOS:

```bash
xcrun simctl status_bar booted override --time 9:41 --batteryState charged --batteryLevel 100 --cellularBars 4 --wifiBars 3
```

Capturas crudas: `adb exec-out screencap -p > 01_hoy.png` y `xcrun simctl io booted screenshot
01_hoy.png`. Modo claro en todas.

## 7. Gráfico de cabecera de Play e icono de la ficha

`tools/store/cabecera.py` deja `store/feature/<idioma>.png` (1024x500, los cinco idiomas) y
`store/icon-512.png`. Fondo crema, "Purl" en Literata a la izquierda, la línea de abajo debajo, y a
la derecha las cinco vueltas de cápsulas del icono (`docs/pantallas.md` 13) sobre crema.

| Idioma | Línea |
|---|---|
| en-US | One line a day. Five years on one page. |
| es-ES | Una línea al día. Cinco años en una página. |
| pt-BR | Uma linha por dia. Cinco anos em uma página. |
| de-DE | Eine Zeile am Tag. Fünf Jahre auf einer Seite. |
| fr-FR | Une ligne par jour. Cinq ans sur une page. |

## 8. Dónde se suben

Todo **a mano**: la API de listings solo escribe texto.

- Play Console, *Crecer > Presencia en la tienda > Ficha principal*: icono, cabecera por idioma y las
  seis de `play/` en en-US y es-ES. Sin capturas de tablet en v1.0: Play marca la app como "no
  optimizada para tablets" en pantallas grandes, que es asumible.
- App Store Connect, página de la versión, por idioma: las seis de `iphone/` en el hueco de 6,9" y las
  seis de `ipad/` en el de 13".
