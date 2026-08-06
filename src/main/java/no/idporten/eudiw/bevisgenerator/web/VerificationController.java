package no.idporten.eudiw.bevisgenerator.web;

import jakarta.validation.Valid;
import net.minidev.json.JSONObject;
import no.idporten.eudiw.bevisgenerator.exception.IssuerUiException;
import no.idporten.eudiw.bevisgenerator.integration.byobservice.model.Display;
import no.idporten.eudiw.bevisgenerator.integration.issuerserver.IssuerServerService;
import no.idporten.eudiw.bevisgenerator.integration.issuerserver.config.CredentialConfiguration;
import no.idporten.eudiw.bevisgenerator.integration.issuerserver.config.IssuerServerProperties;
import no.idporten.eudiw.bevisgenerator.integration.issuerserver.credentialdefinitionmodel.ClaimMetadata;
import no.idporten.eudiw.bevisgenerator.integration.issuerserver.credentialdefinitionmodel.CredentialIssuerMetadata;
import no.idporten.eudiw.bevisgenerator.integration.verifierservice.VerifierService;
import no.idporten.eudiw.bevisgenerator.integration.verifierservice.model.CredentialDefinitionDisplayData;
import no.idporten.eudiw.bevisgenerator.integration.verifierservice.model.SelectableClaim;
import no.idporten.eudiw.bevisgenerator.integration.verifierservice.model.VerificationResult;
import no.idporten.eudiw.bevisgenerator.integration.verifierservice.model.VerificationTransactionData;
import no.idporten.eudiw.bevisgenerator.web.models.StartVerificationForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Controller
public class VerificationController {

    private final IssuerServerService issuerServerService;
    private final IssuerServerProperties properties;
    private final VerifierService verifierService;
    private final ObjectMapper objectMapper;

    public VerificationController(
            IssuerServerService issuerServerService,
            IssuerServerProperties properties,
            VerifierService verifierService,
            ObjectMapper objectMapper
    ) {
        this.issuerServerService = issuerServerService;
        this.properties = properties;
        this.verifierService = verifierService;
        this.objectMapper = objectMapper;
    }

    @ModelAttribute("issuerUrl")
    public String issuerUrl() {
        return properties.credentialIssuer();
    }

    @GetMapping("/verification-start")
    public ModelAndView verify() {
        List<CredentialIssuerMetadata> credentialIssuerMetadata = issuerServerService.getAllCredentialIssuerMetadata();
        createViewModelForCredentialIssuerMetadata(credentialIssuerMetadata);
        return baseView(new StartVerificationForm());
    }

    private List<CredentialDefinitionDisplayData> createViewModelForCredentialIssuerMetadata(List<CredentialIssuerMetadata> credentialIssuerMetadata) {
        List<CredentialDefinitionDisplayData> displayDataList = new ArrayList<>();
        for (CredentialIssuerMetadata metadata : credentialIssuerMetadata) {

            for (String key : metadata.credentialConfigurationsSupported().keySet()) {
                no.idporten.eudiw.bevisgenerator.integration.issuerserver.credentialdefinitionmodel.CredentialConfiguration config = metadata.credentialConfigurationsSupported().get(key);
                Display display = config.credentialMetadata().display().stream().findFirst().orElse(null);
                List<ClaimMetadata> claimMetadata = config.credentialMetadata().claims();

                List<SelectableClaim> claims = new ArrayList<>();
                for (ClaimMetadata claim : claimMetadata) {
                    claims.add(new SelectableClaim(
                            claim.display().stream().findFirst().map(Display::name).orElse("No display name found"),
                            String.join(".", claim.path()))
                    );
                }
                JSONObject meta = "dc+sd-jwt".equals(config.format())
                        ? new JSONObject().appendField("vct_values", List.of(config.vct()))
                        : new JSONObject().appendField("doctype_value", config.doctype());

                displayDataList.add(new CredentialDefinitionDisplayData(
                        display != null ? display.name() : "No display name found",
                        metadata.credentialIssuer(),
                        config.format(),
                        meta,
                        claims
                ));
            }
        }
        return displayDataList;
    }

//    @SneakyThrows
//    public JSONObject makeDCQLQuery(CredentialConfiguration credentialConfiguration, String id) {
//        JSONObject credential = new JSONObject()
//                .appendField("id", id)
//                .appendField("format", credentialConfiguration.getFormat())
//                .appendField("meta",
//                        "dc+sd-jwt".equals(credentialConfiguration.getFormat())
//                                ?
//                                new JSONObject().appendField("vct_values", List.of(credentialConfiguration.getVct()))
//                                :
//                                new JSONObject().appendField("doctype_value", credentialConfiguration.getDoctype()))
//                .appendField("claims",
//                        credentialConfiguration.getCredentialMetadata().getClaimsDescriptions().stream()
//                                .map(cd -> new JSONObject().appendField("path", calculatePath(credentialConfiguration.getFormat(), cd)))
//                                .toList());
//        return new JSONObject().appendField("credentials", new JSONArray().appendElement(credential));
//    }
//
//    protected List<String> calculatePath(String credentialFormat, ClaimsDescription claimsDescription) {
//        if ("dc+sd-jwt".equals(credentialFormat)) {
//            return claimsDescription.getPath();
//        }
//        // do not ask for map or list elements in mdoc credentials
//        if (claimsDescription.getPath().size() == 2) {
//            return claimsDescription.getPath();
//        }
//        return claimsDescription.getPath().subList(0, 2);
//    }

    @PostMapping("/verification-start")
    public ModelAndView startVerification(@Valid @ModelAttribute("verificationForm") StartVerificationForm form,
                                          BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return baseView(form);
        }

        VerificationTransactionData verificationTransactionData = verifierService.startVerification(form.dcql());

        redirectAttributes.addFlashAttribute("qrCode", verificationTransactionData.verificationStartResponse().authorizationRequestQrCode());
        redirectAttributes.addFlashAttribute("authorizationRequest", verificationTransactionData.verificationStartResponse().authorizationRequest());
        redirectAttributes.addFlashAttribute("transactionId", verificationTransactionData.verificationStartResponse().verifierTransactionId());
        redirectAttributes.addFlashAttribute("statusUri", verificationTransactionData.statusUri());

        return new ModelAndView("redirect:/verification-presentation");
    }

    @GetMapping("/verification-presentation")
    public ModelAndView verificationPresentation() {
        return new ModelAndView("verification-presentation");
    }

    @GetMapping("/verification-result")
    public ModelAndView verificationResult(String transactionId) {
        if (transactionId == null || transactionId.isBlank()) {
            throw new IssuerUiException("Missing transactionId");
        }

        VerificationResult result = verifierService.retrieveVerificationResult(transactionId);
        return new ModelAndView("verification-result")
                .addObject("result", result)
                .addObject("resultJson", toPrettyJsonString(result));
    }

    private ModelAndView baseView(StartVerificationForm form) {
        List<CredentialConfiguration> credentialConfigurations = new ArrayList<>(issuerServerService.getAll());
        credentialConfigurations.addAll(issuerServerService.getAllSubjectCredentialConfigurations());
        return new ModelAndView("verification-start")
                .addObject("verificationForm", form)
                .addObject("credentialConfigurations", credentialConfigurations);
    }

    private String toPrettyJsonString(VerificationResult result) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result.credentials());
        } catch (JacksonException e) {
            throw new IssuerUiException("Failed to convert verification result to pretty Json string", e);
        }
    }
}
