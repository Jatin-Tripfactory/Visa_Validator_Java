package Visa_Validator_Java.Assignment.src;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class VisaRuleEvaluatorTest {

    public static void main(String[] args) throws Exception {

        RuleLoader loader =
            new RuleLoader(Paths.get("src/visa-rules.yaml"));

        RuleRepository repo =
            new RuleRepository(loader.load());

        VisaRuleEvaluator evaluator =
            new VisaRuleEvaluator(repo);

        testValidRule(evaluator);
        testNoRuleFound(evaluator);
        testConflict();
        testMissingConfig();

        System.out.println("\nALL TESTS PASSED");
    }

    // ---------------------------------------------
    // Test 1: Valid rule match
    // ---------------------------------------------
    static void testValidRule(VisaRuleEvaluator evaluator) {

        VisaDecision decision =
            evaluator.evaluator(
                DestinationCountry.JP,
                PassportCountry.IN,
                TravelPurpose.TOURISM,
                30
            );

        assert decision.isVisaRequired();
        assert decision.getVisaType() == VisaType.TOURIST;
        assert decision.getEstimatedProcessingDays() == 7;
        assert decision.getDocuments().size() == 2;
    }

    // ---------------------------------------------
    // Test 2: No rule found
    // ---------------------------------------------
    static void testNoRuleFound(VisaRuleEvaluator evaluator) {

        VisaDecision decision =
            evaluator.evaluator(
                DestinationCountry.IN,
                PassportCountry.JP,
                TravelPurpose.BUSINESS,
                10
            );

        assert !decision.isVisaRequired();
        assert decision.getWarnings().size() > 0;
    }

    // ---------------------------------------------
    // Test 3: Conflicting rules
    // ---------------------------------------------
    static void testConflict() {

        VisaRuleConfig r1 = new VisaRuleConfig();
        r1.setRequired(true);

        VisaRuleConfig r2 = new VisaRuleConfig();
        r2.setRequired(false);

        RuleRepository fakeRepo =
            new RuleRepository(minimalValidConfig()) {

                @Override
                public List<VisaRuleConfig> findRule(
                    PassportCountry p,
                    DestinationCountry d,
                    TravelPurpose t
                ) {
                    return List.of(r1, r2);
                }
            };

        VisaRuleEvaluator evaluator =
            new VisaRuleEvaluator(fakeRepo);

        VisaDecision decision =
            evaluator.evaluator(
                DestinationCountry.JP,
                PassportCountry.IN,
                TravelPurpose.TOURISM,
                20
            );

        assert !decision.isVisaRequired();
        assert decision.getWarnings().contains("Conflicting visa rules detected");
    }

    // ---------------------------------------------
    // Test 4: Missing config fields
    // ---------------------------------------------
    static void testMissingConfig() {

        try {
            new RuleRepository(new VisaConfig());
            throw new AssertionError("Expected InvalidConfigException");
        } catch (InvalidConfigException e) {
            // expected
        }
    }

    // ---------------------------------------------
    // Minimal valid config for tests
    // ---------------------------------------------
    static VisaConfig minimalValidConfig() {

        VisaRuleConfig rule = new VisaRuleConfig();
        rule.setRequired(true);
        rule.setVisaType(VisaType.TOURIST);
        rule.setDocuments(List.of(DocumentType.PASSPORT));
        rule.setProcessingDays(1);
        rule.setMaxstayDays(30);
        rule.setWarnings(List.of());

        CountryVisaConfig countryVisaConfig = new CountryVisaConfig();
        countryVisaConfig.setPassportRules(Map.of(
            PassportCountry.IN,
            Map.of(
                TravelPurpose.TOURISM,
                rule
            )
        ));

        VisaConfig config = new VisaConfig();
        config.setCountries(Map.of(
            DestinationCountry.JP,
            countryVisaConfig
        ));

        return config;
    }
}
