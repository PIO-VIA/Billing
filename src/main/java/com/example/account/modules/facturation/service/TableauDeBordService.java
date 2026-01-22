package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.response.TableauDeBordResponse;
import com.example.account.modules.facturation.model.enums.StatutFacture;
import com.example.account.modules.facturation.repository.DevisRepository;
import com.example.account.modules.facturation.repository.FactureRepository;
import com.example.account.modules.tiers.repository.ClientRepository;
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
    private final DevisRepository devisRepository;
    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public TableauDeBordResponse getTableauDeBord() {
        log.info("Calcul des indicateurs du tableau de bord");

        LocalDate now = LocalDate.now();
        LocalDate debutMois = now.withDayOfMonth(1);
        LocalDate debutAnnee = now.withDayOfYear(1);

        // Chiffre d'affaires
        BigDecimal caMois = factureRepository.sumMontantByDateBetween(debutMois, now);
        BigDecimal caAnnee = factureRepository.sumMontantByDateBetween(debutAnnee, now);

        // Factures
        Long nbFacturesEmises = factureRepository.count();
        Long nbFacturesPayees = factureRepository.countByEtat(StatutFacture.PAYE);
        Long nbFacturesEnAttente = factureRepository.countByEtat(StatutFacture.EN_ATTENTE);
        
        // Clients
        Long nbClients = clientRepository.count();

        return TableauDeBordResponse.builder()
                .chiffreAffairesMois(caMois != null ? caMois : BigDecimal.ZERO)
                .chiffreAffairesAnnee(caAnnee != null ? caAnnee : BigDecimal.ZERO)
                .nombreFacturesEmises(nbFacturesEmises)
                .nombreFacturesPayees(nbFacturesPayees)
                .nombreFacturesEnAttente(nbFacturesEnAttente)
                .nombreClients(nbClients)
                .topProduits(new ArrayList<>())
                .topClients(new ArrayList<>())
                .evolutionCA12Mois(new ArrayList<>())
                .dateGeneration(now)
                .build();
    }
}
