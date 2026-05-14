# Banking Identifier Standards And Validation Guide - Oman

This guide summarizes the identifier rules used by this assignment. It is intentionally narrower than a full banking-system specification.

## Identifier Hierarchy

| Identifier | Level | Scope | Purpose |
| :--- | :--- | :--- | :--- |
| CIF | Customer profile | Internal bank | Identifies one person/customer. |
| Account number | Bank product | Local bank | Identifies a specific account owned by a customer. |
| IBAN | Transfer address | National/international transfers | Identifies the account used for payment routing. |

## Oman IBAN Structure

For this assignment, an Oman IBAN contains 23 characters after removing spaces:

```text
OM + 2 check digits + 3 bank-code digits + 16 account digits
```

Example:

```text
OM040270000001234567001
```

Breakdown:

| Segment | Length | Example | Description |
| :--- | ---: | :--- | :--- |
| Country code | 2 | `OM` | Oman country code. |
| Check digits | 2 | `04` | IBAN checksum digits. |
| Bank code | 3 | `027` | Bank identifier. |
| Account section | 16 | `0000001234567001` | Padded account value. |

The assignment uses the middle 7 digits of the account section as the customer CIF:

```text
000000 1234567 001
```

In this example, the CIF is `1234567`.

## Account Number Structure

A supported account number contains 14 digits:

```text
4 branch-code digits + 7 CIF digits + 3 account-type digits
```

Example:

```text
03151234567001
```

Breakdown:

```text
0315 1234567 001
```

In this example, the CIF is `1234567`.

## CIF Structure

A supported CIF contains exactly 7 digits and must not be all zeros.

Example:

```text
1234567
```

## Ownership Verification

Valid identifiers belong to the same person when their extracted CIF values are equal.

Invalid identifiers are reported in the summary but are not used for ownership verification.
