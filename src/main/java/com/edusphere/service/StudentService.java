package com.edusphere.service;

import com.edusphere.dao.StudentDAO;
import com.edusphere.dto.StudentProfile;
import com.edusphere.model.Student;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class StudentService {
    private final StudentDAO studentDAO;

    public StudentService(){
        this.studentDAO=new StudentDAO();
    }

    public Student createStudent(
            Long userId,
            LocalDate dateOfBirth,
            String phone
    )throws SQLException{

        if(userId==null){
            throw new IllegalArgumentException("userId is required");
        }

        if (phone != null) {
            phone = phone.trim();

            if (phone.isBlank()) {
                phone = null;
            }
        }
        Student student = new Student();

        student.setUserId(userId);
        student.setDateOfBirth(dateOfBirth);
        student.setPhone(phone);

        return studentDAO.createStudent(student);
    }

    public Optional<Student> findByUserId(
            Long userId
    ) throws SQLException {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID is required."
            );
        }

        return studentDAO.findByUserId(userId);
    }

    public Optional<Student> findById(Long id)throws SQLException{
        if(id==null){
            throw new IllegalArgumentException("ID is required");
        }
        return studentDAO.findById(id);
    }

    public Optional<StudentProfile> getProfile(Long userId)throws SQLException{
        if(userId==null){
            throw new IllegalArgumentException("User id is required.");
        }
      return studentDAO.findProfileByUserId(userId);
    }
}
