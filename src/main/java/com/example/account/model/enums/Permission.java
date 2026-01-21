package com.example.account.model.enums;

/**
 * Fine-grained permissions for users within an organization.
 * These permissions control specific actions users can perform.
 * Permissions are assigned to users within the context of an organization.
 */
public enum Permission {
    // Pricing permissions
    NEGOTIATE_PRICE("Negotiate Price", "Allow negotiating prices with customers"),
    APPLY_DISCOUNT("Apply Discount", "Allow applying discounts to invoices and quotes"),
    OVERRIDE_PRICE("Override Price", "Allow overriding product prices"),
    
    // Invoice permissions
    CREATE_INVOICE("Create Invoice", "Allow creating new invoices"),
    EDIT_INVOICE("Edit Invoice", "Allow editing existing invoices"),
    DELETE_INVOICE("Delete Invoice", "Allow deleting invoices"),
    VALIDATE_INVOICE("Validate Invoice", "Allow validating and finalizing invoices"),
    SEND_INVOICE("Send Invoice", "Allow sending invoices to customers"),
    
    // Quote permissions
    CREATE_QUOTE("Create Quote", "Allow creating new quotes"),
    EDIT_QUOTE("Edit Quote", "Allow editing existing quotes"),
    DELETE_QUOTE("Delete Quote", "Allow deleting quotes"),
    CONVERT_QUOTE("Convert Quote", "Allow converting quotes to invoices"),
    
    // Purchase order permissions
    CREATE_PURCHASE_ORDER("Create Purchase Order", "Allow creating purchase orders"),
    EDIT_PURCHASE_ORDER("Edit Purchase Order", "Allow editing purchase orders"),
    VALIDATE_PURCHASE_ORDER("Validate Purchase Order", "Allow validating purchase orders"),
    MANAGE_PURCHASE_ORDERS("Manage Purchase Orders", "Full access to purchase orders"),
    
    // Delivery permissions
    CREATE_DELIVERY("Create Delivery", "Allow creating delivery notes"),
    EDIT_DELIVERY("Edit Delivery", "Allow editing delivery notes"),
    VALIDATE_DELIVERY("Validate Delivery", "Allow validating deliveries"),
    MANAGE_DELIVERIES("Manage Deliveries", "Full access to delivery management"),
    
    // Payment permissions
    RECORD_PAYMENT("Record Payment", "Allow recording customer payments"),
    VALIDATE_PAYMENT("Validate Payment", "Allow validating payments"),
    ISSUE_REFUND("Issue Refund", "Allow issuing refunds to customers"),
    
    // Analytics permissions
    VIEW_ANALYTICS("View Analytics", "Allow viewing business analytics and reports"),
    VIEW_FINANCIAL_REPORTS("View Financial Reports", "Allow viewing financial reports"),
    
    // Customer/Supplier management
    MANAGE_CUSTOMERS("Manage Customers", "Full access to customer management"),
    MANAGE_SUPPLIERS("Manage Suppliers", "Full access to supplier management"),
    
    // Product management
    MANAGE_PRODUCTS("Manage Products", "Full access to product management"),
    MANAGE_STOCK("Manage Stock", "Full access to stock management");

    private final String displayName;
    private final String description;

    Permission(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
