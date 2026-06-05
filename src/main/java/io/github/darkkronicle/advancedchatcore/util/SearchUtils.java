package io.github.darkkronicle.advancedchatcore.util;

import io.github.darkkronicle.advancedchatcore.chat.MessageOwner;
import io.github.darkkronicle.advancedchatcore.interfaces.IFinder;
import net.minecraft.class_2561;
import net.minecraft.class_634;
import net.minecraft.class_640;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public final class SearchUtils {
    private static final Pattern COLOR_CODES = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");

    private SearchUtils() {
    }

    public static boolean isMatch(String text, String filter, FindType findType) {
        IFinder finder = findType.getFinder();
        return finder != null && finder.isMatch(text, filter);
    }

    public static boolean isMatch(class_2561 text, String filter, FindType findType) {
        IFinder finder = findType.getFinder();
        return finder != null && finder.isMatch(text, filter);
    }

    public static String replaceGroups(List<StringMatch> matches, String replacement) {
        if (replacement.length() < 2 || !replacement.contains("$")) {
            return replacement;
        }

        StringBuilder builder = new StringBuilder();
        int last = 0;
        for (StringMatch match : matches) {
            if (match.match.length() < 2 || match.match.charAt(0) != '$') {
                continue;
            }
            int index;
            try {
                index = Integer.parseInt(match.match.substring(1, 2));
            } catch (NumberFormatException ignored) {
                continue;
            }
            if (index <= 0 || index >= matches.size()) {
                continue;
            }
            builder.append(replacement, last, match.start);
            builder.append(matches.get(index));
            last = match.end;
        }
        if (last != replacement.length()) {
            builder.append(replacement, last, replacement.length());
        }
        return builder.toString();
    }

    public static Optional<List<StringMatch>> findMatches(String text, String filter, FindType findType) {
        IFinder finder = findType.getFinder();
        if (finder == null) {
            return Optional.empty();
        }
        Set<StringMatch> matches = new TreeSet<>(finder.getMatches(text, filter));
        if (matches.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(List.copyOf(matches));
    }

    public static Optional<List<StringMatch>> findMatches(class_2561 text, String filter, FindType findType) {
        IFinder finder = findType.getFinder();
        if (finder == null) {
            return Optional.empty();
        }
        Set<StringMatch> matches = new TreeSet<>(finder.getMatches(text, filter));
        if (matches.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(List.copyOf(matches));
    }

    public static Optional<StringMatch> getMatch(String text, String filter, FindType findType) {
        return findMatches(text, filter, findType).map(matches -> matches.get(0));
    }

    public static Optional<StringMatch> getMatch(class_2561 text, String filter, FindType findType) {
        return findMatches(text, filter, findType).map(matches -> matches.get(0));
    }

    public static MessageOwner getAuthor(class_634 handler, String message) {
        if (handler == null || message == null || message.isBlank()) {
            return null;
        }

        String plainMessage = stripColorCodes(message);
        for (class_640 entry : handler.method_2880()) {
            String profileName = entry.method_2966().name();
            if (profileName == null || profileName.isBlank()) {
                continue;
            }
            if (containsMinecraftName(plainMessage, profileName)) {
                return new MessageOwner(profileName, entry);
            }

            class_2561 displayName = entry.method_2971();
            if (displayName != null) {
                String cleanDisplay = stripColorCodes(displayName.getString());
                if (!cleanDisplay.equals(profileName) && containsDisplayName(plainMessage, cleanDisplay)) {
                    return new MessageOwner(profileName, entry);
                }
            }
        }
        return null;
    }

    public static String stripColorCodes(String text) {
        if (text == null) {
            return "";
        }
        return COLOR_CODES.matcher(text).replaceAll("");
    }

    public static String toRoman(int number) {
        if (number <= 0) {
            return "";
        }
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] romans = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                builder.append(romans[i]);
                number -= values[i];
            }
        }
        return builder.toString();
    }

    private static boolean containsMinecraftName(String text, String name) {
        Pattern pattern = Pattern.compile("(?<![A-Za-z0-9_])" + Pattern.quote(name) + "(?![A-Za-z0-9_])");
        return pattern.matcher(text).find();
    }

    private static boolean containsDisplayName(String text, String displayName) {
        if (displayName == null || displayName.isBlank()) {
            return false;
        }
        String trimmed = displayName.trim();
        if (trimmed.length() < 3 || trimmed.length() > 32) {
            return false;
        }
        return text.contains(trimmed);
    }
}
