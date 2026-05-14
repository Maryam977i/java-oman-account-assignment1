package com.progressoft.dto;

import com.progressoft.model.AccountCheck;
import com.progressoft.model.Identifier;
import com.progressoft.model.OwnershipResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AccountCheckResponse {

    private String          requestId;
    private String          customerReference;
    private LocalDateTime   submittedAt;
    private String          status;
    private OwnershipResult ownershipResult;
    private String          extractedCif;
    private List<IdentifierDto> identifiers;

    public static AccountCheckResponse from(AccountCheck ac) {
        AccountCheckResponse r = new AccountCheckResponse();
        r.requestId         = ac.getRequestId();
        r.customerReference = ac.getCustomerReference();
        r.submittedAt       = ac.getSubmittedAt();
        r.status            = ac.getStatus() != null ? ac.getStatus().name() : null;
        r.ownershipResult   = ac.getOwnershipResult();
        r.extractedCif      = ac.getExtractedCif();
        r.identifiers       = ac.getIdentifiers().stream()
                .map(IdentifierDto::from).collect(Collectors.toList());
        return r;
    }

    public String          getRequestId()         { return requestId; }
    public String          getCustomerReference() { return customerReference; }
    public LocalDateTime   getSubmittedAt()       { return submittedAt; }
    public String          getStatus()            { return status; }
    public OwnershipResult getOwnershipResult()   { return ownershipResult; }
    public String          getExtractedCif()      { return extractedCif; }
    public List<IdentifierDto> getIdentifiers()   { return identifiers; }

    public static class IdentifierDto {
        private int     position;
        private String  rawValue;
        private String  type;
        private boolean valid;
        private String  validationError;
        private String  extractedCif;

        public static IdentifierDto from(Identifier id) {
            IdentifierDto d = new IdentifierDto();
            d.position        = id.getPosition();
            d.rawValue        = id.getRawValue();
            d.type            = id.getIdentifierType() != null ? id.getIdentifierType().name() : null;
            d.valid           = Boolean.TRUE.equals(id.getIsValid());
            d.validationError = id.getValidationError();
            d.extractedCif    = id.getExtractedCif();
            return d;
        }

        public int    getPosition()        { return position; }
        public String getRawValue()        { return rawValue; }
        public String getType()            { return type; }
        public boolean isValid()           { return valid; }
        public String getValidationError() { return validationError; }
        public String getExtractedCif()    { return extractedCif; }
    }
}