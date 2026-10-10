package com.edusphere.dao;

import com.edusphere.model.User;

public class UserDAOTest {

    public static void main(String args[]){
        UserDAO userDAO=new UserDAO();

        User user=new User();
        user.setName("Sam Test");
        user.setEmail("ahmad@gmail.com");
        user.setPasswordHash("Temporary-Test-Hash");
        user.setRole("STUDENT");

        try {
//            User savedUser=userDAO.createUser(user);
//            System.out.println("User created successfully!");
//            System.out.println("UserID :"+savedUser.getId());

            userDAO.findByEmail("ahmad@gmail.com").ifPresent(foundUser->
                    System.out.println("User name :"+foundUser.getName()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
