
package com.edusphere.dao;

import com.edusphere.util.MongoDBConfig;
import com.edusphere.model.LearningActivity;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class LearningActivityDAO {

    private final MongoCollection<Document> collection;

    public LearningActivityDAO() {
        MongoDatabase database = MongoDBConfig.getDatabase();

        this.collection =
                database.getCollection("learning_activities");

        createIndexes();
    }

    private void createIndexes() {
        collection.createIndex(
                Indexes.compoundIndex(
                        Indexes.ascending("studentId"),
                        Indexes.descending("occurredAt")
                )
        );

        collection.createIndex(
                Indexes.compoundIndex(
                        Indexes.ascending("courseId"),
                        Indexes.descending("occurredAt")
                )
        );
    }

    public LearningActivity create(
            LearningActivity activity) {

        if (activity == null) {
            throw new IllegalArgumentException(
                    "Learning activity is required."
            );
        }

        Document document = toDocument(activity);

        collection.insertOne(document);

        activity.setId(document.getObjectId("_id"));

        return activity;
    }

    public List<LearningActivity> findRecentByStudent(
            Long studentId,
            int limit) {

        if (studentId == null || studentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid student ID."
            );
        }

        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException(
                    "Limit must be between 1 and 100."
            );
        }

        List<LearningActivity> activities =
                new ArrayList<>();

        collection.find(eq("studentId", studentId))
                .sort(Sorts.descending("occurredAt"))
                .limit(limit)
                .forEach(document ->
                        activities.add(fromDocument(document)));

        return activities;
    }

    public List<LearningActivity> findByCourse(
            Long courseId,
            int limit) {

        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid course ID."
            );
        }

        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException(
                    "Limit must be between 1 and 100."
            );
        }

        List<LearningActivity> activities =
                new ArrayList<>();

        collection.find(eq("courseId", courseId))
                .sort(Sorts.descending("occurredAt"))
                .limit(limit)
                .forEach(document ->
                        activities.add(fromDocument(document)));

        return activities;
    }

    private Document toDocument(
            LearningActivity activity) {

        Document document = new Document()
                .append("userId", activity.getUserId())
                .append("studentId", activity.getStudentId())
                .append("eventType", activity.getEventType())
                .append("courseId", activity.getCourseId())
                .append("resourceId", activity.getResourceId())
                .append("occurredAt",
                        activity.getOccurredAt() == null
                                ? Date.from(Instant.now())
                                : Date.from(activity.getOccurredAt()));

        Map<String, Object> metadata = activity.getMetadata();

        if (metadata != null) {
            document.append("metadata", new Document(metadata));
        }

        return document;
    }

    @SuppressWarnings("unchecked")
    private LearningActivity fromDocument(
            Document document) {

        LearningActivity activity = new LearningActivity();

        activity.setId(document.getObjectId("_id"));
        activity.setUserId(getLong(document, "userId"));
        activity.setStudentId(getLong(document, "studentId"));
        activity.setEventType(document.getString("eventType"));
        activity.setCourseId(getLong(document, "courseId"));
        activity.setResourceId(getLong(document, "resourceId"));

        Date occurredAt = document.getDate("occurredAt");

        if (occurredAt != null) {
            activity.setOccurredAt(occurredAt.toInstant());
        }

        Document metadata = document.get("metadata", Document.class);

        if (metadata != null) {
            activity.setMetadata(
                    (Map<String, Object>) (Map<?, ?>) metadata
            );
        }

        return activity;
    }

    private Long getLong(
            Document document,
            String field) {

        Number value = document.get(field, Number.class);

        return value == null ? null : value.longValue();
    }
}
