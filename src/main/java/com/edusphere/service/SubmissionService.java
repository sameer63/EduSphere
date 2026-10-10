package com.edusphere.service;

import com.edusphere.dao.AssignmentDAO;
import com.edusphere.dao.SubmissionDAO;
import com.edusphere.model.Assignment;
import com.edusphere.model.Course;
import com.edusphere.model.Submission;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SubmissionService {

    private final SubmissionDAO submissionDAO;
    private final AssignmentDAO assignmentDAO;
    private final CourseService courseService;
    private final StudentService studentService;
    private final EnrolmentService enrolmentService;
    private final TeacherService teacherService;

    public SubmissionService() {
        this.submissionDAO = new SubmissionDAO();
        this.assignmentDAO = new AssignmentDAO();
        this.courseService = new CourseService();
        this.studentService = new StudentService();
        this.enrolmentService = new EnrolmentService();
        this.teacherService = new TeacherService();
    }

    /**
     * Create a new submission.
     */
    public Submission createSubmission(
            Long studentId,
            Long assignmentId,
            String submissionText)
            throws Exception {

        validateStudentId(studentId);
        validateAssignmentId(assignmentId);
        validateSubmissionText(submissionText);

        // Verify student exists
        if (studentService.findById(studentId) == null) {
            throw new IllegalArgumentException(
                    "Student not found."
            );
        }

        // Find assignment
        Assignment assignment =
                assignmentDAO.findById(assignmentId);

        if (assignment == null) {
            throw new IllegalArgumentException(
                    "Assignment not found."
            );
        }

        // Find course
        Course course =
                courseService.findById(assignment.getCourseId());

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        // Student must be enrolled
        if (!enrolmentService.isStudentEnrolled(
                studentId,
                course.getId())) {

            throw new IllegalArgumentException(
                    "Student is not enrolled in this course."
            );
        }

        // Prevent duplicate submission
        Submission existing =
                submissionDAO.findByAssignmentAndStudent(
                        assignmentId,
                        studentId
                );

        if (existing != null) {
            throw new IllegalArgumentException(
                    "You have already submitted this assignment."
            );
        }

        // Check deadline
        validateDeadline(assignment);

        Submission submission =
                new Submission(
                        assignmentId,
                        studentId,
                        submissionText.trim()
                );

        return submissionDAO.createSubmission(submission);
    }


    /**
     * Find a submission by ID.
     */
    public Submission findById(Long submissionId)
            throws Exception {

        if (submissionId == null || submissionId <= 0) {
            return null;
        }

        return submissionDAO.findById(submissionId);
    }


    /**
     * Find a student's submission for a particular assignment.
     */
    public Submission findByAssignmentAndStudent(
            Long assignmentId,
            Long studentId)
            throws Exception {

        if (assignmentId == null || assignmentId <= 0) {
            return null;
        }

        if (studentId == null || studentId <= 0) {
            return null;
        }

        return submissionDAO.findByAssignmentAndStudent(
                assignmentId,
                studentId
        );
    }


    /**
     * Find all submissions made by a student.
     */
    public List<Submission> findByStudent(Long studentId)
            throws Exception {

        validateStudentId(studentId);

        return submissionDAO.findByStudent(studentId);
    }


    /**
     * Find all submissions for an assignment.
     *
     * Used by teachers.
     */
    public List<Submission> findByAssignment(
            Long assignmentId)
            throws Exception {

        validateAssignmentId(assignmentId);

        return submissionDAO.findByAssignment(assignmentId);
    }


    /**
     * Update an existing submission.
     *
     * This supports resubmission using the existing
     * submission row because the database has:
     *
     * UNIQUE(assignment_id, student_id)
     */
    public Submission updateSubmission(
            Long studentId,
            Long submissionId,
            String submissionText)
            throws Exception {

        validateStudentId(studentId);

        if (submissionId == null || submissionId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid submission ID."
            );
        }

        validateSubmissionText(submissionText);

        Submission existing =
                submissionDAO.findById(submissionId);

        if (existing == null) {
            throw new IllegalArgumentException(
                    "Submission not found."
            );
        }

        if (existing.getMarks() != null) {
            throw new IllegalArgumentException(
                    "A graded submission cannot be modified."
            );
        }

        // Security: student can update only their own submission
        if (!existing.getStudentId().equals(studentId)) {
            throw new IllegalArgumentException(
                    "You are not authorised to update this submission."
            );
        }

        Assignment assignment =
                assignmentDAO.findById(
                        existing.getAssignmentId()
                );

        if (assignment == null) {
            throw new IllegalArgumentException(
                    "Assignment not found."
            );
        }

        Course course =
                courseService.findById(
                        assignment.getCourseId()
                );

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        // Student must still be enrolled
        if (!enrolmentService.isStudentEnrolled(
                studentId,
                course.getId())) {

            throw new IllegalArgumentException(
                    "Student is not enrolled in this course."
            );
        }

        // Resubmission must also obey deadline
        validateDeadline(assignment);

        existing.setSubmissionText(
                submissionText.trim()
        );

        return submissionDAO.updateSubmission(existing);
    }


    /**
     * Grade a submission.
     *
     * Only the teacher who owns the course can grade it.
     */
    public Submission gradeSubmission(
            Long teacherId,
            Long submissionId,
            BigDecimal marks,
            String feedback)
            throws Exception {

        if (teacherId == null || teacherId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid teacher ID."
            );
        }

        if (submissionId == null || submissionId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid submission ID."
            );
        }

        if (marks == null) {
            throw new IllegalArgumentException(
                    "Marks are required."
            );
        }

        if (marks.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Marks cannot be negative."
            );
        }

        Submission submission =
                submissionDAO.findById(submissionId);

        if (submission == null) {
            throw new IllegalArgumentException(
                    "Submission not found."
            );
        }

        Assignment assignment =
                assignmentDAO.findById(
                        submission.getAssignmentId()
                );

        if (assignment == null) {
            throw new IllegalArgumentException(
                    "Assignment not found."
            );
        }

        Course course =
                courseService.findById(
                        assignment.getCourseId()
                );

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        // Verify teacher profile
        var teacher =
                teacherService.findById(course.getTeacherId());

        if (teacher == null) {
            throw new IllegalArgumentException(
                    "Teacher not found."
            );
        }

        // Security: only course owner can grade
        if (!teacher.getId().equals(teacherId)) {
            throw new IllegalArgumentException(
                    "You are not authorised to grade this submission."
            );
        }

        // Marks cannot exceed assignment maximum
        if (marks.compareTo(assignment.getMaxMarks()) > 0) {
            throw new IllegalArgumentException(
                    "Marks cannot exceed the maximum marks."
            );
        }

        if (feedback != null) {
            feedback = feedback.trim();

            if (feedback.isBlank()) {
                feedback = null;
            }

            if (feedback != null && feedback.length() > 5000) {
                throw new IllegalArgumentException(
                        "Feedback cannot exceed 5000 characters."
                );
            }
        }

        return submissionDAO.gradeSubmission(
                submissionId,
                marks,
                feedback
        );
    }


    /*
     * -------------------------
     * Validation helpers
     * -------------------------
     */

    private void validateStudentId(Long studentId) {

        if (studentId == null || studentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid student ID."
            );
        }
    }


    private void validateAssignmentId(Long assignmentId) {

        if (assignmentId == null || assignmentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid assignment ID."
            );
        }
    }


    private void validateSubmissionText(String submissionText) {

        if (submissionText == null ||
                submissionText.isBlank()) {

            throw new IllegalArgumentException(
                    "Submission cannot be empty."
            );
        }

        if (submissionText.trim().length() > 10000) {
            throw new IllegalArgumentException(
                    "Submission cannot exceed 10000 characters."
            );
        }
    }


    private void validateDeadline(
            Assignment assignment) {

        LocalDateTime deadline =
                assignment.getDeadline();

        // Null deadline = self-paced assignment
        if (deadline == null) {
            return;
        }

        if (LocalDateTime.now().isAfter(deadline)) {
            throw new IllegalArgumentException(
                    "The submission deadline has passed."
            );
        }
    }
}