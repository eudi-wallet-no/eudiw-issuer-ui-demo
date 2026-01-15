package no.idporten.eudiw.issuer.ui.demo.certificates;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.idporten.eudiw.issuer.ui.demo.byob.ByobService;
import no.idporten.eudiw.issuer.ui.demo.byob.model.CredentialDefinition;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateService {
    private final ByobService byobService;
    private final CredentialMapper mapper;

    public CertificateService(ByobService byobService, CredentialMapper mapper) {
        this.byobService = byobService;
        this.mapper = mapper;
    }

    public List<CertificateDto> getCertificates() {
        List<CredentialDefinition> credentialDefinitions = byobService.getCustomCredentialDefinitions();

        return credentialDefinitions.stream().map(cd -> {
            try {
                return mapper.toDto(cd);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public CertificateDto findCertificate(String cvt) throws JsonProcessingException {
        CredentialDefinition cd = byobService.getCredentialDefinitionByCvt(cvt);
        return mapper.toDto(cd);
    }

    public void storeCertificate(CertificateDto certificateDto) throws JsonProcessingException {
        CredentialDefinition cd =  mapper.fromDto(certificateDto);

        cd.setVct(certificateDto.vct());
        byobService.addCredentialDefinition(cd);
    }

    public void deleteCertificate(String cvt) {
        byobService.removeCustomCredentialDefinition(cvt);
    }
}
