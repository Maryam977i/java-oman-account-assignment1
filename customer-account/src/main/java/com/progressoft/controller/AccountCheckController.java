package com.progressoft.controller;

import com.progressoft.dto.AccountCheckResponse;
import com.progressoft.dto.BatchSummaryResponse;
import com.progressoft.repository.AccountCheckRepository;
import com.progressoft.repository.ImportBatchRepository;
import com.progressoft.service.ImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AccountCheckController {

    private static final Logger log = LoggerFactory.getLogger(AccountCheckController.class);

    @Autowired private ImportService          importService;
    @Autowired private AccountCheckRepository accountCheckRepository;
    @Autowired private ImportBatchRepository  importBatchRepository;

    @PostMapping("/account-checks/import")
    public ResponseEntity<?> importCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("Uploaded file is empty");
        try {
            return ResponseEntity.ok(importService.importCsv(file));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Import error", e);
            return ResponseEntity.internalServerError().body("Import failed: " + e.getMessage());
        }
    }

    @GetMapping("/account-checks/{requestId}")
    public ResponseEntity<?> getByRequestId(@PathVariable String requestId) {
        return accountCheckRepository.findByRequestId(requestId)
                .map(ac -> ResponseEntity.ok(AccountCheckResponse.from(ac)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/account-checks")
    public ResponseEntity<List<AccountCheckResponse>> getByCustomerReference(
            @RequestParam String customerReference) {
        return ResponseEntity.ok(
                accountCheckRepository.findByCustomerReference(customerReference)
                        .stream().map(AccountCheckResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/account-checks/failures")
    public ResponseEntity<List<AccountCheckResponse>> getFailures() {
        return ResponseEntity.ok(
                accountCheckRepository.findAllWithInvalidIdentifiers()
                        .stream().map(AccountCheckResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/import-batches")
    public ResponseEntity<List<BatchSummaryResponse>> getAllBatches() {
        return ResponseEntity.ok(
                importBatchRepository.findAll()
                        .stream().map(BatchSummaryResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/import-batches/{batchId}")
    public ResponseEntity<?> getBatchSummary(@PathVariable UUID batchId) {
        return importBatchRepository.findById(batchId)
                .map(b -> ResponseEntity.ok(BatchSummaryResponse.from(b)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}