package com.example.account.modules.facturation.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.account.modules.facturation.dto.request.FactureFournisseurCreateRequest;
import com.example.account.modules.facturation.dto.response.FactureFournisseurResponse;
import com.example.account.modules.facturation.mapper.FactureFournisseurMapper;
import com.example.account.modules.facturation.model.entity.FactureFournisseur;
import com.example.account.modules.facturation.repository.FactureFournisseurRepository;

@Service
public class FactureFournisseurService {
    
    @Autowired
    private FactureFournisseurRepository factureFournisseurRepository;
    @Autowired
    private FactureFournisseurMapper factureFournisseurMapper;

    public FactureFournisseurResponse createFacture(FactureFournisseurCreateRequest dto){
        FactureFournisseur factureFournisseur = factureFournisseurMapper.toEntity(dto);
        return factureFournisseurMapper.toDto(factureFournisseurRepository.save(factureFournisseur));
    }

    public List<FactureFournisseurResponse> getAllFactures(){
        List<FactureFournisseur> factures = factureFournisseurRepository.findAll();
        return factureFournisseurMapper.toDtoList(factures);
    }

    public FactureFournisseurResponse updateFacture(UUID id, FactureFournisseurResponse dto) throws Exception{
        FactureFournisseur factureFournisseur = factureFournisseurRepository.findById(id).orElseThrow(()->new Exception("Facture Fournisseur does not exists"));
        factureFournisseurMapper.updateEntityFromDto(dto, factureFournisseur);
        factureFournisseurRepository.save(factureFournisseur);
        return factureFournisseurMapper.toDto(factureFournisseur);
    }
}