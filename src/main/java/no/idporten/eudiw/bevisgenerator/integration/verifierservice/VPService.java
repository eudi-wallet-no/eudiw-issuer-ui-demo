package no.idporten.eudiw.bevisgenerator.integration.verifierservice;

import no.idporten.eudiw.bevisgenerator.integration.verifierservice.model.VerificationStartResponse;
import org.springframework.stereotype.Service;

@Service
public class VPService implements VerifierService {

    @Override
    public VerificationStartResponse startVerification(String dcqlQuery) {
        return new VerificationStartResponse(
                "eudi-openid4vp://abr.vc.local?client_id=x509_hash:x5fcy9RPCTxkurc1sSJaO-ZWA72SLnVvLMSMVgyzIJQ&request_uri=http://localhost:9285/openid4vp/authz-request/idporten-login/35178b96-2e7b-4170-8425-9d6f338a1d1a?flow=same_device",
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMgAAADIAQAAAACFI5MzAAADP0lEQVR4Xu2XQY6rOhBFCzHwjGwAiW145i2FDRDYANkSM28DiQ3AzANEvWOSTvf/0hukMntqhKKEg2K7qnxvWfRvl/z/wev6Jf8kWUWubhn93uosobzHXaQxk033zteVn29p70Qu037TD8hUt1quQbqwDFrDr+4jwtdVuOWSpHL550eEpU/lKuUR5Bb/M87bRFk04eRmnFncz+i8TZhsG+uf94/MvU0eF7Me+Hs+Y3M+MJJVWHR99XKVUtNeEIxnDCzk8M026erninCmpXfL+py1iYhUgbhqH6hE7YVXHrM2EX8O4pf7WY+V34v0NYP3yaZzmxrWvelyUD6u7F977n1yhHL0zShNLzqoFInR7IT8sOe2RDjrNqENzfCctYVsU9kH9gfbF13ZxRMGNZNDdjTgknRIUijJee0SE/FkWNeA+C33Kef8Gp7jWIjMXb4RP73H+qYizk62tIyhVEWPyXbOT/8R2dtUjkGKuFOPROIyPWJgIUdosIuO6pYsgYfT8VWJ7xNNdSeLRl0dsleOWRses7aQVZaRiDq2nR6uOWTJomUlVHQxzVU2jfKemsOTHzshBqujorOnEYzLuZvN5PQfankuolzwxkgM7CTrSpxRZcbpA5KAzKiZ4IfieJzFmOmjzfLMj4VssdQoIliQat5zVKWd8N8smiQjgdtESZbf67GQpQ+5b0KuVl+uPvuGmRyecKLEpAVvRGmWb298n6BSQ8J89s5lbxxzU2AnW+6YlvsJ24i05NHMRGPD0gdlNPy/vkTFjsxko5BZt+iYq1vopKov5TOQ1Z8hxM104bPCf9xjHAvZaKhDLZ5sIzB1OxFXO6GWDwirn1DluphEgp1QO5Urs2+gMW5HElpetxKeHZS2QwnoTXBI6b6d6W2y8pX80Cd6muv5os0a7ATD6WkEpmWYyA8xoPG0E/pWaodICI2YzHQoH5EszNLlE1iNtBznGcVM6P1vkfyUFHjH5uMI9dxzFnKe56SdmmyJCQlstk/IVOe2+uGNmhW6iI9ZG0lLRUvNgyHRJ+owvcYxEQ4QuRHLbV1F7y8fEOWUQ6oxNLrOZYi0n3aSz+iyF4oe8Eqe/pc3Wshfrl/yS/L1B0gTANXQA4dBAAAAAElFTkSuQmCC",
                "a28da391-5cda-4c3d-a882-fd30657c3ed7");
    }
}
