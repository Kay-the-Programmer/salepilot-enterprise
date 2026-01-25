package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Supplier entity for managing product suppliers and vendor information.
 */
@Entity
@Table(name = "suppliers", indexes = {
        @Index(name = "idx_suppliers_store_id", columnList = "store_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends TenantAware {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "address", columnDefinition = "text")
    private String address;

    @Column(name = "payment_terms")
    private String paymentTerms; // e.g., "Net 30", "COD"

    @Column(name = "banking_details", columnDefinition = "text")
    private String bankingDetails;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;
}
