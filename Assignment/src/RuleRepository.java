package Visa_Validator_Java.Assignment.src;

import java.util.List;
import java.util.Map;
class InvalidConfigException extends RuntimeException{
    InvalidConfigException(String msg){
        super(msg);
    }
}

class ConfigValidator {
    // Basic checks have been added. More checks can be added later on
    public void validate(VisaConfig config) {
        if (config == null || config.getCountries() == null || config.getCountries().isEmpty()) {
            throw new InvalidConfigException("No countries defined");
        }

        for (var countryEntry : config.getCountries().entrySet()) {
            DestinationCountry country = countryEntry.getKey();
            CountryVisaConfig countryConfig = countryEntry.getValue();

            validateCountry(country, countryConfig);
        }
    }

    private void validateCountry(DestinationCountry country, CountryVisaConfig countryConfig) {
        if (countryConfig == null || countryConfig.getPassportRules() == null) {
            throw new InvalidConfigException(
                "No passport rules for country: " + country
            );
        }

        for (var passportEntry : countryConfig.getPassportRules().entrySet()) {
            PassportCountry passport = passportEntry.getKey();
            Map<TravelPurpose, VisaRuleConfig> purposes = passportEntry.getValue();

            validatePassportRules(country, passport, purposes);
        }
    }

    private void validatePassportRules(
        DestinationCountry country,
        PassportCountry passport,
        Map<TravelPurpose, VisaRuleConfig> purposes
    ) {
        if (purposes == null || purposes.isEmpty()) {
            throw new InvalidConfigException(
                "No travel purposes for passport " + passport + " in " + country
            );
        }

        for (var purposeEntry : purposes.entrySet()) {
            TravelPurpose purpose = purposeEntry.getKey();
            VisaRuleConfig rule = purposeEntry.getValue();

            validateRule(country, passport, purpose, rule);
        }
    }

    private void validateRule(
        DestinationCountry country,
        PassportCountry passport,
        TravelPurpose purpose,
        VisaRuleConfig rule
    ) {


        if (rule.getProcessingDays() < 0) {
            throw new InvalidConfigException(
                "Negative processingDays for " + country + " / " + passport + " / " + purpose
            );
        }

        if (rule.getMaxstayDays() <= 0) {
            throw new InvalidConfigException(
                "Invalid maxstayDays for " + country + " / " + passport + " / " + purpose
            );
        }

        if (rule.isRequired() && rule.getVisaType() == null) {
            throw new InvalidConfigException(
                "Visa required but visaType missing for "
                + country + " / " + passport + " / " + purpose
            );
        }

        if (rule.getDocuments() == null || rule.getDocuments().isEmpty()) {
            throw new InvalidConfigException(
                "No documents specified for "
                + country + " / " + passport + " / " + purpose
            );
        }
    }
}


public class RuleRepository {

    private final VisaConfig config;
    private final ConfigValidator validator = new ConfigValidator();
    public RuleRepository(VisaConfig config) {
        validator.validate(config);
        this.config = config;
    }
    

public List<VisaRuleConfig> findRule(
    /* This function takes the inputs and finds the matching rules for them. Returns a List of all matching rules */
    PassportCountry passportCountry,
    DestinationCountry destination,
    TravelPurpose purpose
) {
    CountryVisaConfig countryConfig = config.getCountries().get(destination);
    if (countryConfig == null) {
        return List.of();
    }

    Map<TravelPurpose, VisaRuleConfig> purposes =
        countryConfig.getPassportRules().get(passportCountry);
    if (purposes == null) {
        return List.of();
    }

    VisaRuleConfig rule = purposes.get(purpose);
    if (rule == null) {
        return List.of();
    }


    return List.of(rule);
}

}
