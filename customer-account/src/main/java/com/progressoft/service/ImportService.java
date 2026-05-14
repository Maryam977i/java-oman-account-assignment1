package com.progressoft.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.progressoft.model.*;
import com.progressoft.repository.AccountCheckRepository;
import com.progressoft.repository.ImportBatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);

    @Autowired private AccountCheckRepository  accountCheckRepository;
    @Autowired private ImportBatchRepository   importBatchRepository;
    @Autowired private ValidationService       validationService;
    @Autowired private OwnershipService        ownershipService;

    @Transactional
    public ImportResult importCsv(MultipartFile file) {
        List<String[]> rows = parseCsv(file);


        ImportBatch batch = new ImportBatch(
                LocalDateTime.now(), file.getOriginalFilename(),
                rows.size(), 0, 0, 0);
        importBatchRepository.save(batch);

        ImportResult result = new ImportResult();
        result.batchId   = batch.getId();
        result.totalRows = rows.size();

        Set<String> seenInFile = new LinkedHashSet<>();

        for (String[] line : rows) {
            RowResult rowResult = processRow(line, batch, seenInFile);
            result.rows.add(rowResult);

            switch (rowResult.status) {
                case "IMPORTED"  -> result.importedRows++;
                case "DUPLICATE" -> result.duplicateRows++;
                case "INVALID"   -> result.invalidRows++;
            }

            if (line.length > 0 && line[0] != null) {
                seenInFile.add(line[0].trim());
            }
        }

        batch.setImportedRows(result.importedRows);
        batch.setDuplicateRows(result.duplicateRows);
        batch.setInvalidRows(result.invalidRows);
        importBatchRepository.save(batch);

        return result;
    }

    private RowResult processRow(String[] line, ImportBatch batch, Set<String> seenInFile) {
        try {
            if (line.length < 6) return RowResult.invalid(null, "Row has fewer than 6 columns");

            String requestId         = line[0] != null ? line[0].trim() : "";
            String customerReference = line[1] != null ? line[1].trim() : "";
            String identifier1       = line[2];
            String identifier2       = line.length > 3 ? line[3] : null;
            String identifier3       = line.length > 4 ? line[4] : null;
            String submittedAtStr    = line[5] != null ? line[5].trim() : "";

            if (requestId.isEmpty()) return RowResult.invalid(null, "request_id is empty");

            if (seenInFile.contains(requestId))
                return RowResult.duplicate(requestId, "Duplicate request_id within the same file");

            if (accountCheckRepository.existsByRequestId(requestId))
                return RowResult.duplicate(requestId, "Already imported in a previous batch");


            LocalDateTime submittedAt;
            try {
                submittedAt = OffsetDateTime.parse(submittedAtStr).toLocalDateTime();
            } catch (Exception e) {
                return RowResult.invalid(requestId, "Invalid submitted_at: " + submittedAtStr);
            }

            List<ValidationService.ValidationResult> allResults   = new ArrayList<>();
            List<Identifier>                         identEntities = new ArrayList<>();

            processIdentifier(identifier1, 1, allResults, identEntities);
            processIdentifier(identifier2, 2, allResults, identEntities);
            processIdentifier(identifier3, 3, allResults, identEntities);

            List<ValidationService.ValidationResult> validResults = allResults.stream()
                    .filter(ValidationService.ValidationResult::isValid).toList();

            OwnershipResult ownership = ownershipService.determineOwnership(validResults);
            String          cif       = ownershipService.determineExtractedCif(validResults);

            AccountCheck check = new AccountCheck();
            check.setBatch(batch);
            check.setRequestId(requestId);
            check.setCustomerReference(customerReference);
            check.setSubmittedAt(submittedAt);
            check.setOwnershipResult(ownership);
            check.setExtractedCif(cif);
            check.setStatus(RowStatus.IMPORTED);
            check.setIdentifiers(identEntities);
            identEntities.forEach(id -> id.setAccountCheck(check));
            accountCheckRepository.save(check);

            return RowResult.imported(requestId, ownership);

        } catch (Exception e) {
            String reqId = (line.length > 0 && line[0] != null) ? line[0].trim() : null;
            log.error("Error processing row {}: {}", reqId, e.getMessage(), e);
            return RowResult.invalid(reqId, "Processing error: " + e.getMessage());
        }
    }

    private void processIdentifier(String value, int position,
                                   List<ValidationService.ValidationResult> results,
                                   List<Identifier> entities) {
        if (value == null || value.isBlank()) return;

        ValidationService.ValidationResult vr = validationService.validateIdentifier(value, position);
        results.add(vr);

        Identifier id = new Identifier();
        id.setPosition(position);
        id.setRawValue(value.strip());
        id.setIdentifierType(IdentifierType.valueOf(vr.getType()));
        id.setIsValid(vr.isValid());
        id.setValidationError(vr.getError());
        id.setExtractedCif(vr.getExtractedCif());
        entities.add(id);
    }

    private List<String[]> parseCsv(MultipartFile file) {
        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                .withSkipLines(1).build()) {

            List<String[]> rows = new ArrayList<>();
            String[] line;
            while ((line = reader.readNext()) != null) {
                boolean empty = true;
                for (String cell : line) {
                    if (cell != null && !cell.isBlank()) { empty = false; break; }
                }
                if (!empty) rows.add(line);
            }
            return rows;
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse CSV: " + e.getMessage());
        }
    }

    public static class ImportResult {
        public UUID            batchId;
        public int             totalRows;
        public int             importedRows;
        public int             duplicateRows;
        public int             invalidRows;
        public List<RowResult> rows = new ArrayList<>();

        public UUID            getBatchId()       { return batchId; }
        public int             getTotalRows()     { return totalRows; }
        public int             getImportedRows()  { return importedRows; }
        public int             getDuplicateRows() { return duplicateRows; }
        public int             getInvalidRows()   { return invalidRows; }
        public List<RowResult> getRows()          { return rows; }
    }

    public static class RowResult {
        public String        requestId;
        public String        status;
        public OwnershipResult ownershipResult;
        public String        reason;

        public RowResult(String requestId, String status, OwnershipResult ownershipResult, String reason) {
            this.requestId       = requestId;
            this.status          = status;
            this.ownershipResult = ownershipResult;
            this.reason          = reason;
        }

        public static RowResult imported(String id, OwnershipResult or) { return new RowResult(id, "IMPORTED",  or,   null);   }
        public static RowResult duplicate(String id, String r)          { return new RowResult(id, "DUPLICATE", null, r);      }
        public static RowResult invalid(String id, String r)            { return new RowResult(id, "INVALID",   null, r);      }

        public String          getRequestId()        { return requestId; }
        public String          getStatus()           { return status; }
        public OwnershipResult getOwnershipResult()  { return ownershipResult; }
        public String          getReason()           { return reason; }
    }
}