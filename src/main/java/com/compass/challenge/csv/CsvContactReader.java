package com.compass.challenge.csv;

import com.compass.challenge.domain.Contact;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.DuplicateHeaderMode;

import java.io.IOException;
import java.io.PushbackReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class CsvContactReader {
    private static final List<String> REQUIRED_HEADERS = List.of(
            "contactID", "name", "name1", "email", "postalZip", "address");

    public List<Contact> read(Path path) throws IOException {
        var contacts = new ArrayList<Contact>();
        var contactIds = new HashSet<Long>();
        var format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setDuplicateHeaderMode(DuplicateHeaderMode.DISALLOW)
                .build();

        try (var reader = new PushbackReader(Files.newBufferedReader(path), 1)) {
            // Some spreadsheet exports begin with a UTF-8 BOM.
            int first = reader.read();
            if (first != -1 && first != '\uFEFF') {
                reader.unread(first);
            }
            try (var parser = format.parse(reader)) {
                for (String header : REQUIRED_HEADERS) {
                    if (!parser.getHeaderMap().containsKey(header)) {
                        throw new IllegalArgumentException("Missing CSV header: " + header);
                    }
                }
                for (CSVRecord record : parser) {
                    if (!record.isConsistent()) {
                        throw invalid(record, "Incorrect number of columns");
                    }
                    long id = parseContactId(record);
                    if (!contactIds.add(id)) {
                        throw invalid(record, "Duplicate contact ID: " + id);
                    }
                    contacts.add(new Contact(id, record.get("name"), record.get("name1"),
                            record.get("email"), record.get("postalZip"), record.get("address")));
                }
            }
        }
        return contacts;
    }

    private long parseContactId(CSVRecord record) {
        String raw = record.get("contactID");
        if (raw.isBlank()) {
            throw invalid(record, "Contact ID is required");
        }
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException exception) {
            throw invalid(record, "Invalid contact ID: " + raw);
        }
    }

    private IllegalArgumentException invalid(CSVRecord record, String message) {
        return new IllegalArgumentException("Record " + record.getRecordNumber() + ": " + message);
    }
}
