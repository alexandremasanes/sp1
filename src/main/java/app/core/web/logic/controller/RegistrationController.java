package app.core.web.logic.controller;

import app.core.business.logic.CustomerService;
import app.core.business.logic.CustomerService.*;
import app.core.web.logic.helper.MessageHelper;
import app.core.web.model.databinding.command.Login;
import app.core.web.model.databinding.command.Registration;
import app.core.web.model.databinding.validation.RegistrationValidator;


import app.util.MessageCode;
import app.util.RedirectView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


/**
 * Created by alexandremasanes on 12/03/2017.
 */
@Controller
@RequestMapping("/inscription")
public class RegistrationController extends GuestController {

    public final static String VIEW_NAME = "registration";

    @Autowired
    private CustomerService       customerService;

    @Autowired
    private MessageHelper         messageHelper;

    @Autowired
    private RegistrationValidator registrationValidator;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getIndex() {
        return render(new ModelMap(new Registration()));
    }

    @RequestMapping(method = RequestMethod.POST)
    public Object signUp(
            @ModelAttribute Registration       registration,
            @ModelAttribute MessageCode        messageCode,
                            Login              login,
                            BindingResult      bindingResult,
                            RedirectAttributes redirectAttributes
    ) {
        if (!login.isEmpty()) {
            registration.setEmailAddress(login.getEmailAddress());
            registration.setPassword(login.getPassword());
            return render(new ModelMap(registration));
        }

        registrationValidator.validate(registration, bindingResult);

        String                  redirectUri;
        HttpStatus              httpStatus;
        RegistrationResult      registrationResult;

        httpStatus = HttpStatus.BAD_REQUEST;

        if (!bindingResult.hasErrors()) {
            registrationResult = customerService.preRegister(registration);

            if (registrationResult.isSuccessful()) {
                redirectUri = getWebroot();
                httpStatus = HttpStatus.CREATED;
                messageCode.setValue(registrationResult.getMessageCode());
                redirectAttributes.addFlashAttribute(messageCode);
                return new RedirectView(redirectUri, httpStatus);

            } else
                messageCode.setValue(registrationResult.getMessageCode());
        }

        ModelAndView modelAndView = render();
        modelAndView.setStatus(httpStatus);
        return modelAndView;
    }
    /*
    @RequestMapping(params = "login", method = RequestMethod.POST)
    public ModelAndView getIndexFromLoginPage(@ModelAttribute Registration registration) {
        return getIndex(registration);
    } */

    @RequestMapping(value = "/validation", method = RequestMethod.POST)
    public RedirectView validateNewAccount(
            @RequestParam String             validationCode,
                          RedirectAttributes redirectAttributes
    ) throws NoSuchAlgorithmException, IOException {
        MessageCode messageCode;
        messageCode = new MessageCode(customerService.register(validationCode) ?
                "notfication.emailAddressValidation.success" :
                "notfication.emailAddressValidation.failure");

        redirectAttributes.addFlashAttribute(messageCode);
        return new RedirectView(getWebroot());
    }

    @Override
    protected String getViewName() {
        return VIEW_NAME;
    }
}