package com.example.account.modules.facturation.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.account.modules.facturation.dto.request.FactureFournisseurCreateRequest;
import com.example.account.modules.facturation.dto.response.FactureFournisseurResponse;
import com.example.account.modules.facturation.service.FactureFournisseurService;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/facture-fournisseurs")
public class FactureFournisseurController {

    @Autowired
    private FactureFournisseurService factureFournisseurService;

    @GetMapping
    public ResponseEntity<List<FactureFournisseurResponse>> getFactures() {
        return ResponseEntity.status(200).body(factureFournisseurService.getAllFactures());
    }
    
    @PostMapping
    public ResponseEntity<FactureFournisseurResponse> createFacture(@RequestBody FactureFournisseurCreateRequest dto) {
        return ResponseEntity.status(201).body(factureFournisseurService.createFacture(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FactureFournisseurResponse> updateFacture(
        @PathVariable UUID id, 
        @RequestBody FactureFournisseurResponse updatedData) {
    
        try {
            FactureFournisseurResponse result = factureFournisseurService.updateFacture(id, updatedData);
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}