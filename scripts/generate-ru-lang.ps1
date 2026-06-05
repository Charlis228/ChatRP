$ErrorActionPreference = "Stop"

$sourceRoot = Join-Path $PSScriptRoot "..\build_tmp_lang"
$targetRoot = Join-Path $PSScriptRoot "..\src\main\resources"

$exact = @{
    "Correct!" = "Готово!";
    "Back" = "Назад";
    "Settings" = "Настройки";
    "Success!" = "Успешно!";
    "HUD" = "HUD";
    "Core" = "Ядро";
    "General" = "Общее";
    "Chat Screen" = "Окно чата";
    "Hotkeys" = "Горячие клавиши";
    "Open Settings GUI" = "Открыть меню настроек";
    "Open Chat" = "Открыть чат";
    "Toggle Permanent Focus" = "Переключить постоянный фокус";
    "Toggled Permanent" = "Постоянный фокус переключен";
    "Open Chat with Last Message" = "Открыть чат с последним сообщением";
    "Open Chat with Movement Enabled" = "Открыть чат с движением";
    "Time Format" = "Формат времени";
    "Complete Time Format" = "Полный формат времени";
    "Show Time" = "Показывать время";
    "Time Color" = "Цвет времени";
    "Clear Chat Messages on Disconnect" = "Очищать чат при отключении";
    "Stack Duplicate Messages" = "Объединять повторяющиеся сообщения";
    "Resend Stacked Messages" = "Показывать объединенные сообщения заново";
    "Display Chat Heads" = "Показывать головы игроков";
    "Message Owner Regex" = "Шаблон определения отправителя";
    "Filter Profanity" = "Фильтр грубых слов";
    "Profanity Threshold" = "Порог фильтра грубых слов";
    "Disable Partial Matches" = "Не искать внутри слов";
    "Persistent Text" = "Сохранять набранный текст";
    "Extended Text Limit" = "Расширенный лимит текста";
    "Chat Color" = "Цвет чата";
    "Show Chat Icons" = "Показывать иконки чата";
    "Modified Color" = "Цвет измененных сообщений";
    "Filtered Color" = "Цвет отфильтрованных сообщений";
    "System Color" = "Цвет системных сообщений";
    "Not Secure Color" = "Цвет небезопасных сообщений";
    "Configure" = "Настроить";
    "Literal" = "Точный текст";
    "Upper-Lower" = "Без учета регистра";
    "RegEx" = "Регулярное выражение";
    "Custom" = "Пользовательский";
    "All" = "Все";
    "New Chat Tab" = "Новая вкладка чата";
    "Reload Konstruct Tabs" = "Перезагрузить вкладки Konstruct";
    "Chat HUD" = "HUD чата";
    "Chat Tabs" = "Вкладки чата";
    "Matches" = "Совпадения";
    "Export" = "Экспорт";
    "Add Match" = "Добавить совпадение";
    "Remove" = "Удалить";
    "Delete" = "Удалить";
    "Enable Vanilla Chat HUD" = "Включить стандартный HUD чата";
    "Default Chat Width" = "Ширина чата по умолчанию";
    "Default Chat Height" = "Высота чата по умолчанию";
    "Show Chat Tabs" = "Показывать вкладки чата";
    "Left-Side Chat Padding" = "Отступ чата слева";
    "Right-Side Chat Padding" = "Отступ чата справа";
    "Bottom-Side Chat Padding" = "Отступ чата снизу";
    "Top-Side Chat Padding" = "Отступ чата сверху";
    "Max Abbreviation Length" = "Максимальная длина сокращения";
    "Chat Visibility" = "Видимость чата";
    "Vanilla" = "Стандартно";
    "Always" = "Всегда";
    "Focus Only" = "Только в фокусе";
    "Spacing Between Lines" = "Расстояние между строками";
    "Default Chat X" = "Позиция чата X";
    "Default Chat Y" = "Позиция чата Y";
    "Padding Between Messages" = "Отступ между сообщениями";
    "Chat Scale" = "Масштаб чата";
    "Fade Speed" = "Скорость исчезновения";
    "Time Until Fade" = "Время до исчезновения";
    "Fade Style" = "Стиль исчезновения";
    "Linear" = "Линейный";
    "Sinusoidal" = "Синусоидальный";
    "Quadratic" = "Квадратичный";
    "Quartic" = "Четвертичный";
    "Circular" = "Круговой";
    "HUD Background Color" = "Цвет фона HUD";
    "Empty Text Color" = "Цвет обычного текста";
    "Background Line Style" = "Стиль фона строк";
    "Full" = "Полный";
    "Compact" = "Компактный";
    "Striped Messages" = "Чередовать фон сообщений";
    "Stored Lines" = "Хранимые строки";
    "Right Tab Buttons" = "Кнопки вкладок справа";
    "Render Chat in Other GUI" = "Показывать чат в других меню";
    "Hide Chat With F1" = "Скрывать чат при F1";
    "Scroll Time" = "Время прокрутки";
    "Scroll Distance" = "Дистанция прокрутки";
    "Scroll Acceleration" = "Ускорение прокрутки";
    "Scroll Type" = "Тип прокрутки";
    "Always Send Message to Focused Tab" = "Отправлять свои сообщения в активную вкладку";
    "Change Start Message" = "Менять начальный текст";
    "Import Tab" = "Импорт вкладки";
    "Tab Name" = "Название вкладки";
    "Order" = "Порядок";
    "Starting Message" = "Начальное сообщение";
    "Tab HUD Abbreviation" = "Сокращение вкладки в HUD";
    "Forward Messages to Main" = "Дублировать сообщения в основную вкладку";
    "Find String" = "Строка поиска";
    "Find Type" = "Тип поиска";
    "Accent Color" = "Акцентный цвет";
    "Border Color" = "Цвет рамки";
    "Background Color" = "Цвет фона";
    "Show Unread Messages" = "Показывать непрочитанные сообщения";
    "Window X" = "Позиция окна X";
    "Window Y" = "Позиция окна Y";
    "Window Width" = "Ширина окна";
    "Window Height" = "Высота окна";
    "Align Right" = "Выравнивать справа";
    "Render Top First" = "Рисовать сверху вниз";
    "Minimalist Mode" = "Минималистичный режим";
    "Copied" = "Скопировано";
    "Copy" = "Копировать";
    "Toggle Minimalist" = "Переключить минимализм";
    "Remove All Windows" = "Удалить все окна";
    "Clear All Messages" = "Очистить все сообщения";
    "Duplicate Window" = "Дублировать окно";
    "Configure Window" = "Настроить окно";
    "Reply" = "Ответить";
    "Spell Checker" = "Проверка орфографии";
    "Command Spell Check" = "Проверка команд";
    "Add Shortcut" = "Добавить сокращение";
    "Box" = "Поле ввода";
    "Suggestors" = "Подсказчики";
    "Formatters" = "Форматирование";
    "Configure Suggestors" = "Настроить подсказки";
    "Configure Formatters" = "Настроить форматирование";
    "Suggest Players" = "Подсказывать игроков";
    "Suggest Shortcuts" = "Подсказывать сокращения";
    "In-Chat Calculator" = "Калькулятор в чате";
    "Color Code Formatting" = "Форматирование цветовых кодов";
    "Highlight JSON in Commands" = "Подсвечивать JSON в командах";
    "Custom Command Highlighting" = "Своя подсветка команд";
    "Command Color" = "Цвет команд";
    "Error Color" = "Цвет ошибок";
    "Default Palette" = "Палитра по умолчанию";
    "Highlight Color" = "Цвет выделения";
    "Non-Highlighted Color" = "Цвет без выделения";
    "Number of Suggestions" = "Количество подсказок";
    "Remove Namespace" = "Убирать пространство имен";
    "Prune Player Suggestions" = "Фильтровать подсказки игроков";
    "Available Suggestion Color" = "Цвет доступных подсказок";
    "Spell Check Commands" = "Команды с проверкой текста";
    "Spell Checker Hover Format" = "Формат подсказки проверки текста";
    "Test Input" = "Тестовый ввод";
    "Sound" = "Звук";
    "Narrator" = "Диктор";
    "Warning" = "Предупреждение";
    "Advanced Filters" = "Расширенные фильтры";
    "Toast" = "Всплывающее уведомление";
    "New Filter" = "Новый фильтр";
    "Open Directory" = "Открыть папку";
    "Import Filters" = "Импорт фильтров";
    "Import Filter" = "Импорт фильтра";
    "Install" = "Установить";
    "Successfully imported!" = "Успешно импортировано!";
    "Could not import" = "Не удалось импортировать";
    "Chat Filters" = "Фильтры чата";
    "Filter Settings" = "Настройки фильтра";
    "Child Filter Settings" = "Настройки дочерних фильтров";
    "Filter Name" = "Название фильтра";
    "String to Find" = "Строка для поиска";
    "Filter Type" = "Тип фильтра";
    "Ignore Colors" = "Игнорировать цвета";
    "Replace to" = "Заменять на";
    "Replace Type" = "Тип замены";
    "None" = "Нет";
    "Children" = "Дочерние";
    "Full Message" = "Все сообщение";
    "Only Match" = "Только совпадение";
    "Rainbow" = "Радуга";
    "Roman Numeral" = "Римское число";
    "Reverse" = "Обратный порядок";
    "Message Processor Settings" = "Настройки обработки сообщений";
    "Forward to Chat" = "Передавать в чат";
    "Action Bar" = "Панель действий";
    "Narrator Message" = "Сообщение диктора";
    "Title" = "Заголовок";
    "Description" = "Описание";
    "Text Color" = "Цвет текста";
    "Replace Text Color?" = "Заменять цвет текста?";
    "Chat Log" = "История чата";
    "Open Log" = "Открыть историю";
    "Saved Lines" = "Сохраненные строки";
    "Reload Lines" = "Загружаемые строки";
    "Scroll Multiplier" = "Множитель прокрутки";
    "Clean Saved Lines" = "Очищать сохраненные строки";
    "Only Manual Clear" = "Только ручная очистка";
    "No messages found!" = "Сообщения не найдены!";
    "RegEx search Error" = "Ошибка поиска по регулярному выражению";
}

