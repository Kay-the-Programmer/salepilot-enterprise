package com.salepilot.backend.repository;

import com.salepilot.backend.entity.JournalEntryLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface JournalEntryLineRepository extends JpaRepository<JournalEntryLine, Long> {

    List<JournalEntryLine> findByJournalEntry_Id(Long journalEntryId);

    List<JournalEntryLine> findByStoreIdAndAccount_Id(String storeId, Long accountId);

    @Query("SELECT SUM(CASE WHEN jel.type = 'DEBIT' THEN jel.amount ELSE 0 END) FROM JournalEntryLine jel " +
            "WHERE jel.journalEntry.id = :journalEntryId")
    BigDecimal sumDebits(@Param("journalEntryId") Long journalEntryId);

    @Query("SELECT SUM(CASE WHEN jel.type = 'CREDIT' THEN jel.amount ELSE 0 END) FROM JournalEntryLine jel " +
            "WHERE jel.journalEntry.id = :journalEntryId")
    BigDecimal sumCredits(@Param("journalEntryId") Long journalEntryId);
}
