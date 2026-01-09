package Visa_Validator_Java.Assignment.src;

import java.util.Map;
import java.util.List;
import java.io.IOException;
import java.nio.file.Path;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

enum VisaType {
    TOURIST,    
    BUSINESS,
    EMPLOYMENT,
    STUDENT
}

enum DestinationCountry {IN, US, JP}

enum PassportCountry {IN, US, JP}

enum TravelPurpose { 
    TOURISM, 
    BUSINESS, 
    STUDY, 
    TRANSIT
}

enum DocumentType {
    PASSPORT,
    PHOTO,
    BANK_STATEMENT,
    FLIGHT_ITINERARY
}

class VisaConfig {
    private Map<DestinationCountry, CountryVisaConfig> countries;

    public Map<DestinationCountry, CountryVisaConfig> getCountries() {
        return countries;
    }

    public void setCountries(Map<DestinationCountry, CountryVisaConfig> countries) {
        this.countries = countries;
    }
}

class CountryVisaConfig{
    private Map<PassportCountry, Map<TravelPurpose, VisaRuleConfig>> passportRules;



    public Map<PassportCountry, Map<TravelPurpose, VisaRuleConfig>> getPassportRules() {
        return passportRules;
        
    }

    public void setPassportRules(Map<PassportCountry, Map<TravelPurpose, VisaRuleConfig>> passportRules) {
        this.passportRules = passportRules;
        
    }
}


class VisaRuleConfig {
    private boolean required;
    private VisaType visaType;
    private List<DocumentType> documents;
    private int processingDays;
    private int maxstayDays;
    private List<String> warnings;
    public boolean isRequired() {
        return required;
    }
    public void setRequired(boolean required) {
        this.required = required;
    }
    public VisaType getVisaType() {
        return visaType;
    }
    public void setVisaType(VisaType visaType) {
        this.visaType = visaType;
    }
    public List<DocumentType> getDocuments() {
        return documents;
    }
    public void setDocuments(List<DocumentType> documents) {
        this.documents = documents;
    }
    public int getProcessingDays() {
        return processingDays;
    }
    public void setProcessingDays(int processingDays) {
        this.processingDays = processingDays;
    }
    public int getMaxstayDays() {
        return maxstayDays;
    }
    public void setMaxstayDays(int maxstayDays) {
        this.maxstayDays = maxstayDays;
    }
    public List<String> getWarnings() {
        return warnings;
    }
    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}

public class RuleLoader {
    /*The loader uses jackson to load the config from a YAML file. All the type checkings, unnmatched values for fields, will be handled by jackson */
    private final Path filePath;
    private final ObjectMapper mapper;

    public RuleLoader(Path filePath){
        this.filePath = filePath;
        this.mapper = new ObjectMapper(new YAMLFactory());
    }

    public VisaConfig load() throws IOException{
        return mapper.readValue(filePath.toFile(), VisaConfig.class);
    }
     
}
