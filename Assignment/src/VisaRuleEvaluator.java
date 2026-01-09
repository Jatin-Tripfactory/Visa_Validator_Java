package Visa_Validator_Java.Assignment.src;

import java.util.List;
import java.util.Collections;

class VisaDecision {

    private final boolean visaRequired;
    private final VisaType type;
    private final List<DocumentType> documents;
    private final int estimatedProcessingDays;
    private final List<String> warnings;

    public VisaDecision(
        boolean visaRequired,
        VisaType type,
        List<DocumentType> documents,
        int estimatedProcessingDays,
        List<String> warnings
    ) {
        this.visaRequired = visaRequired;
        this.type = type;
        this.documents = documents;
        this.estimatedProcessingDays = estimatedProcessingDays;
        this.warnings = warnings;
    }

    public boolean isVisaRequired() {
        return visaRequired;
    }

    public VisaType getVisaType() {
        return type;
    }

    public List<DocumentType> getDocuments() {
        return documents;
    }

    public int getEstimatedProcessingDays() {
        return estimatedProcessingDays;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}

public class VisaRuleEvaluator {

    private final RuleRepository repo;

    public VisaRuleEvaluator(RuleRepository repo) {
        this.repo = repo;
    }

    public VisaDecision evaluator(
        DestinationCountry destinationCountry,
        PassportCountry passportCountry,
        TravelPurpose purpose,
        int stayDuration
    ) {

        List<VisaRuleConfig> rules =
            repo.findRule(passportCountry, destinationCountry, purpose);

        // Since the visaRequired field is boolean, ambigous situations are set to false and warnings are given.
        // Case 1: When no rules are found, visaRequired set to false, warning raised.
        if (rules.isEmpty()) {
            return new VisaDecision(
                false,
                null,
                Collections.emptyList(),
                0,
                List.of(
                    "No visa rule found for the given input",
                    "Manual verification required"
                )
            );
        }

        // Case 2: When more than one rule is found, it would mean there exists a conflict,
        // so visaRequired set to false and warning raised.
        if (rules.size() > 1) {
            return new VisaDecision(
                false,
                null,
                Collections.emptyList(),
                0,
                List.of(
                    "Conflicting visa rules detected",
                    "Manual review required"
                )
            );
        }

        // Case 3: Exactly one rule.
        VisaRuleConfig rule = rules.get(0);

        // If stayDuration exceeds the max allowed days, a warning is raised.
        if (stayDuration > rule.getMaxstayDays()) {
            return new VisaDecision(
                false,
                null,
                Collections.emptyList(),
                0,
                List.of(
                    "Requested stay duration exceeds maximum allowed stay of "
                        + rule.getMaxstayDays() + " days",
                    "Manual verification required"
                )
            );
        }

        return new VisaDecision(
            rule.isRequired(),
            rule.isRequired() ? rule.getVisaType() : null,
            rule.getDocuments(),
            rule.getProcessingDays(),
            rule.getWarnings()
        );
    }
}
