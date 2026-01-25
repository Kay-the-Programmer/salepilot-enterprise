package com.salepilot.backend.dto;

import com.salepilot.backend.entity.JournalEntry;
import com.salepilot.backend.entity.JournalEntryLine;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * DTO for Journal Entry manual posting and viewing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryDTO {

    private Long id;
    private Instant date;

    @NotEmpty(message = "Description is required")
    private String description;

    private JournalEntry.SourceType sourceType;
    private String sourceId;

    @NotEmpty(message = "Must have at least two lines (Debit/Credit)")
    @Valid
    private List<JournalEntryLineDTO> lines;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JournalEntryLineDTO {
        @NotNull(message = "Account ID is required")
        private Long accountId;

        private String accountName;
        private String accountNumber;

        @NotNull(message = "Line type (DEBIT/CREDIT) is required")
        private JournalEntryLine.LineType type;

        @DecimalMin(value = "0.01", message = "Amount must be positive")
        private BigDecimal amount;
    }
}
