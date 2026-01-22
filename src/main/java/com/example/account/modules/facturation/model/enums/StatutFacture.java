package com.example.account.modules.facturation.model.enums;

public enum StatutFacture {
    BROUILLON("Brouillon"),
    ENVOYE("Envoyé"),
    PAYE("Payé"),
    PARTIELLEMENT_PAYE("Partiellement payé"),
    EN_RETARD("En retard"),
    ANNULE("Annulé"),
    EN_ATTENTE("en attente");

    private final String libelle;

    StatutFacture(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}