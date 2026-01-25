package com.example.account.modules.facturation.model.enums;

import lombok.Getter;

@Getter
public enum StatutProforma {
    BROUILLON("Brouillon"),
    ENVOYE("Envoyé"),
    ACCEPTE("Accepté"),
    REFUSE("Refusé"),
    EXPIRE("Expiré"),
    ANNULE("Annulé"),
    CONVERTI_EN_FACTURE("Converti en facture");

    private final String libelle;

    StatutProforma(String libelle) {
        this.libelle = libelle;
    }
}
