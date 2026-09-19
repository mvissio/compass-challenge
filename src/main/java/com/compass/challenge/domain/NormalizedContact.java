package com.compass.challenge.domain;

public record NormalizedContact(
        long id,
        String firstName,
        String lastName,
        String email,
        String emailLocalPart,
        String postalCode,
        String address
) {
}