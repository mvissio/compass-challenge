package com.compass.challenge.matching;

import com.compass.challenge.domain.Confidence;
import com.compass.challenge.domain.MatchEvidence;

import static com.compass.challenge.domain.MatchEvidence.AddressMatch;
import static com.compass.challenge.domain.MatchEvidence.EmailMatch;
import static com.compass.challenge.domain.MatchEvidence.NameMatch;

public final class MatchScorer {

    public static final int MINIMUM_MATCH_SCORE = 10;

    public int calculate(MatchEvidence evidence) {

        int score = 0;

        score += switch (evidence.email()) {
            case EXACT -> 40;
            case SAME_LOCAL_PART -> 25;
            case NONE -> 0;
        };

        score += switch (evidence.lastName()) {
            case EXACT -> 15;
            case INITIAL -> 7;
            case NONE -> 0;
        };

        score += switch (evidence.firstName()) {
            case EXACT -> 10;
            case INITIAL -> 5;
            case NONE -> 0;
        };

        score += switch (evidence.address()) {
            case EXACT -> 25;
            case NONE -> 0;
        };

        if (evidence.samePostalCode()) {
            score += 10;
        }

        return Math.min(score, 100);
    }

    public boolean isPotentialMatch(
            MatchEvidence evidence,
            int score) {

        if (score < MINIMUM_MATCH_SCORE) {
            return false;
        }

        boolean strongEvidence =
                evidence.email() != EmailMatch.NONE
                        || evidence.address()
                        != AddressMatch.NONE;

        boolean compatibleName =
                evidence.firstName() != NameMatch.NONE
                        && evidence.lastName()
                        != NameMatch.NONE;

        return strongEvidence || compatibleName;
    }

    public Confidence confidence(int score) {

        if (score >= 75) {
            return Confidence.HIGH;
        }

        if (score >= 45) {
            return Confidence.MEDIUM;
        }

        return Confidence.LOW;
    }
}