# The Gods

Mod Minecraft ambiziosa su divinità giocabili, poteri giganteschi, animazioni e modelli custom.

**Prima divinità: Poseidon, Sovrano delle Maree.**
Minecraft **1.20.1** · **Forge 47.4.28** · Java 17.

---

## Cosa c'è già

- **Energia divina** (0–100) con rigenerazione, consumo e svuotamento.
- **Forma divina** attivabile: respirazione acquatica, grazia del delfino, potere del condotto, cura in acqua.
- **4 poteri** con cooldown e costi:
  - `R` — **Tidal Surge**: ondata perforante che danneggia e respinge.
  - `F` — **Trident Storm**: fulmine divino sul bersaglio mirato.
  - `G` — **Abyssal Barrier**: scudo di assorbimento e resistenza.
  - `V` — **Tsunami**: onda d'urto radiale devastante.
- **Ali d'acqua animate** attorno al giocatore in forma divina.
- **HUD** con barra dell'energia e icone cooldown.
- **Altare delle Maree** con rituale.
- **Cuore delle Maree** e **Tridente degli Abissi**.
- Ricette, loot table, advancement, comandi di test, localizzazione IT/EN.

## Come si gioca

1. Crafta l'**Altare delle Maree** (`nautilus shell` + `conduit` + `prismarine bricks`).
2. Crafta il **Cuore delle Maree** (`heart of the sea` + `prismarine shard` + `nautilus shell`).
3. Clic destro sull'altare con il Cuore in mano.
4. Usa `R`, `F`, `G`, `V` per i poteri e `H` per entrare/uscire dalla forma divina.

Comandi di test (richiedono permessi da operator):

```mcfunction
/thegods unlock
/thegods form
/thegods energy 100
```

## Build

```powershell
.\gradlew.bat build
```

Il jar esce in `build/libs/thegods-<versione>.jar`.

## Doppio ZIP a ogni release

```powershell
.\scripts\package.ps1
```

Genera in `dist/`:

| File | A cosa serve |
|---|---|
| `TheGods-Poseidon-<versione>-curseforge.zip` | Modpack importabile nell'app CurseForge per giocare subito |
| `TheGods-Poseidon-<versione>-checkpoint.zip` | Sorgenti + `CHECKPOINT.md`, da usare come segnaposto per altre chat |

Per creare tag, commit e release GitHub:

```powershell
.\scripts\release.ps1 -Version 0.1.0 -Message "Poseidon vertical slice"
```

Il workflow `.github/workflows/release.yml` allega automaticamente i due ZIP alla release quando pushi un tag `v*`.

## GitHub

Il repository è pronto per essere inizializzato e pushato. Se `gh` non è autenticato:

```powershell
gh auth login
git remote add origin https://github.com/<tuo-utente>/the-gods.git
git push -u origin main
```

## Struttura

```
src/main/java/com/thegods/
  TheGods.java                 entrypoint
  capability/                  energia divina e dato persistente
  ability/                     enum + implementazione poteri Poseidon
  network/                     pacchetti abilità, forma e sync HUD
  registry/                    item, blocchi, tab, capability
  item/                        Cuore delle Maree, Tridente
  block/                       Altare delle Maree
  event/                       tick, login, respawn
  command/                     /thegods
  client/                      HUD, keybind, ali d'acqua
  util/                        advancement helper
src/main/resources/
  assets/thegods/              modelli, texture, lang
  data/thegods/                ricette, loot, advancement
tools/gen_textures.py          arte procedurale riproducibile
scripts/                       packaging e release
```

## Roadmap

- [x] Vertical slice Poseidon
- [ ] Modello 3D GeckoLib del tridente e dell'armatura divina
- [ ] Reame delle Maree (dimensione Atlantide)
- [ ] Boss Leviatano e servitori del mare
- [ ] Seconda divinità (Sole / Vulcano / Tempesta)
- [ ] Config bilanciamento
- [ ] Pubblicazione CurseForge

## Nota creativa

“The Gods” usa un pantheon originale ispirato alle mitologie. Non copia personaggi, nomi o design di altre opere: è la base giusta per pubblicare senza problemi di proprietà intellettuale.
