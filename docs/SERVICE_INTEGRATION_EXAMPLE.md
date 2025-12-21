# Service Integration Example: Adding Organization Support

This document shows how to update existing services to support multi-tenancy.

## Example: ClientService Integration

### Before (Current Code)

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ClientEventProducer clientEventProducer;

    @Transactional
    public ClientResponse createClient(ClientCreateRequest request) {
        log.info("Création d'un nouveau client: {}", request.getUsername());

        // Vérifications
        if (clientRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Un client avec ce username existe déjà");
        }

        // Créer et sauvegarder le client
        Client client = clientMapper.toEntity(request);
        Client savedClient = clientRepository.save(client);
        ClientResponse response = clientMapper.toResponse(savedClient);

        // Publier l'événement
        clientEventProducer.publishClientCreated(response);

        return response;
    }
}
```

### After (With Organization Support)

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ClientEventProducer clientEventProducer;
    private final EntityOrganizationHelper organizationHelper;  // ← ADD THIS

    @Transactional
    public ClientResponse createClient(ClientCreateRequest request) {
        UUID orgId = OrganizationContext.getCurrentOrganizationId();  // ← ADD THIS
        log.info("Création d'un nouveau client: {} pour organisation: {}",
            request.getUsername(), orgId);  // ← UPDATE LOG

        // Vérifications (scoped to organization automatically via Hibernate filter)
        if (clientRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Un client avec ce username existe déjà dans cette organisation");
        }

        // Créer le client
        Client client = clientMapper.toEntity(request);
        organizationHelper.setOrganizationId(client);  // ← ADD THIS: Auto-inject org ID

        Client savedClient = clientRepository.save(client);
        ClientResponse response = clientMapper.toResponse(savedClient);

        // Publier l'événement
        clientEventProducer.publishClientCreated(response);

        return response;
    }

    @Transactional
    @CachePut(value = "clients", key = "#clientId")
    public ClientResponse updateClient(UUID clientId, ClientUpdateRequest request) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("Client non trouvé"));

        organizationHelper.validateOrganizationMatch(client);  // ← ADD THIS: Security check

        clientMapper.updateEntityFromRequest(request, client);
        Client updatedClient = clientRepository.save(client);

        return clientMapper.toResponse(updatedClient);
    }
}
```

## Key Changes Required

### 1. Add EntityOrganizationHelper Dependency

```java
private final EntityOrganizationHelper organizationHelper;
```

### 2. Auto-inject Organization ID on Create

```java
Client client = clientMapper.toEntity(request);
organizationHelper.setOrganizationId(client);  // ← Add this
Client savedClient = clientRepository.save(client);
```

### 3. Validate Organization Match on Update/Delete

```java
Client client = clientRepository.findById(clientId)
    .orElseThrow(() -> new IllegalArgumentException("Client non trouvé"));

organizationHelper.validateOrganizationMatch(client);  // ← Add this security check
```

### 4. Update Cache Keys (If Using Caching)

```java
// BEFORE
@Cacheable(value = "clients", key = "#clientId")
public ClientResponse getClientById(UUID clientId) { ... }

// AFTER - Include organization in cache key
@Cacheable(value = "clients", key = "T(com.example.account.context.OrganizationContext).getCurrentOrganizationId() + ':' + #clientId")
public ClientResponse getClientById(UUID clientId) { ... }
```

### 5. Update Kafka Events (Add Organization Context)

```java
// In EventProducer classes, add organizationId to event payload
public void publishClientCreated(ClientResponse client) {
    UUID orgId = OrganizationContext.getCurrentOrganizationId();

    ClientEvent event = ClientEvent.builder()
        .organizationId(orgId)  // ← Add this
        .clientId(client.getIdClient())
        .username(client.getUsername())
        .build();

    kafkaProducerService.sends("client-created", event.getClientId().toString(), event);
}
```

## Complete Updated Service Example

