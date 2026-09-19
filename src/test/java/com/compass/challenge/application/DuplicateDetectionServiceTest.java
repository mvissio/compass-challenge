package com.compass.challenge.application;

import com.compass.challenge.domain.Contact;
import com.compass.challenge.domain.Confidence;
import com.compass.challenge.domain.MatchResult;
import com.compass.challenge.matching.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateDetectionServiceTest {
    private final DuplicateDetectionService service = new DuplicateDetectionService(
            new ContactNormalizer(), new ContactMatcher(), new MatchScorer());

    @Test
    void emptyOrSingleContactProducesNoPairs() {
        assertTrue(service.findPotentialDuplicates(List.of()).isEmpty());
        assertTrue(service.findPotentialDuplicates(List.of(contact(1))).isEmpty());
    }

    @Test
    void reportsEveryPairOnceAndDoesNotDependOnInputOrder() {
        var expected = List.of(new MatchResult(1, 2, 100, Confidence.HIGH),
                new MatchResult(1, 3, 100, Confidence.HIGH),
                new MatchResult(2, 3, 100, Confidence.HIGH));
        assertEquals(expected, service.findPotentialDuplicates(List.of(contact(3), contact(1), contact(2))));
        assertEquals(expected, service.findPotentialDuplicates(List.of(contact(2), contact(3), contact(1))));
    }

    @Test
    void punctuationOnlyAddressesDoNotCreateDuplicates() {
        assertTrue(service.findPotentialDuplicates(List.of(
                new Contact(1, null, null, null, null, "---"),
                new Contact(2, null, null, null, null, "..."))).isEmpty());
    }

    @Test
    void reproducesHighAndLowAssessmentExamples() {
        var a = new Contact(1001, "C", "F", "mollis.lectus.pede@outlook.net", null, "449-6990 Tellus. Rd.");
        var b = new Contact(1002, "C", "French", "mollis.lectus.pede@outlook.net", "39746", "449-6990 Tellus. Rd.");
        var c = new Contact(1003, "Ciara", "F", "non.lacinia.at@zoho.ca", "39746", null);
        assertEquals(List.of(new MatchResult(1001, 1002, 77, Confidence.HIGH),
                        new MatchResult(1001, 1003, 12, Confidence.LOW),
                        new MatchResult(1002, 1003, 22, Confidence.LOW)),
                service.findPotentialDuplicates(List.of(a, b, c)));
    }

    private Contact contact(long id) {
        return new Contact(id, "Ana", "Perez", "ana@example.com", "02110", "123 Main St");
    }
}