function Strip-Formatting {
    param([string] $Text)
    return ($Text -replace "§.", "").Trim()
}

function Add-FormattingBack {
    param([string] $Original, [string] $Translated)
    if ($Original -match "^(§.)+") {
        return $Matches[0] + $Translated
    }
    return $Translated
}

function Translate-ByKey {
    param([string] $Key)

    $rawLeaf = ($Key -split "\.")[-1]
    $leaf = $rawLeaf.ToLowerInvariant()
    $map = @{
        name = "Название";
        order = "Порядок";
        startingmessage = "Начальное сообщение";
        abbreviation = "Сокращение";
        forward = "Пересылать сообщения";
        findstring = "Строка поиска";
        findtype = "Тип поиска";
        maincolor = "Основной цвет";
        bordercolor = "Цвет рамки";
        innercolor = "Цвет фона";
        showunread = "Показывать непрочитанные";
        x = "Позиция X";
        y = "Позиция Y";
        width = "Ширина";
        height = "Высота";
        color = "Цвет";
        active = "%s";
    }
    if ($map.ContainsKey($leaf)) { return $map[$leaf] }

    $words = ($rawLeaf -creplace "([a-z])([A-Z])", '$1 $2' -replace "_", " " -replace "-", " ").Trim()
    if (!$words) { return "Параметр ChatRp" }
    return "Параметр: $words"
}

