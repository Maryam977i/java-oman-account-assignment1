package com.progressoft;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerAccountTest {

    @Test
    public void summaryReturnsNoIdentifiersForEmptyList() {
        assertEquals(
                "Total Identifiers: 0, no identifiers provided",
                CustomerAccount.of(Arrays.asList()).getSummary()
        );
    }

    @Test
    public void summaryReturnsNoIdentifiersForNullList() {
        assertEquals(
                "Total Identifiers: 0, no identifiers provided",
                CustomerAccount.of((List<String>) null).getSummary()
        );
    }

    @Test
    public void summaryTreatsUnknownStringAsInvalid() {
        assertEquals(
                "Total Identifiers: 1, [1] UNKNOWN is invalid, no valid identifiers",
                CustomerAccount.of(Arrays.asList("INVALID")).getSummary()
        );
    }

    @Test
    public void summaryTreatsNullEntryAsInvalidUnknown() {
        assertEquals(
                "Total Identifiers: 1, [1] UNKNOWN is invalid, no valid identifiers",
                CustomerAccount.of(Arrays.asList((String) null)).getSummary()
        );
    }

    @Test
    public void summaryTreatsBlankEntryAsInvalidUnknown() {
        assertEquals(
                "Total Identifiers: 1, [1] UNKNOWN is invalid, no valid identifiers",
                CustomerAccount.of(Arrays.asList("   ")).getSummary()
        );
    }

    @Test
    public void summaryAcceptsValidOmanIban() {
        assertEquals(
                "Total Identifiers: 1, [1] IBAN is valid, all identifiers for same person",
                CustomerAccount.of(Arrays.asList("OM040270000001234567001")).getSummary()
        );
    }

    @Test
    public void summaryAcceptsValidOmanIbanWithSpaces() {
        assertEquals(
                "Total Identifiers: 1, [1] IBAN is valid, all identifiers for same person",
                CustomerAccount.of(Arrays.asList("OM04 027 000000 1234567 001")).getSummary()
        );
    }

    @Test
    public void summaryRejectsIbanWithInvalidChecksum() {
        assertEquals(
                "Total Identifiers: 1, [1] IBAN is invalid, no valid identifiers",
                CustomerAccount.of(Arrays.asList("OM990270000001234567001")).getSummary()
        );
    }

    @Test
    public void summaryRejectsNonOmanIban() {
        assertEquals(
                "Total Identifiers: 1, [1] IBAN is invalid, no valid identifiers",
                CustomerAccount.of(Arrays.asList("GB29NWBK60161331926819")).getSummary()
        );
    }

    @Test
    public void summaryAcceptsValidAccountNumber() {
        assertEquals(
                "Total Identifiers: 1, [1] Account number is valid, all identifiers for same person",
                CustomerAccount.of(Arrays.asList("03151234567001")).getSummary()
        );
    }

    @Test
    public void summaryRejectsAccountNumberWithInvalidLength() {
        assertEquals(
                "Total Identifiers: 1, [1] UNKNOWN is invalid, no valid identifiers",
                CustomerAccount.of(Arrays.asList("03151234567")).getSummary()
        );
    }

    @Test
    public void summaryRejectsAllZeroAccountNumber() {
        assertEquals(
                "Total Identifiers: 1, [1] UNKNOWN is invalid, no valid identifiers",
                CustomerAccount.of(Arrays.asList("00000000000000")).getSummary()
        );
    }

    @Test
    public void summaryAcceptsValidCifNumber() {
        assertEquals(
                "Total Identifiers: 1, [1] CIF is valid, all identifiers for same person",
                CustomerAccount.of(Arrays.asList("1234567")).getSummary()
        );
    }

    @Test
    public void summaryRejectsCifNumberWithInvalidLength() {
        assertEquals(
                "Total Identifiers: 1, [1] UNKNOWN is invalid, no valid identifiers",
                CustomerAccount.of(Arrays.asList("123456")).getSummary()
        );
    }

    @Test
    public void summaryRejectsAllZeroCifNumber() {
        assertEquals(
                "Total Identifiers: 1, [1] UNKNOWN is invalid, no valid identifiers",
                CustomerAccount.of(Arrays.asList("0000000")).getSummary()
        );
    }

    @Test
    public void summaryMatchesIbanAccountAndCifForSamePerson() {
        assertEquals(
                "Total Identifiers: 3, [1] IBAN is valid, [2] Account number is valid, [3] CIF is valid, all identifiers for same person",
                CustomerAccount.of(Arrays.asList("OM040270000001234567001", "03151234567001", "1234567")).getSummary()
        );
    }

    @Test
    public void summaryMatchesIbanAndAccountForSamePerson() {
        assertEquals(
                "Total Identifiers: 2, [1] IBAN is valid, [2] Account number is valid, all identifiers for same person",
                CustomerAccount.of(Arrays.asList("OM040270000001234567001", "03151234567001")).getSummary()
        );
    }

    @Test
    public void summaryMatchesAccountAndCifForSamePerson() {
        assertEquals(
                "Total Identifiers: 2, [1] Account number is valid, [2] CIF is valid, all identifiers for same person",
                CustomerAccount.of(Arrays.asList("03151234567001", "1234567")).getSummary()
        );
    }

    @Test
    public void summaryReportsOnlyValidIdentifiersWhenInvalidIdentifierIsPresent() {
        assertEquals(
                "Total Identifiers: 3, [1] IBAN is valid, [2] Account number is valid, [3] UNKNOWN is invalid, identifiers [1][2] for same person",
                CustomerAccount.of(Arrays.asList("OM040270000001234567001", "03151234567001", "INVALID")).getSummary()
        );
    }

    @Test
    public void summaryMatchesDuplicateIdentifiersForSamePerson() {
        assertEquals(
                "Total Identifiers: 3, [1] CIF is valid, [2] CIF is valid, [3] Account number is valid, all identifiers for same person",
                CustomerAccount.of(Arrays.asList("1234567", "1234567", "03151234567001")).getSummary()
        );
    }

    @Test
    public void summaryReportsMismatchingIbanAndAccount() {
        assertEquals(
                "Total Identifiers: 2, [1] IBAN is valid, [2] Account number is valid, identifiers [1][2] do not match",
                CustomerAccount.of(Arrays.asList("OM040270000001234567001", "03159999999001")).getSummary()
        );
    }

    @Test
    public void summaryReportsMismatchingAccounts() {
        assertEquals(
                "Total Identifiers: 2, [1] Account number is valid, [2] Account number is valid, identifiers [1][2] do not match",
                CustomerAccount.of(Arrays.asList("03151234567001", "03159999999001")).getSummary()
        );
    }

    @Test
    public void summaryReportsMismatchingCifs() {
        assertEquals(
                "Total Identifiers: 2, [1] CIF is valid, [2] CIF is valid, identifiers [1][2] do not match",
                CustomerAccount.of(Arrays.asList("1234567", "9999999")).getSummary()
        );
    }

    @Test
    public void summaryReportsAllMismatchingPairsInAscendingOrder() {
        assertEquals(
                "Total Identifiers: 3, [1] IBAN is valid, [2] Account number is valid, [3] CIF is valid, identifiers [1][2] do not match, [2][3] do not match",
                CustomerAccount.of(Arrays.asList("OM040270000001234567001", "03159999999001", "1234567")).getSummary()
        );
    }

    @Test
    public void summaryReportsNoValidIdentifiersWhenAllInputsAreInvalid() {
        assertEquals(
                "Total Identifiers: 4, [1] UNKNOWN is invalid, [2] UNKNOWN is invalid, [3] UNKNOWN is invalid, [4] UNKNOWN is invalid, no valid identifiers",
                CustomerAccount.of(Arrays.asList("INVALID_1", "123456", "   ", null)).getSummary()
        );
    }
}
