package com.example.account.service;

import com.example.account.dto.response.TableauDeBordResponse;
import com.example.account.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class TableauDeBordService {

    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final StockRepository stockRepository;
    private final BonCommandeRepository bonCommandeRepository;
    private final BonAchatRepository bonAchatRepository;
    private final PlanTresorerieRepository planTresorerieRepository;

    @Transactional(readOnly = true)
    public TableauDeBordResponse getTableauDeBord() {
        log.info("Génération du tableau de bord");

        LocalDate aujourdhui = LocalDate.now();
        LocalDate debutMois = aujourdhui.withDayOfMonth(1);
        LocalDate debutAnnee = aujourdhui.withDayOfYear(1);

        return TableauDeBordResponse.builder()
                // Indicateurs financiers
                .chiffreAffairesMois(calculerCA(debutMois, aujourdhui))
                .chiffreAffairesAnnee(calculerCA(debutAnnee, aujourdhui))
                .evolutionCA(BigDecimal.ZERO)

                // Factures
                .nombreFacturesEmises(factureRepository.count())
                .nombreFacturesPayees(factureRepository.countByStatut("PAYEE"))
                .nombreFacturesEnAttente(factureRepository.countByStatut("EN_ATTENTE"))
                .montantFacturesEnAttente(getOrZero(factureRepository.sumMontantByStatut("EN_ATTENTE")))
                .montantFacturesEnRetard(BigDecimal.ZERO)

                // Clients
                .nombreClients(clientRepository.count())
                .nombreNouveauxClients(0L)
                .montantMoyenParClient(BigDecimal.ZERO)

                // Produits
                .nombreProduits(produitRepository.count())
                .nombreProduitsActifs(produitRepository.countByActive(true))
                .nombreProduitsAlerteStock((long) stockRepository.findStocksSousMinimum().size())

                // Stock
                .valeurTotaleStock(getOrZero(stockRepository.calculerValeurTotaleStock()))
                .nombreMouvementsStock(0L)

                // Achats
                .montantAchatsMois(BigDecimal.ZERO)
                .nombreBonsCommande(bonCommandeRepository.count())
                .nombreBonsAchat(bonAchatRepository.count())

                // Trésorerie
                .soldeTresorerie(BigDecimal.ZERO)
                .encaissementsPrevus(BigDecimal.ZERO)
                .decaissementsPrevus(BigDecimal.ZERO)

                // Lists
                .topProduits(new ArrayList<>())
                .topClients(new ArrayList<>())
                .evolutionCA12Mois(new ArrayList<>())

                .dateGeneration(aujourdhui)
                .build();
    }

    private BigDecimal calculerCA(LocalDate debut, LocalDate fin) {
        return getOrZero(factureRepository.sumMontantByDateBetween(debut, fin));
    }

    private BigDecimal getOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
