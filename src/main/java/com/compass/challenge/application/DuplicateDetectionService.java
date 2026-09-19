package com.compass.challenge.application;

import com.compass.challenge.domain.Contact;
import com.compass.challenge.domain.MatchEvidence;
import com.compass.challenge.domain.MatchResult;
import com.compass.challenge.domain.NormalizedContact;
import com.compass.challenge.matching.ContactMatcher;
import com.compass.challenge.matching.ContactNormalizer;
import com.compass.challenge.matching.MatchScorer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class DuplicateDetectionService {

    private final ContactNormalizer normalizer;
    private final ContactMatcher matcher;
    private final MatchScorer scorer;

    public DuplicateDetectionService(
            ContactNormalizer normalizer,
            ContactMatcher matcher,
            MatchScorer scorer) {

        this.normalizer = normalizer;
        this.matcher = matcher;
        this.scorer = scorer;
    }

    public List<MatchResult> findPotentialDuplicates(
            List<Contact> contacts) {

        List<NormalizedContact> normalizedContacts =
                contacts.stream()
                        .map(normalizer::normalize)
                        .toList();

        List<MatchResult> results =
                new ArrayList<>();

        for (int i = 0;
             i < normalizedContacts.size();
             i++) {

            for (int j = i + 1;
                 j < normalizedContacts.size();
                 j++) {

                NormalizedContact left =
                        normalizedContacts.get(i);

                NormalizedContact right =
                        normalizedContacts.get(j);

                MatchEvidence evidence =
                        matcher.compare(left, right);

                int score =
                        scorer.calculate(evidence);

                if (!scorer.isPotentialMatch(
                        evidence,
                        score)) {

                    continue;
                }

                results.add(
                        new MatchResult(
                                Math.min(left.id(), right.id()),
                                Math.max(left.id(), right.id()),
                                score,
                                scorer.confidence(score)
                        )
                );
            }
        }

        return results.stream()
                .sorted(
                        Comparator
                                .comparingLong(
                                        MatchResult::sourceContactId
                                )
                                .thenComparingLong(
                                        MatchResult::matchedContactId
                                )
                )
                .toList();
    }
}