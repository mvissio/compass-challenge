package com.compass.challenge.domain;

public record MatchResult(
        long sourceContactId,
        long matchedContactId,
        int score,
        Confidence confidence
) {
}