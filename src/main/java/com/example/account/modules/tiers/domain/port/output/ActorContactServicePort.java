package com.example.account.modules.tiers.domain.port.output;

import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Kernel's third-party record has no contact-detail fields — the closest
 * thing to an email is common-core's separate "address book" resource
 * (GET /api/contacts?contactableType=ACTOR&contactableId=...), keyed by
 * actor id (a third party's partyId), not by third-party id.
 */
public interface ActorContactServicePort {
    /** Best-guess email for this actor: the favorite contact's email if one is marked, else the first contact with a non-blank email. Empty if there are no contacts or none has an email. */
    Mono<String> getPrimaryEmail(UUID actorId);
}
