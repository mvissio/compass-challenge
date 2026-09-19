package com.compass.challenge.csv;

import com.compass.challenge.domain.Confidence;
import com.compass.challenge.domain.MatchResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvMatchWriterTest {
    @TempDir Path directory;
    private final CsvMatchWriter writer = new CsvMatchWriter();

    @Test
    void writesHeaderAndAllResultFieldsAndCreatesParent() throws Exception {
        Path output = directory.resolve("nested/matches.csv");
        writer.write(output, List.of(new MatchResult(1, 2, 77, Confidence.HIGH)));
        assertEquals(List.of("sourceContactId,matchedContactId,score,accuracy", "1,2,77,HIGH"),
                Files.readAllLines(output));
    }

    @Test
    void emptyResultsStillProduceAHeader() throws Exception {
        Path output = directory.resolve("matches.csv");
        writer.write(output, List.of());
        assertEquals(List.of("sourceContactId,matchedContactId,score,accuracy"), Files.readAllLines(output));
    }
}