```java
package com.example.account.service;

import com.example.account.context.OrganizationContext;
import com.example.account.dto.request.ClientCreateRequest;
import com.example.account.dto.response.ClientResponse;
import com.example.account.mapper.ClientMapper;
import com.example.account.model.entity.Client;
import com.example.account.repository.ClientRepository;
import com.example.account.service.producer.ClientEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ClientEventProducer clientEventProducer;
    private final EntityOrganizationHelper organizationHelper;

    @Transactional
    public ClientResponse createClient(ClientCreateRequest request) {
        UUID orgId = OrganizationContext.getCurrentOrganizationId();
        log.info("Creating client: {} for organization: {}", request.getUsername(), orgId);

        // Validations (automatically scoped to organization)
        if (clientRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Client with this username already exists in organization");
        }

        Client client = clientMapper.toEntity(request);
        organizationHelper.setOrganizationId(client);

        Client savedClient = clientRepository.save(client);
        ClientResponse response = clientMapper.toResponse(savedClient);

        clientEventProducer.publishClientCreated(response);

        log.info("Client created successfully: {} in org: {}", savedClient.getIdClient(), orgId);
        return response;
    }

    @Cacheable(
        value = "clients",
        key = "T(com.example.account.context.OrganizationContext).getCurrentOrganizationId() + ':' + #clientId"
    )
    public ClientResponse getClientById(UUID clientId) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));

        // Hibernate filter automatically ensures client belongs to current organization
        return clientMapper.toResponse(client);
    }

    public List<ClientResponse> getAllClients() {
        // Automatically filtered by organization via Hibernate filter
        List<Client> clients = clientRepository.findAll();
        return clientMapper.toResponseList(clients);
    }

    @Transactional
    public void deleteClient(UUID clientId) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));

        // Security check: ensure client belongs to current organization
        organizationHelper.validateOrganizationMatch(client);

        clientRepository.delete(client);
        clientEventProducer.publishClientDeleted(clientId);
    }
}
```

## Important Notes

### Automatic Filtering

Thanks to Hibernate filters enabled in `OrganizationFilter`, **ALL repository queries automatically filter by organization_id**.

```java
// This query:
clientRepository.findAll();

// Automatically becomes:
SELECT * FROM clients WHERE organization_id = :currentOrgId;
```

### Security Layers

The architecture provides **3 layers of security**:

1. **HTTP Filter** (`OrganizationFilter`): Validates org context from header
2. **Hibernate Filter**: Auto-filters all queries by org_id
3. **Service Validation** (`organizationHelper.validateOrganizationMatch`): Explicit security check

### What You DON'T Need to Change

✅ **Repository methods** - unchanged, filtering is automatic
✅ **Controller endpoints** - unchanged, just pass org ID in header
✅ **Database queries** - automatically filtered
✅ **Entity structure** - already updated to extend `OrganizationScoped`

### What You NEED to Change

✔️ Add `EntityOrganizationHelper` to service constructors
✔️ Call `setOrganizationId()` before saving new entities
✔️ Call `validateOrganizationMatch()` before updating/deleting entities
✔️ Update cache keys to include organization ID
✔️ Add organization ID to Kafka event payloads

## Migration Checklist for Each Service

- [ ] Add `EntityOrganizationHelper` dependency
- [ ] Update `create` methods to call `setOrganizationId()`
- [ ] Update `update`/`delete` methods to call `validateOrganizationMatch()`
- [ ] Update cache annotations to include org ID in key
- [ ] Update event producers to include org ID
- [ ] Update log statements to include org ID
- [ ] Test with multiple organizations

## Testing

```java
// In tests, set organization context manually
@Test
public void testCreateClient() {
    UUID orgId = UUID.randomUUID();
    OrganizationContext.setCurrentOrganizationId(orgId);

    try {
        ClientResponse client = clientService.createClient(request);
        assertEquals(orgId, client.getOrganizationId());
    } finally {
        OrganizationContext.clear();
    }
}
```
