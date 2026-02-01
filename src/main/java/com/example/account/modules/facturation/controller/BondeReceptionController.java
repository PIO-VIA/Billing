package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.request.BondeReceptionCreateRequest;
import com.example.account.modules.facturation.dto.response.BondeReceptionResponse;
import com.example.account.modules.facturation.service.BonReceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/facturation/bon-receptions")
@RequiredArgsConstructor
public class BondeReceptionController {

    private final BonReceptionService bonReceptionService;

    @GetMapping
    public ResponseEntity<List<BondeReceptionResponse>> getBons() {
        return ResponseEntity.ok(bonReceptionService.getAllBondeReception());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BondeReceptionResponse> getBonById(@PathVariable UUID id) {
        return ResponseEntity.ok(bonReceptionService.getBondeReceptionById(id));
    }

    @PostMapping
    public ResponseEntity<BondeReceptionResponse> createBon(@RequestBody BondeReceptionCreateRequest dto) {
        return ResponseEntity.status(201).body(bonReceptionService.createBondeReception(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BondeReceptionResponse> updateBon(@PathVariable UUID id, @RequestBody BondeReceptionResponse updatedData) {
        return ResponseEntity.ok(bonReceptionService.updateBondeReception(id, updatedData));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBon(@PathVariable UUID id) {
        bonReceptionService.deleteBondeReception(id);
        return ResponseEntity.noContent().build();
    }
}
