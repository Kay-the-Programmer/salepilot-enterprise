package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity for storing diverse system settings and configurations per store.
 * Uses a Key-Value pair approach for maximum flexibility.
 */
@Entity
@Table(name = "settings", indexes = {
        @Index(name = "idx_settings_store_id", columnList = "store_id"),
        @Index(name = "uidx_settings_store_key", columnList = "store_id, setting_key", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setting extends TenantAware {

    @Column(name = "setting_key", nullable = false)
    private String key;

    @Column(name = "setting_value", columnDefinition = "text")
    private String value;

    @Column(name = "description")
    private String description; // Optional help text for UI

    @Column(name = "group_name")
    private String group; // e.g., "GENERAL", "TAX", "NOTIFICATION"

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @Builder.Default
    private SettingType type = SettingType.STRING;

    /**
     * Type of data stored in value
     */
    public enum SettingType {
        STRING,
        NUMBER,
        BOOLEAN,
        JSON
    }
}
