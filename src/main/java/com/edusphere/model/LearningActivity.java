
package com.edusphere.model;

import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.Map;

public class LearningActivity {

    private ObjectId id;
    private Long userId;
    private Long studentId;
    private String eventType;
    private Long courseId;
    private Long resourceId;
    private Instant occurredAt;
    private Map<String, Object> metadata;

    public LearningActivity() {
    }

    public LearningActivity(
            Long userId,
            Long studentId,
            String eventType,
            Long courseId,
            Long resourceId,
            Map<String, Object> metadata) {

        this.userId = userId;
        this.studentId = studentId;
        this.eventType = eventType;
        this.courseId = courseId;
        this.resourceId = resourceId;
        this.occurredAt = Instant.now();
        this.metadata = metadata;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
