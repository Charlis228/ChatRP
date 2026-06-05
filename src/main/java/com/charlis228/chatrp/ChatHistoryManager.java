package com.charlis228.chatrp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.time.Instant;

public class ChatHistoryManager {
    private static final Path historyPath = findHistoryPath();
    private static final Object lock = new Object();

    private static Path findHistoryPath() {
        String appdata = System.getenv("APPDATA");
        Path p;
        if (appdata != null && !appdata.isEmpty()) {
            p = Paths.get(appdata, ".minecraft", "config", "chatrp", "chat_history.json");
        } else {
            p = Paths.get(System.getProperty("user.home"), ".minecraft", "config", "chatrp", "chat_history.json");
        }
        try {
            Files.createDirectories(p.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static void append(String channel, String player, String message) {
        synchronized (lock) {
            try {
                String content = "";
                if (Files.exists(historyPath)) {
                    content = new String(Files.readAllBytes(historyPath), java.nio.charset.StandardCharsets.UTF_8).trim();
                }
                if (content.isEmpty()) {
                    content = "[]";
                }
                String timestamp = Instant.now().toString();
                String obj = String.format("{\"timestamp\":\"%s\",\"type\":\"chat\",\"channel\":\"%s\",\"player\":\"%s\",\"message\":\"%s\"}",
                        escape(timestamp), escape(channel), escape(player), escape(message));
                if (content.equals("[]")) {
                    content = "[" + obj + "]";
                } else {
                    content = content.substring(0, content.length() - 1) + "," + obj + "]";
                }
                Files.write(historyPath, content.getBytes(java.nio.charset.StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    public static Path getHistoryPath() {
        return historyPath;
    }
}
