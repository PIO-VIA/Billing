package com.example.account.modules.core.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum defining all granular permissions available in the system.
 * These permissions are used by the RBAC (Role-Based Access Control) system
 * to control access to various modules and actions.
 */
@Getter
@RequiredArgsConstructor
public enum Permission {

    // --- Organization Management ---
    ORGANIZATION_READ("Visualiser les informations de l'organisation"),
    ORGANIZATION_MANAGE("Gérer les paramètres de l'organisation"),
    ORGANIZATION_MANAGE_ROLES("Gérer les rôles et permissions des membres"),

    // --- User Management (within organization) ---
    USER_READ("Visualiser les membres de l'organisation"),
    USER_INVITE("Inviter de nouveaux membres"),
    USER_DEACTIVATE("Désactiver l'accès d'un membre"),

    // --- Facturation (Invoices) ---
    FACTURE_READ("Visualiser les factures"),
    FACTURE_CREATE("Créer de nouvelles factures"),
    FACTURE_EDIT("Modifier les factures existantes"),
    FACTURE_DELETE("Supprimer les factures"),
    FACTURE_VALIDATE("Valider les factures (passage de BROUILLON à VALIDÉ)"),
    FACTURE_SEND("Envoyer les factures par email"),
    FACTURE_PAY("Enregistrer des paiements sur les factures"),

    // --- Devis (Quotes) ---
    DEVIS_READ("Visualiser les devis"),
    DEVIS_CREATE("Créer de nouveaux devis"),
    DEVIS_EDIT("Modifier les devis existantes"),
    DEVIS_DELETE("Supprimer les devis"),
    DEVIS_CONVERT("Convertir un devis en facture"),

    // --- Clients ---
    CLIENT_READ("Visualiser les clients"),
    CLIENT_CREATE("Créer de nouveaux clients"),
    CLIENT_EDIT("Modifier les clients existants"),
    CLIENT_DELETE("Supprimer les clients"),

    // --- Fournisseurs (Suppliers) ---
    FOURNISSEUR_READ("Visualiser les fournisseurs"),
    FOURNISSEUR_CREATE("Créer de nouveaux fournisseurs"),
    FOURNISSEUR_EDIT("Modifier les fournisseurs existants"),
    FOURNISSEUR_DELETE("Supprimer les fournisseurs"),

    // --- Taxes ---
    TAXE_READ("Visualiser les taxes"),
    TAXE_MANAGE("Gérer les configurations de taxes"),

    // --- Journals & Analytics ---
    JOURNAL_READ("Visualiser les journaux de vente"),
    ANALYTICS_READ("Accéder aux tableaux de bord et rapports"),

    // --- Legacy Compatibility (referenced in comments/placeholders) ---
    CREATE_INVOICE("Créer une facture"),
    EDIT_INVOICE("Modifier une facture"),
    DELETE_INVOICE("Supprimer une facture");

    private final String description;
}
