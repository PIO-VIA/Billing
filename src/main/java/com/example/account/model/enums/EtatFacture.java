package com.example.account.model.enums;

public enum EtatFacture {
    ACTIF("Actif"),
    INACTIF("Inactif"),
    ARCHIVE("Archivé"),
    SUSPENDU("Suspendu");

    private final String libelle;

    EtatFacture(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}