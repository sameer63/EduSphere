package com.edusphere.service;

import com.edusphere.dao.CourseDAO;
import com.edusphere.dao.EnrolmentDAO;
import com.edusphere.dao.StudentDAO;
import com.edusphere.model.Course;
import com.edusphere.model.Enrolment;
import com.edusphere.model.Student;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EnrolmentService {

    private final EnrolmentDAO enrolmentDAO;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final PaymentService paymentService =
            new PaymentService();


    public EnrolmentService() {
        this(
                new EnrolmentDAO(),
                new StudentDAO(),
                new CourseDAO()
        );
    }

    // Constructor for dependency injection and unit testing
    public EnrolmentService(
            EnrolmentDAO enrolmentDAO,
            StudentDAO studentDAO,
            CourseDAO courseDAO) {

        this.enrolmentDAO = enrolmentDAO;
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
    }


    /**
     * Enrol a student in a course.
     */
    public Enrolment enrolStudent(
            Long studentId,
            Long courseId) throws Exception,SQLException {

        // Validate student ID
        if (studentId == null || studentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid student ID."
            );
        }

        // Validate course ID
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid course ID."
            );
        }

        // Verify student exists
        Student student =
                studentDAO.findById(studentId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Student not found."
                                ));

        // Verify course exists
        Course course =
                courseDAO.findById(courseId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Course not found."
                                ));

        if (course.getPrice() == null) {
            throw new IllegalArgumentException(
                    "Course price is not configured."
            );
        }

        if (course.getPrice()
                .compareTo(BigDecimal.ZERO) > 0) {

            boolean paid =
                    paymentService.hasSuccessfulPayment(
                            studentId,
                            courseId
                    );

            if (!paid) {

                throw new IllegalArgumentException(
                        "Payment is required before enrolling in this course."
                );
            }
        }

        /*
         * Students should only be able to enrol
         * in published courses.
         */
        if (!"PUBLISHED".equals(course.getStatus())) {

            throw new IllegalArgumentException(
                    "This course is not available for enrolment."
            );
        }

        /*
         * Check whether the student has already
         * enrolled in this course.
         */
        if (enrolmentDAO
                .findByStudentAndCourse(
                        studentId,
                        courseId
                )
                .isPresent()) {

            throw new IllegalArgumentException(
                    "You are already enrolled in this course."
            );
        }

        /*
         * New enrolments always start as ACTIVE.
         */
        Enrolment enrolment =
                new Enrolment();

        enrolment.setStudentId(studentId);
        enrolment.setCourseId(courseId);
        enrolment.setStatus("ACTIVE");

        return enrolmentDAO.createEnrolment(
                enrolment
        );
    }

    /**
     * Find an enrolment by ID.
     */
    public Enrolment findById(Long id) {

        if (id == null || id <= 0) {

            throw new IllegalArgumentException(
                    "Invalid enrolment ID."
            );
        }

        return enrolmentDAO.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Enrolment not found."
                        ));
    }

    /**
     * Get all courses in which a student
     * is enrolled.
     */
    public List<Enrolment> findByStudent(
            Long studentId) throws SQLException {

        if (studentId == null || studentId <= 0) {

            throw new IllegalArgumentException(
                    "Invalid student ID."
            );
        }

        // Make sure student exists
        studentDAO.findById(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found."
                        ));

        return enrolmentDAO.findByStudent(
                studentId
        );
    }

    /**
     * Get all students enrolled in a course.
     */
    public List<Enrolment> findByCourse(
            Long courseId) throws SQLException {

        if (courseId == null || courseId <= 0) {

            throw new IllegalArgumentException(
                    "Invalid course ID."
            );
        }

        // Make sure course exists
        courseDAO.findById(courseId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Course not found."
                        ));

        return enrolmentDAO.findByCourse(
                courseId
        );
    }

    /**
     * Check whether a student is enrolled
     * in a particular course.
     */
    public boolean isStudentEnrolled(
            Long studentId,
            Long courseId) {

        if (studentId == null || studentId <= 0) {

            throw new IllegalArgumentException(
                    "Invalid student ID."
            );
        }

        if (courseId == null || courseId <= 0) {

            throw new IllegalArgumentException(
                    "Invalid course ID."
            );
        }

        Optional<Enrolment> optionalEnrolment =
                enrolmentDAO.findByStudentAndCourse(
                        studentId,
                        courseId
                );


        if (optionalEnrolment.isEmpty()) {
            return false;
        }

        Enrolment enrolment=optionalEnrolment.get();
            return "ACTIVE".equals(enrolment.getStatus())
                    || "COMPLETED".equals(enrolment.getStatus());

    }
}