package com.compass.challenge.matching;

import com.compass.challenge.domain.Confidence;
import com.compass.challenge.domain.MatchEvidence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.compass.challenge.domain.MatchEvidence.*;
import static org.junit.jupiter.api.Assertions.*;

class MatchScorerTest {
    private final MatchScorer scorer = new MatchScorer();

    @ParameterizedTest
    @CsvSource({
            "EXACT,EXACT,EXACT,true,EXACT,100",
            "INITIAL,INITIAL,EXACT,false,EXACT,77",
            "INITIAL,INITIAL,NONE,false,NONE,12",
            "NONE,NONE,SAME_LOCAL_PART,false,NONE,25",
            "NONE,NONE,NONE,true,NONE,10",
            "NONE,NONE,NONE,false,NONE,0"})
    void scoresRepresentativeEvidence(NameMatch first, NameMatch last, EmailMatch email,
                                      boolean postal, AddressMatch address, int expected) {
        assertEquals(expected, scorer.calculate(new MatchEvidence(first, last, email, postal, address)));
    }

    @ParameterizedTest
    @CsvSource({"0,LOW", "44,LOW", "45,MEDIUM", "74,MEDIUM", "75,HIGH", "100,HIGH"})
    void respectsConfidenceBoundaries(int score, Confidence expected) {
        assertEquals(expected, scorer.confidence(score));
    }

    @Test
    void rejectsPostalCodeAloneDespiteTenPoints() {
        var evidence = new MatchEvidence(NameMatch.NONE, NameMatch.NONE,
                EmailMatch.NONE, true, AddressMatch.NONE);
        assertFalse(scorer.isPotentialMatch(evidence, scorer.calculate(evidence)));
    }

    @Test
    void acceptsCompatibleInitialsAsLowConfidence() {
        var evidence = new MatchEvidence(NameMatch.INITIAL, NameMatch.INITIAL,
                EmailMatch.NONE, false, AddressMatch.NONE);
        assertTrue(scorer.isPotentialMatch(evidence, scorer.calculate(evidence)));
        assertEquals(Confidence.LOW, scorer.confidence(scorer.calculate(evidence)));
    }
}
