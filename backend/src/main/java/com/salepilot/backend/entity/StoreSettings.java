package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Store settings entity containing configuration for each store/tenant.
 * This includes tax rates, currency, payment methods, and other business
 * settings.
 */
@Entity
@Table(name = "store_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreSettings {

    @Id
    @Column(name = "store_id")
    private String storeId; // One-to-one with Store

    // Store Information
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "website")
    private String website;

    // Financial Settings
    @Column(name = "tax_rate", nullable = false)
    private Double taxRate; // Percentage, e.g., 10.0 for 10%

    @Column(name = "currency", columnDefinition = "jsonb", nullable = false)
    private String currency; // JSON: {symbol: "$", code: "USD", position: "before"}

    @Column(name = "receipt_message")
    private String receiptMessage;

    // Inventory Settings
    @Column(name = "low_stock_threshold", nullable = false)
    @Builder.Default
    private Integer lowStockThreshold = 10;

    @Column(name = "sku_prefix")
    private String skuPrefix; // e.g., "SP-"

    // POS Settings
    @Column(name = "enable_store_credit", nullable = false)
    @Builder.Default
    private Boolean enableStoreCredit = true;

    @Column(name = "payment_methods", columnDefinition = "jsonb")
    private String paymentMethods; // JSON array of payment method objects

    @Column(name = "supplier_payment_methods", columnDefinition = "jsonb")
    private String supplierPaymentMethods; // JSON array

    @Column(name = "is_online_store_enabled", nullable = false)
    @Builder.Default
    private Boolean isOnlineStoreEnabled = true;

    // Accounting Account Mappings
    @Column(name = "tax_account_id")
    private String taxAccountId;

    @Column(name = "revenue_account_id")
    private String revenueAccountId;

    @Column(name = "cogs_account_id")
    private String cogsAccountId;

    @Column(name = "inventory_account_id")
    private String inventoryAccountId;

    @Column(name = "cash_account_id")
    private String cashAccountId;

    @Column(name = "ar_account_id")
    private String arAccountId; // Accounts Receivable

    @Column(name = "ap_account_id")
    private String apAccountId; // Accounts Payable
}
