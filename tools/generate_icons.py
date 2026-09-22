#!/usr/bin/env python3
"""Regenerates every Purl icon from one geometry. Needs rsvg-convert (brew install librsvg).

The metaphor is the knit stitch: five rows of stitches, one per year of the diary, each row a
pastel of the family; the last stitch, today's, in cream. Geometry: docs/pantallas.md 13.

Run from anywhere:  python3 tools/generate_icons.py
"""
import math
import os
import pathlib
import subprocess

S = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(S)

# The app's own dark tokens, so the icon is a slice of the product and not a separate brand.
BG_TOP, BG_BOT, BG_FLAT = "#2C2820", "#17150F", "#221E17"
CREAM = "#FBF8F3"
ROWS = ["#F0AFBE", "#EDDC98", "#B6D6AB", "#A2C3E9", "#D9AFE6"]  # rose, butter, sage, sky, lilac

CANVAS = 1024
CAP_W, CAP_H, CAP_R = 110, 60, 30
GAP_X, GAP_Y = 20, 40
BLOCK_W = 4 * CAP_W + 3 * GAP_X          # 500
BLOCK_H = 5 * CAP_H + 4 * GAP_Y          # 460
LEFT = (CANVAS - BLOCK_W) / 2            # 262
# Ten below the true centre: the block reads as centred once the eye weighs the gradient.
FIRST_ROW_CENTRE = (CANVAS - BLOCK_H) / 2 + CAP_H / 2 + 10   # 312
TILT = 8


def capsules():
    """Every stitch: its box, its colour and whether it is the cream one of today."""
    for row in range(5):
        for col in range(4):
            x = LEFT + col * (CAP_W + GAP_X)
            y = FIRST_ROW_CENTRE + row * (CAP_H + GAP_Y) - CAP_H / 2
            today = row == 4 and col == 3
            # Rows two and four lean one way, the rest the other: the zigzag of knitting, with no
            # thread drawn.
            tilt = TILT if row % 2 == 1 else -TILT
            yield x, y, CREAM if today else ROWS[row], tilt, today


def svg(size, shape, scale=1.0, flat=False, mono=None):
    if shape == "circle":
        plate = f'<circle cx="{CANVAS/2}" cy="{CANVAS/2}" r="{CANVAS/2}" fill="{"url(#bg)"}"/>'
    elif shape == "rounded":
        plate = f'<rect width="{CANVAS}" height="{CANVAS}" rx="{CANVAS*0.22}" fill="url(#bg)"/>'
    elif shape == "none":
        plate = ""
    else:
        plate = f'<rect width="{CANVAS}" height="{CANVAS}" fill="url(#bg)"/>'
    if flat:
        plate = f'<rect width="{CANVAS}" height="{CANVAS}" fill="{BG_FLAT}"/>'

    body = [plate]
    for x, y, colour, tilt, _ in capsules():
        cx, cy = x + CAP_W / 2, y + CAP_H / 2
        body.append(
            f'<rect x="{x}" y="{y}" width="{CAP_W}" height="{CAP_H}" rx="{CAP_R}" '
            f'fill="{mono or colour}" transform="rotate({tilt} {cx} {cy})"/>'
        )
    inner = "".join(body[1:])
    if scale != 1.0:
        middle = CANVAS / 2
        inner = f'<g transform="translate({middle} {middle}) scale({scale}) translate({-middle} {-middle})">{inner}</g>'
    return (
        f'<svg xmlns="http://www.w3.org/2000/svg" width="{size}" height="{size}" '
        f'viewBox="0 0 {CANVAS} {CANVAS}"><defs>'
        f'<linearGradient id="bg" x1="0" y1="0" x2="0" y2="1">'
        f'<stop offset="0" stop-color="{BG_TOP}"/><stop offset="1" stop-color="{BG_BOT}"/>'
        f'</linearGradient></defs>' + body[0] + inner + "</svg>"
    )


def png(svg_text, out, size):
    src = f"{S}/_tmp.svg"
    pathlib.Path(src).write_text(svg_text)
    pathlib.Path(out).parent.mkdir(parents=True, exist_ok=True)
    subprocess.run(["rsvg-convert", "-w", str(size), "-h", str(size), src, "-o", out], check=True)


# --- iOS: full bleed, the system applies its own mask ---
png(svg(CANVAS, "square"), f"{ROOT}/iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/app-icon-1024.png", 1024)

# --- Android legacy launcher icons (API 24 and 25 have no adaptive icons) ---
for folder, size in [("mdpi", 48), ("hdpi", 72), ("xhdpi", 96), ("xxhdpi", 144), ("xxxhdpi", 192)]:
    base = f"{ROOT}/androidApp/src/main/res/mipmap-{folder}"
    png(svg(CANVAS, "rounded"), f"{base}/ic_launcher.png", size)
    png(svg(CANVAS, "circle", scale=0.82), f"{base}/ic_launcher_round.png", size)

res = f"{ROOT}/androidApp/src/main/res"

