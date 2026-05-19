package com.hms.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Domain Entity: MaintenanceTask
 * Represents a maintenance job assigned to a staff member.
 */
public class MaintenanceTask {

    private String        taskId;
    private String        complaintId;
    private String        staffId;
    private String        status; // ASSIGNED | IN_PROGRESS | COMPLETED | CLOSED
    private LocalDateTime assignedAt;

    // ── Enriched via JOIN (not stored columns) ────────────────
    private String complaintDescription;
    private String complaintCategory;
    private String complaintPriority;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public MaintenanceTask(String taskId, String complaintId, String staffId,
                           String status, LocalDateTime assignedAt) {
        this.taskId      = taskId;
        this.complaintId = complaintId;
        this.staffId     = staffId;
        this.status      = status;
        this.assignedAt  = assignedAt;
    }

    // ── Information Expert ─────────────────────────────────────
    public boolean isCompleted()  { return "COMPLETED".equalsIgnoreCase(status); }
    public boolean isInProgress() { return "IN_PROGRESS".equalsIgnoreCase(status); }
    public boolean isClosed()     { return "CLOSED".equalsIgnoreCase(status); }
    public boolean canUpdate()    { return !isCompleted() && !isClosed(); }

    public String formattedDate() {
        return assignedAt != null ? assignedAt.format(FMT) : "—";
    }

    // ── Getters & Setters ─────────────────────────────────────
    public String        getTaskId()                { return taskId; }
    public String        getComplaintId()           { return complaintId; }
    public String        getStaffId()               { return staffId; }
    public String        getStatus()                { return status; }
    public LocalDateTime getAssignedAt()            { return assignedAt; }
    public String        getComplaintDescription()  { return complaintDescription; }
    public String        getComplaintCategory()     { return complaintCategory; }
    public String        getComplaintPriority()     { return complaintPriority; }

    public void setStatus(String status)                           { this.status               = status; }
    public void setComplaintDescription(String desc)               { this.complaintDescription = desc; }
    public void setComplaintCategory(String category)              { this.complaintCategory    = category; }
    public void setComplaintPriority(String priority)              { this.complaintPriority    = priority; }
}
