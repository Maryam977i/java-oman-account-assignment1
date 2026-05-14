package com.progressoft.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "import_batches")
public class ImportBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "imported_at", nullable = false)
    private LocalDateTime importedAt;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "total_rows", nullable = false)
    private int totalRows;

    @Column(name = "imported_rows", nullable = false)
    private int importedRows;

    @Column(name = "duplicate_rows", nullable = false)
    private int duplicateRows;

    @Column(name = "invalid_rows", nullable = false)
    private int invalidRows;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccountCheck> accountChecks = new ArrayList<>();

    public ImportBatch() {}

    public ImportBatch(LocalDateTime importedAt, String fileName,
                       int totalRows, int importedRows, int duplicateRows, int invalidRows) {
        this.importedAt    = importedAt;
        this.fileName      = fileName;
        this.totalRows     = totalRows;
        this.importedRows  = importedRows;
        this.duplicateRows = duplicateRows;
        this.invalidRows   = invalidRows;
    }

    public UUID getId()                           { return id; }
    public LocalDateTime getImportedAt()          { return importedAt; }
    public String getFileName()                   { return fileName; }
    public int getTotalRows()                     { return totalRows; }
    public void setTotalRows(int v)               { totalRows = v; }
    public int getImportedRows()                  { return importedRows; }
    public void setImportedRows(int v)            { importedRows = v; }
    public int getDuplicateRows()                 { return duplicateRows; }
    public void setDuplicateRows(int v)           { duplicateRows = v; }
    public int getInvalidRows()                   { return invalidRows; }
    public void setInvalidRows(int v)             { invalidRows = v; }
    public List<AccountCheck> getAccountChecks()  { return accountChecks; }
}