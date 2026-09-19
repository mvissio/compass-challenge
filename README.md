# Contact Duplicate Finder

Java command-line application created for the **Compass Find Duplicates coding assessment**. It identifies potential duplicate contacts, assigns each pair a score and confidence level, and processes all data in memory.

## Requirements

- JDK 25
- Gradle Wrapper included

## Run

The assessment input is available as `contacts-input.csv` in the project root.

```bash
./gradlew run --args="contacts-input.csv matches.csv"
```

The result is written to `matches.csv`. Input and output must be different files.

## Input and output

Expected CSV headers:

```csv
contactID,name,name1,email,postalZip,address
```

Generated output:

```csv
sourceContactId,matchedContactId,score,accuracy
1,501,80,HIGH
```

Each pair appears once, with the smaller contact ID first.

## Scoring

| Evidence | Points |
|---|---:|
| Exact email | 40 |
| Same email local part | 25 |
| Exact last name / compatible initial | 15 / 7 |
| Exact first name / compatible initial | 10 / 5 |
| Exact normalized address | 25 |
| Exact postal code | 10 |

A pair needs at least 10 points and either email/address evidence or compatible first and last names. A postal-code match alone is not sufficient.

| Score | Confidence |
|---|---|
| 75–100 | HIGH |
| 45–74 | MEDIUM |
| Below 45 | LOW |

The score is a heuristic confidence value, not a probability.

## Tests

```bash
./gradlew clean test
```

Tests cover normalization, matching, scoring, duplicate detection, CSV processing and the complete application flow.

## Author

Marcos Vissio
