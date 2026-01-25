package com.salepilot.backend.repository;

import com.salepilot.backend.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Account entity (Chart of Accounts).
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Find all accounts for a store
     */
    List<Account> findByStoreIdOrderByNumber(String storeId);

    /**
     * Find account by number within store
     */
    Optional<Account> findByStoreIdAndNumber(String storeId, String number);

    /**
     * Find account by sub-type within store
     */
    Optional<Account> findByStoreIdAndSubType(String storeId, Account.AccountSubType subType);

    /**
     * Find accounts by type within store
     */
    List<Account> findByStoreIdAndType(String storeId, Account.AccountType type);
}
