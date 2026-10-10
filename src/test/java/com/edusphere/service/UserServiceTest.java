package com.edusphere.service;

import com.edusphere.model.User;

public class UserServiceTest {
    public static void main(String[] args){
        UserService userService=new UserService();

        try {
            User user=userService.registerUser("Shah",
                    "shams@hotmail.com",
                          "shams12344$");
            System.out.println("Registration successful");
            System.out.println(
                    "ID: " + user.getId()
            );

            System.out.println(
                    "Name: " + user.getName()
            );

            System.out.println(
                    "Email: " + user.getEmail()
            );

            System.out.println(
                    "Role: " + user.getRole()
            );

            System.out.println(
                    "Password hash: " +
                            user.getPasswordHash()
            );
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
