package com.progressoft.service;

import org.springframework.stereotype.Service;
import java.math.BigInteger;

@Service
public class ValidationService {

    public ValidationResult validateIdentifier(String rawValue, int position) {
        if (rawValue == null || rawValue.isBlank()) {
            return ValidationResult.invalid(position, rawValue, "UNKNOWN", "Identifier is null or blank");
        }

        String trimmed = rawValue.strip();

        if (!trimmed.matches("\\d+")) {
            return validateIban(position, trimmed);
        }

        if (trimmed.length() == 14) return validateAccountNumber(position, trimmed);
        if (trimmed.length() == 7)  return validateCif(position, trimmed);

        return ValidationResult.invalid(position, trimmed, "UNKNOWN", "Unknown identifier format");
    }

    private ValidationResult validateIban(int position, String raw) {
        String iban = raw.replaceAll("\\s", "").toUpperCase();

        if (!iban.startsWith("OM"))
            return ValidationResult.invalid(position, raw, "IBAN", "Only Oman IBANs (OM) are supported");
        if (iban.length() != 23)
            return ValidationResult.invalid(position, raw, "IBAN", "Oman IBAN must be 23 characters, got " + iban.length());
        if (!iban.matches("[A-Z0-9]+"))
            return ValidationResult.invalid(position, raw, "IBAN", "IBAN must contain letters and digits only");
        if (!ibanMod97(iban))
            return ValidationResult.invalid(position, raw, "IBAN", "IBAN failed mod-97 checksum");

        // OM(2) + check(2) + bank(3) + padding(6) + CIF(7) + type(3)
        String cif = iban.substring(13, 20);
        return ValidationResult.valid(position, raw, "IBAN", cif);
    }

    private ValidationResult validateAccountNumber(int position, String raw) {
        if (raw.chars().allMatch(c -> c == '0'))
            return ValidationResult.invalid(position, raw, "UNKNOWN", "Account number must not be all zeros");

        // branch(4) + CIF(7) + type(3)
        String cif = raw.substring(4, 11);
        return ValidationResult.valid(position, raw, "ACCOUNT_NUMBER", cif);
    }

    private ValidationResult validateCif(int position, String raw) {
        if (raw.chars().allMatch(c -> c == '0'))
            return ValidationResult.invalid(position, raw, "UNKNOWN", "CIF must not be all zeros");

        return ValidationResult.valid(position, raw, "CIF_NUMBER", raw);
    }

    private boolean ibanMod97(String iban) {
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        StringBuilder numeric = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isLetter(c)) numeric.append(c - 'A' + 10);
            else                       numeric.append(c);
        }
        return new BigInteger(numeric.toString()).mod(BigInteger.valueOf(97)).intValue() == 1;
    }

    public static class ValidationResult {
        private final int     position;
        private final String  rawValue;
        private final String  type;
        private final boolean valid;
        private final String  error;
        private final String  extractedCif;

        private ValidationResult(int position, String rawValue, String type,
                                 boolean valid, String error, String extractedCif) {
            this.position     = position;
            this.rawValue     = rawValue;
            this.type         = type;
            this.valid        = valid;
            this.error        = error;
            this.extractedCif = extractedCif;
        }

        public static ValidationResult valid(int pos, String raw, String type, String cif) {
            return new ValidationResult(pos, raw, type, true, null, cif);
        }

        public static ValidationResult invalid(int pos, String raw, String type, String error) {
            return new ValidationResult(pos, raw, type, false, error, null);
        }

        public int     getPosition()     { return position; }
        public String  getRawValue()     { return rawValue; }
        public String  getType()         { return type; }
        public boolean isValid()         { return valid; }
        public String  getError()        { return error; }
        public String  getExtractedCif() { return extractedCif; }
    }
}