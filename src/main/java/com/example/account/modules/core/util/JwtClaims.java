package com.example.account.modules.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads claims out of a JWT's payload without verifying its signature.
 * Used only to recover routing metadata Kernel already embeds in every
 * access token it issues (e.g. "tid", the tenant the token belongs to) —
 * not for authorization decisions, since Kernel itself enforces that on
 * every call regardless of what headers we send alongside the token.
 */
public final class JwtClaims {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Pattern AGENCY_SCOPE = Pattern.compile("#AGENCY:([0-9a-fA-F-]{36})");

    private JwtClaims() {
    }

    public static String readClaim(String token, String claim) {
        JsonNode node = decodePayload(token);
        if (node == null) {
            return null;
        }
        JsonNode value = node.get(claim);
        return value != null && !value.isNull() ? value.asText() : null;
    }

    /**
     * Kernel has no dedicated "agencyId" claim — agency scoping only shows up
     * as a "#AGENCY:<uuid>" suffix on individual entries in "permissions".
     * Returns the agency UUID only when every scoped permission agrees on the
     * same one; a token spanning more than one distinct agency is genuinely
     * ambiguous about which agency a given request is for, so this returns
     * null rather than guessing — callers then just omit X-Agency-Id, same as
     * today, rather than risk silently routing to the wrong agency.
     */
    public static String readSingleAgencyId(String token) {
        JsonNode node = decodePayload(token);
        if (node == null) {
            return null;
        }
        JsonNode permissions = node.get("permissions");
        if (permissions == null || !permissions.isArray()) {
            return null;
        }
        Set<String> agencyIds = new HashSet<>();
        for (JsonNode permission : permissions) {
            Matcher matcher = AGENCY_SCOPE.matcher(permission.asText(""));
            if (matcher.find()) {
                agencyIds.add(matcher.group(1));
            }
        }
        return agencyIds.size() == 1 ? agencyIds.iterator().next() : null;
    }

    private static JsonNode decodePayload(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            return null;
        }
        try {
            String payloadSegment = parts[1];
            int padding = (4 - payloadSegment.length() % 4) % 4;
            byte[] payload = Base64.getUrlDecoder().decode(payloadSegment + "=".repeat(padding));
            return MAPPER.readTree(new String(payload, StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }
}
