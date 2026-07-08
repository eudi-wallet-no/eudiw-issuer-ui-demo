package no.idporten.eudiw.bevisgenerator.web.models;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class VerificationController {

    @GetMapping("verification-start")
    public ModelAndView verify() {
        return new ModelAndView("verification-start");
    }
}
