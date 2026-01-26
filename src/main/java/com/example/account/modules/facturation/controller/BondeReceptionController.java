package com.example.account.modules.facturation.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.account.modules.facturation.dto.request.BondeReceptionCreateRequest;
import com.example.account.modules.facturation.dto.response.BondeReceptionResponse;
import com.example.account.modules.facturation.mapper.BondeReceptionMapper;
import com.example.account.modules.facturation.service.BonReceptionService;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/api/bonreceptions")
public class BondeReceptionController {
    @Autowired
    private BonReceptionService bonReceptionService;
    @GetMapping
    public ResponseEntity<List<BondeReceptionResponse>> getBons() {
        return ResponseEntity.status(200).body(bonReceptionService.getAllBondeReception());
    }
    
    @PostMapping
    public ResponseEntity< BondeReceptionResponse> postMethodName(@RequestBody BondeReceptionCreateRequest dto) {
        return ResponseEntity.status(201).body(bonReceptionService.createBondeReception(dto));
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<BondeReceptionResponse> updateBon(
        @PathVariable UUID id, 
        @RequestBody BondeReceptionResponse updatedData) {
    
    try {
        BondeReceptionResponse result = bonReceptionService.updateBondeReception(id, updatedData);
        return ResponseEntity.ok(result);
    } catch (EntityNotFoundException e) {
        
        return ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}
}
