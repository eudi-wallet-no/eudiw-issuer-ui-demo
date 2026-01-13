package no.idporten.eudiw.issuer.ui.demo.byob;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.idporten.eudiw.issuer.ui.demo.byob.model.CredentialDefinition;
import no.idporten.eudiw.issuer.ui.demo.issuer.config.CredentialConfiguration;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ByobService {
   private final Map<String, CredentialDefinition> customCredentialDefinitions = new HashMap<>();

    public Map<String, CredentialDefinition> getCustomCredentialDefinitions() {
        return customCredentialDefinitions;
    }

    public void addCustomCredentialDefinition(CredentialDefinition cd) {
        customCredentialDefinitions.put(cd.getVct(), cd);
    }

    public CredentialDefinition getCustomCredentialDefinition(String key) {
        return customCredentialDefinitions.get(key);
    }

    public void removeCustomCredentialDefinition(String key) {
        customCredentialDefinitions.remove(key);
    }

    public List<CredentialConfiguration> getCredentialConfigurations() {
        Collection<CredentialDefinition> credentialDefinitions = getCustomCredentialDefinitions().values();

        ObjectMapper objectMapper = new ObjectMapper();

        return credentialDefinitions.stream().map(cd -> {
            try {
                return new CredentialConfiguration(cd.getVct(), "", "16903349844", cd.getVct(), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cd), false);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
}
