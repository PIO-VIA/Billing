package com.example.account.modules.facturation.adapter.output.external;

import com.example.account.modules.core.context.ReactiveOrganizationContext;
import com.example.account.modules.core.exception.SalesCoreErrorMapper;
import com.example.account.modules.facturation.domain.port.output.ProductServicePort;
import com.example.account.modules.facturation.dto.response.ExternalResponses.ProductResponse;
import com.example.account.modules.facturation.model.entity.ProductSaleSizeConfig;
import com.example.account.modules.facturation.repository.ProductSaleSizeConfigRepository;
import com.example.account.modules.shared.dto.kernel.KernelApiResponse;
import com.example.account.modules.shared.dto.kernel.KernelProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductServiceAdapter implements ProductServicePort {

    private final WebClient salesCoreWebClient;
    private final ProductSaleSizeConfigRepository saleSizeConfigRepository;
    private final R2dbcEntityTemplate entityTemplate;

    private static final ParameterizedTypeReference<KernelApiResponse<List<KernelProductResponse>>> PRODUCT_LIST_TYPE =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<KernelApiResponse<KernelProductResponse>> PRODUCT_TYPE =
            new ParameterizedTypeReference<>() {};

    public ProductServiceAdapter(@Qualifier("salesCoreWebClient") WebClient salesCoreWebClient,
                                  ProductSaleSizeConfigRepository saleSizeConfigRepository,
                                  R2dbcEntityTemplate entityTemplate) {
        this.salesCoreWebClient = salesCoreWebClient;
        this.saleSizeConfigRepository = saleSizeConfigRepository;
        this.entityTemplate = entityTemplate;
    }

    @Override
    public Flux<ProductResponse> fetchProductsByOrganization(UUID organizationId) {
        log.info("Fetching products from sales-core for organization: {}", organizationId);
        // Kernel's product-core moved this route from the old flat /api/products to
        // /api/product-core/products (org scoping is now implicit via the
        // X-Organization-Id header — the endpoint's only query param is "kind"),
        // as part of restructuring the monolith's product model into
        // product-core/organization-products/variants. salesCoreWebClient's own
        // filter forwards the caller's Kernel token (and org id) from the Reactor
        // context — no explicit Authorization header needed here.
        return salesCoreWebClient
                .get()
                .uri("/api/product-core/products")
                .header("X-Organization-Id", organizationId.toString())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(err -> Mono.error(new RuntimeException("sales-core products error: " + SalesCoreErrorMapper.extractMessage(err)))))
                .bodyToMono(PRODUCT_LIST_TYPE)
                .map(KernelApiResponse::getData)
                .flatMapMany(Flux::fromIterable)
                .map(this::mapToProductResponse)
                .collectList()
                .flatMapMany(this::mergeLocalConfigs);
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
                .uri("/api/product-core/products/{id}", productId)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        resp -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + productId)))
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(err -> Mono.error(new RuntimeException("sales-core product error: " + SalesCoreErrorMapper.extractMessage(err)))))
                .bodyToMono(PRODUCT_TYPE)
                .map(KernelApiResponse::getData)
                .map(this::mapToProductResponse)
                .flatMap(product -> saleSizeConfigRepository.findByProductId(productId)
                        .map(config -> applyConfig(product, config))
                        .defaultIfEmpty(product));
    }

    /**
     * Batch-fetches local configs for every product id in one query rather
     * than per-product, since this backs the organization-wide list endpoint.
     */
    private Flux<ProductResponse> mergeLocalConfigs(List<ProductResponse> products) {
        List<UUID> productIds = products.stream().map(ProductResponse::getIdProduit).toList();
        return saleSizeConfigRepository.findByProductIdIn(productIds)
                .collectMap(ProductSaleSizeConfig::getProductId, c -> c)
                .flatMapMany(configsById -> Flux.fromIterable(products)
                        .map(product -> {
                            ProductSaleSizeConfig config = configsById.get(product.getIdProduit());
                            return config == null ? product : applyConfig(product, config);
                        }));
    }

    private ProductResponse applyConfig(ProductResponse product, ProductSaleSizeConfig config) {
        if (config.getPhoto() != null) {
            product.setPhoto(config.getPhoto());
        }
        product.setVatApplicable(config.isVatApplicable());
        product.setAllowedSaleSizes(config.getSaleSizes() != null ? config.getSaleSizes() : List.of());
        return product;
    }

    /**
     * Sale sizes, image, and VAT applicability are Billing's own local
     * config, not a Kernel product-core field — product-core's "variants"
     * are a generic sku/name concept with no price/image/VAT field anywhere
     * in the new product schemas. Upserts the config row, then returns the
     * full product (Kernel catalog data + this config merged in).
     */
    @Override
    public Mono<ProductResponse> updateSaleSizes(UUID productId, List<ProductResponse.SaleSize> allowedSaleSizes,
                                                  String photo, Boolean vatApplicable) {
        log.info("Updating sale-size config for product {}", productId);
        return ReactiveOrganizationContext.getOrganizationId()
                .flatMap(organizationId -> upsertConfig(productId, organizationId, photo, vatApplicable, allowedSaleSizes))
                .then(fetchProductById(productId));
    }

    @Override
    public Mono<String> updatePhoto(UUID productId, String photo) {
        log.info("Updating photo for product {}", productId);
        return ReactiveOrganizationContext.getOrganizationId()
                .flatMap(organizationId -> upsertConfig(productId, organizationId, photo, null, null))
                .thenReturn(photo);
    }

    private Mono<ProductSaleSizeConfig> upsertConfig(UUID productId, UUID organizationId, String photo,
                                                       Boolean vatApplicable, List<ProductResponse.SaleSize> saleSizes) {
        LocalDateTime now = LocalDateTime.now();
        return saleSizeConfigRepository.findByProductId(productId)
                .flatMap(existing -> {
                    if (photo != null) existing.setPhoto(photo);
                    if (vatApplicable != null) existing.setVatApplicable(vatApplicable);
                    if (saleSizes != null) existing.setSaleSizes(saleSizes);
                    existing.setUpdatedAt(now);
                    return saleSizeConfigRepository.save(existing);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    ProductSaleSizeConfig created = ProductSaleSizeConfig.builder()
                            .id(UUID.randomUUID())
                            .productId(productId)
                            .organizationId(organizationId)
                            .photo(photo)
                            .vatApplicable(vatApplicable == null || vatApplicable)
                            .saleSizes(saleSizes != null ? saleSizes : List.of())
                            .createdAt(now)
                            .updatedAt(now)
                            .build();
                    return entityTemplate.insert(created);
                }));
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
                .vatApplicable(true)
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
