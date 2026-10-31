param(
    [string]$Version = "",
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

function Write-Utf8NoBom([string]$Path, [string]$Content) {
    $encoding = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllText($Path, $Content, $encoding)
}

if (-not $Version) {
    $match = Select-String -Path (Join-Path $root "gradle.properties") -Pattern '^mod_version=(.+)$'
    if (-not $match) { throw "mod_version not found in gradle.properties" }
    $Version = $match.Matches[0].Groups[1].Value.Trim()
}

Write-Host "Packaging The Gods v$Version" -ForegroundColor Cyan

if (-not $SkipBuild) {
    if ($IsWindows -or $env:OS -eq "Windows_NT") {
        & "$root\gradlew.bat" clean build --no-daemon --console=plain
    } else {
        & "$root/gradlew" clean build --no-daemon --console=plain
    }
    if ($LASTEXITCODE -ne 0) { throw "Gradle build failed" }
}

$jar = Get-ChildItem (Join-Path $root "build/libs") -Filter "thegods-$Version.jar" -ErrorAction SilentlyContinue |
    Select-Object -First 1
if (-not $jar) {
    throw "Jar not found: build/libs/thegods-$Version.jar. Run the build first."
}

$dist = Join-Path $root "dist"
$cfStage = Join-Path $dist "curseforge-stage"
$cpStage = Join-Path $dist "checkpoint-stage"
Remove-Item $cfStage, $cpStage -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path (Join-Path $cfStage "overrides/mods") | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $cfStage "overrides/config") | Out-Null
New-Item -ItemType Directory -Force -Path $cpStage | Out-Null

# ---------- CurseForge / modpack zip ----------
$manifest = [ordered]@{
    minecraft = [ordered]@{
        version = "1.20.1"
        modLoaders = @(
            [ordered]@{
                id = "forge-47.4.28"
                primary = $true
            }
        )
    }
    manifestType = "minecraftModpack"
    manifestVersion = 1
    name = "The Gods - Poseidon"
    version = $Version
    author = "The Gods Team"
    files = @()
    overrides = "overrides"
}
Write-Utf8NoBom (Join-Path $cfStage "manifest.json") ($manifest | ConvertTo-Json -Depth 10)

$modlist = @"
<h1>The Gods - Poseidon v$Version</h1>
<ul>
  <li><strong>thegods-$Version.jar</strong> - bundled in overrides/mods</li>
</ul>
<p>Minecraft 1.20.1 / Forge 47.4.28</p>
"@
Write-Utf8NoBom (Join-Path $cfStage "modlist.html") $modlist

Copy-Item $jar.FullName (Join-Path $cfStage "overrides/mods/thegods-$Version.jar") -Force

$readme = @"
THE GODS - POSEIDON v$Version
=============================

Minecraft 1.20.1 - Forge 47.4.28

This pack contains the local development build of The Gods.
Import the zip in the CurseForge app (Minecraft > Import) or copy
overrides/mods/thegods-$Version.jar into your mods folder.

Ritual:
1. Craft the Altar of the Tides.
2. Craft the Heart of the Tides.
3. Right-click the altar while holding the Heart of the Tides.

Controls:
R - Tidal Surge
F - Trident Storm
G - Abyssal Barrier
V - Tsunami
H - Toggle divine form
"@
Write-Utf8NoBom (Join-Path $cfStage "overrides/README.txt") $readme

$cfZip = Join-Path $dist "TheGods-Poseidon-$Version-curseforge.zip"
Compress-Archive -Path (Join-Path $cfStage "*") -DestinationPath $cfZip -Force

# ---------- Checkpoint / source zip ----------
$exclude = @("build", ".gradle", "run", "run-data", "dist", ".git", ".idea", "out")
Get-ChildItem -Path $root -Force | Where-Object { $exclude -notcontains $_.Name } | ForEach-Object {
    Copy-Item $_.FullName -Destination $cpStage -Recurse -Force
}

$checkpoint = @"
# The Gods - Checkpoint v$Version

Data: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
Versione: $Version
Stack: Minecraft 1.20.1 - Forge 47.4.28 - Java 17

## Stato

Prima divinità giocabile: **Poseidon, Sovrano delle Maree**.

Implementato:
- Energia divina (0-100) con rigenerazione e consumo.
- Forma divina con passivi: respirazione acquatica, grazia del delfino, potere del condotto, cura in acqua.
- 4 abilità: Tidal Surge (R), Trident Storm (F), Abyssal Barrier (G), Tsunami (V).
- HUD con barra energia e cooldown.
- Ali d'acqua animate (render layer client).
- Altare delle Maree + Cuore delle Maree + Tridente degli Abissi.
- Ricette, loot table, advancement, comandi `/thegods`.
- Localizzazione EN/IT.

## Comandi di test

    /thegods unlock
    /thegods form
    /thegods energy 100

## Build

    .\gradlew.bat build

## Packaging

    .\scripts\package.ps1

Genera:
- dist/TheGods-Poseidon-$Version-curseforge.zip (modpack importabile)
- dist/TheGods-Poseidon-$Version-checkpoint.zip (sorgenti + questo file)

## Prossimi passi

1. Testare in gioco la vertical slice e bilanciare i poteri.
2. Aggiungere un modello 3D GeckoLib per il tridente e per l'armatura divina.
3. Aggiungere il Reame delle Maree (dimensione Atlantide).
4. Aggiungere il boss Leviatano e i servitori del mare.
5. Preparare la seconda divinità (Sole/Vulcano/Tempesta).
6. Pubblicare su CurseForge e collegare il repository GitHub.

## File chiave

- src/main/java/com/thegods/TheGods.java
- src/main/java/com/thegods/capability/DivineData.java
- src/main/java/com/thegods/ability/PoseidonAbilities.java
- src/main/java/com/thegods/client/WaterWingsLayer.java
- src/main/java/com/thegods/client/DivineHUD.java
- src/main/resources/assets/thegods/lang/it_it.json
"@
Write-Utf8NoBom (Join-Path $cpStage "CHECKPOINT.md") $checkpoint

$cpZip = Join-Path $dist "TheGods-Poseidon-$Version-checkpoint.zip"
Compress-Archive -Path (Join-Path $cpStage "*") -DestinationPath $cpZip -Force

Remove-Item $cfStage, $cpStage -Recurse -Force -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "Creati:" -ForegroundColor Green
Write-Host "  $cfZip"
Write-Host "  $cpZip"
