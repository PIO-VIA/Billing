package com.example.account.modules.facturation.model.enums;

import lombok.Getter;

@Getter
public enum StatusBonReception {
    DRAFT("Brouillon"),
    PARTIALLY_RECEIVED("Reçu partiellement"),
    RECEIVED("Reçu"),
    REJECTED("Rejeté"),
    ANNULE("Annulé");

    private final String libelle;

    StatusBonReception(String libelle) {
        this.libelle = libelle;
    }
}