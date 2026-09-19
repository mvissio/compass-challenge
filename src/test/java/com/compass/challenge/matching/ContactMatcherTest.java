package com.compass.challenge.matching;

import com.compass.challenge.domain.Contact;
import com.compass.challenge.domain.MatchEvidence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.compass.challenge.domain.MatchEvidence.*;
import static org.junit.jupiter.api.Assertions.*;

class ContactMatcherTest {
    private final ContactNormalizer normalizer = new ContactNormalizer();
    private final ContactMatcher matcher = new ContactMatcher();

    @ParameterizedTest
    @CsvSource({"C,Ciara,INITIAL", "Ciara,C,INITIAL", "C,C,INITIAL",
            "Ciara,Ciara,EXACT", "Ciara,Carlos,NONE"})
    void distinguishesInitialsFromDifferentFullNames(String left, String right, NameMatch expected) {
        assertEquals(expected, compare(new Contact(1, left, null, null, null, null),
                new Contact(2, right, null, null, null, null)).firstName());
    }

    @Test
    void missingFieldsAreNotMatches() {
        assertEquals(new MatchEvidence(NameMatch.NONE, NameMatch.NONE, EmailMatch.NONE,
                false, AddressMatch.NONE), compare(new Contact(1, null, null, null, null, null),
                new Contact(2, null, null, null, null, null)));
    }

    @Test
    void distinguishesExactEmailFromSameLocalPart() {
        var left = new Contact(1, null, null, "ana@example.com", null, null);
        assertEquals(EmailMatch.EXACT, compare(left,
                new Contact(2, null, null, "ANA@example.com", null, null)).email());
        assertEquals(EmailMatch.SAME_LOCAL_PART, compare(left,
                new Contact(3, null, null, "ana@other.com", null, null)).email());
    }

    private MatchEvidence compare(Contact left, Contact right) {
        return matcher.compare(normalizer.normalize(left), normalizer.normalize(right));
    }
}
