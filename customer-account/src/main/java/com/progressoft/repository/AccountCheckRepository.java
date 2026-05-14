package com.progressoft.repository;

import com.progressoft.model.AccountCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountCheckRepository extends JpaRepository<AccountCheck, Long> {

    Optional<AccountCheck> findByRequestId(String requestId);

    boolean existsByRequestId(String requestId);

    List<AccountCheck> findByCustomerReference(String customerReference);


    List<AccountCheck> findByBatch_Id(UUID batchId);

    @Query("SELECT DISTINCT ac FROM AccountCheck ac JOIN ac.identifiers i WHERE i.isValid = false")
    List<AccountCheck> findAllWithInvalidIdentifiers();
}