package com.example.account.modules.facturation.model.enums;

import lombok.Getter;

@Getter
public enum StatutNoteCredit {
    BROUILLON ("BROUILLON"),
        APPLIQUÉ  ("APPLIQUÉ"), // Deducted from client's balance
        REMBOURSÉ  ("REMBOURSÉ"), // Money sent back to client
        ANNULÉ ( "ANNULÉ");

    private final String libelle;

    StatutNoteCredit(String libelle) {
        this.libelle = libelle;
    }
}
