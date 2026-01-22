package com.example.account.modules.facturation.model.enums;

/**
 * Sale size categories for sellers.
 * Defines the permitted sale volumes a seller can handle.
 * Matches the frontend TypeScript enum exactly.
 */
public enum SaleSize {
    /**
     * Retail / Detail sales - individual customer sales
     */
    DETAIL("Détail", "Vente au détail"),
    
    /**
     * Semi-wholesale - medium volume sales
     */
    DEMIS_GROS("Demi-Gros", "Vente demi-gros"),
    
    /**
     * Wholesale - large volume sales
     */
    GROS("Gros", "Vente en gros"),
    
    /**
     * Super wholesale - very large volume sales
     */
    SUPER_GROS("Super Gros", "Vente super gros");

    private final String displayName;
    private final String description;

    SaleSize(String displayName, String description) {
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
