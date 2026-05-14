package com.progressoft.dto;

import com.progressoft.model.ImportBatch;
import java.time.LocalDateTime;
import java.util.UUID;

public class BatchSummaryResponse {

    private UUID          id;
    private LocalDateTime importedAt;
    private String        fileName;
    private int           totalRows;
    private int           importedRows;
    private int           duplicateRows;
    private int           invalidRows;

    public static BatchSummaryResponse from(ImportBatch b) {
        BatchSummaryResponse r = new BatchSummaryResponse();
        r.id           = b.getId();
        r.importedAt   = b.getImportedAt();
        r.fileName     = b.getFileName();
        r.totalRows    = b.getTotalRows();
        r.importedRows = b.getImportedRows();
        r.duplicateRows = b.getDuplicateRows();
        r.invalidRows  = b.getInvalidRows();
        return r;
    }

    public UUID          getId()            { return id; }
    public LocalDateTime getImportedAt()   { return importedAt; }
    public String        getFileName()     { return fileName; }
    public int           getTotalRows()    { return totalRows; }
    public int           getImportedRows() { return importedRows; }
    public int           getDuplicateRows(){ return duplicateRows; }
    public int           getInvalidRows()  { return invalidRows; }
}