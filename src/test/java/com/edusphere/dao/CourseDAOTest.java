package com.edusphere.dao;

import com.edusphere.model.Course;

import java.math.BigDecimal;
import java.util.Optional;

public class CourseDAOTest {
    public static void main(String[] args){
        CourseDAO courseDAO=new CourseDAO();
        Course course=new Course();



        try{
            Optional<Course> course1=courseDAO.findById(1L);
            Course course2=course1.get();
            System.out.println("id :"+course2.getId());
            System.out.println("teacher id:"+course2.getTeacherId());
            System.out.println("category id:"+course2.getCategoryId());
            System.out.println("Title:"+course2.getTitle());
            System.out.println("Description :"+course2.getDescription());

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
