package pon.purr.utils;

import java.util.*;
import java.util.stream.Collectors;


public class MentionChecker {
    private static final Map<String, String> TRANSLIT_MAP = new LinkedHashMap<>();

    static {
        // Сначала двузвучные и трёхзвучные
        TRANSLIT_MAP.put("shch", "щ");
        TRANSLIT_MAP.put("ay", "э");
        TRANSLIT_MAP.put("yo", "ё");
        TRANSLIT_MAP.put("zh", "ж");
        TRANSLIT_MAP.put("kh", "х");
        TRANSLIT_MAP.put("ts", "ц");
        TRANSLIT_MAP.put("ch", "ч");
        TRANSLIT_MAP.put("sh", "ш");
        TRANSLIT_MAP.put("yu", "ю");
        TRANSLIT_MAP.put("ya", "я");
        // цифры
        TRANSLIT_MAP.put("4", "ч");
        TRANSLIT_MAP.put("3", "е");
        // Однозвучные
        TRANSLIT_MAP.put("a", "а");
        TRANSLIT_MAP.put("b", "б");
        TRANSLIT_MAP.put("v", "в");
        TRANSLIT_MAP.put("g", "г");
        TRANSLIT_MAP.put("d", "д");
        TRANSLIT_MAP.put("e", "е");
        TRANSLIT_MAP.put("z", "з");
        TRANSLIT_MAP.put("i", "и");
        TRANSLIT_MAP.put("y", "й");
        TRANSLIT_MAP.put("k", "к");
        TRANSLIT_MAP.put("l", "л");
        TRANSLIT_MAP.put("m", "м");
        TRANSLIT_MAP.put("n", "н");
        TRANSLIT_MAP.put("w", "в");
        TRANSLIT_MAP.put("o", "о");
        TRANSLIT_MAP.put("p", "п");
        TRANSLIT_MAP.put("r", "р");
        TRANSLIT_MAP.put("s", "с");
        TRANSLIT_MAP.put("t", "т");
        TRANSLIT_MAP.put("u", "у");
        TRANSLIT_MAP.put("f", "ф");
        TRANSLIT_MAP.put("h", "х");
        TRANSLIT_MAP.put("’", "ь");
        TRANSLIT_MAP.put("'", "ь");
    }
    public static String transliterateToCyrillic(String latin) {
        StringBuilder rus = new StringBuilder();
        String lower = latin.toLowerCase();
        int i = 0, len = lower.length();

        while (i < len) {
            boolean matched = false;
            for (int l = 4; l > 0; l--) {
                if (i + l <= len) {
                    String chunk = lower.substring(i, i + l);
                    String repl = TRANSLIT_MAP.get(chunk);
                    if (repl != null) {
                        rus.append(repl);
                        i += l;
                        matched = true;
                        break;
                    }
                }
            }
            if (!matched) {
                rus.append(lower.charAt(i));
                i++;
            }
        }
        return rus.toString();
    }

    public static String checkMention(String text, String mention, Boolean autoOut, String mentions) {
        Set<String> variations = new LinkedHashSet<>();

        String lowerText = text.toLowerCase();

        if (mentions != null && !mentions.isEmpty()) {
            String[] customArray = mentions.split(",");
            for (String custom : customArray) {
                variations.add(custom.strip().toLowerCase());
            }
        }

        if (autoOut) {
            String lowerName = mention.toLowerCase();
            // Генерируем все вариации
            String englishNoDigits = lowerName.replaceAll("\\d", "");
            String nameRus = transliterateToCyrillic(lowerName);
            String russianNoDigits = nameRus.replaceAll("\\d", "");

            variations.add(lowerName);
            variations.add(nameRus);
            variations.add(englishNoDigits);
            variations.add(russianNoDigits);
        }
        // Удаляем пустые строки
        List<String> toCheck = variations.stream()
                .filter(var -> var != null && !var.isEmpty())
                .collect(Collectors.toList());

        // Ищем первую подходящую вариацию
        for (String variant : toCheck) {
            if (lowerText.contains(variant)) {
                return variant;
            }
        }
        return null;
    }
    public static String checkMention(String text, String mention, Boolean autoOut) {
        return checkMention(text, mention, autoOut, "");
    }
    public static String checkMention(String text, String mention) {
        return checkMention(text, mention, true, "");
    }
}
