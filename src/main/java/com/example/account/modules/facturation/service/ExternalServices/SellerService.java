package com.example.account.modules.facturation.service.ExternalServices;

import java.util.List;
import java.util.UUID;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerAuthResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final RestTemplate restTemplate;

    @Value("${comops.core.backend_ip}")
    private String comOpsUrl;

    public List<SellerAuthResponse> getSellersByOrganization(UUID organizationId) {
        
        String url = String.format(
                "http://%s/sellers/organization/%s/sellers",
                comOpsUrl,
                organizationId
        );

        System.out.println("Requesting sellers from: " + url);

        try {
            ResponseEntity<List<SellerAuthResponse>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<List<SellerAuthResponse>>() {}
                    );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                System.out.println("Successfully retrieved " + response.getBody().size() + " sellers.");
                return response.getBody();
            }
            
            System.out.println("Response received but body was empty or status not 2xx");
            return Collections.emptyList();

        } catch (RestClientException e) {
            System.out.println("Error calling external Seller service: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}