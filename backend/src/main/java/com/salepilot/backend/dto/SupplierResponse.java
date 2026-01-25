package com.salepilot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for supplier data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {

    private Long id;

    private String name;

    private String contactPerson;

    private String phone;

    private String email;

    private String address;

    private String paymentTerms;

    private String bankingDetails;

    private String notes;
}
