package com.example.account.modules.facturation.service;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.account.modules.facturation.dto.request.FactureFournisseurCreateRequest;
import com.example.account.modules.facturation.dto.response.FactureFournisseurResponse;
import com.example.account.modules.facturation.mapper.FactureFournisseurMapper;
import com.example.account.modules.facturation.model.entity.FactureFournisseur;
import com.example.account.modules.facturation.repository.FactureFournisseurRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FactureFournisseurService {
    
    private final FactureFournisseurRepository factureFournisseurRepository;
    private final FactureFournisseurMapper factureFournisseurMapper;

    @Transactional
    public Mono<FactureFournisseurResponse> createFacture(FactureFournisseurCreateRequest dto){
        FactureFournisseur factureFournisseur = factureFournisseurMapper.toEntity(dto);
        return factureFournisseurRepository.save(factureFournisseur)
                .map(factureFournisseurMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Flux<FactureFournisseurResponse> getAllFactures(){
        return factureFournisseurRepository.findAll()
                .map(factureFournisseurMapper::toDto);
    }

    @Transactional
    public Mono<FactureFournisseurResponse> updateFacture(UUID id, FactureFournisseurResponse dto) {
        return factureFournisseurRepository.findById(id)
                .switchIfEmpty(Mono.error(new Exception("Facture Fournisseur does not exists")))
                .flatMap(factureFournisseur -> {
                    factureFournisseurMapper.updateEntityFromDto(dto, factureFournisseur);
                    return factureFournisseurRepository.save(factureFournisseur);
                })
                .map(factureFournisseurMapper::toDto);
    }
}