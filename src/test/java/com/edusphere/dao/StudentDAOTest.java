package com.edusphere.dao;

import com.edusphere.model.Student;

import java.sql.SQLException;
import java.time.LocalDate;

public class StudentDAOTest {
    public static void main(String args[]){
        StudentDAO studentDAO=new StudentDAO();
        UserDAO userDAO=new UserDAO();
        Student student=new Student();
        student.setUserId(5L);

        student.setDateOfBirth(
                LocalDate.of(2000, 1, 15)
        );

        student.setPhone(
                "9999999999"
        );
        try {
//            Student savedStudent=studentDAO.createStudent(student);
//            System.out.println("Student created successfully");
//
//            System.out.println("Created student id:"+savedStudent.getId());

            studentDAO.findByUserId(
                    5L
            ).ifPresent(found -> {

                System.out.println(
                        "Student found!"
                );

                System.out.println(
                        "Student ID: "
                                + found.getId()
                );

                try {
                    userDAO.findById(found.getUserId()).ifPresent(Name->{
                        System.out.println("Name :"+Name.getName());
                    });
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                System.out.println(
                        "User ID: "
                                + found.getUserId()
                );

            });

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("User not found");
        }
    }
}
