package com.example.account.modules.facturation.model.enums;

/**
 * Enumération représentant les différents types de documents dans le système ERP
 */
public enum TypeDocument {
    
    /**
     * Facture de vente
     */
    FACTURE("Facture"),
    
    /**
     * Facture d'achat
     */
    FACTURE_ACHAT("Facture d'achat"),
    
    /**
     * Devis/Estimation
     */
    DEVIS("Devis"),
    
    /**
     * Avoir/Note de crédit
     */
    AVOIR("Avoir"),
    
    /**
     * Commande de vente
     */
    COMMANDE("Commande"),
    
    /**
     * Commande d'achat
     */
    COMMANDE_ACHAT("Commande d'achat"),
    
    /**
     * Bon de commande
     */
    BON_COMMANDE("Bon de commande"),
    
    /**
     * Bon de livraison
     */
    BON_LIVRAISON("Bon de livraison"),
    
    /**
     * Bon de réception
     */
    BON_RECEPTION("Bon de réception"),
    
    /**
     * Reçu de paiement
     */
    RECU_PAIEMENT("Reçu de paiement"),
    
    /**
     * Contrat
     */
    CONTRAT("Contrat"),
    
    /**
     * Accord cadre
     */
    ACCORD_CADRE("Accord cadre"),
    
    /**
     * Remboursement
     */
    REMBOURSEMENT("Remboursement"),
    
    /**
     * Bordereau de remise
     */
    BORDEREAU_REMISE("Bordereau de remise"),
    
    /**
     * Relevé de compte
     */
    RELEVE_COMPTE("Relevé de compte"),
    
    /**
     * Rappel de paiement
     */
    RAPPEL_PAIEMENT("Rappel de paiement"),
    
    /**
     * Mise en demeure
     */
    MISE_EN_DEMEURE("Mise en demeure"),
    
    /**
     * Attestation
     */
    ATTESTATION("Attestation"),
    
    /**
     * Certificat
     */
    CERTIFICAT("Certificat");

    private final String libelle;

    TypeDocument(String libelle) {
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