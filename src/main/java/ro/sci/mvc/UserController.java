package ro.sci.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import ro.sci.ems.domain.User;
import ro.sci.ems.exception.ValidationException;
import ro.sci.ems.service.UserService;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/register")
public class UserController {

    private static Logger LOGGER = LoggerFactory.getLogger("UserController");

    @Autowired
    private UserService userService;

    @RequestMapping("")
    public ModelAndView add() {
        ModelAndView modelAndView = new ModelAndView("register/add");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }


    @RequestMapping("/save")
    public ModelAndView save(@Valid User user,
                             BindingResult bindingResult) throws SQLException {

        ModelAndView modelAndView = new ModelAndView();
        User existing = userService.findByEmail(user.getEmail());

        if (existing != null){
            bindingResult.rejectValue("email", null, "There is already an account registered with that email");
        }

        if (bindingResult.hasErrors()){
            List<String> errors = new LinkedList<>();

            for (FieldError error :
                    bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ":" + error.getCode());
            }
            modelAndView.addObject("errors", errors);
            RedirectView redirectView = new RedirectView("/register?error");
            modelAndView.setView(redirectView);
        }

        if (!bindingResult.hasErrors()) {
            try {
                userService.save(user);
                RedirectView redirectView = new RedirectView("/login?success");
                modelAndView.setView(redirectView);
            } catch (ValidationException ex) {

                LOGGER.error("validation error", ex);

                List<String> errors = new LinkedList<>();
                errors.add(ex.getMessage());
                modelAndView = new ModelAndView("register/add");
                modelAndView.addObject("errors", errors);
                modelAndView.addObject("user", user);
            }

        } else {
            List<String> errors = new LinkedList<>();

            for (FieldError error :
                    bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ":" + error.getCode());
            }

            modelAndView = new ModelAndView("register/add");
            modelAndView.addObject("errors", errors);
            modelAndView.addObject("user", user);
        }

        return modelAndView;
    }
}
