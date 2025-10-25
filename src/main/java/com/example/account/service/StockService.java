package com.example.account.service;

import com.example.account.dto.response.StockResponse;
import com.example.account.mapper.StockMapper;
import com.example.account.model.entity.Stock;
import com.example.account.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public StockResponse getStockById(UUID stockId) {
        log.info("Récupération du stock: {}", stockId);
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("Stock non trouvé: " + stockId));
        return mapToResponse(stock);
    }

    @Transactional(readOnly = true)
    public StockResponse getStockByProduit(UUID idProduit) {
        log.info("Récupération du stock pour le produit: {}", idProduit);
        Stock stock = stockRepository.findByIdProduit(idProduit)
                .orElseThrow(() -> new IllegalArgumentException("Stock non trouvé pour le produit: " + idProduit));
        return mapToResponse(stock);
    }

    @Transactional(readOnly = true)
    public List<Stock> getStocksSousMinimum() {
        log.info("Récupération des stocks sous le minimum");
        return stockRepository.findStocksSousMinimum();
    }

    @Transactional(readOnly = true)
    public BigDecimal getValeurTotaleStock() {
        log.info("Calcul de la valeur totale du stock");
        return stockRepository.calculerValeurTotaleStock();
    }

    private StockResponse mapToResponse(Stock stock) {
        return StockResponse.builder()
                .idStock(stock.getIdStock())
                .idProduit(stock.getIdProduit())
                .nomProduit(stock.getNomProduit())
                .referenceProduit(stock.getReferenceProduit())
                .quantite(stock.getQuantite())
                .quantiteReservee(stock.getQuantiteReservee())
                .quantiteDisponible(stock.getQuantiteDisponible())
                .stockMinimum(stock.getStockMinimum())
                .stockMaximum(stock.getStockMaximum())
                .uniteMesure(stock.getUniteMesure())
                .emplacement(stock.getEmplacement())
                .zone(stock.getZone())
                .valeurStock(stock.getValeurStock())
                .coutMoyenUnitaire(stock.getCoutMoyenUnitaire())
                .statut(stock.getStatut())
                .alerteStockBas(stock.getQuantite() != null && stock.getStockMinimum() != null
                    && stock.getQuantite().compareTo(stock.getStockMinimum()) < 0)
                .build();
    }
}
