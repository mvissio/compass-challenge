package com.compass.challenge.csv;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CsvContactReaderTest {
    @TempDir Path directory;
    private static final String HEADER = "contactID,name,name1,email,postalZip,address\n";
    private final CsvContactReader reader = new CsvContactReader();

    @Test
    void supportsBomQuotedCommasAndLeadingZeroPostalCode() throws Exception {
        var contacts = reader.read(file("\uFEFF" + HEADER + "1,Ana,Perez,a@b.com,02110,\"123 Main St, Apt 2\"\n"));
        assertEquals(1, contacts.size());
        assertEquals("02110", contacts.getFirst().postalCode());
        assertEquals("123 Main St, Apt 2", contacts.getFirst().address());
    }

    @Test
    void headerOnlyFileContainsNoContacts() throws Exception {
        assertTrue(reader.read(file(HEADER)).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "contactID,name\n", "contactID,name,name1,email,postalZip,address,name\n"})
    void rejectsMissingOrDuplicateHeaders(String csv) throws Exception {
        Path path = file(csv);
        assertThrows(IllegalArgumentException.class, () -> reader.read(path));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abc", "9223372036854775808"})
    void rejectsInvalidIdsWithRecordContext(String id) throws Exception {
        Path path = file(HEADER + id + ",Ana,Perez,a@b.com,02110,Main St\n");
        var error = assertThrows(IllegalArgumentException.class, () -> reader.read(path));
        assertTrue(error.getMessage().contains("Record 1"));
    }

    @Test
    void rejectsRepeatedIds() throws Exception {
        Path path = file(HEADER + "1,Ana,Perez,a@b.com,02110,Main St\n1,Other,Person,b@b.com,12345,Other St\n");
        var error = assertThrows(IllegalArgumentException.class, () -> reader.read(path));
        assertTrue(error.getMessage().contains("Duplicate contact ID: 1"));
    }

    @Test
    void rejectsRowsWithMissingColumns() throws Exception {
        Path path = file(HEADER + "1,Ana\n");
        assertThrows(IllegalArgumentException.class, () -> reader.read(path));
    }

    private Path file(String content) throws Exception {
        return Files.writeString(directory.resolve("input.csv"), content);
    }
}
