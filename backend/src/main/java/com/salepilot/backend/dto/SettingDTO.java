package com.salepilot.backend.dto;

import com.salepilot.backend.entity.Setting;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Setting management
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingDTO {

    @NotBlank(message = "Key is required")
    private String key;

    private String value;
    private String description;
    private String group;
    private Setting.SettingType type;
}
