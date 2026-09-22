#!/usr/bin/env python3
"""Grafico de cabecera de Play (1024x500, los cinco idiomas) y el icono de 512 de la ficha.

Se lee a tamano de sello en la lista de la tienda: caben el nombre, una linea y el motivo de las
vueltas de punto, y nada mas. Detalle: store/capturas.md 7.
"""
import pathlib
import subprocess

CREMA = "#FBF8F3"
TINTA = "#39352E"
GRIS = "#8B8479"
# Las mismas cinco vueltas del icono, aqui sobre crema: docs/pantallas.md 13.
FILAS = ["#F0AFBE", "#EDDC98", "#B6D6AB", "#A2C3E9", "#D9AFE6"]
PUNTO_HOY = "#E7E0D4"

LINEAS = {
    "en-US": "One line a day. Five years on one page.",
    "es-ES": "Una línea al día. Cinco años en una página.",
    "pt-BR": "Uma linha por dia. Cinco anos em uma página.",
    "de-DE": "Eine Zeile am Tag. Fünf Jahre auf einer Seite.",
    "fr-FR": "Une ligne par jour. Cinq ans sur une page.",
}

CAP_W, CAP_H, CAP_R = 55, 30, 15
GAP_X, GAP_Y = 10, 20
IZQUIERDA, ARRIBA = 664, 125
TILT = 8


def puntos():
    """Veinte capsulas: la ultima en crema oscura, que es el punto de hoy sin fondo oscuro detras."""
    partes = []
    for fila in range(5):
        for col in range(4):
            x = IZQUIERDA + col * (CAP_W + GAP_X)
            y = ARRIBA + fila * (CAP_H + GAP_Y)
            color = PUNTO_HOY if (fila, col) == (4, 3) else FILAS[fila]
            giro = TILT if fila % 2 else -TILT
            partes.append(
                '<rect x="%d" y="%d" width="%d" height="%d" rx="%d" fill="%s" '
                'transform="rotate(%d %d %d)"/>'
                % (x, y, CAP_W, CAP_H, CAP_R, color, giro, x + CAP_W / 2, y + CAP_H / 2)
            )
    return "".join(partes)


def main():
    raiz = pathlib.Path(__file__).resolve().parents[2]
    salida = raiz / "store" / "feature"
    salida.mkdir(parents=True, exist_ok=True)
    for idioma, linea in LINEAS.items():
        # Literata es la del texto del diario; si no esta instalada, cualquier serifa de la casa.
        svg = """<svg xmlns="http://www.w3.org/2000/svg" width="1024" height="500" viewBox="0 0 1024 500">
  <rect width="1024" height="500" fill="{crema}"/>
  <text x="64" y="262" font-family="Literata, Georgia, serif" font-size="104" fill="{tinta}">Purl</text>
  <text x="66" y="318" font-family="Helvetica Neue, Helvetica, Arial, sans-serif" font-size="27"
        fill="{gris}">{linea}</text>
  {puntos}
</svg>""".format(crema=CREMA, tinta=TINTA, gris=GRIS, linea=linea, puntos=puntos())
        tmp = salida / (idioma + ".svg")
        tmp.write_text(svg, encoding="utf-8")
        subprocess.run(
            ["rsvg-convert", "-w", "1024", "-h", "500", str(tmp), "-o", str(salida / (idioma + ".png"))],
            check=True,
        )
        tmp.unlink()
        print(salida / (idioma + ".png"))

    icono = raiz / "store" / "icon-512.png"
    subprocess.run(
        ["rsvg-convert", "-w", "512", "-h", "512", str(raiz / "tools" / "icon-master.svg"), "-o", str(icono)],
        check=True,
    )
    print(icono)


if __name__ == "__main__":
    main()
