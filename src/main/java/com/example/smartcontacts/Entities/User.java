package com.example.smartcontacts.Entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "USER")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    private String role;
    private boolean statusEnabled; // Login status
    @NotBlank(message = "User Name can not be empty")
    @Size(min = 3, max = 25, message = "Name must be between 3-25 characters")
    private String name;
    @Column(unique = true)
    @Email(regexp = "^[0-9a-zA-Z._-]+@[0-9a-zA-Z._-]+\\.[a-zA-Z._-]{2,}$", message = "Must be a well-formed email address")
    private String email;
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be of 10 digits")
    private String phone;
    // @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character")
    private String password;
    private String image;
    @Column(length = 500)
    private String about;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user") // Fetch lazy
    private List<Contacts> contacts = new ArrayList<>();

    public User() {
    }
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public boolean isStatusEnabled() {
        return statusEnabled;
    }
    public void setStatusEnabled(boolean statusEnabled) {
        this.statusEnabled = statusEnabled;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getAbout() {
        return about;
    }
    public void setAbout(String about) {
        this.about = about;
    }
    public List<Contacts> getContacts() {
        return contacts;
    }
    public void setContacts(List<Contacts> contacts) {
        this.contacts = contacts;
    }
    @Override
    public String toString() {
        return "User [userId=" + userId + ", role=" + role + ", statusEnabled=" + statusEnabled + ", name=" + name
                + ", email=" + email + ", phone=" + phone + ", password=" + password + ", image=" + image + ", about="
                + about + ", contacts=" + contacts + "]";
    }

    
}
