package com.compass.challenge;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CompassChallengeApplicationTest {
    @TempDir Path directory;
    private final ByteArrayOutputStream errors = new ByteArrayOutputStream();

    @Test
    void requiresTwoArguments() {
        assertEquals(2, run());
        assertTrue(errors.toString().contains("Usage:"));
    }

    @Test
    void reportsMissingInputWithoutCreatingOutput() {
        Path output = directory.resolve("out.csv");
        assertEquals(1, run(directory.resolve("absent.csv").toString(), output.toString()));
        assertFalse(Files.exists(output));
        assertTrue(errors.toString().contains("Error:"));
    }

    @Test
    void preventsOverwritingInput() throws Exception {
        Path input = Files.writeString(directory.resolve("input.csv"), "original");
        assertEquals(1, run(input.toString(), input.toString()));
        assertEquals("original", Files.readString(input));
    }

    @Test
    void reportsInvalidCsvWithoutCreatingOutput() throws Exception {
        Path input = Files.writeString(directory.resolve("input.csv"), "wrong,header\n");
        Path output = directory.resolve("out.csv");
        assertEquals(1, run(input.toString(), output.toString()));
        assertTrue(errors.toString().contains("Missing CSV header"));
        assertFalse(Files.exists(output));
    }

    private int run(String... args) {
        return CompassChallengeApplication.run(args, new PrintStream(new ByteArrayOutputStream()),
                new PrintStream(errors));
    }
}
