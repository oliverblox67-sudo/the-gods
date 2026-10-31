"""Procedural art for The Gods. Run from the project root: python tools/gen_textures.py"""
from __future__ import annotations

import math
import random
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter, ImageFont

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src" / "main" / "resources" / "assets" / "thegods"
ITEM_DIR = ASSETS / "textures" / "item"
ENTITY_DIR = ASSETS / "textures" / "entity"
ROOT_DIR = ROOT / "src" / "main" / "resources"

NAVY = (4, 14, 30, 255)
DEEP = (8, 44, 96, 255)
BLUE = (24, 118, 210, 255)
CYAN = (90, 220, 255, 255)
PALE = (210, 250, 255, 255)
GOLD = (240, 200, 110, 255)


def heart_texture() -> Image.Image:
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()

    # Classic 16x16 heart silhouette.
    rows = {
        2: (4, 11),
        3: (3, 12),
        4: (2, 13),
        5: (1, 14),
        6: (1, 14),
        7: (1, 14),
        8: (2, 13),
        9: (3, 12),
        10: (4, 11),
        11: (5, 10),
        12: (6, 9),
        13: (7, 8),
    }
    for y, (x0, x1) in rows.items():
        for x in range(x0, x1 + 1):
            t = (y - 2) / 11.0
            # Vertical tide gradient.
            r = int(30 + 80 * (1 - t))
            g = int(120 + 130 * (1 - t))
            b = int(190 + 65 * (1 - t))
            px[x, y] = (r, g, b, 255)

    # Dark outline.
    for y in range(1, 15):
        for x in range(1, 15):
            if px[x, y][3] == 0:
                continue
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < 16 and 0 <= ny < 16 and px[nx, ny][3] == 0:
                    px[nx, ny] = NAVY

    # Highlights and a small trident mark.
    for x, y in ((4, 4), (5, 4), (4, 5), (10, 3), (11, 3), (11, 4)):
        px[x, y] = PALE
    for y in range(6, 11):
        px[8, y] = (190, 245, 255, 255)
    for x in (6, 7, 8, 9, 10):
        px[x, 6] = (190, 245, 255, 255)
    px[6, 7] = (190, 245, 255, 255)
    px[10, 7] = (190, 245, 255, 255)

    return img


def trident_texture() -> Image.Image:
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()

    def put(x: int, y: int, color: tuple[int, int, int, int]) -> None:
        if 0 <= x < 16 and 0 <= y < 16:
            px[x, y] = color

    # Shaft.
    for y in range(6, 16):
        put(7, y, GOLD)
        put(8, y, (170, 130, 60, 255))

    # Central prong.
    for y in range(1, 7):
        put(7, y, CYAN)
        put(8, y, BLUE)

    # Side prongs.
    for y in range(2, 6):
        put(4, y, CYAN)
        put(11, y, CYAN)
        put(3 if y > 2 else 4, y, BLUE)
        put(12 if y > 2 else 11, y, BLUE)

    # Crossguard / branches.
    for x in range(4, 12):
        put(x, 6, GOLD)
    for y in (4, 5):
        put(5, y, CYAN)
        put(10, y, CYAN)

    # Outline.
    for y in range(16):
        for x in range(16):
            if px[x, y][3] == 0:
                continue
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < 16 and 0 <= ny < 16 and px[nx, ny][3] == 0:
                    px[nx, ny] = NAVY

    # Sparkles.
    for x, y in ((7, 0), (3, 1), (12, 1)):
        put(x, y, PALE)

    return img


