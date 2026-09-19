package com.compass.challenge.csv;

import com.compass.challenge.domain.MatchResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CsvMatchWriter {
    public void write(Path output, List<MatchResult> matches) throws IOException {
        Path parent = output.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        var format = CSVFormat.DEFAULT.builder()
                .setHeader("sourceContactId", "matchedContactId", "score", "accuracy")
                .build();
        try (var writer = Files.newBufferedWriter(output);
             var printer = new CSVPrinter(writer, format)) {
            for (MatchResult match : matches) {
                printer.printRecord(match.sourceContactId(), match.matchedContactId(),
                        match.score(), match.confidence());
            }
        }
    }
}
