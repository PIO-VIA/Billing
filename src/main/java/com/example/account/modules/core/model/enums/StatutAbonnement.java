package com.example.account.modules.core.model.enums;

import lombok.Getter;

@Getter
public enum StatutAbonnement {
    ACTIF("Actif"),
    SUSPENDU("Suspendu"),
    EXPIRE("Expiré"),
    ANNULE("Annulé"),
    EN_ATTENTE("En attente"),
    BROUILLON("Brouillon");

    private final String libelle;

    StatutAbonnement(String libelle) {
        this.libelle = libelle;
    }
}