def water_wings_texture() -> Image.Image:
    size = 64
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    px = img.load()
    rng = random.Random(3450)

    for y in range(size):
        t = y / (size - 1)
        r = int(30 + 120 * (1 - t))
        g = int(90 + 140 * (1 - t))
        b = int(170 + 80 * (1 - t))
        a = int(190 * (1 - t * 0.55))
        for x in range(size):
            wave = 0.5 + 0.5 * math.sin((x * 0.22) + (y * 0.11))
            alpha = int(a * (0.55 + 0.45 * wave))
            px[x, y] = (r, g, b, alpha)

    # Bright foam streaks.
    for _ in range(90):
        x = rng.randrange(size)
        y = rng.randrange(size)
        length = rng.randrange(2, 10)
        for i in range(length):
            if x + i < size and y + i < size:
                px[x + i, y + i] = (200, 245, 255, min(255, 120 + rng.randrange(80)))

    # Bubble specks.
    for _ in range(60):
        x = rng.randrange(size)
        y = rng.randrange(size)
        px[x, y] = (230, 250, 255, rng.randrange(120, 220))

    return img


def logo() -> Image.Image:
    size = 256
    img = Image.new("RGBA", (size, size), NAVY)
    draw = ImageDraw.Draw(img, "RGBA")

    # Radial ocean glow.
    for radius in range(150, 0, -2):
        alpha = int(70 * (1 - radius / 150))
        draw.ellipse(
            (size // 2 - radius, size // 2 - radius, size // 2 + radius, size // 2 + radius),
            fill=(20, 120 + alpha, 200, alpha // 2),
        )

    # Wave bands.
    for band in range(7):
        y = 150 + band * 12
        points = []
        for x in range(0, size + 1, 4):
            offset = math.sin(x * 0.035 + band * 0.8) * (10 - band)
            points.append((x, y + offset))
        draw.line(points, fill=(90, 220, 255, 80 - band * 8), width=3)

    # Trident silhouette.
    cx = size // 2
    draw.rounded_rectangle((cx - 5, 70, cx + 5, 205), radius=4, fill=(220, 240, 255, 235))
    draw.rectangle((cx - 38, 70, cx + 38, 82), fill=(220, 240, 255, 235))
    for offset in (-30, 0, 30):
        draw.rounded_rectangle((cx + offset - 5, 36, cx + offset + 5, 84), radius=4,
                               fill=(220, 240, 255, 235))
        draw.polygon([(cx + offset, 26), (cx + offset - 9, 44), (cx + offset + 9, 44)],
                     fill=(140, 230, 255, 240))
    draw.rectangle((cx - 46, 60, cx + 46, 70), fill=(240, 200, 110, 255))

    # Title.
    try:
        font_big = ImageFont.truetype("C:/Windows/Fonts/arialbd.ttf", 40)
        font_small = ImageFont.truetype("C:/Windows/Fonts/arialbd.ttf", 18)
    except OSError:
        font_big = ImageFont.load_default()
        font_small = ImageFont.load_default()

    title = "THE GODS"
    subtitle = "POSEIDON"
    bbox = draw.textbbox((0, 0), title, font=font_big)
    draw.text(((size - (bbox[2] - bbox[0])) / 2, 196), title, font=font_big, fill=PALE)
    bbox = draw.textbbox((0, 0), subtitle, font=font_small)
    draw.text(((size - (bbox[2] - bbox[0])) / 2, 236), subtitle, font=font_small, fill=(140, 230, 255, 255))

    img = img.filter(ImageFilter.GaussianBlur(0.4))
    return img


def main() -> None:
    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    ENTITY_DIR.mkdir(parents=True, exist_ok=True)

    heart_texture().save(ITEM_DIR / "heart_of_the_tides.png")
    trident_texture().save(ITEM_DIR / "trident_of_the_deep.png")
    water_wings_texture().save(ENTITY_DIR / "water_wings.png")
    logo().save(ROOT_DIR / "logo.png")

    print("Generated:")
    for path in (
        ITEM_DIR / "heart_of_the_tides.png",
        ITEM_DIR / "trident_of_the_deep.png",
        ENTITY_DIR / "water_wings.png",
        ROOT_DIR / "logo.png",
    ):
        print(f"  {path.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
