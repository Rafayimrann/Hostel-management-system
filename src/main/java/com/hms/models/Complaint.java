package com.hms.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Domain Entity: Complaint
 *
 * GRASP INFORMATION EXPERT:
 * The Complaint class owns all complaint data and therefore
 * provides status-query methods (isResolved, isPending, etc.).
 */
public class Complaint {

    private String        complaintId;
    private String        studentId;
    private String        description;
    private String        category;
    private String        priority;   // LOW | MEDIUM | HIGH
    private String        status;     // PENDING | ASSIGNED | IN_PROGRESS | RESOLVED
    private LocalDateTime submittedAt;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public Complaint(String complaintId, String studentId, String description,
                     String category, String priority, String status,
                     LocalDateTime submittedAt) {
        this.complaintId  = complaintId;
        this.studentId    = studentId;
        this.description  = description;
        this.category     = category;
        this.priority     = priority;
        this.status       = status;
        this.submittedAt  = submittedAt;
    }

    /** Convenience constructor for new complaints (no ID or timestamp yet). */
    public Complaint(String studentId, String description,
                     String category, String priority) {
        this(null, studentId, description, category, priority, "PENDING", LocalDateTime.now());
    }

    // ── Information Expert: status logic ──────────────────────
    public boolean isResolved()   { return "RESOLVED".equalsIgnoreCase(status); }
    public boolean isPending()    { return "PENDING".equalsIgnoreCase(status); }
    public boolean isInProgress() { return "IN_PROGRESS".equalsIgnoreCase(status); }
    public boolean isAssigned()   { return "ASSIGNED".equalsIgnoreCase(status); }
    public boolean isHighPriority() { return "HIGH".equalsIgnoreCase(priority); }

    public String formattedDate() {
        return submittedAt != null ? submittedAt.format(FMT) : "—";
    }

    // ── Getters & Setters ─────────────────────────────────────
    public String        getComplaintId()  { return complaintId; }
    public String        getStudentId()    { return studentId; }
    public String        getDescription()  { return description; }
    public String        getCategory()     { return category; }
    public String        getPriority()     { return priority; }
    public String        getStatus()       { return status; }
    public LocalDateTime getSubmittedAt()  { return submittedAt; }

    public void setComplaintId(String id)           { this.complaintId = id; }
    public void setStudentId(String studentId)      { this.studentId   = studentId; }
    public void setDescription(String description)  { this.description = description; }
    public void setCategory(String category)        { this.category    = category; }
    public void setPriority(String priority)        { this.priority    = priority; }
    public void setStatus(String status)            { this.status      = status; }
    public void setSubmittedAt(LocalDateTime ts)    { this.submittedAt = ts; }
}
