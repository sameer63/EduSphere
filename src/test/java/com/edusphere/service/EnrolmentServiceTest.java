
package com.edusphere.service;

import com.edusphere.dao.CourseDAO;
import com.edusphere.dao.EnrolmentDAO;
import com.edusphere.dao.StudentDAO;
import com.edusphere.model.Enrolment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EnrolmentServiceTest {

    private EnrolmentDAO enrolmentDAO;
    private EnrolmentService enrolmentService;

    @BeforeEach
    void setUp() {
        enrolmentDAO = mock(EnrolmentDAO.class);

        StudentDAO studentDAO = mock(StudentDAO.class);
        CourseDAO courseDAO = mock(CourseDAO.class);

        enrolmentService = new EnrolmentService(
                enrolmentDAO,
                studentDAO,
                courseDAO
        );
    }

    @Test
    void shouldReturnTrueWhenEnrolmentIsActive() {
        Enrolment enrolment = new Enrolment();
        enrolment.setStatus("ACTIVE");

        when(enrolmentDAO.findByStudentAndCourse(1L, 2L))
                .thenReturn(Optional.of(enrolment));

        assertTrue(
                enrolmentService.isStudentEnrolled(1L, 2L)
        );
    }

    @Test
    void shouldReturnTrueWhenEnrolmentIsCompleted() {
        Enrolment enrolment = new Enrolment();
        enrolment.setStatus("COMPLETED");

        when(enrolmentDAO.findByStudentAndCourse(1L, 2L))
                .thenReturn(Optional.of(enrolment));

        assertTrue(
                enrolmentService.isStudentEnrolled(1L, 2L)
        );
    }

    @Test
    void shouldReturnFalseWhenEnrolmentIsCancelled() {
        Enrolment enrolment = new Enrolment();
        enrolment.setStatus("CANCELLED");

        when(enrolmentDAO.findByStudentAndCourse(1L, 2L))
                .thenReturn(Optional.of(enrolment));

        assertFalse(
                enrolmentService.isStudentEnrolled(1L, 2L)
        );
    }

    @Test
    void shouldReturnFalseWhenEnrolmentDoesNotExist() {
        when(enrolmentDAO.findByStudentAndCourse(1L, 2L))
                .thenReturn(Optional.empty());

        assertFalse(
                enrolmentService.isStudentEnrolled(1L, 2L)
        );
    }
}
