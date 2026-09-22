#!/usr/bin/env python3
"""Writes the demo diary the store screenshots are taken from (store/capturas.md 4).

Never a real diary: a diary of someone's own in a public listing is exactly what this app promises
not to do. Every rule here is fixed, so two runs on the same day give the same diary.

    python3 tools/demo/generar.py --idioma es-ES [--hoy AAAA-MM-DD]
"""
import argparse
import datetime
import json
import pathlib
import shutil
import subprocess
import sys

ROOT = pathlib.Path(__file__).resolve().parent
OUT = ROOT / "salida"
PHOTOS_IN = ROOT / "fotos"

FIXED = {
    "en-US": {
        "today": "Market with Leo. We bought far too many peaches.",
        "year1": "Same street, new flat. First night among boxes.",
        "year2": "Rained all day. Painted the hallway green, no regrets.",
        "d400": "Coffee with Ana at the place by the river. Two hours gone.",
        "d200": "New coffee grinder. The kitchen smells like a cafe.",
        "d45": "Coffee on the balcony before anyone was awake.",
        "d9": "Too much coffee, too little sleep. Still a good day.",
    },
    "es-ES": {
        "today": "Mercado con Leo. Compramos demasiados melocotones.",
        "year1": "Misma calle, piso nuevo. Primera noche entre cajas.",
        "year2": "Llovió todo el día. Pintamos el pasillo de verde, sin arrepentimientos.",
        "d400": "Café con Ana en el sitio del río. Se nos fueron dos horas.",
        "d200": "Molinillo de café nuevo. La cocina huele a cafetería.",
        "d45": "Café en el balcón antes de que nadie se despertara.",
        "d9": "Demasiado café, poco sueño. Aun así, buen día.",
    },
}

