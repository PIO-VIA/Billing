package com.example.account.modules.facturation.model.enums;

import lombok.Getter;

@Getter
public enum StatusBonCommande {
    BROUILLON("Brouillon"),
    VALIDE("Validé"),           // Confirmé par le client
    EN_COURS("En cours"),       // En cours de préparation/traitement
    EXPEDIE("Expédié"),         // A quitté l'entrepôt
    LIVRE("Livré"),             // Livraison finale confirmée
    RECU_PARTIEL("Reçu partiellement"),
    RECU("Reçu"),
    REJETE("Rejeté"),
    ANNULE("Annulé");

    private final String libelle;

    // Le nom du constructeur doit être identique au nom de l'Enum
    StatusBonCommande(String libelle) {
        this.libelle = libelle;
    }
}