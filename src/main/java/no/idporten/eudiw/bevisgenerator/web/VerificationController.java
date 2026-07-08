package no.idporten.eudiw.bevisgenerator.web;

import no.idporten.eudiw.bevisgenerator.integration.issuerserver.IssuerServerService;
import no.idporten.eudiw.bevisgenerator.integration.issuerserver.config.CredentialConfiguration;
import no.idporten.eudiw.bevisgenerator.web.models.StartVerificationForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class VerificationController {

    private final IssuerServerService issuerServerService;

    public VerificationController(IssuerServerService issuerServerService) {
        this.issuerServerService = issuerServerService;
    }

    @GetMapping("verification-start")
    public ModelAndView verify() {
        List<CredentialConfiguration> credentialConfigurations = issuerServerService.getAll();
        issuerServerService.getAll().addAll(issuerServerService.getAllSubjectCredentialConfigurations());
        return new ModelAndView("verification-start")
                .addObject("verification-form", new StartVerificationForm())
                .addObject("credentialConfigurations", credentialConfigurations);
    }

    @PostMapping("verification-start")
    public ModelAndView startVerification(StartVerificationForm form) {
        // Handle form submission
        return new ModelAndView("verification-start")
                .addObject("verification-form", form);
    }
}
