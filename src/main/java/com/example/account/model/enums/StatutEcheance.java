package com.example.account.model.enums;

/**
 * Enumération représentant les différents statuts d'une échéance de paiement
 */
public enum StatutEcheance {
    
    /**
     * Échéance créée mais pas encore due
     */
    EN_ATTENTE("En attente"),
    
    /**
     * Échéance due mais non payée
     */
    ECHUE("Échue"),
    
    /**
     * Échéance partiellement payée
     */
    PARTIELLEMENT_PAYEE("Partiellement payée"),
    
    /**
     * Échéance entièrement payée
     */
    PAYEE("Payée"),
    
    /**
     * Échéance en retard de paiement
     */
    EN_RETARD("En retard"),
    
    /**
     * Échéance annulée
     */
    ANNULEE("Annulée"),
    
    /**
     * Échéance reportée
     */
    REPORTEE("Reportée"),
    
    /**
     * Échéance en litige
     */
    EN_LITIGE("En litige"),
    
    /**
     * Échéance gelée (temporairement suspendue)
     */
    GELEE("Gelée"),
    
    /**
     * Échéance avec escompte appliqué
     */
    AVEC_ESCOMPTE("Avec escompte");

    private final String libelle;

    StatutEcheance(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}