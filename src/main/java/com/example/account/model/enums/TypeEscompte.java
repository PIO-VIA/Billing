package com.example.account.model.enums;

import lombok.Getter;

@Getter
public enum TypeEscompte {
    /**
     * Escompte basé sur un pourcentage du montant
     */
    POURCENTAGE("Escompte en pourcentage"),
    
    /**
     * Escompte d'un montant fixe
     */
    MONTANT_FIXE("Escompte montant fixe"),
    
    /**
     * Escompte dégressif selon le nombre de jours d'avance
     */
    DEGRESSIF("Escompte dégressif"),
    
    /**
     * Escompte pour paiement anticipé
     */
    PAIEMENT_ANTICIPE("Escompte paiement anticipé"),
    
    /**
     * Escompte de volume
     */
    VOLUME("Escompte de volume"),
    
    /**
     * Escompte fidélité
     */
    FIDELITE("Escompte fidélité"),
    
    /**
     * Escompte commercial
     */
    COMMERCIAL("Escompte commercial"),
    
    /**
     * Escompte saisonnier
     */
    SAISONNIER("Escompte saisonnier"),
    
    /**
     * Escompte promotionnel
     */
    PROMOTION("Escompte promotionnel"),
    
    /**
     * Escompte de liquidation
     */
    LIQUIDATION("Escompte de liquidation"),
    
    /**
     * Escompte nouveau client
     */
    NOUVEAU_CLIENT("Escompte nouveau client"),
    
    /**
     * Escompte personnalisé
     */
    PERSONNALISE("Escompte personnalisé");

    private final String libelle;

    TypeEscompte(String libelle) {
        this.libelle = libelle;
    }
}