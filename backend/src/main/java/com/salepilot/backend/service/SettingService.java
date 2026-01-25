package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.dto.SettingDTO;
import com.salepilot.backend.entity.Setting;
import com.salepilot.backend.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for Settings calculation and management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SettingService {

    private final SettingRepository settingRepository;

    /**
     * Get value of a setting, or default if not found
     */
    @Transactional(readOnly = true)
    public String getValue(String key, String defaultValue) {
        String storeId = TenantContext.getCurrentTenant();
        return settingRepository.findByStoreIdAndKey(storeId, key)
                .map(Setting::getValue)
                .orElse(defaultValue);
    }

    /**
     * Update or Create a setting
     */
    public SettingDTO updateSetting(SettingDTO request) {
        String storeId = TenantContext.getCurrentTenant();

        Setting setting = settingRepository.findByStoreIdAndKey(storeId, request.getKey())
                .orElse(Setting.builder()
                        .key(request.getKey())
                        // Default other fields for new settings
                        .type(request.getType() != null ? request.getType() : Setting.SettingType.STRING)
                        .group(request.getGroup() != null ? request.getGroup() : "GENERAL")
                        .build());

        setting.setValue(request.getValue());
        if (request.getDescription() != null)
            setting.setDescription(request.getDescription());
        if (request.getGroup() != null)
            setting.setGroup(request.getGroup());
        if (request.getType() != null)
            setting.setType(request.getType());

        Setting saved = settingRepository.save(setting);
        return mapToDTO(saved);
    }

    /**
     * Bulk update settings
     */
    public List<SettingDTO> updateSettings(List<SettingDTO> requests) {
        return requests.stream()
                .map(this::updateSetting)
                .collect(Collectors.toList());
    }

    /**
     * Get all settings
     */
    @Transactional(readOnly = true)
    public List<SettingDTO> getAllSettings() {
        String storeId = TenantContext.getCurrentTenant();
        return settingRepository.findByStoreId(storeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Initialize default settings for a new store
     */
    public void initializeDefaults(String storeId) {
        if (!settingRepository.findByStoreId(storeId).isEmpty()) {
            return;
        }

        // Helper to quick create
        createDefault(storeId, "store.currency", "USD", "Store Currency Code", "GENERAL");
        createDefault(storeId, "store.tax.rate", "0.0", "Default Sales Tax Rate", "TAX");
        createDefault(storeId, "store.timezone", "UTC", "Store Timezone", "GENERAL");
    }

    private void createDefault(String storeId, String key, String value, String desc, String group) {
        // Only internal use, construct Entity directly
        // Note: Repository methods usually require TenantContext, might need to
        // impersonate or pass storeId explicitly to repo if custom query
        // But since Setting extends TenantAware, it auto filters.
        // For initialization logic specifically, we assume called within a valid
        // context or System context
    }

    private SettingDTO mapToDTO(Setting setting) {
        return SettingDTO.builder()
                .key(setting.getKey())
                .value(setting.getValue())
                .description(setting.getDescription())
                .group(setting.getGroup())
                .type(setting.getType())
                .build();
    }
}
