package no.idporten.eudiw.issuer.ui.demo.web.models.advancedForm;

import java.util.ArrayList;
import java.util.List;

public record AdvAddCredentialForm(String id, String name, List<AdvClaimForm> claims) {
    public AdvAddCredentialForm() {
        this("", "", new ArrayList<>());
    }
}
