package no.idporten.eudiw.issuer.ui.demo.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import no.idporten.eudiw.issuer.ui.demo.byob.CredentialDefinitionFactory;
import no.idporten.eudiw.issuer.ui.demo.byob.model.CredentialDefinition;
import no.idporten.eudiw.issuer.ui.demo.certificates.CertificateDto;
import no.idporten.eudiw.issuer.ui.demo.certificates.CertificateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CertificateService certificateService;

    public AdminController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }


    @GetMapping("/admin")
    public ModelAndView admin() {
        return new ModelAndView("admin", "certificates", certificateService.getCertificates());
    }

    @GetMapping("/add-credential")
    public ModelAndView addCredential() throws JsonProcessingException {
        CredentialDefinition empty = CredentialDefinitionFactory.empty();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(empty);

        return new ModelAndView("add", "addCredentialForm", new AddCredentialForm(json));
    }

    @PostMapping("/add-credential")
    public ModelAndView addNewCredential(@Valid AddCredentialForm addCredentialForm, BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) {
            System.out.println("BindingResult errors: " + bindingResult.getAllErrors());
            return new ModelAndView("add", "addCredentialForm", addCredentialForm);
        }

        certificateService.storeCertificate(new CertificateDto(addCredentialForm.vct(), addCredentialForm.json()));

        return new ModelAndView("redirect:/admin", "certificates", certificateService.getCertificates());
    }

    @GetMapping("/edit-credential/{credential_configuration_id}")
    public ModelAndView editCredential(@PathVariable("credential_configuration_id") String credentialConfigurationId) throws JsonProcessingException {
        logger.info("Editing credential with id {}", credentialConfigurationId);
        CertificateDto cd = certificateService.findCertificate(credentialConfigurationId);
        return new ModelAndView("add", "addCredentialForm", new AddCredentialForm(credentialConfigurationId, cd.name(), cd.json()));
    }

    @GetMapping("/delete-credential/{credential_configuration_id}")
    public ModelAndView deleteCredential(@PathVariable("credential_configuration_id") String credentialConfigurationId) {
        logger.info("Deleting credential with id {}", credentialConfigurationId);

        certificateService.deleteCertificate(credentialConfigurationId);
        return new ModelAndView("redirect:/admin", "certificates", certificateService.getCertificates());
    }
}
