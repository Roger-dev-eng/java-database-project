package app.validation;

import java.sql.Date;

public final class Validator {
    private Validator() {
    }

    public static String requiredText(String value, String fieldName) {
        String normalized = normalize(value);
        if (normalized.isEmpty()) {
            throw new ValidationException("Preencha o campo " + fieldName + "!");
        }
        return normalized;
    }

    public static Integer requiredInt(String value, String fieldName) {
        String normalized = requiredText(value, fieldName);
        try {
            return Integer.parseInt(normalized);
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " deve ser um número!");
        }
    }

    public static Date requiredDate(String value, String fieldName) {
        String normalized = requiredText(value, fieldName);
        try {
            return Date.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(fieldName + " inválida! Use yyyy-MM-dd.");
        }
    }

    public static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    public static void rangeInclusive(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new ValidationException(fieldName + " deve estar entre " + min + " e " + max + "!");
        }
    }
}
