package com.example.account.modules.facturation.model.enums;

import lombok.Getter;

@Getter
public enum StatutNoteCredit {
    BROUILLON("Brouillon"),
    ENVOYE("Envoyé"),
    PAYE("Payé"),
    PARTIELLEMENT_PAYE("Partiellement payé"),
    EN_RETARD("En retard"),
    ANNULE("Annulé");

    private final String libelle;

    StatutNoteCredit(String libelle) {
        this.libelle = libelle;
    }
}
