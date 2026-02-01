package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.request.NoteCreditRequest;
import com.example.account.modules.facturation.dto.response.NoteCreditResponse;
import com.example.account.modules.facturation.service.NoteCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/facturation/note-credits")
@RequiredArgsConstructor
public class NoteCreditController {

    private final NoteCreditService noteCreditService;

    @PostMapping
    public ResponseEntity<NoteCreditResponse> createNoteCredit(@RequestBody NoteCreditRequest request) {
        return ResponseEntity.ok(noteCreditService.createNoteCredit(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteCreditResponse> updateNoteCredit(@PathVariable UUID id, @RequestBody NoteCreditRequest request) {
        return ResponseEntity.ok(noteCreditService.updateNoteCredit(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteCreditResponse> getNoteCreditById(@PathVariable UUID id) {
        return ResponseEntity.ok(noteCreditService.getNoteCreditById(id));
    }

    @GetMapping
    public ResponseEntity<List<NoteCreditResponse>> getAllNoteCredits() {
        return ResponseEntity.ok(noteCreditService.getAllNoteCredits());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteCredit(@PathVariable UUID id) {
        noteCreditService.deleteNoteCredit(id);
        return ResponseEntity.noContent().build();
    }
}
