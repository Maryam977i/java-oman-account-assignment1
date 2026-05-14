package com.progressoft.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "account_checks")
public class AccountCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private ImportBatch batch;

    @Column(name = "request_id", nullable = false, unique = true)
    private String requestId;

    @Column(name = "customer_reference", nullable = false)
    private String customerReference;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "ownership_result")
    private OwnershipResult ownershipResult;

    @Column(name = "extracted_cif")
    private String extractedCif;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RowStatus status;

    @OneToMany(mappedBy = "accountCheck", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Identifier> identifiers = new ArrayList<>();

    public AccountCheck() {}

    public Long getId()                              { return id; }
    public ImportBatch getBatch()                    { return batch; }
    public void setBatch(ImportBatch v)              { batch = v; }
    public String getRequestId()                     { return requestId; }
    public void setRequestId(String v)               { requestId = v; }
    public String getCustomerReference()             { return customerReference; }
    public void setCustomerReference(String v)       { customerReference = v; }
    public LocalDateTime getSubmittedAt()            { return submittedAt; }
    public void setSubmittedAt(LocalDateTime v)      { submittedAt = v; }
    public OwnershipResult getOwnershipResult()      { return ownershipResult; }
    public void setOwnershipResult(OwnershipResult v){ ownershipResult = v; }
    public String getExtractedCif()                  { return extractedCif; }
    public void setExtractedCif(String v)            { extractedCif = v; }
    public RowStatus getStatus()                     { return status; }
    public void setStatus(RowStatus v)               { status = v; }
    public List<Identifier> getIdentifiers()         { return identifiers; }
    public void setIdentifiers(List<Identifier> v)   { identifiers = v; }
}