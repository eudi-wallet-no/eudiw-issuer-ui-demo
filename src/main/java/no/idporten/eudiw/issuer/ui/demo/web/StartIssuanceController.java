package no.idporten.eudiw.issuer.ui.demo.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import no.idporten.eudiw.issuer.ui.demo.exception.IssuerUiException;
import no.idporten.eudiw.issuer.ui.demo.issuer.IssuerServerService;
import no.idporten.eudiw.issuer.ui.demo.issuer.config.IssuerServerProperties;
import no.idporten.eudiw.issuer.ui.demo.issuer.domain.IssuanceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
public class StartIssuanceController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(StartIssuanceController.class);

    private final IssuerServerService issuerServerService;

    private final IssuerServerProperties properties;

    @Autowired
    public StartIssuanceController(IssuerServerService issuerServerService, IssuerServerProperties properties) {
        this.issuerServerService = issuerServerService;
        this.properties = properties;
    }

    @ModelAttribute("issuerUrl")
    public String issuerUrl() {
        return properties.getBaseUrl();
    }

    @GetMapping("/")
    public String start(Model model) {
        model.addAttribute("jsonRequest", new JsonRequest(defaultJsonRequest()));
        return "start";
    }

    @PostMapping("/start-issuance")
    public String startIssuance(@ModelAttribute("jsonRequest") JsonRequest jsonRequest, Model model) {

        String normalizedJson = jsonRequest.json().replaceAll("\\s", ""); // TODO add validation
        logger.info(normalizedJson);

        model.addAttribute("request", createRequestTraceing(jsonRequest));

        IssuanceResponse response = issuerServerService.startIssuance(normalizedJson);

        String uri = convertToCredentialOfferUri(response);
        String qrCode = null;
        try {
            qrCode = Base64.getEncoder().encodeToString(createQRCodeImage(uri));
        } catch (IOException | WriterException e) {
            logger.error("Failed to create QRCode for uri=" + uri, e);
            model.addAttribute("error", "Generering av QR kode feila.");
        }

        Issuance issuance = new Issuance(toPrettyJsonString(response), uri, qrCode);
        model.addAttribute("issuance", issuance);
        return "issuer_response";
    }

    private IssuanceRequest createRequestTraceing(JsonRequest jsonRequest) {
        String contentType = "Content-Type: " + MediaType.APPLICATION_JSON;
        String authorization = "Authorization: Bearer [Maskinporten-token]";
        return new IssuanceRequest(jsonRequest.json(), properties.getIssuanceUrl(), authorization, contentType);
    }

    private String convertToCredentialOfferUri(IssuanceResponse response) {
        String jsonString = toJsonString(response);
        String offerEncoded = URLEncoder.encode(jsonString, StandardCharsets.UTF_8);
        String uri = "openid-credential-offer://?credential_offer=" + offerEncoded;
        logger.info("Issuer offer: " + response);
        logger.info("Issuer offer encoded: " + offerEncoded);
        return uri;
    }

    private String toJsonString(IssuanceResponse response) {
        try {
            return objectMapper.writeValueAsString(response.credentialOffer());
        } catch (JsonProcessingException e) {
            throw new IssuerUiException("Failed to convert response to Json string", e);
        }
    }
    private String toPrettyJsonString(IssuanceResponse response) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new IssuerUiException("Failed to convert response to pretty Json string", e);
        }
    }

    private byte[] createQRCodeImage(String text) throws IOException, WriterException {
        int width = 200;
        int height = 200;
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }


    private String defaultJsonRequest() {
        return """
                {
                   "credential_configuration_id": "no.skatteetaten.nnid_mso_mdoc",
                   "claims": [
                     {
                       "name": "norwegian_national_id_number",
                       "value": "12345678901"
                     },
                     {
                       "name": "norwegian_national_id_number_type",
                       "value": "D-nummer"
                     }
                   ]
                 }
                """;
    }

}
