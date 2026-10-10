package com.edusphere.service;

import com.edusphere.model.Lesson;

import java.sql.SQLException;

public class LessonServiceTest {
    public static void main(String[] args){
        LessonService lessonService=new LessonService();
        try {
            Lesson lesson=lessonService.createLesson(2L,1L,"Intro to Java",
                    "Java was developed by James Goslin",0);
            System.out.println("Lesson id :"+lesson.getId()+"is created ");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
