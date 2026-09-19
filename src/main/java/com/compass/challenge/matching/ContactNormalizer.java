package com.compass.challenge.matching;

import com.compass.challenge.domain.Contact;
import com.compass.challenge.domain.NormalizedContact;

import java.util.Locale;

public final class ContactNormalizer {

    public NormalizedContact normalize(Contact contact) {

        String normalizedEmail =
                normalizeEmail(contact.email());

        return new NormalizedContact(
                contact.id(),
                normalizeName(contact.firstName()),
                normalizeName(contact.lastName()),
                normalizedEmail,
                extractEmailLocalPart(normalizedEmail),
                normalizePostalCode(contact.postalCode()),
                normalizeAddress(contact.address())
        );
    }

    private String normalizeName(String value) {
        return normalizeBasic(value);
    }

    private String normalizeEmail(String value) {
        return normalizeBasic(value);
    }

    private String normalizePostalCode(String value) {
        return normalizeBasic(value);
    }

    private String normalizeAddress(String value) {

        String normalized = normalizeBasic(value);

        if (normalized == null) {
            return null;
        }

        String cleaned = normalized
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        return cleaned.isEmpty() ? null : cleaned;
    }

    private String extractEmailLocalPart(String email) {

        if (email == null) {
            return null;
        }

        int separator = email.indexOf('@');

        if (separator <= 0) {
            return null;
        }

        return email.substring(0, separator);
    }

    private String normalizeBasic(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }
}