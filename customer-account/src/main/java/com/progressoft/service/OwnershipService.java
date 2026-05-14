package com.progressoft.service;

import com.progressoft.model.OwnershipResult;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OwnershipService {

    public OwnershipResult determineOwnership(List<ValidationService.ValidationResult> validIdentifiers) {
        if (validIdentifiers.isEmpty()) return OwnershipResult.NO_VALID_IDENTIFIERS;

        Set<String> uniqueCifs = validIdentifiers.stream()
                .map(ValidationService.ValidationResult::getExtractedCif)
                .collect(Collectors.toSet());

        return uniqueCifs.size() == 1 ? OwnershipResult.SAME_PERSON : OwnershipResult.MISMATCH;
    }

    public String determineExtractedCif(List<ValidationService.ValidationResult> validIdentifiers) {
        if (validIdentifiers.isEmpty()) return null;

        Set<String> uniqueCifs = validIdentifiers.stream()
                .map(ValidationService.ValidationResult::getExtractedCif)
                .collect(Collectors.toSet());

        return uniqueCifs.size() == 1 ? uniqueCifs.iterator().next() : null;
    }
}