function Get-SettingNameFromInfoKey {
    param([string] $Key)

    $base = $Key -replace "\.info\.", "."
    $name = Translate-ByKey -Key $base
    if ($name.StartsWith("Параметр: ")) {
        $name = $name.Substring("Параметр: ".Length)
    }
    return $name.ToLowerInvariant()
}

function Translate-Description {
    param([string] $Key)

    $lower = $Key.ToLowerInvariant()
    if ($lower -match "vanillahud") { return "Включает стандартное окно чата Minecraft как часть системы вкладок ChatRp." }
    if ($lower -match "chatheads") { return "Включает или отключает отображение головы игрока рядом с его сообщением в чате." }
    if ($lower -match "timeformat") { return "Задает формат времени, которое показывается рядом с сообщениями чата и в истории." }
    if ($lower -match "showtime") { return "Включает или отключает отображение времени рядом с сообщениями." }
    if ($lower -match "clearondisconnect") { return "Определяет, будет ли чат очищаться при выходе с сервера или из одиночного мира." }
    if ($lower -match "stored|saved|reload") { return "Задает количество строк чата, которые хранятся, сохраняются или загружаются обратно." }
    if ($lower -match "width") { return "Задает ширину окна или области чата." }
    if ($lower -match "height") { return "Задает высоту окна или области чата." }
    if ($lower -match "leftpad") { return "Задает отступ от левого края области чата до текста сообщений." }
    if ($lower -match "rightpad") { return "Задает отступ от правого края области чата до переноса текста." }
    if ($lower -match "bottompad") { return "Задает отступ от нижнего края области чата до новых сообщений." }
    if ($lower -match "toppad") { return "Задает отступ от верхнего края области чата до старых сообщений." }
    if ($lower -match "tabsidechars") { return "Задает максимальную длину сокращения вкладки, которое показывается рядом с чатом." }
    if ($lower -match "visibility") { return "Выбирает, когда будет виден HUD чата: как в Minecraft, всегда или только при открытом чате." }
    if ($lower -match "linespace") { return "Задает расстояние в пикселях между строками сообщений." }
    if ($lower -match "\.x$|info\.x$") { return "Задает положение окна чата по горизонтали." }
    if ($lower -match "\.y$|info\.y$") { return "Задает положение окна чата по вертикали." }
    if ($lower -match "messagespace") { return "Задает расстояние между отдельными сообщениями чата." }
    if ($lower -match "chatscale") { return "Задает масштаб текста внутри окна чата." }
    if ($lower -match "fadetime") { return "Задает скорость плавного исчезновения новых сообщений." }
    if ($lower -match "fadestart") { return "Задает количество тиков до начала исчезновения сообщения." }
    if ($lower -match "fadetype") { return "Выбирает тип анимации исчезновения сообщений." }
    if ($lower -match "hudlinetype") { return "Выбирает, как фон HUD подстраивается под строки сообщений." }
    if ($lower -match "alternatelines") { return "Включает чередование фона строк, чтобы сообщения легче различались." }
    if ($lower -match "renderinother") { return "Определяет, будет ли чат отображаться поверх других экранов интерфейса." }
    if ($lower -match "hidewithf1") { return "Скрывает кастомный чат вместе с остальным HUD при нажатии F1." }
    if ($lower -match "changestartmessage") { return "Меняет начальный текст поля ввода при переключении окон или вкладок чата." }
    if ($lower -match "sendtocurrenttab") { return "Показывает отправленные вами сообщения в активной вкладке независимо от фильтра." }
    if ($lower -match "showunread") { return "Показывает количество непрочитанных сообщений на вкладке чата." }
    if ($lower -match "color") { return "Задает цвет этого элемента интерфейса ChatRp." }
    if ($lower -match "tab") { return "Настраивает отображение и фильтрацию сообщений для вкладки чата." }
    if ($lower -match "filter") { return "Настраивает правило фильтрации, поиска или замены сообщений чата." }
    if ($lower -match "sound") { return "Определяет, будет ли проигрываться звук при срабатывании правила." }
    if ($lower -match "toast") { return "Настраивает всплывающее уведомление при срабатывании правила." }
    if ($lower -match "narrator") { return "Настраивает сообщение, которое будет произнесено диктором." }
    if ($lower -match "scroll") { return "Настраивает скорость, дистанцию или стиль прокрутки чата." }
    if ($lower -match "hotkeys?") { return "Назначает клавишу для быстрого действия ChatRp." }
    if ($lower -match "suggest") { return "Настраивает подсказки, которые появляются во время ввода текста в чат." }
    if ($lower -match "spell") { return "Настраивает проверку текста и подсказки исправлений при вводе в чат." }
    if ($lower -match "format") { return "Настраивает форматирование и подсветку текста в чате." }
    if ($lower -match "window") { return "Настраивает положение, размер и поведение отдельного окна чата." }
    if ($lower -match "persistenttext") { return "Сохраняет набранный, но не отправленный текст после закрытия окна чата." }
    if ($lower -match "moretext") { return "Позволяет отправлять длинные сообщения, разбивая их на несколько частей." }
    if ($lower -match "forward") { return "Определяет, будут ли сообщения из этой вкладки также отображаться в основном чате." }
    if ($lower -match "findtype") { return "Выбирает способ поиска совпадений: точный текст, без учета регистра, регулярное выражение или все сообщения." }
    if ($lower -match "findstring") { return "Текст или шаблон, по которому сообщения будут попадать в фильтр или вкладку." }
    if ($lower -match "messageownerregex") { return "Регулярное выражение, по которому ChatRp определяет отправителя для показа головы игрока." }
    if ($lower -match "chatstackupdate") { return "Определяет, будут ли объединенные повторяющиеся сообщения снова показываться как новые." }
    if ($lower -match "chatstack") { return "Задает, сколько предыдущих строк проверяется для объединения одинаковых сообщений. Значение 0 отключает объединение." }
    if ($lower -match "profanityabove") { return "Задает минимальный уровень грубости слова, при котором фильтр начнет замену." }
    if ($lower -match "profanitywordboundaries") { return "Запрещает фильтру срабатывать на часть слова, если запрещенное слово находится внутри другого слова." }
    if ($lower -match "showchaticons") { return "Показывает служебные иконки, которые объясняют тип подписи или статус сообщения." }
    if ($lower -match "modified") { return "Задает цвет сообщений, которые были изменены после подписи." }
    if ($lower -match "system") { return "Задает цвет системных сообщений в окне чата." }
    if ($lower -match "notsecure") { return "Задает цвет сообщений, которые Minecraft считает небезопасными или неподписанными." }
    if ($lower -match "name") { return "Название, которое будет показано в списке настроек и интерфейсе." }
    if ($lower -match "order") { return "Число для сортировки: чем меньше значение, тем выше пункт в списке." }
    $setting = Get-SettingNameFromInfoKey -Key $Key
    return "Настраивает параметр «$setting» в интерфейсе ChatRp."
}

