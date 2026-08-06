package no.idporten.eudiw.bevisgenerator.web;

import jakarta.validation.Valid;
import net.minidev.json.JSONObject;
import no.idporten.eudiw.bevisgenerator.exception.IssuerUiException;
import no.idporten.eudiw.bevisgenerator.integration.byobservice.model.Display;
import no.idporten.eudiw.bevisgenerator.integration.issuerserver.IssuerServerService;
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

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        return baseView(new StartVerificationForm());
    }

    private List<CredentialDefinitionDisplayData> createViewModelForCredentialIssuerMetadata(List<CredentialIssuerMetadata> credentialIssuerMetadata) {
        List<CredentialDefinitionDisplayData> displayDataList = new ArrayList<>();
        for (CredentialIssuerMetadata metadata : credentialIssuerMetadata) {

            for (String key : metadata.credentialConfigurationsSupported().keySet()) {
                no.idporten.eudiw.bevisgenerator.integration.issuerserver.credentialdefinitionmodel.CredentialConfiguration config = metadata.credentialConfigurationsSupported().get(key);
                var credentialMetadata = config.credentialMetadata();
                Display display = credentialMetadata != null && credentialMetadata.display() != null
                        ? credentialMetadata.display().stream().findFirst().orElse(null)
                        : null;
                List<ClaimMetadata> claimMetadata = credentialMetadata != null && credentialMetadata.claims() != null
                        ? credentialMetadata.claims()
                        : List.of();

                List<SelectableClaim> claims = new ArrayList<>();
                for (ClaimMetadata claim : claimMetadata) {
                    claims.add(new SelectableClaim(
                            claim.display() != null
                                    ? claim.display().stream().findFirst().map(Display::name).orElse("No display name found")
                                    : "No display name found",
                            claim.path() != null ? claim.path() : List.of())
                    );
                }

                Map<String, Object> meta = new HashMap<>();
                if ("dc+sd-jwt".equals(config.format()) && config.vct() != null) {
                    meta.put("vct_values", List.of(config.vct()));
                } else if (config.doctype() != null) {
                    meta.put("doctype_value", config.doctype());
                }

                String id = normalizeDcqlId(key, displayDataList.size() + 1);

                displayDataList.add(new CredentialDefinitionDisplayData(
                        id,
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

    private static final Pattern NON_ALLOWED = Pattern.compile("[^A-Za-z0-9_-]+");

    static String normalizeDcqlId(String key, int indexFallback) {
        String base = key == null ? "" : NON_ALLOWED.matcher(key).replaceAll("-");
        base = base.replaceAll("^-+|-+$", "");   // trim leading/trailing '-'
        base = base.replaceAll("-{2,}", "-");    // collapse repeated '-'
        if (base.isBlank()) {
            base = "cred-" + indexFallback;      // guaranteed non-empty fallback
        }
        return base;
    }

    @PostMapping("/verification-start")
    public ModelAndView startVerification(@Valid @ModelAttribute("verificationForm") StartVerificationForm form,
                                          BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {
        List<CredentialDefinitionDisplayData> credentialDefinitions = createViewModelForCredentialIssuerMetadata(
                issuerServerService.getAllCredentialIssuerMetadata()
        );

        if (bindingResult.hasErrors()) {
            return baseView(form, credentialDefinitions);
        }

        CredentialDefinitionDisplayData credentialDefinition = credentialDefinitions.stream()
                .filter(definition -> definition.id().equals(form.credentialConfigurationId()))
                .findFirst()
                .orElse(null);

        if (credentialDefinition == null) {
            bindingResult.rejectValue(
                    "credentialConfigurationId",
                    "credentialConfigurationId.invalid",
                    "Ukjend credential configuration"
            );
            return baseView(form, credentialDefinitions);
        }

        String dcql = buildDcql(credentialDefinition, form.selectedClaimPaths());
        VerificationTransactionData verificationTransactionData = verifierService.startVerification(dcql);

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
        List<CredentialDefinitionDisplayData> credentialDefinitions = createViewModelForCredentialIssuerMetadata(
                issuerServerService.getAllCredentialIssuerMetadata()
        );
        return baseView(form, credentialDefinitions);
    }

    private ModelAndView baseView(StartVerificationForm form, List<CredentialDefinitionDisplayData> credentialDefinitions) {
        return new ModelAndView("verification-start")
                .addObject("verificationForm", form)
                .addObject("credentialDefinitions", credentialDefinitions)
                .addObject("credentialDefinitionsJson", toJsonString(credentialDefinitions))
                .addObject("selectedClaimPathsJson", toJsonString(form.selectedClaimPaths()));
    }

    private String toPrettyJsonString(VerificationResult result) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result.credentials());
        } catch (JacksonException e) {
            throw new IssuerUiException("Failed to convert verification result to pretty Json string", e);
        }
    }

    private String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JacksonException e) {
            throw new IssuerUiException("Failed to convert object to Json string", e);
        }
    }

    private String buildDcql(CredentialDefinitionDisplayData definition, List<String> selectedClaimPaths) {
        Set<String> selectedPaths = selectedClaimPaths == null
                ? Set.of()
                : selectedClaimPaths.stream().collect(Collectors.toSet());
        List<Map<String, List<String>>> claims = definition.claims().stream()
                .filter(claim -> selectedPaths.contains(String.join(".", claim.path())))
                .map(claim -> Map.of("path", claim.path()))
                .toList();

        return toJsonString(Map.of(
                "credentials", List.of(Map.of(
                        "id", definition.id(),
                        "format", definition.format(),
                        "meta", definition.meta(),
                        "claims", claims
                ))
        ));
    }
}
