package io.github.darkkronicle.advancedchatbox.chat;

import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.class_10799;
import net.minecraft.class_11908;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_640;
import net.minecraft.class_768;
import net.minecraft.class_8685;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ChatSuggestorGui$SuggestionWindow {
    private static final int ROW_HEIGHT = 14;
    private static final int HEAD_SIZE = 8;
    private static final int MAX_ROWS = 18;
    private static final int BACKGROUND = 0xD0000000;
    private static final int SELECTED = 0x603A3A3A;
    private static final int TEXT = 0xFFEFEFEF;
    private static final int TEXT_SELECTED = 0xFFFFD700;

    private final ChatSuggestorGui this$0;
    private final class_768 area;
    private final String typedText;
    private final List<AdvancedSuggestion> suggestions;
    private final int visibleRows;
    private int inWindowIndex;
    private int selection;
    private boolean completed;

    ChatSuggestorGui$SuggestionWindow(ChatSuggestorGui owner, int x, int y, int width, List<AdvancedSuggestion> suggestions, boolean narrateFirst) {
        this.this$0 = owner;
        this.suggestions = suggestions;
        this.visibleRows = Math.min(Math.min(MAX_ROWS, getMaxSuggestionSize(owner)), suggestions.size());
        this.typedText = textField().method_1882();

        int drawWidth = Math.max(96, width + HEAD_SIZE + 8);
        int drawX = Math.max(0, x - HEAD_SIZE - 4);
        int drawY = getBoolean(owner, "chatScreenSized") ? y - visibleRows * ROW_HEIGHT - 3 : y;
        this.area = new class_768(drawX, drawY, drawWidth, visibleRows * ROW_HEIGHT);
        this.selection = narrateFirst ? -1 : 0;
        select(0);
    }

    public void render(class_332 context, int mouseX, int mouseY) {
        context.method_25294(area.method_3321(), area.method_3322(), area.method_3321() + area.method_3319(), area.method_3322() + area.method_3320(), BACKGROUND);

        for (int row = 0; row < visibleRows; row++) {
            int index = row + inWindowIndex;
            if (index < 0 || index >= suggestions.size()) {
                continue;
            }
            AdvancedSuggestion suggestion = suggestions.get(index);
            int rowY = area.method_3322() + row * ROW_HEIGHT;
            boolean hovered = mouseX >= area.method_3321()
                    && mouseX < area.method_3321() + area.method_3319()
                    && mouseY >= rowY
                    && mouseY < rowY + ROW_HEIGHT;
            if (hovered) {
                select(index);
            }
            if (index == selection) {
                context.method_25294(area.method_3321(), rowY, area.method_3321() + area.method_3319(), rowY + ROW_HEIGHT, SELECTED);
            }

            class_640 player = findPlayer(suggestion);
            if (player != null) {
                drawHead(context, player, area.method_3321() + 3, rowY + 3);
            }

            int color = index == selection ? TEXT_SELECTED : TEXT;
            context.method_27535(textRenderer(), suggestion.getRender(), area.method_3321() + 15, rowY + 3, color);
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!area.method_3318(mouseX, mouseY)) {
            return false;
        }
        int row = (mouseY - area.method_3322()) / ROW_HEIGHT;
        int index = row + inWindowIndex;
        if (index >= 0 && index < suggestions.size()) {
            select(index);
            complete();
        }
        return true;
    }

    public boolean mouseScrolled(double amount) {
        int maxStart = Math.max(0, suggestions.size() - visibleRows);
        inWindowIndex = clamp(inWindowIndex - (int) amount, 0, maxStart);
        return true;
    }

    public boolean keyPressed(class_11908 key) {
        int code = key.comp_4795();
        if (code == 265) {
            scroll(-1);
            completed = false;
            return true;
        }
        if (code == 264) {
            scroll(1);
            completed = false;
            return true;
        }
        if (code == 258) {
            if (completed) {
                scroll(1);
            }
            complete();
            return true;
        }
        if (code == 256) {
            discard();
            return true;
        }
        return false;
    }

    public void scroll(int offset) {
        select(selection + offset);
        if (selection < inWindowIndex) {
            inWindowIndex = selection;
        }
        if (selection >= inWindowIndex + visibleRows) {
            inWindowIndex = selection - visibleRows + 1;
        }
        inWindowIndex = clamp(inWindowIndex, 0, Math.max(0, suggestions.size() - visibleRows));
    }

    public void select(int index) {
        if (suggestions.isEmpty()) {
            selection = 0;
            return;
        }
        if (index < 0) {
            index += suggestions.size();
        }
        if (index >= suggestions.size()) {
            index -= suggestions.size();
        }
        selection = clamp(index, 0, suggestions.size() - 1);
        Suggestion suggestion = suggestions.get(selection);
        textField().method_1887(getSuggestionSuffix(textField().method_1882(), suggestion.apply(typedText)));
    }

    public void complete() {
        if (suggestions.isEmpty()) {
            return;
        }
        Suggestion suggestion = suggestions.get(selection);
        setBoolean(this$0, "completingSuggestions", true);
        class_342 field = textField();
        field.method_1852(suggestion.apply(typedText));
        int cursor = suggestion.getRange().getStart() + suggestion.getText().length();
        field.method_1875(cursor);
        field.method_1884(cursor);
        select(selection);
        setBoolean(this$0, "completingSuggestions", false);
        completed = true;
    }

    public void discard() {
        set(this$0, "window", null);
    }

    private class_640 findPlayer(AdvancedSuggestion suggestion) {
        String wanted = cleanName(suggestion.getText());
        if (wanted.isBlank()) {
            wanted = cleanName(suggestion.getRender().getString());
        }
        if (wanted.isBlank()) {
            return null;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null || client.field_1724.field_3944 == null) {
            return null;
        }
        for (class_640 entry : client.field_1724.field_3944.method_2880()) {
            String profileName = entry.method_2966().name();
            if (profileName != null && profileName.equalsIgnoreCase(wanted)) {
                return entry;
            }
            if (entry.method_2971() != null && cleanName(entry.method_2971().getString()).equalsIgnoreCase(wanted)) {
                return entry;
            }
        }
        return null;
    }

    private static void drawHead(class_332 context, class_640 player, int x, int y) {
        class_8685 skin = player.method_52810();
        if (skin == null || skin.comp_1626() == null) {
            return;
        }
        class_2960 texture = skin.comp_1626().comp_3627();
        context.method_25290(class_10799.field_56883, texture, x, y, 8.0F, 8.0F, HEAD_SIZE, HEAD_SIZE, 64, 64);
        context.method_25290(class_10799.field_56883, texture, x, y, 40.0F, 8.0F, HEAD_SIZE, HEAD_SIZE, 64, 64);
    }

    private class_342 textField() {
        return get(this$0, "textField");
    }

    private class_327 textRenderer() {
        return get(this$0, "textRenderer");
    }

    private static String cleanName(String value) {
        return value == null ? "" : value.replaceAll("(?i)\\u00a7[0-9A-FK-OR]", "").trim().toLowerCase(Locale.ROOT);
    }

    private static String getSuggestionSuffix(String typed, String suggestion) {
        return suggestion.startsWith(typed) ? suggestion.substring(typed.length()) : null;
    }

    private static int getMaxSuggestionSize(ChatSuggestorGui owner) {
        return Math.max(1, Math.min(MAX_ROWS, getInt(owner, "maxSuggestionSize")));
    }

    @SuppressWarnings("unchecked")
    private static <T> T get(Object target, String name) {
        try {
            Field field = ChatSuggestorGui.class.getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("ChatRp не смог получить поле AdvancedChatBox: " + name, exception);
        }
    }

    private static int getInt(Object target, String name) {
        try {
            Field field = ChatSuggestorGui.class.getDeclaredField(name);
            field.setAccessible(true);
            return field.getInt(target);
        } catch (ReflectiveOperationException exception) {
            return MAX_ROWS;
        }
    }

    private static boolean getBoolean(Object target, String name) {
        try {
            Field field = ChatSuggestorGui.class.getDeclaredField(name);
            field.setAccessible(true);
            return field.getBoolean(target);
        } catch (ReflectiveOperationException exception) {
            return false;
        }
    }

    private static void setBoolean(Object target, String name, boolean value) {
        try {
            Field field = ChatSuggestorGui.class.getDeclaredField(name);
            field.setAccessible(true);
            field.setBoolean(target, value);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private static void set(Object target, String name, Object value) {
        try {
            Field field = ChatSuggestorGui.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
