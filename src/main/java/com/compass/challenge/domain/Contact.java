package com.compass.challenge.domain;

public record Contact(
        long id,
        String firstName,
        String lastName,
        String email,
        String postalCode,
        String address
) {
}