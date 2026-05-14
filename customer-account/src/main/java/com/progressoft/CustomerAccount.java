package com.progressoft;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerAccount {

    public enum IdentifierType {
        IBAN, ACCOUNT_NUMBER, CIF_NUMBER, UNKNOWN
    }

    private final List<String> identifiers;

    private CustomerAccount(List<String> identifiers) {
        this.identifiers = identifiers != null ? new ArrayList<>(identifiers) : new ArrayList<>();
    }

    public static CustomerAccount of(List<String> identifiers) {
        return new CustomerAccount(identifiers);
    }

    public String getSummary() {
        int total = identifiers.size();

        if (total == 0) {
            return "Total Identifiers: 0, no identifiers provided";
        }

        List<Classified> classified = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            classified.add(classify(i + 1, identifiers.get(i)));
        }

        String labels = classified.stream()
                .map(Classified::label)
                .collect(Collectors.joining(", "));

        String conclusion = buildConclusion(classified);

        return "Total Identifiers: " + total + ", " + labels + ", " + conclusion;
    }

    private String buildConclusion(List<Classified> all) {
        List<Classified> valid = all.stream().filter(c -> c.valid).collect(Collectors.toList());

        if (valid.isEmpty()) {
            return "no valid identifiers";
        }

        List<String> mismatchParts = new ArrayList<>();
        for (int i = 0; i < valid.size() - 1; i++) {
            Classified a = valid.get(i);
            Classified b = valid.get(i + 1);
            if (!a.cif.equals(b.cif)) {
                mismatchParts.add("[" + a.position + "][" + b.position + "] do not match");
            }
        }

        if (mismatchParts.isEmpty()) {
            boolean allInputsValid = all.stream().allMatch(c -> c.valid);
            if (allInputsValid) {
                return "all identifiers for same person";
            } else {
                String validPositions = valid.stream()
                        .map(c -> "[" + c.position + "]")
                        .collect(Collectors.joining(""));
                return "identifiers " + validPositions + " for same person";
            }
        }

        return "identifiers " + String.join(", ", mismatchParts);
    }

    private Classified classify(int position, String raw) {
        if (raw == null || raw.isBlank()) {
            return Classified.invalid(position, IdentifierType.UNKNOWN);
        }

        String trimmed = raw.strip();
        String noSpaces = trimmed.replaceAll("\\s", "");


        if (noSpaces.matches("\\d+")) {
            if (noSpaces.length() == 14) return classifyAccountNumber(position, noSpaces);
            if (noSpaces.length() == 7)  return classifyCif(position, noSpaces);
            return Classified.invalid(position, IdentifierType.UNKNOWN);
        }


        if (noSpaces.matches("[A-Za-z0-9]+") && noSpaces.matches(".*[A-Za-z].*") && noSpaces.matches(".*\\d.*")) {
            return classifyIban(position, trimmed);
        }


        return Classified.invalid(position, IdentifierType.UNKNOWN);
    }

    private Classified classifyIban(int position, String raw) {
        String iban = raw.replaceAll("\\s", "").toUpperCase();

        if (!iban.startsWith("OM"))      return Classified.invalid(position, IdentifierType.IBAN);
        if (iban.length() != 23)         return Classified.invalid(position, IdentifierType.IBAN);
        if (!iban.matches("[A-Z0-9]+"))  return Classified.invalid(position, IdentifierType.IBAN);
        if (!ibanMod97(iban))            return Classified.invalid(position, IdentifierType.IBAN);

        String cif = iban.substring(13, 20);
        return Classified.valid(position, IdentifierType.IBAN, cif);
    }

    private Classified classifyAccountNumber(int position, String raw) {
        if (raw.chars().allMatch(c -> c == '0')) {
            return Classified.invalid(position, IdentifierType.UNKNOWN);
        }
        String cif = raw.substring(4, 11);
        return Classified.valid(position, IdentifierType.ACCOUNT_NUMBER, cif);
    }

    private Classified classifyCif(int position, String raw) {
        if (raw.chars().allMatch(c -> c == '0')) {
            return Classified.invalid(position, IdentifierType.UNKNOWN);
        }
        return Classified.valid(position, IdentifierType.CIF_NUMBER, raw);
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

    private static class Classified {
        final int            position;
        final IdentifierType type;
        final boolean        valid;
        final String         cif;

        private Classified(int position, IdentifierType type, boolean valid, String cif) {
            this.position = position;
            this.type     = type;
            this.valid    = valid;
            this.cif      = cif;
        }

        static Classified valid(int pos, IdentifierType type, String cif) {
            return new Classified(pos, type, true, cif);
        }

        static Classified invalid(int pos, IdentifierType type) {
            return new Classified(pos, type, false, null);
        }

        String label() {
            String typeName = switch (type) {
                case IBAN           -> "IBAN";
                case ACCOUNT_NUMBER -> "Account number";
                case CIF_NUMBER     -> "CIF";
                case UNKNOWN        -> "UNKNOWN";
            };
            return "[" + position + "] " + typeName + (valid ? " is valid" : " is invalid");
        }
    }
}