$ErrorActionPreference = "Stop"

$root = Resolve-Path (Join-Path $PSScriptRoot "..")
$staging = Join-Path $root "build\merged-staging"
$classes = Join-Path $root "build\classes"
$stubs = Join-Path $root "build\compile-stubs"
$libs = Join-Path $root "build\libs"
$backups = Join-Path $root "backups"

New-Item -ItemType Directory -Force $libs, $backups | Out-Null
Remove-Item -Recurse -Force $staging, $classes, $stubs -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force $staging, $classes, $stubs | Out-Null

$sourceJars = @(
    (Join-Path $root "..\advancedchatcore-1.21.11-1.21.11-1.5.17-alpha.1.jar"),
    (Join-Path $root "..\AdvancedChatBox-1.21.11-1.6.1.jar"),
    (Join-Path $root "..\AdvancedChatFilters-1.21.11-1.6.1.jar"),
    (Join-Path $root "..\AdvancedChatHUD-1.21.11-1.6.3.jar"),
    (Join-Path $root "..\AdvancedChatLog-1.21.11-1.6.0.jar")
)

foreach ($jarPath in $sourceJars) {
    if (!(Test-Path $jarPath)) {
        throw "Не найден исходный мод: $jarPath"
    }
    Push-Location $staging
    & jar xf $jarPath
    Pop-Location
}

Remove-Item -Force (Join-Path $staging "fabric.mod.json") -ErrorAction SilentlyContinue
Remove-Item -Force (Join-Path $staging "META-INF\MANIFEST.MF") -ErrorAction SilentlyContinue

$loaderJar = Get-ChildItem "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1\net.fabricmc\fabric-loader" -Recurse -Filter "fabric-loader-*.jar" |
    Where-Object { $_.Name -notmatch "sources|javadoc" } |
    Sort-Object FullName -Descending |
    Select-Object -First 1
$minecraftJar = Get-ChildItem "$env:USERPROFILE\.gradle\caches\fabric-loom\minecraftMaven\net\minecraft\minecraft-merged-intermediary" -Recurse -Filter "*.jar" -ErrorAction SilentlyContinue |
    Where-Object { $_.FullName -match "1\.21\.11" } |
    Sort-Object FullName -Descending |
    Select-Object -First 1
$authlibJar = Get-ChildItem "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1\com.mojang\authlib" -Recurse -Filter "*.jar" -ErrorAction SilentlyContinue |
    Where-Object { $_.Name -notmatch "sources|javadoc" } |
    Sort-Object FullName -Descending |
    Select-Object -First 1
$brigadierJar = Get-ChildItem "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1\com.mojang\brigadier" -Recurse -Filter "*.jar" -ErrorAction SilentlyContinue |
    Where-Object { $_.Name -notmatch "sources|javadoc" } |
    Sort-Object FullName -Descending |
    Select-Object -First 1
if ($loaderJar) {
    $javaFiles = Get-ChildItem (Join-Path $root "src\main\java") -Recurse -Filter "*.java"
    if ($javaFiles) {
        $stubInterfaceDir = Join-Path $stubs "fi\dy\masa\malilib\config"
        New-Item -ItemType Directory -Force $stubInterfaceDir | Out-Null
        @"
package fi.dy.masa.malilib.config;
public interface IConfigOptionListEntry {
    String getStringValue();
    String getDisplayName();
    IConfigOptionListEntry cycle(boolean forward);
    IConfigOptionListEntry fromString(String value);
}
"@ | Set-Content -Encoding UTF8 (Join-Path $stubInterfaceDir "IConfigOptionListEntry.java")
        & javac --release 21 -encoding UTF-8 -d $stubs (Join-Path $stubInterfaceDir "IConfigOptionListEntry.java")
        if ($LASTEXITCODE -ne 0) { throw "Не удалось скомпилировать compile-only заглушки." }

        $compileClasspath = "$($loaderJar.FullName);$($sourceJars -join ';')"
        if ($minecraftJar) {
            $compileClasspath = "$compileClasspath;$($minecraftJar.FullName)"
        }
        if ($authlibJar) {
            $compileClasspath = "$compileClasspath;$($authlibJar.FullName)"
        }
        if ($brigadierJar) {
            $compileClasspath = "$compileClasspath;$($brigadierJar.FullName)"
        }
        $compileClasspath = "$compileClasspath;$stubs"
        & javac --release 21 -encoding UTF-8 -cp $compileClasspath -d $classes @($javaFiles.FullName)
        if ($LASTEXITCODE -ne 0) { throw "Компиляция Java-классов ChatRp не удалась." }
        Copy-Item -Path (Join-Path $classes "*") -Destination $staging -Recurse -Force
    }
} else {
    Write-Warning "fabric-loader не найден в Gradle cache; Java-классы ChatRp не скомпилированы."
}

Copy-Item -Path (Join-Path $root "src\main\resources\*") -Destination $staging -Recurse -Force
Copy-Item -Path (Join-Path $root "fabric.mod.json") -Destination (Join-Path $staging "fabric.mod.json") -Force

$notice = @"
ChatRp bundles AdvancedChat components licensed under MPL-2.0.
Original bundled modules: AdvancedChatCore, AdvancedChatBox, AdvancedChatFilters, AdvancedChatHUD, AdvancedChatLog.
User-facing branding and resources are changed to ChatRp by CHARLIS228.
"@
New-Item -ItemType Directory -Force (Join-Path $staging "META-INF") | Out-Null
$notice | Set-Content -Encoding UTF8 (Join-Path $staging "META-INF\CHATRP-NOTICE.txt")

$jarName = "ChatRp-1.0.0+mc1.21.11.jar"
$jarPath = Join-Path $libs $jarName
Remove-Item -Force $jarPath -ErrorAction SilentlyContinue
Push-Location $staging
& jar --create --file $jarPath -C $staging .
Pop-Location

$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
$backupPath = Join-Path $backups "ChatRp-build-$stamp.zip"
Compress-Archive -Path $jarPath, (Join-Path $root "fabric.mod.json"), (Join-Path $root "src"), (Join-Path $root "scripts") -DestinationPath $backupPath -Force

Write-Host "Собрано: $jarPath"
Write-Host "Бэкап: $backupPath"
