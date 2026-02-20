package com.salepilot.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for Onboarding Wizard.
 * Uses Settings to track progress.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingService {

    private final SettingService settingService;
    private static final String STEP_KEY = "store.onboarding.step";

    /**
     * Get current onboarding step
     */
    public int getCurrentStep() {
        String val = settingService.getValue(STEP_KEY, "0");
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Update/Complete step
     */
    public void setStep(int step) {
        // Validation: Cannot skip steps widely?
        // For flexibility, allow setting any step

        // Construct a generic setting DTO equivalent (internal call)
        // Since SettingService expects DTO for update, we might need a direct call
        // or expose a simplified internal method in SettingService.
        // For now, assume updateSetting relies on API logic, so I'll create the DTO

        com.salepilot.backend.dto.SettingDTO req = com.salepilot.backend.dto.SettingDTO.builder()
                .key(STEP_KEY)
                .value(String.valueOf(step))
                .type(com.salepilot.backend.entity.Setting.SettingType.NUMBER)
                .group("SYSTEM")
                .description("Current Onboarding Wizard Step")
                .build();

        settingService.updateSetting(req);
    }
}
