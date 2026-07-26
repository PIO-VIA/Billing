package com.example.account.modules.facturation.adapter.output.external;

import com.example.account.modules.core.context.ReactiveOrganizationContext;
import com.example.account.modules.core.exception.SalesCoreErrorMapper;
import com.example.account.modules.facturation.domain.port.output.ProductServicePort;
import com.example.account.modules.facturation.dto.response.ExternalResponses.ProductResponse;
import com.example.account.modules.shared.dto.kernel.KernelApiResponse;
import com.example.account.modules.shared.dto.kernel.KernelProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ProductServiceAdapter implements ProductServicePort {

    private final WebClient salesCoreWebClient;
    private final AccountingKernelAuthService accountingKernelAuthService;

    private static final ParameterizedTypeReference<KernelApiResponse<List<KernelProductResponse>>> PRODUCT_LIST_TYPE =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<KernelApiResponse<KernelProductResponse>> PRODUCT_TYPE =
            new ParameterizedTypeReference<>() {};

    public ProductServiceAdapter(@Qualifier("salesCoreWebClient") WebClient salesCoreWebClient,
                                  AccountingKernelAuthService accountingKernelAuthService) {
        this.salesCoreWebClient = salesCoreWebClient;
        this.accountingKernelAuthService = accountingKernelAuthService;
    }

    @Override
    public Flux<ProductResponse> fetchProductsByOrganization(UUID organizationId) {
        log.info("Fetching products from sales-core for organization: {}", organizationId);
        // Kernel's product-core requires organizationId as a query param (unlike sales-core's
        // old X-Organization-Id-only convention); sent as both to honor the org this method
        // was actually called with regardless of which one the backend reads. This endpoint
        // needs an authenticated, sufficiently-privileged Kernel token regardless of caller
        // (it 403s with no token at all, and a plain customer's own token also lacks the
        // needed permission) — read-only public catalog data, so our own service account
        // is used explicitly rather than whatever caller identity happens to be in context.
        return accountingKernelAuthService.getValidToken()
                .flatMap(token -> salesCoreWebClient
                        .get()
                        .uri(uriBuilder -> uriBuilder.path("/api/products").queryParam("organizationId", organizationId).build())
                        .header("X-Organization-Id", organizationId.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                                resp -> resp.bodyToMono(String.class)
                                        .flatMap(err -> Mono.error(new RuntimeException("sales-core products error: " + SalesCoreErrorMapper.extractMessage(err)))))
                        .bodyToMono(PRODUCT_LIST_TYPE))
                .map(KernelApiResponse::getData)
                .flatMapMany(Flux::fromIterable)
                .map(this::mapToProductResponse);
    }

    @Override
    public Flux<ProductResponse> fetchAllProducts() {
        return ReactiveOrganizationContext.getOrganizationIdOrEmpty()
                .flatMapMany(orgId -> fetchProductsByOrganization(orgId))
                .switchIfEmpty(Flux.defer(() -> {
                    log.warn("fetchAllProducts called without organization context — returning empty");
                    return Flux.empty();
                }));
    }

    @Override
    public Mono<ProductResponse> fetchProductById(UUID productId) {
        log.info("Fetching product {} from sales-core", productId);
        return salesCoreWebClient
                .get()
                .uri("/api/products/{id}", productId)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        resp -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + productId)))
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(err -> Mono.error(new RuntimeException("sales-core product error: " + SalesCoreErrorMapper.extractMessage(err)))))
                .bodyToMono(PRODUCT_TYPE)
                .map(KernelApiResponse::getData)
                .map(this::mapToProductResponse);
    }

    @Override
    public Mono<List<ProductResponse.SaleSize>> updateSaleSizes(UUID productId, List<ProductResponse.SaleSize> allowedSaleSizes) {
        log.info("Updating sale sizes for product {} via sales-core", productId);
        return salesCoreWebClient
                .put()
                .uri("/api/products/{id}/sale-sizes", productId)
                .bodyValue(Map.of("allowedSaleSizes", allowedSaleSizes))
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        resp -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + productId)))
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        SalesCoreErrorMapper.forContext("sales-core sale-sizes error"))
                .bodyToMono(new ParameterizedTypeReference<List<ProductResponse.SaleSize>>() {});
    }

    @Override
    public Mono<String> updatePhoto(UUID productId, String photo) {
        log.info("Updating photo for product {} via sales-core", productId);
        return salesCoreWebClient
                .put()
                .uri("/api/products/{id}/photo", productId)
                .bodyValue(Map.of("photo", photo))
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        resp -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + productId)))
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        SalesCoreErrorMapper.forContext("sales-core product photo error"))
                .bodyToMono(String.class);
    }

    private ProductResponse mapToProductResponse(KernelProductResponse k) {
        return ProductResponse.builder()
                .idProduit(k.getId())
                .organizationId(k.getOrganizationId())
                .nomProduit(k.getName())
                .typeProduit(k.getFamilyCode())
                .prixVente(k.getUnitPrice())
                .cout(k.getCost() != null ? k.getCost() : k.getUnitPrice())
                .categorie(k.getCategoryCode() != null ? k.getCategoryCode() : k.getFamilyCode())
                .reference(k.getSku())
                .codeBarre(k.getBarcode())
                .photo(k.getPhoto())
                .active(k.getStatus() == null || "ACTIVE".equalsIgnoreCase(k.getStatus()))
                .uom(k.getUom())
                .stockQuantity(k.getQuantity())
                // Kernel's catalog only exposes a single total quantity; the available/reserved
                // split is tracked separately, locally, by the stock-reservation websocket flow.
                .availableQuantity(null)
                .reservedQuantity(null)
                .allowedSaleSizes(k.getAllowedSaleSizes() != null ? k.getAllowedSaleSizes() : List.of())
                .activePromotions(List.of())
                .createdAt(parseDate(k.getCreatedAt()))
                .updatedAt(parseDate(k.getUpdatedAt()))
                .build();
    }

    private java.time.LocalDate parseDate(String isoDateTime) {
        if (isoDateTime == null) return null;
        try {
            return java.time.OffsetDateTime.parse(isoDateTime).toLocalDate();
        } catch (Exception e) {
            try {
                return java.time.LocalDateTime.parse(isoDateTime).toLocalDate();
            } catch (Exception ignored) {
                return null;
            }
        }
    }
}
