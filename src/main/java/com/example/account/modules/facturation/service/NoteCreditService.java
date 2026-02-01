package com.example.account.modules.facturation.service;

import com.example.account.modules.core.context.OrganizationContext;
import com.example.account.modules.facturation.dto.request.NoteCreditRequest;
import com.example.account.modules.facturation.dto.response.NoteCreditResponse;
import com.example.account.modules.facturation.mapper.NoteCreditMapper;
import com.example.account.modules.facturation.model.entity.LigneNoteCredit;
import com.example.account.modules.facturation.model.entity.NoteCredit;
import com.example.account.modules.facturation.model.enums.StatutNoteCredit;
import com.example.account.modules.facturation.repository.NoteCreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteCreditService {

    private final NoteCreditRepository noteCreditRepository;
    private final NoteCreditMapper noteCreditMapper;

    @Transactional
    public NoteCreditResponse createNoteCredit(NoteCreditRequest request) {
        log.info("Création d'une nouvelle note de crédit");
        
        NoteCredit entity = noteCreditMapper.toEntity(request);
        entity.setOrganizationId(OrganizationContext.getCurrentOrganizationId());
        
        if (entity.getLignesFacture() != null && !entity.getLignesFacture().isEmpty()) {
            calculateMontants(entity);
        }
        
        NoteCredit saved = noteCreditRepository.save(entity);
        return noteCreditMapper.toResponse(saved);
    }

    @Transactional
    public NoteCreditResponse updateNoteCredit(UUID id, NoteCreditRequest request) {
        log.info("Mise à jour de la note de crédit: {}", id);
        
        NoteCredit entity = noteCreditRepository.findByIdNoteCreditAndOrganizationId(id, OrganizationContext.getCurrentOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Note de crédit non trouvée"));
        
        noteCreditMapper.updateEntityFromRequest(request, entity);
        
        if (entity.getLignesFacture() != null && !entity.getLignesFacture().isEmpty()) {
            calculateMontants(entity);
        }
        
        NoteCredit updated = noteCreditRepository.save(entity);
        return noteCreditMapper.toResponse(updated);
    }

    @Transactional(readOnly = true)
    public NoteCreditResponse getNoteCreditById(UUID id) {
        return noteCreditRepository.findByIdNoteCreditAndOrganizationId(id, OrganizationContext.getCurrentOrganizationId())
                .map(noteCreditMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Note de crédit non trouvée"));
    }

    @Transactional(readOnly = true)
    public List<NoteCreditResponse> getAllNoteCredits() {
        UUID orgId = OrganizationContext.getCurrentOrganizationId();
        List<NoteCredit> entities = noteCreditRepository.findByOrganizationId(orgId);
        return noteCreditMapper.toResponseList(entities);
    }

    @Transactional
    public void deleteNoteCredit(UUID id) {
        NoteCredit entity = noteCreditRepository.findByIdNoteCreditAndOrganizationId(id, OrganizationContext.getCurrentOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Note de crédit non trouvée"));
        noteCreditRepository.delete(entity);
    }

    private void calculateMontants(NoteCredit entity) {
        if (entity.getLignesFacture() == null || entity.getLignesFacture().isEmpty()) {
            return;
        }

        BigDecimal montantHT = entity.getLignesFacture().stream()
                .filter(ligne -> !Boolean.TRUE.equals(ligne.getIsTaxLine()))
                .map(ligne -> ligne.getMontantTotal() != null ? ligne.getMontantTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantTVA = entity.getLignesFacture().stream()
                .filter(ligne -> Boolean.TRUE.equals(ligne.getIsTaxLine()))
                .map(ligne -> ligne.getMontantTotal() != null ? ligne.getMontantTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantTTC = montantHT.add(montantTVA);

        entity.setMontantHT(montantHT);
        entity.setMontantTVA(montantTVA);
        entity.setMontantTTC(montantTTC);
        entity.setMontantTotal(montantTTC);
        entity.setMontantRestant(montantTTC);
        entity.setFinalAmount(montantTTC);
    }
}