FILLER = {
    "en-US": [
        "Long walk after work. The light was orange the whole way home.",
        "Soup, a blanket and two episodes. Exactly what the day needed.",
        "Finished the book. The last chapter was worth the wait.",
        "Rain all morning, sun by five. Ran anyway.",
        "Lunch with Marta. We laughed about the same old story.",
        "Fixed the kitchen tap myself. Small victory.",
        "Bought tomatoes that actually taste like tomatoes.",
        "Slow Sunday. Nothing planned, nothing missed.",
        "First swim of the year. Freezing and perfect.",
        "Called grandma. She remembered my birthday before I did.",
        "The train was late; found a new bakery while waiting.",
        "Planted basil on the balcony. Fingers crossed.",
        "Long day, but the presentation went well.",
        "Watched the storm from the window with tea.",
    ],
    "es-ES": [
        "Paseo largo al salir del trabajo. Luz naranja todo el camino.",
        "Sopa, manta y dos capítulos. Justo lo que pedía el día.",
        "Terminé el libro. El último capítulo mereció la espera.",
        "Lluvia toda la mañana, sol a las cinco. Salí a correr igual.",
        "Comida con Marta. Nos reímos de la misma historia de siempre.",
        "Arreglé el grifo de la cocina sin ayuda. Pequeña victoria.",
        "Compré tomates que saben a tomate.",
        "Domingo lento. Nada planeado, nada echado de menos.",
        "Primer baño del año. Helada y perfecta.",
        "Llamé a la abuela. Se acordó de mi cumpleaños antes que yo.",
        "El tren llegó tarde; encontré una panadería nueva esperando.",
        "Planté albahaca en el balcón. A ver si prende.",
        "Día largo, pero la presentación salió bien.",
        "Vi la tormenta desde la ventana con un té.",
    ],
}


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--idioma", required=True, choices=sorted(FIXED))
    parser.add_argument("--hoy")
    args = parser.parse_args()

    lang = args.idioma
    today = datetime.date.fromisoformat(args.hoy) if args.hoy else datetime.date.today()
    if (today.month, today.day) in ((1, 1), (2, 29)):
        sys.exit("El 1 de enero saca el hito del aniversario y el 29 de febrero no tiene años anteriores.")

    fixed_dates = {
        today: "today",
        today.replace(year=today.year - 1): "year1",
        today.replace(year=today.year - 2): "year2",
        today - datetime.timedelta(days=400): "d400",
        today - datetime.timedelta(days=200): "d200",
        today - datetime.timedelta(days=45): "d45",
        today - datetime.timedelta(days=9): "d9",
    }
    always = set(fixed_dates)
    always |= {datetime.date(today.year - n, 1, 1) for n in (0, 1, 2)}
    always |= {today - datetime.timedelta(days=n) for n in range(1, 13)}

    entries = {}
    day = datetime.date(today.year - 2, 1, 1)
    while day <= today:
        if day in always or (day.toordinal() * 7919) % 10 < 8:
            key = fixed_dates.get(day)
            text = FIXED[lang][key] if key else FILLER[lang][day.toordinal() % 14]
            entries[day.isoformat()] = {"text": text}
        day += datetime.timedelta(days=1)

    out = OUT / lang
    shutil.rmtree(out, ignore_errors=True)
    (out / "photos").mkdir(parents=True)
    photo_days = [
        today.replace(year=today.year - 1),
        today - datetime.timedelta(days=30),
        today - datetime.timedelta(days=120),
    ]
    for index, source_day in enumerate(photo_days, start=1):
        source = PHOTOS_IN / f"{index}.jpg"
        if not source.exists():
            print(f"Sin {source}: el diario sale sin esa foto.")
            continue
        name = f"p-{index:08d}.jpg"
        shutil.copy(source, out / "photos" / name)
        # sips comes with macOS: no dependency for three photos once every few months.
        subprocess.run(["sips", "-Z", "1024", str(out / "photos" / name)], check=True,
                       stdout=subprocess.DEVNULL)
        key = source_day.isoformat()
        entries.setdefault(key, {"text": FILLER[lang][source_day.toordinal() % 14]})
        entries[key]["photo"] = name

    journal = {
        "version": 1,
        "entries": dict(sorted(entries.items())),
        "settings": {
            "reminderOn": True,
            "reminderOffered": True,
            "cover": "sage",
            "lastBackup": today.isoformat(),
            "backupNoticeDone": True,
            "pro": True,
        },
    }
    (out / "entries.json").write_text(json.dumps(journal, ensure_ascii=False, indent=2))

    check(journal["entries"], today)
    print(f"{len(entries)} lineas en {out}/entries.json")


def check(entries, today):
    """What the screenshots depend on: no future day, no milestone banner, three photos."""
    assert max(entries) <= today.isoformat(), "hay una fecha posterior a hoy"
    assert milestone(entries, today) is None, "hoy dispara un hito y taparia la pantalla"
    with_photo = sum(1 for e in entries.values() if "photo" in e)
    assert with_photo in (0, 3), f"{with_photo} fotos, tienen que ser tres (o ninguna)"
    if with_photo == 0:
        print("Aviso: sin tools/demo/fotos/1.jpg a 3.jpg, la escena 01 sale sin foto.")


def milestone(entries, today):
    """docs/tecnico.md 6.3, only to prove the demo diary never lands on one."""
    written = today.isoformat() in entries
    first = datetime.date.fromisoformat(min(entries))
    if today == first.replace(year=first.year + 1):
        return "Anniversary"
    if written and three_years_date(entries) == today:
        return "ThreeYears"
    if written and len(entries) in (100, 30, 1):
        return {100: "Hundred", 30: "Thirty", 1: "FirstLine"}[len(entries)]
    return None


def three_years_date(entries):
    seen = {}
    for key in sorted(entries):
        seen[key[5:]] = seen.get(key[5:], 0) + 1
        if seen[key[5:]] == 3:
            return datetime.date.fromisoformat(key)
    return None


if __name__ == "__main__":
    main()
