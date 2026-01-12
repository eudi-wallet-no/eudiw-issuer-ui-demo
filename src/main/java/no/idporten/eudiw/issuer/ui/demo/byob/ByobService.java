package no.idporten.eudiw.issuer.ui.demo.byob;

import no.idporten.eudiw.issuer.ui.demo.byob.model.CredentialDefinition;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    public boolean removeCustomCredentialDefinition(String key) {
        return customCredentialDefinitions.remove(key) != null;
    }
}
