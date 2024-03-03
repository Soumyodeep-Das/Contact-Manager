package com.example.smartcontacts.Controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.smartcontacts.DAO.UserRepository;
import com.example.smartcontacts.Entities.User;
import com.example.smartcontacts.Helper.Message;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;



@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String homepageHandler(Model model){
        model.addAttribute("title", "SCM");
        return "home";
    }

    @GetMapping("/about")
    public String aboutpageHandler(Model model){
        model.addAttribute("title", "About Page");
        return "about";
    }

    @GetMapping("/signup")
    public String signupPageHandler(Model model, HttpSession session){
        model.addAttribute("title", "Sign Up");
        model.addAttribute("user", new User());

        if (session.getAttribute("message") != null) {
            model.addAttribute("sessionMessage", session.getAttribute("message"));
            session.removeAttribute("message");
        }
        return "signup";
    }

    @GetMapping("/signin")
    public String loginPageHandler(Model model, HttpSession session){
        model.addAttribute("title", "Log In");
        // model.addAttribute("user", new User());

        // if (session.getAttribute("message") != null) {
        //     model.addAttribute("sessionMessage", session.getAttribute("message"));
        //     session.removeAttribute("message");
        // }
        return "login";
    }

    // register_form
    @PostMapping("/register_form")
    public String resgisterFormHandler(
        @Valid
        @ModelAttribute("user") User user,
        BindingResult result, 
        @RequestParam("userProfile") MultipartFile file, 
        @RequestParam(value = "agreed", defaultValue = "false") boolean agreed, 
        Model model,
        HttpSession session
        ) {
        try {
            if(result.hasErrors()){
                System.out.println(result.toString());
                model.addAttribute("user", user);
                return "signup";
            }
            if (!agreed){
                System.out.println("Not Agreed to the T&C.");
                throw new Exception("Not Agreed to the T&C.");
            }
            if (file.isEmpty()) {
                // If the file is empty then send error message
                System.out.println("File not found");
                user.setImage("default.jpeg");
            } else {
                // save the file to folder and update the name to contact
                user.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/images").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image is uploaded");

            }
            user.setRole("ROLE_USER");
            user.setStatusEnabled(true);
            // user.setImage("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
    
            User dbUser = this.userRepository.save(user);
            model.addAttribute("user", dbUser);
            System.out.println(dbUser);
            session.setAttribute("message", new Message("Successfully Registered", "alert-success"));
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something Went Wrong\n"+(e.getMessage().length() > 100 ? e.getMessage().substring(0, 100) : e.getMessage()), "alert-danger"));
        }
        return "signup";
    }

    

}
