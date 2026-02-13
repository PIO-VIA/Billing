package com.example.account.modules.facturation.service.ExternalServices;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import com.example.account.modules.facturation.model.entity.Facture;
import com.example.account.modules.facturation.repository.FactureRepository;

@Service
public class AccountingService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FactureRepository factureRepository;

    @Value("${comops.accounting_back.ip}")
    private String accoutingBackURL;

    /**
     * Sends a specific Facture to the external accounting backend via POST.
     */
   public void sendFactureData(UUID factureId) throws Exception {
    
    String url = String.format("http://%s/api/accounting/invoices/sale", accoutingBackURL);

    // 1. Fetch the data
    Facture facture = factureRepository.findById(factureId)
            .orElseThrow(() -> new Exception("Facture does not exist"));

    try {
        // 2. Create and set your Headers
        HttpHeaders headers = new HttpHeaders();
         // Example: Auth token
         headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.set("X-Tenant-ID", facture.getOrganizationId().toString()); 
        headers.set("X-Tenant-ID" ,"550e8400-e29b-41d4-a716-446655440000");        // Example: Custom header
            // Good practice for POST

        // 3. Wrap the Facture and Headers into an HttpEntity
        HttpEntity<Facture> requestEntity = new HttpEntity<>(facture, headers);

        // 4. Use exchange instead of postForEntity
        ResponseEntity<Facture> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Facture.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Facture sent successfully with headers!");
        }

    } catch (RestClientException e) {
        System.err.println("Error: " + e.getMessage());
        throw new Exception("External service communication failed.");
    }
}
}