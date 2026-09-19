package com.compass.challenge.matching;

import com.compass.challenge.domain.MatchEvidence;
import com.compass.challenge.domain.NormalizedContact;

import static com.compass.challenge.domain.MatchEvidence.AddressMatch;
import static com.compass.challenge.domain.MatchEvidence.EmailMatch;
import static com.compass.challenge.domain.MatchEvidence.NameMatch;

public final class ContactMatcher {

    public MatchEvidence compare(
            NormalizedContact left,
            NormalizedContact right) {

        return new MatchEvidence(
                compareNames(
                        left.firstName(),
                        right.firstName()
                ),
                compareNames(
                        left.lastName(),
                        right.lastName()
                ),
                compareEmails(left, right),
                sameNonNull(
                        left.postalCode(),
                        right.postalCode()
                ),
                compareAddresses(
                        left.address(),
                        right.address()
                )
        );
    }

    private NameMatch compareNames(
            String left,
            String right) {

        if (left == null || right == null) {
            return NameMatch.NONE;
        }

        if (left.equals(right)) {

            if (left.length() == 1) {
                return NameMatch.INITIAL;
            }

            return NameMatch.EXACT;
        }

        boolean leftIsInitial =
                left.length() == 1
                        && right.startsWith(left);

        boolean rightIsInitial =
                right.length() == 1
                        && left.startsWith(right);

        if (leftIsInitial || rightIsInitial) {
            return NameMatch.INITIAL;
        }

        return NameMatch.NONE;
    }

    private EmailMatch compareEmails(
            NormalizedContact left,
            NormalizedContact right) {

        if (left.email() == null
                || right.email() == null) {

            return EmailMatch.NONE;
        }

        if (left.email().equals(right.email())) {
            return EmailMatch.EXACT;
        }

        if (left.emailLocalPart() != null
                && left.emailLocalPart()
                .equals(right.emailLocalPart())) {

            return EmailMatch.SAME_LOCAL_PART;
        }

        return EmailMatch.NONE;
    }

    private AddressMatch compareAddresses(
            String left,
            String right) {

        if (sameNonNull(left, right)) {
            return MatchEvidence.AddressMatch.EXACT;
        }

        return AddressMatch.NONE;
    }

    private boolean sameNonNull(
            String left,
            String right) {

        return left != null
                && right != null
                && left.equals(right);
    }
}
