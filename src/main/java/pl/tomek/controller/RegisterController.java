package pl.tomek.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.tomek.model.User;
import pl.tomek.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Controller
public class RegisterController {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String register(Model model)
    {
        model.addAttribute("user",new User());
        return "registerForm";
    }

    @PostMapping("/register")
    public String SaveUser(@Valid @ModelAttribute User user, BindingResult bindingResult,Model model)
    {
        if(bindingResult.hasErrors())
        {
            return "registerForm";
        }
        else
        {
            List<User> all=userRepository.findAll();
            for(User a:all)
            {
             if(user.getLogin().equals(a.getLogin()))
             {
                model.addAttribute("login",a.getLogin());
                 return "registerForm";
             }
             if(user.getEmail().equals(a.getEmail()))
             {
                 model.addAttribute("email",a.getEmail());
                 return "registerForm";
             }

            }

            userRepository.save(user);
            return "succesForm";
        }



    }

}
