package com.compass.challenge.domain;

public record MatchEvidence(
        NameMatch firstName,
        NameMatch lastName,
        EmailMatch email,
        boolean samePostalCode,
        AddressMatch address
) {

    public enum NameMatch {
        NONE,
        INITIAL,
        EXACT
    }

    public enum EmailMatch {
        NONE,
        SAME_LOCAL_PART,
        EXACT
    }

    public enum AddressMatch {
        NONE,
        EXACT
    }
}