param(
    [Parameter(Mandatory = $true)]
    [string]$Version,
    [string]$Message = "The Gods release"
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

$properties = Get-Content (Join-Path $root "gradle.properties")
$properties = $properties -replace '^mod_version=.*$', "mod_version=$Version"
$properties | Set-Content (Join-Path $root "gradle.properties") -Encoding UTF8

& "$PSScriptRoot/package.ps1" -Version $Version
if ($LASTEXITCODE -ne 0) { throw "Packaging failed" }

git add -A
git commit -m "$Message v$Version"
git tag "v$Version"

$gh = "C:\Program Files\GitHub CLI\gh.exe"
if (Test-Path $gh) {
    $status = & $gh auth status 2>&1
    if ($LASTEXITCODE -eq 0) {
        git push origin main
        git push origin "v$Version"
        & $gh release create "v$Version" `
            (Join-Path $root "dist/TheGods-Poseidon-$Version-curseforge.zip") `
            (Join-Path $root "dist/TheGods-Poseidon-$Version-checkpoint.zip") `
            --title "The Gods v$Version" `
            --notes "$Message"
    } else {
        Write-Host "GitHub CLI non autenticato. Esegui: gh auth login" -ForegroundColor Yellow
        Write-Host "Poi: git push origin main; git push origin v$Version" -ForegroundColor Yellow
    }
} else {
    Write-Host "GitHub CLI non trovato. Push manuale:" -ForegroundColor Yellow
    Write-Host "  git push origin main" -ForegroundColor Yellow
    Write-Host "  git push origin v$Version" -ForegroundColor Yellow
}