# The 108dp canvas keeps its content inside a 66dp circle: the block has to fit that diagonal.
SAFE = 66 / 108
BLOCK_SCALE = SAFE * CANVAS / math.hypot(BLOCK_W, BLOCK_H)


def vector(size_dp, colour=None, scale=1.0):
    middle = CANVAS / 2
    lines = [
        '<?xml version="1.0" encoding="utf-8"?>',
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"',
        f'    android:width="{size_dp}dp" android:height="{size_dp}dp"',
        f'    android:viewportWidth="{CANVAS}" android:viewportHeight="{CANVAS}">',
        f'    <group android:pivotX="{middle}" android:pivotY="{middle}" '
        f'android:scaleX="{scale:.4f}" android:scaleY="{scale:.4f}">',
    ]
    for x, y, fill, tilt, _ in capsules():
        cx, cy = x + CAP_W / 2, y + CAP_H / 2
        lines.append(f'        <group android:pivotX="{cx}" android:pivotY="{cy}" android:rotation="{tilt}">')
        lines.append(
            f'            <path android:fillColor="{colour or fill}" '
            f'android:pathData="{rounded_rect(x, y, CAP_W, CAP_H, CAP_R)}"/>'
        )
        lines.append("        </group>")
    lines += ["    </group>", "</vector>"]
    return "\n".join(lines) + "\n"


def rounded_rect(x, y, w, h, r):
    return (
        f"M{x+r:.2f},{y:.2f} H{x+w-r:.2f} A{r:.2f},{r:.2f} 0 0 1 {x+w:.2f},{y+r:.2f} "
        f"V{y+h-r:.2f} A{r:.2f},{r:.2f} 0 0 1 {x+w-r:.2f},{y+h:.2f} "
        f"H{x+r:.2f} A{r:.2f},{r:.2f} 0 0 1 {x:.2f},{y+h-r:.2f} "
        f"V{y+r:.2f} A{r:.2f},{r:.2f} 0 0 1 {x+r:.2f},{y:.2f} Z"
    )


pathlib.Path(f"{res}/drawable/ic_launcher_background.xml").write_text(
    '<?xml version="1.0" encoding="utf-8"?>\n'
    '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n'
    '    android:width="108dp" android:height="108dp"\n'
    '    android:viewportWidth="108" android:viewportHeight="108">\n'
    f'    <path android:fillColor="{BG_FLAT}" android:pathData="M0,0h108v108h-108z"/>\n'
    "</vector>\n"
)
pathlib.Path(f"{res}/drawable-v24").mkdir(parents=True, exist_ok=True)
pathlib.Path(f"{res}/drawable-v24/ic_launcher_foreground.xml").write_text(vector(108, scale=BLOCK_SCALE))
# One colour and no cream stitch: the system paints this layer itself.
pathlib.Path(f"{res}/drawable/ic_launcher_monochrome.xml").write_text(
    vector(108, colour="#FFFFFFFF", scale=BLOCK_SCALE)
)
pathlib.Path(f"{res}/mipmap-anydpi-v26").mkdir(parents=True, exist_ok=True)
for name in ("ic_launcher.xml", "ic_launcher_round.xml"):
    pathlib.Path(f"{res}/mipmap-anydpi-v26/{name}").write_text(
        '<?xml version="1.0" encoding="utf-8"?>\n'
        '<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">\n'
        '    <background android:drawable="@drawable/ic_launcher_background" />\n'
        '    <foreground android:drawable="@drawable/ic_launcher_foreground" />\n'
        '    <monochrome android:drawable="@drawable/ic_launcher_monochrome" />\n'
        "</adaptive-icon>\n"
    )

# --- Notification icon: Android tints it white, so it is a flat silhouette of four stitches ---
notification = [
    '<?xml version="1.0" encoding="utf-8"?>',
    '<vector xmlns:android="http://schemas.android.com/apk/res/android"',
    '    android:width="24dp" android:height="24dp"',
    '    android:viewportWidth="24" android:viewportHeight="24">',
]
for index, (x, y) in enumerate([(3, 8), (13, 8), (3, 13), (13, 13)]):
    tilt = TILT if index >= 2 else -TILT
    notification.append(f'    <group android:pivotX="{x+4}" android:pivotY="{y+2}" android:rotation="{tilt}">')
    notification.append(
        f'        <path android:fillColor="#FFFFFFFF" android:pathData="{rounded_rect(x, y, 8, 4, 2)}"/>'
    )
    notification.append("    </group>")
notification.append("</vector>")
pathlib.Path(f"{ROOT}/shared/src/androidMain/res/drawable").mkdir(parents=True, exist_ok=True)
pathlib.Path(f"{ROOT}/shared/src/androidMain/res/drawable/ic_notification.xml").write_text(
    "\n".join(notification) + "\n"
)

pathlib.Path(f"{S}/_tmp.svg").unlink()
pathlib.Path(f"{S}/icon-master.svg").write_text(svg(CANVAS, "square"))
print("assets written")
