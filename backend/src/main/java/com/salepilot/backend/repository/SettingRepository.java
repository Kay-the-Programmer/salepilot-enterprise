package com.salepilot.backend.repository;

import com.salepilot.backend.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Setting entity.
 */
@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {

    /**
     * Find setting by key for a store
     */
    Optional<Setting> findByStoreIdAndKey(String storeId, String key);

    /**
     * Find all settings for a store
     */
    List<Setting> findByStoreId(String storeId);

    /**
     * Find settings by group
     */
    List<Setting> findByStoreIdAndGroup(String storeId, String group);
}
