package com.example.account.modules.facturation.service.ExternalServices;

import com.example.account.modules.facturation.dto.response.ExternalResponses.ProductResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor

public class ProductController {

    private final ProductExternalService productService;

    /**
     * GET /api/v1/products
     * Returns a stream of all products (including nested JSON sizes/promos)
     */
    @GetMapping
    public Flux<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * GET /api/v1/products/{id}
     */
    @GetMapping("/{id}")
    public Mono<ProductResponse> getProductById(@PathVariable UUID id) {
        return productService.getProductById(id);
    }


      @GetMapping("/organization/{orgId}")
    public Flux<ProductResponse> getProductByOrganization(@PathVariable UUID orgId) {
        return productService.getAllProductsForOrganization(orgId);
    }

    /**
     * POST /api/v1/products
     * Used for creating a new product from a JSON body
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductResponse> createProduct(@RequestBody ProductResponse productRequest) {
        return productService.saveProduct(productRequest);
    }

    
    /**
     * PUT /api/v1/products/{id}
     * Full update of product details
     */
    @PutMapping("/{id}")
    public Mono<ProductResponse> updateProduct(
            @PathVariable UUID id, 
            @RequestBody ProductResponse productRequest) {
        // Ensure ID consistency
        productRequest.setIdProduit(id);
        return productService.saveProduct(productRequest);
    }

    /**
     * DELETE /api/v1/products/{id}
     */
 
}