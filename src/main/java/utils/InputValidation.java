package utils;

import java.util.regex.Pattern;

public final class InputValidation {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+[1-9]\\d{6,14}$");
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^\\d{6}$");

    private InputValidation() { }

    public static void requireValidPassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Parola trebuie să aibă minimum 8 caractere, cel puțin o literă, o cifră și un simbol.");
        }
    }

    public static void requireValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalArgumentException("Numărul de telefon trebuie să fie în format internațional, cu prefix de țară.");
        }
    }

    public static void requireValidPostalCode(String postalCode) {
        if (postalCode == null || !POSTAL_CODE_PATTERN.matcher(postalCode).matches()) {
            throw new IllegalArgumentException("Codul poștal trebuie să conțină exact 6 cifre.");
        }
    }
}
