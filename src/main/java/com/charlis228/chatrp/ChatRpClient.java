package com.charlis228.chatrp;

import net.fabricmc.api.ClientModInitializer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class ChatRpClient implements ClientModInitializer {
    private static final String STRICT_OWNER_REGEX = "(?:(?<=^)|(?<=[\\\\s<\\\\[\\\\(]))[A-Za-z0-9_]{3,16}(?=[:>\\\\]\\\\)\\\\s])";

    @Override
    public void onInitializeClient() {
        ensureRpChatTab();
        ensureStrictChatHeadOwnerRegex();
        ensurePersistentLargeChatHistory();
        ensurePlayerSuggestionsEnabled();
        enablePlayerSuggestionsInMemory();
        System.out.println("[ChatRp] Клиент ChatRp загружен. Автор: CHARLIS228");
    }

    private static void ensureRpChatTab() {
        Path hudConfig = minecraftConfigPath()
                .resolve("advancedchat")
                .resolve("advancedchathud.json");
        try {
            Files.createDirectories(hudConfig.getParent());
            if (!Files.exists(hudConfig)) {
                Files.writeString(hudConfig, createDefaultHudConfig(), StandardCharsets.UTF_8);
                return;
            }

            String content = Files.readString(hudConfig, StandardCharsets.UTF_8);
            if (content.contains("\"name\":\"RP\"") || content.contains("\"name\": \"RP\"")) {
                return;
            }

            int tabsKey = content.indexOf("\"tabs\"");
            if (tabsKey < 0) {
                Files.writeString(hudConfig, createDefaultHudConfig(), StandardCharsets.UTF_8);
                return;
            }

            int arrayStart = content.indexOf('[', tabsKey);
            if (arrayStart < 0) {
                Files.writeString(hudConfig, createDefaultHudConfig(), StandardCharsets.UTF_8);
                return;
            }

            String rpTab = createRpTabJson();
            String before = content.substring(0, arrayStart + 1);
            String after = content.substring(arrayStart + 1);
            String separator = after.trim().startsWith("]") ? "\n" : "\n,";
            Files.writeString(hudConfig, before + "\n" + rpTab + separator + after, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.err.println("[ChatRp] Не удалось создать вкладку RP: " + exception.getMessage());
        }
    }

    private static void ensureStrictChatHeadOwnerRegex() {
        Path coreConfig = minecraftConfigPath()
                .resolve("advancedchat")
                .resolve("advancedchatcore.json");
        try {
            Files.createDirectories(coreConfig.getParent());
            if (!Files.exists(coreConfig)) {
                Files.writeString(coreConfig, createDefaultCoreConfig(), StandardCharsets.UTF_8);
                return;
            }

            String content = Files.readString(coreConfig, StandardCharsets.UTF_8);
            if (content.contains("\"messageOwnerRegex\"")) {
                content = content.replaceAll(
                        "\"messageOwnerRegex\"\\s*:\\s*\"(?:\\\\.|[^\"])*\"",
                        java.util.regex.Matcher.quoteReplacement("\"messageOwnerRegex\": \"" + jsonEscape(STRICT_OWNER_REGEX) + "\"")
                );
                Files.writeString(coreConfig, content, StandardCharsets.UTF_8);
                return;
            }

            int generalKey = content.indexOf("\"general\"");
            int objectStart = generalKey >= 0 ? content.indexOf('{', generalKey) : -1;
            if (objectStart >= 0) {
                String before = content.substring(0, objectStart + 1);
                String after = content.substring(objectStart + 1);
                String separator = after.trim().startsWith("}") ? "\n" : "\n,";
                Files.writeString(coreConfig,
                        before + "\n    \"messageOwnerRegex\": \"" + jsonEscape(STRICT_OWNER_REGEX) + "\"" + separator + after,
                        StandardCharsets.UTF_8);
            } else {
                Files.writeString(coreConfig, createDefaultCoreConfig(), StandardCharsets.UTF_8);
            }
        } catch (IOException exception) {
            System.err.println("[ChatRp] Не удалось обновить определение ников для голов игроков: " + exception.getMessage());
        }
    }

    private static void ensurePersistentLargeChatHistory() {
        Path advancedChatDir = minecraftConfigPath().resolve("advancedchat");
        try {
            Files.createDirectories(advancedChatDir);
            Path hudConfig = advancedChatDir.resolve("advancedchathud.json");
            String hud = Files.exists(hudConfig)
                    ? Files.readString(hudConfig, StandardCharsets.UTF_8)
                    : createDefaultHudConfig();
            hud = ensureNumberSetting(hud, "general", "storedLines", 1000);
            Files.writeString(hudConfig, hud, StandardCharsets.UTF_8);

            Path logConfig = advancedChatDir.resolve("advancedchatlog.json");
            String log = Files.exists(logConfig)
                    ? Files.readString(logConfig, StandardCharsets.UTF_8)
                    : createDefaultLogConfig();
            log = ensureNumberSetting(log, "general", "stored_lines", 10000);
            log = ensureNumberSetting(log, "general", "saved_lines", 10000);
            log = ensureNumberSetting(log, "general", "reload_lines", 10000);
            log = ensureBooleanSetting(log, "general", "only_manual_clear", true);
            log = ensureBooleanSetting(log, "general", "clean_save", false);
            Files.writeString(logConfig, log, StandardCharsets.UTF_8);

            Path chatRpConfig = minecraftConfigPath().resolve("chatrp").resolve("config.json");
            Files.createDirectories(chatRpConfig.getParent());
            String chatRp = Files.exists(chatRpConfig)
                    ? Files.readString(chatRpConfig, StandardCharsets.UTF_8)
                    : "{}";
            chatRp = ensureBooleanSetting(chatRp, null, "saveHistory", true);
            chatRp = ensureStringSetting(chatRp, null, "historyLimit", "none");
            chatRp = ensureStringSetting(chatRp, null, "historyScope", "all");
            Files.writeString(chatRpConfig, chatRp, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.err.println("[ChatRp] Не удалось обновить настройки истории чата: " + exception.getMessage());
        }
    }

    private static void ensurePlayerSuggestionsEnabled() {
        Path boxConfig = minecraftConfigPath()
                .resolve("advancedchat")
                .resolve("advancedchatbox.json");
        try {
            Files.createDirectories(boxConfig.getParent());
            String box = Files.exists(boxConfig)
                    ? Files.readString(boxConfig, StandardCharsets.UTF_8)
                    : createDefaultBoxConfig();
            box = ensureBooleanSetting(box, "general", "prunePlayerSuggestions", false);
            box = ensureNumberSetting(box, "general", "suggestionSize", 18);
            box = ensureRegistryActive(box, "suggestors", "players", true);
            Files.writeString(boxConfig, box, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.err.println("[ChatRp] Не удалось включить подсказки игроков: " + exception.getMessage());
        }
    }

    private static void enablePlayerSuggestionsInMemory() {
        try {
            Class<?> registryClass = Class.forName("io.github.darkkronicle.advancedchatbox.registry.ChatSuggestorRegistry");
            Object registry = registryClass.getMethod("getInstance").invoke(null);
            Object options = registryClass.getMethod("getAll").invoke(registry);
            if (!(options instanceof Iterable<?> iterable)) {
                return;
            }
            for (Object option : iterable) {
                Method saveStringMethod = option.getClass().getMethod("getSaveString");
                Object saveString = saveStringMethod.invoke(option);
                if (!"players".equals(saveString)) {
                    continue;
                }
                Method getActiveMethod = option.getClass().getMethod("getActive");
                Object saveable = getActiveMethod.invoke(option);
                Field configField = saveable.getClass().getField("config");
                Object config = configField.get(saveable);
                Method setBoolean = config.getClass().getMethod("setBooleanValue", boolean.class);
                setBoolean.invoke(config, true);
                return;
            }
        } catch (ReflectiveOperationException | LinkageError exception) {
            System.err.println("[ChatRp] Не удалось включить подсказки игроков в памяти: " + exception.getMessage());
        }
    }

    private static Path minecraftConfigPath() {
        String appData = System.getenv("APPDATA");
        if (appData != null && !appData.isBlank()) {
            return Path.of(appData, ".minecraft", "config");
        }
        return Path.of(System.getProperty("user.home"), ".minecraft", "config");
    }

    private static String createDefaultHudConfig() {
        return """
                {
                  "maintab": {
                    "name": "Основная",
                    "order": 0,
                    "startingMessage": "",
                    "forward": true,
                    "abbreviation": "Основная",
                    "mainColor": "#66FFFFFF",
                    "borderColor": "#99FFD700",
                    "innerColor": "#33000000",
                    "showUnread": true,
                    "match": [
                      {
                        "pattern": ".*",
                        "findtype": "regex"
                      }
                    ],
                    "uuid": "%s"
                  },
                  "tabs": [
                %s
                  ],
                  "windows": [],
                  "config_version": 1
                }
                """.formatted(UUID.randomUUID(), createRpTabJson());
    }

    private static String createDefaultCoreConfig() {
        return """
                {
                  "general": {
                    "messageOwnerRegex": "%s",
                    "clearOnDisconnect": false
                  },
                  "config_version": 1
                }
                """.formatted(jsonEscape(STRICT_OWNER_REGEX));
    }

    private static String createDefaultLogConfig() {
        return """
                {
                  "general": {
                    "stored_lines": 10000,
                    "saved_lines": 10000,
                    "reload_lines": 10000,
                    "only_manual_clear": true,
                    "clean_save": false
                  },
                  "config_version": 1
                }
                """;
    }

    private static String createDefaultBoxConfig() {
        return """
                {
                  "general": {
                    "suggestionSize": 18,
                    "removeIdentifier": true,
                    "prunePlayerSuggestions": false
                  },
                  "spellchecker": {},
                  "suggestors": {
                    "players": {
                      "active": true
                    }
                  },
                  "config_version": 1
                }
                """;
    }

    private static String createRpTabJson() {
        return """
                    {
                      "name": "RP",
                      "order": 1,
                      "startingMessage": "",
                      "forward": true,
                      "abbreviation": "RP",
                      "mainColor": "#99FFD700",
                      "borderColor": "#CCFFD700",
                      "innerColor": "#66000000",
                      "showUnread": true,
                      "match": [
                        {
                          "pattern": "(?i).*(?:^|\\\\s)/(?:me|mee|do|try|roll|action|todo|whisp|w|shout|sh|b)\\\\b.*",
                          "findtype": "regex"
                        },
                        {
                          "pattern": "(?i).*(?:улыбнулся|попытался|выкрикнул|прошептал|действие|удачно|неудачно).*",
                          "findtype": "regex"
                        }
                      ],
                      "uuid": "%s"
                    }""".formatted(UUID.randomUUID());
    }

    private static String jsonEscape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String ensureNumberSetting(String json, String objectName, String key, int value) {
        return ensureRawSetting(json, objectName, key, Integer.toString(value));
    }

    private static String ensureBooleanSetting(String json, String objectName, String key, boolean value) {
        return ensureRawSetting(json, objectName, key, Boolean.toString(value));
    }

    private static String ensureStringSetting(String json, String objectName, String key, String value) {
        return ensureRawSetting(json, objectName, key, "\"" + jsonEscape(value) + "\"");
    }

    private static String ensureRawSetting(String json, String objectName, String key, String rawValue) {
        String keyPattern = "\"" + java.util.regex.Pattern.quote(key) + "\"\\s*:\\s*(?:\"(?:\\\\.|[^\"])*\"|-?\\d+(?:\\.\\d+)?|true|false|null)";
        String replacement = java.util.regex.Matcher.quoteReplacement("\"" + key + "\": " + rawValue);
        if (json.matches("(?s).*" + keyPattern + ".*")) {
            return json.replaceAll(keyPattern, replacement);
        }

        if (objectName == null) {
            int rootStart = json.indexOf('{');
            if (rootStart < 0) {
                return "{\n  \"" + key + "\": " + rawValue + "\n}";
            }
            String before = json.substring(0, rootStart + 1);
            String after = json.substring(rootStart + 1);
            String separator = after.trim().startsWith("}") ? "\n" : "\n,";
            return before + "\n  \"" + key + "\": " + rawValue + separator + after;
        }

        int objectKey = json.indexOf("\"" + objectName + "\"");
        int objectStart = objectKey >= 0 ? json.indexOf('{', objectKey) : -1;
        if (objectStart < 0) {
            int rootStart = json.indexOf('{');
            if (rootStart < 0) {
                return "{\n  \"" + objectName + "\": {\n    \"" + key + "\": " + rawValue + "\n  }\n}";
            }
            String before = json.substring(0, rootStart + 1);
            String after = json.substring(rootStart + 1);
            String separator = after.trim().startsWith("}") ? "\n" : "\n,";
            return before + "\n  \"" + objectName + "\": {\n    \"" + key + "\": " + rawValue + "\n  }" + separator + after;
        }

        String before = json.substring(0, objectStart + 1);
        String after = json.substring(objectStart + 1);
        String separator = after.trim().startsWith("}") ? "\n" : "\n,";
        return before + "\n    \"" + key + "\": " + rawValue + separator + after;
    }

    private static String ensureRegistryActive(String json, String registryName, String entryName, boolean active) {
        int registryKey = json.indexOf("\"" + registryName + "\"");
        int registryStart = registryKey >= 0 ? json.indexOf('{', registryKey) : -1;
        if (registryStart < 0) {
            int rootStart = json.indexOf('{');
            if (rootStart < 0) {
                return "{\n  \"" + registryName + "\": {\n    \"" + entryName + "\": {\n      \"active\": " + active + "\n    }\n  }\n}";
            }
            String before = json.substring(0, rootStart + 1);
            String after = json.substring(rootStart + 1);
            String separator = after.trim().startsWith("}") ? "\n" : "\n,";
            return before + "\n  \"" + registryName + "\": {\n    \"" + entryName + "\": {\n      \"active\": " + active + "\n    }\n  }" + separator + after;
        }

        int entryKey = json.indexOf("\"" + entryName + "\"", registryStart);
        int entryStart = entryKey >= 0 ? json.indexOf('{', entryKey) : -1;
        if (entryStart < 0) {
            String before = json.substring(0, registryStart + 1);
            String after = json.substring(registryStart + 1);
            String separator = after.trim().startsWith("}") ? "\n" : "\n,";
            return before + "\n    \"" + entryName + "\": {\n      \"active\": " + active + "\n    }" + separator + after;
        }
        String keyPattern = "\"active\"\\s*:\\s*(?:true|false)";
        String replacement = java.util.regex.Matcher.quoteReplacement("\"active\": " + active);
        int entryEnd = json.indexOf('}', entryStart);
        if (entryEnd < 0) {
            return json;
        }
        String entry = json.substring(entryStart, entryEnd + 1);
        if (entry.matches("(?s).*" + keyPattern + ".*")) {
            return json.substring(0, entryStart) + entry.replaceAll(keyPattern, replacement) + json.substring(entryEnd + 1);
        }
        return json.substring(0, entryStart + 1)
                + "\n      \"active\": " + active + ","
                + json.substring(entryStart + 1);
    }
}
