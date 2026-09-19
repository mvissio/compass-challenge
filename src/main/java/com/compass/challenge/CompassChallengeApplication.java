package com.compass.challenge;

import com.compass.challenge.application.DuplicateDetectionService;
import com.compass.challenge.csv.CsvContactReader;
import com.compass.challenge.csv.CsvMatchWriter;
import com.compass.challenge.matching.ContactMatcher;
import com.compass.challenge.matching.ContactNormalizer;
import com.compass.challenge.matching.MatchScorer;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CompassChallengeApplication {
    private CompassChallengeApplication() {}

    static void main(String[] args) {
        String[] effectiveArgs = args.length == 0
                ? new String[]{"contacts-input.csv", "matches.csv"}
                : args;

        int exitCode = run(effectiveArgs, System.out, System.err);

        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    static int run(String[] args, PrintStream out, PrintStream err) {
        if (args.length != 2) {
            err.println("Usage: ./gradlew run --args=\"contacts.csv matches.csv\"");
            return 2;
        }

        try {
            Path input = Path.of(args[0]).toAbsolutePath().normalize();
            Path output = Path.of(args[1]).toAbsolutePath().normalize();
            if (input.equals(output)
                    || (Files.exists(output) && Files.isSameFile(input, output))) {
                throw new IllegalArgumentException("Input and output must be different files");
            }

            var service = new DuplicateDetectionService(
                    new ContactNormalizer(), new ContactMatcher(), new MatchScorer());
            var contacts = new CsvContactReader().read(input);
            var matches = service.findPotentialDuplicates(contacts);
            new CsvMatchWriter().write(output, matches);

            out.printf("Processed %d contacts. Found %d potential duplicate pairs.%n",
                    contacts.size(), matches.size());
            return 0;
        } catch (IOException | UncheckedIOException | IllegalArgumentException exception) {
            err.println("Error: " + exception.getMessage());
            return 1;
        }
    }
}
