package com.example.account.modules.facturation.model.enums;

import lombok.Getter;

@Getter
public enum ModeReglement {
    VIREMENT("Virement"),
    CARTE_BANCAIRE("Carte bancaire"),
    ESPECES("Espèces"),
    CHEQUE("Chèque"),
    PRELEVEMENT("Prélèvement"),
    PAYPAL("Paypal"),
    AUTRE("Autre");

    private final String libelle;

    ModeReglement(String libelle) {
        this.libelle = libelle;
    }
}
