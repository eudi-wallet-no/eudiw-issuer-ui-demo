package no.idporten.eudiw.bevisgenerator.web;

import no.idporten.eudiw.bevisgenerator.integration.issuerserver.IssuerServerService;
import no.idporten.eudiw.bevisgenerator.integration.issuerserver.config.CredentialConfiguration;
import no.idporten.eudiw.bevisgenerator.integration.issuerserver.config.IssuerServerProperties;
import no.idporten.eudiw.bevisgenerator.integration.issuerserver.domain.CredentialOffer;
import no.idporten.eudiw.bevisgenerator.integration.issuerserver.domain.Grants;
import no.idporten.eudiw.bevisgenerator.integration.issuerserver.domain.IssuanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceView;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class StartIssuanceControllerTest {

    private MockMvc mockMvc;
    private IssuerServerService issuerServerService;
    private CredentialConfiguration credentialConfiguration;

    @BeforeEach
    void setUp() {
        issuerServerService = mock(IssuerServerService.class);
        IssuerServerProperties issuerServerProperties = mock(IssuerServerProperties.class);

        credentialConfiguration = new CredentialConfiguration(
                "http://issuer",
                "no.digdir.eudiw.pid_mso_mdoc",
                "scope",
                "12345678910",
                "PID",
                "{\"a\":\"b\"}"
        );

        when(issuerServerService.getById(credentialConfiguration.credentialConfigurationId())).thenReturn(credentialConfiguration);
        when(issuerServerProperties.issuanceEndpoint()).thenReturn("/api/v1/credential/issuance-transaction");
        when(issuerServerProperties.credentialIssuer()).thenReturn("http://issuer");

        mockMvc = MockMvcBuilders.standaloneSetup(new StartIssuanceController(issuerServerService, issuerServerProperties))
                .setViewResolvers((viewName, locale) -> {
                    InternalResourceView view = new InternalResourceView();
                    view.setUrl("/templates/" + viewName + ".html");
                    return view;
                })
                .build();
    }

    @Test
    void postStartIssuanceAddsIssuedMetadataToModel() throws Exception {
        IssuanceResponse issuanceResponse = new IssuanceResponse(
                new CredentialOffer("http://issuer", List.of(credentialConfiguration.credentialConfigurationId()), new Grants(null, null)),
                "tx-123"
        );
        when(issuerServerService.startIssuance(any(), any())).thenReturn(issuanceResponse);

        mockMvc.perform(post("/start-issuance/{credential_configuration_id}", credentialConfiguration.credentialConfigurationId())
                        .param("json", "{\"a\":\"b\"}")
                        .param("personIdentifier", "12345678910"))
                .andExpect(status().isOk())
                .andExpect(view().name("issuer_response"))
                .andExpect(model().attribute("issuedCredentialConfigurationId", credentialConfiguration.credentialConfigurationId()))
                .andExpect(model().attribute("issuedTransactionId", "tx-123"));
    }
}
