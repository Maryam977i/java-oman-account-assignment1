package com.progressoft.model;

import jakarta.persistence.*;

@Entity
@Table(name = "identifiers")
public class Identifier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false)
    private String rawValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "identifier_type")
    private IdentifierType identifierType;

    @Column(name = "is_valid")
    private Boolean isValid;

    @Column(name = "validation_error", length = 500)
    private String validationError;

    @Column(name = "extracted_cif")
    private String extractedCif;

    @ManyToOne
    @JoinColumn(name = "account_check_id")
    private AccountCheck accountCheck;

    public Identifier() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
    public String getRawValue() { return rawValue; }
    public void setRawValue(String rawValue) { this.rawValue = rawValue; }
    public IdentifierType getIdentifierType() { return identifierType; }
    public void setIdentifierType(IdentifierType identifierType) { this.identifierType = identifierType; }
    public Boolean getIsValid() { return isValid; }
    public void setIsValid(Boolean isValid) { this.isValid = isValid; }
    public String getValidationError() { return validationError; }
    public void setValidationError(String validationError) { this.validationError = validationError; }
    public String getExtractedCif() { return extractedCif; }
    public void setExtractedCif(String extractedCif) { this.extractedCif = extractedCif; }
    public AccountCheck getAccountCheck() { return accountCheck; }
    public void setAccountCheck(AccountCheck accountCheck) { this.accountCheck = accountCheck; }
}