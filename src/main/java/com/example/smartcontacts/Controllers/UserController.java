package com.example.smartcontacts.Controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.smartcontacts.DAO.ContactRepository;
import com.example.smartcontacts.DAO.UserRepository;
import com.example.smartcontacts.Entities.Contacts;
import com.example.smartcontacts.Entities.User;
import com.example.smartcontacts.Helper.Message;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HttpSession session;
    @Autowired
    private ContactRepository contactRepository;

    // method to add common data to Response
    @ModelAttribute
    public void commonMethod(Model model, Principal principal) {
        String username = principal.getName();
        // System.out.println("User Email : "+username);

        User user = userRepository.getUserByUserName(username);
        // System.out.println(user.toString());

        model.addAttribute("user", user);

        // to remove the session msg after showing it
        if (session != null)
            session.removeAttribute("message");
    }

    @GetMapping("/dashboard")
    public String userDashboardHandler(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "users/user-dashboard";
    }

    // add new user
    @GetMapping("/add-contact")
    public String addNewContact(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contacts());
        return "users/add-contact-form";
    }

    // save contacts
    @PostMapping("/process-contact")
    public String saveNewContacts(@ModelAttribute Contacts contact, @RequestParam("profileImage") MultipartFile file,
            Principal principal, HttpSession session) {
        // TODO: process POST request
        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);
            contact.setUser(user);
            user.getContacts().add(contact);

            // proceesing and saving file or image
            if (file.isEmpty()) {
                // If the file is empty then send error message
                System.out.println("File not found");
                contact.setImage("default.jpeg");
            } else {
                // save the file to folder and update the name to contact
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/images").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image is uploaded");

            }
            this.userRepository.save(user);
            System.out.println(contact.toString());

            System.out.println("Added new Contacts");

            // send success msg
            session.setAttribute("message", new Message("New Contact Added Successfully", "success"));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            // ! send unsuccess msg
            session.setAttribute("message", new Message("Something Went Wrong!!\nTry Again.", "danger"));
        }
        return "users/add-contact-form";
    }

    // add new user
    // per page 5 contacts -> pagination
    // current page = o [page]
    @GetMapping("/view-contacts/{page}")
    public String viewAllContact(@PathVariable("page") Integer page, Model model, Principal principal) {
        model.addAttribute("title", "Contacts");
        // *One type
        String name = principal.getName();
        // User user = this.userRepository.getUserByUserName(name);
        // List<Contacts> contactList = user.getContacts();
        // System.out.println(contactList);
        // model.addAttribute("contact-list", contactList);

        // * Another way */

        Pageable pageable = PageRequest.of(page, 5);

        User user = this.userRepository.getUserByUserName(name);
        Page<Contacts> contacts = this.contactRepository.findContactByUser(user.getUserId(), pageable);
        // System.out.println(contacts);
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentpage", page);
        model.addAttribute("totalpages", contacts.getTotalPages());
        return "users/view-contacts";
    }
    /*
     * @GetMapping("/view-contacts/{page}")
     * public String viewAllContact(@PathVariable("page") Integer page, Model model,
     * Principal principal) {
     * model.addAttribute("title", "Contacts");
     * 
     * String name = principal.getName();
     * 
     * // Adjust page number to be zero-based
     * int pageNumber = page - 1;
     * if (pageNumber < 0) {
     * pageNumber = 0; // Ensure the page number is not negative
     * }
     * 
     * Pageable pageable = PageRequest.of(pageNumber, 5);
     * 
     * User user = this.userRepository.getUserByUserName(name);
     * Page<Contacts> contacts =
     * this.contactRepository.findContactByUser(user.getUserId(), pageable);
     * 
     * model.addAttribute("contacts", contacts);
     * model.addAttribute("currentpage", page);
     * model.addAttribute("totalpages", contacts.getTotalPages());
     * 
     * return "users/view-contacts";
     * }
     */
    // Showing specific contact of an user

    @GetMapping("/contact/{cId}")
    public String viewContactById(@PathVariable("cId") Integer cId, Model model, Principal principal) {
        System.out.println("CID :" + cId);

        Optional<Contacts> contactOptional = this.contactRepository.findById(cId);
        Contacts contact = contactOptional.get();

        // find how can access the contacts
        String username = principal.getName();
        User user = this.userRepository.getUserByUserName(username);

        // model.addAttribute("contact", contact);
        if (user.getUserId() == contact.getUser().getUserId()) {
            model.addAttribute("contact", contact);
        } else
            return "users/no-contact";
        return "users/contact-details";
    }

    // Delete user
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId, Model model, Principal principal, HttpSession session)
            throws IOException {
        Optional<Contacts> contactOptional = this.contactRepository.findById(cId);
        Contacts contact = contactOptional.get();

        // find how can access the contacts
        String username = principal.getName();
        User user = this.userRepository.getUserByUserName(username);

        if (user.getUserId() == contact.getUser().getUserId()) {

            // lets delete the profile image of the contact
            if (!contact.getImage().equals("default.jpeg")) {
                File deleteFile = new File("target/classes/static/images/", contact.getImage()); // always use the
                                                                                                 // folder's path where
                                                                                                 // the picture is
                                                                                                 // storing in runtime
                if (deleteFile.exists()) {
                    if (deleteFile.delete()) {
                        System.out.println("Image deleted successfully");
                    } else {
                        System.out.println("Failed to delete the image");
                    }
                } else {
                    System.out.println("Image file not found");
                }
            }
            contact.setUser(null);
            this.contactRepository.delete(contact);
            session.setAttribute("delete",
                    new Message("Contact named " + contact.getName() + " is deleted.", "danger"));
        } else
            return "users/no-contact";
        return "redirect:/user/view-contacts/0";
    }

    @PostMapping("/update/{cId}")
    public String updateContactById(@PathVariable("cId") Integer cId, Model model, Principal principal) {
        System.out.println("CID :" + cId);

        Contacts contact = this.contactRepository.findById(cId).get();

        // find how can access the contacts
        // String username = principal.getName();
        // User user = this.userRepository.getUserByUserName(username);

        model.addAttribute("contact", contact);
        // if (user.getUserId() == contact.getUser().getUserId()) {
        //     model.addAttribute("contact", contact);
        // } else
        //     return "users/no-contact";
        return "users/update-contact";
    }

    // save edited contacts
    @PostMapping("/edited-contact") 
    public String saveEditedContacts(
        @ModelAttribute Contacts contact, 
        @RequestParam("profileImage") MultipartFile file,  
        Principal principal, 
        HttpSession session) {
        // TODO: process POST request
        try {
            // old contact details
            Contacts oldcontactDetails = this.contactRepository.findById(contact.getcId()).get();

            // Image part
            if (!file.isEmpty()) {
                
                // delete old photo
                File deleteFile = new ClassPathResource("static/images/").getFile();
                File tempFile = new File(deleteFile, oldcontactDetails.getImage());
                tempFile.delete();
                // update new photo

                File saveFile = new ClassPathResource("static/images").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                contact.setImage(file.getOriginalFilename());
            } else {
                contact.setImage(oldcontactDetails.getImage());
            }

            User user = this.userRepository.getUserByUserName(principal.getName());

            contact.setUser(user);

            this.contactRepository.save(contact);
            // send success msg
            session.setAttribute("update", new Message("Contact Updated Successfully", "success"));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            // ! send unsuccess msg
            session.setAttribute("update", new Message("Something Went Wrong!!\nTry Again.", "danger"));
        }
        return "redirect:/user/view-contacts/0";
    }


    @GetMapping("/profile")
    public String userProfile() {
        return "users/user-profile";
    }
    



}
