package com.compass.challenge.matching;

import com.compass.challenge.domain.Contact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ContactNormalizerTest {

    private final ContactNormalizer normalizer = new ContactNormalizer();

    @Test
    void normalizesWithoutChangingOriginal() {
        var original = new Contact(
                1,
                " MARY   JANE ",
                " PEREZ ",
                " ANA@EXAMPLE.COM ",
                "02110",
                "123 Main St."
        );

        var result = normalizer.normalize(original);

        assertAll(
                () -> assertEquals("mary jane", result.firstName()),
                () -> assertEquals("perez", result.lastName()),
                () -> assertEquals("ana@example.com", result.email()),
                () -> assertEquals("ana", result.emailLocalPart()),
                () -> assertEquals("02110", result.postalCode()),
                () -> assertEquals("123 main st", result.address()),
                () -> assertEquals(" MARY   JANE ", original.firstName())
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "---", "..."})
    void emptyAddressProvidesNoInformation(String address) {
        var contact = new Contact(
                1, null, null, null, null, address
        );

        var result = normalizer.normalize(contact);

        assertNull(result.address());
    }

    @Test
    void missingValuesRemainUnknown() {
        var contact = new Contact(
                1, " ", null, "", null, null
        );

        var result = normalizer.normalize(contact);

        assertAll(
                () -> assertNull(result.firstName()),
                () -> assertNull(result.lastName()),
                () -> assertNull(result.email()),
                () -> assertNull(result.emailLocalPart()),
                () -> assertNull(result.postalCode()),
                () -> assertNull(result.address())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "@example.com"})
    void doesNotExtractLocalPartWithoutAUsableSeparator(String email) {
        var contact = new Contact(
                1, null, null, email, null, null
        );

        var result = normalizer.normalize(contact);

        assertNull(result.emailLocalPart());
    }
}