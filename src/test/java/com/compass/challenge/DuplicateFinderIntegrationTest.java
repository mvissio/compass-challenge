package com.compass.challenge;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateFinderIntegrationTest {
    @TempDir Path directory;

    @Test
    void executesTheWholePipelineWithExactResults() throws Exception {
        Path input = directory.resolve("contacts.csv");
        try (var resource = getClass().getResourceAsStream("/contacts-test.csv")) {
            assertNotNull(resource);
            Files.copy(resource, input);
        }
        Path output = directory.resolve("matches.csv");
        var stdout = new ByteArrayOutputStream();
        var stderr = new ByteArrayOutputStream();
        int code = CompassChallengeApplication.run(new String[]{input.toString(), output.toString()},
                new PrintStream(stdout), new PrintStream(stderr));
        assertEquals(0, code, stderr.toString());
        assertEquals(List.of("sourceContactId,matchedContactId,score,accuracy", "1,2,100,HIGH"),
                Files.readAllLines(output));
        assertTrue(stdout.toString().contains("Processed 4 contacts"));
        assertEquals("", stderr.toString());
    }
}
