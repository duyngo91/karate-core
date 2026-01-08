package core.platform.web.common;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class Common {
    private Common() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    public static String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    public static String removeAccentAndCamel(String s) {
        return toCamelCase(removeAccent(s.trim()));
    }

    public static String toCamelCase(String inputString) {
        if (inputString == null || inputString.isEmpty()) {
            return inputString;
        }

        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;

        for (int i = 0; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);

            if (currentChar == '_' || currentChar == ' ') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(currentChar));
                    nextUpperCase = false;
                } else {
                    result.append(currentChar);
                }
            }
        }
        return result.toString();
    }
}
