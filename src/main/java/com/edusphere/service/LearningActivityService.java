
package com.edusphere.service;

import com.edusphere.dao.LearningActivityDAO;
import com.edusphere.model.Course;
import com.edusphere.model.LearningActivity;
import com.edusphere.model.Student;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LearningActivityService {

    private final LearningActivityDAO activityDAO =
            new LearningActivityDAO();

    private final StudentService studentService =
            new StudentService();

    private final CourseService courseService =
            new CourseService();

    private static final Set<String> ALLOWED_EVENT_TYPES =
            Set.of(
                    "LESSON_VIEWED",
                    "QUIZ_STARTED",
                    "QUIZ_SUBMITTED",
                    "ASSIGNMENT_SUBMITTED"
            );

    public LearningActivity recordActivity(
            Long userId,
            Long studentId,
            String eventType,
            Long courseId,
            Long resourceId,
            Map<String, Object> metadata
    ) throws Exception {

        validatePositiveId(userId, "User ID");
        validatePositiveId(studentId, "Student ID");
        validatePositiveId(courseId, "Course ID");

        if (resourceId != null && resourceId <= 0) {
            throw new IllegalArgumentException(
                    "Resource ID must be positive."
            );
        }

        if (eventType == null ||
                !ALLOWED_EVENT_TYPES.contains(eventType)) {
            throw new IllegalArgumentException(
                    "Unsupported learning activity type."
            );
        }

        if (metadata != null && metadata.size() > 20) {
            throw new IllegalArgumentException(
                    "Too many metadata fields."
            );
        }

        // Validate that the student exists.
        Student student =
                studentService.findById(studentId)
                        .orElseThrow(()->new IllegalArgumentException("Student not found"));

        if (student == null) {
            throw new IllegalArgumentException(
                    "Student not found."
            );
        }

        // Ensure the student belongs to the supplied user.
        if (!userId.equals(student.getUserId())) {
            throw new IllegalArgumentException(
                    "Student does not belong to this user."
            );
        }

        // Validate that the course exists.
        Course course =
                courseService.findById(courseId);

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        LearningActivity activity =
                new LearningActivity(
                        userId,
                        studentId,
                        eventType,
                        courseId,
                        resourceId,
                        metadata
                );

        return activityDAO.create(activity);
    }

    public List<LearningActivity> findRecentByStudent(
            Long studentId,
            int limit
    ) throws Exception {

        validatePositiveId(studentId, "Student ID");

        Student student =
                studentService.findById(studentId)
                        .orElseThrow(()->new IllegalArgumentException("Student not found"));

        if (student == null) {
            throw new IllegalArgumentException(
                    "Student not found."
            );
        }

        return activityDAO.findRecentByStudent(
                studentId,
                limit
        );
    }

    public List<LearningActivity> findByCourse(
            Long courseId,
            int limit
    ) throws Exception {

        validatePositiveId(courseId, "Course ID");

        Course course =
                courseService.findById(courseId);

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        return activityDAO.findByCourse(
                courseId,
                limit
        );
    }

    private void validatePositiveId(
            Long id,
            String fieldName) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be positive."
            );
        }
    }
}
