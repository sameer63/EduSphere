package com.edusphere.service;

import com.edusphere.dao.StudentDAO;
import com.edusphere.dao.UserDAO;
import com.edusphere.model.Student;
import com.edusphere.model.User;
import com.edusphere.util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO;

    public UserService(){
        this.userDAO=new UserDAO();
    }

    public User registerStudent(
            String name,
            String email,
            String password,
            LocalDate dateOfBirth,
            String phone
    )throws Exception{
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Name is required."
            );
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required."
            );
        }

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters."
            );
        }

        email = email.trim().toLowerCase();
        name = name.trim();

        /*
         * Check before starting the transaction.
         */
        if (userDAO.findByEmail(email).isPresent()) {

            throw new IllegalArgumentException(
                    "Email is already registered."
            );
        }

        String passwordHash=BCrypt.hashpw(password,BCrypt.gensalt(12));

        User user=new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setRole("STUDENT");
        user.setActive(true);

        try (Connection connection= DBConnection.getConnection()){
            try {
                connection.setAutoCommit(false);

                //1. Create User
                userDAO.createUser(user,connection);

                //2. Create student profile
                Student student=new Student();
                student.setUserId(
                        user.getId()
                );

                student.setDateOfBirth(
                        dateOfBirth
                );

                student.setPhone(
                        phone
                );

                StudentDAO studentDAO=new StudentDAO();
                studentDAO.createStudent(student,connection);

                //Both operations succeeded
                connection.commit();
                return user;
            } catch (Exception e) {
                //something failed
                connection.rollback();
                throw e;
            }

        }catch (SQLException e){
            throw e;
        }catch (Exception e) {

            throw new SQLException(
                    "Student registration failed.",
                    e
            );
        }
    }

    public User registerUser(String name,
                             String email,
                             String password)throws SQLException {
        //Basic Validation
        if(name.isBlank()||name.isBlank()){
            throw new IllegalArgumentException("Name is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required."
            );
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters."
            );
        }

        //2.Normalise email
        email=email.trim().toLowerCase();

        //3.Check weather email exists
        Optional<User> existingUser=userDAO.findByEmail(email);

        if(existingUser.isPresent()){
            throw new IllegalArgumentException("Email is already registered.");
        }

        //4.Hash Password
        String passwordHash= BCrypt.hashpw(password,BCrypt.gensalt(12));

        //5. Create User
        User user=new User();
        user.setName(name.trim());
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setRole("STUDENT");
        user.setActive(true);


        //6. Save user by dao
        return userDAO.createUser(user);
    }

    public User loginUser(String email,
                          String password
            )throws SQLException{
        if(email==null||email.isBlank()){
            throw new IllegalArgumentException("Email is required");
        }

        if(password==null||password.isBlank()){
            throw new IllegalArgumentException("Password is required");
        }

        email=email.trim().toLowerCase();

        Optional<User> optionalUser=userDAO.findByEmail(email);

        if(optionalUser.isEmpty()){
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user=optionalUser.get();

        if(!user.isActive()){
            throw new IllegalArgumentException(
                    "This account is inactive."
            );
        }

        boolean passwordMatches=BCrypt.checkpw(password,user.getPasswordHash());
        if(!passwordMatches){
            throw new IllegalArgumentException("Invalid email or password");
        }
        return user;
    }
}