function Translate-Value {
    param([string] $Key, [string] $Value)

    if ($Value -eq "%s") { return "%s" }
    if ($Key -match "\.info\.|\.description$|\.warning\.") {
        return Translate-Description -Key $Key
    }

    $clean = Strip-Formatting -Text $Value
    if ($exact.ContainsKey($clean)) {
        return Add-FormattingBack -Original $Value -Translated $exact[$clean]
    }

    if ($Key -match "screen\.main") { return "§fНастройки Chat§6Rp" }
    if ($Key -match "tabdescription") { return "§7Название§r: <name> \n§aСтрока поиска§r: <find>\n§eТип поиска§r: <findtype>" }
    if ($Key -match "filterdescription") { return "§7Название§r: <name> \n§aСтрока поиска§r: <find>\n§eТип поиска§r: <findtype>" }
    if ($Key -match "warning\.advancedfilters") { return "Расширенные фильтры сейчас отключены. Включайте их только если понимаете, какой код добавляете." }

    return Translate-ByKey -Key $Key
}

Get-ChildItem -Path $sourceRoot -Recurse -Filter en_us.json | ForEach-Object {
    $relative = $_.FullName.Substring($sourceRoot.Length).TrimStart("\")
    $parts = $relative -split "\\"
    $assetIndex = [Array]::IndexOf($parts, "assets")
    if ($assetIndex -lt 0 -or $parts.Length -lt ($assetIndex + 4)) { return }

    $namespace = $parts[$assetIndex + 1]
    $targetDir = Join-Path $targetRoot "assets\$namespace\lang"
    New-Item -ItemType Directory -Force $targetDir | Out-Null

    $json = Get-Content -Raw $_.FullName | ConvertFrom-Json
    $ordered = [ordered]@{}
    foreach ($property in $json.PSObject.Properties) {
        $ordered[$property.Name] = Translate-Value -Key $property.Name -Value ([string]$property.Value)
    }

    foreach ($lang in @("ru_ru", "en_us")) {
        $target = Join-Path $targetDir "$lang.json"
        $ordered | ConvertTo-Json -Depth 4 | Set-Content -Encoding UTF8 $target
        Write-Host "Создан перевод: $target"
    }
}
