package com.edusphere.dto;

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String dateOfBirth;
    private String phone;

    public RegisterRequest(){}

    public RegisterRequest(String name,
                           String email,
                           String password,
                           String dateOfBirth,
                           String phone){
        this.name=name;
        this.email=email;
        this.password=password;
        this.dateOfBirth=dateOfBirth;
        this.phone=phone;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
