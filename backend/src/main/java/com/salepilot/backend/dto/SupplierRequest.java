package com.salepilot.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating/updating suppliers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequest {

    @NotBlank(message = "Supplier name is required")
    private String name;

    private String contactPerson;

    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private String address;

    private String paymentTerms; // e.g., "Net 30", "COD"

    private String bankingDetails;

    private String notes;
}
