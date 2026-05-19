package com.hms.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Domain Entity: VisitorLog
 */
public class VisitorLog {

    private String        visitId;
    private String        studentId;
    private String        visitorName;
    private String        visitorCnic;
    private String        purpose;
    private LocalDateTime visitDate;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public VisitorLog(String visitId, String studentId, String visitorName,
                      String visitorCnic, String purpose, LocalDateTime visitDate) {
        this.visitId     = visitId;
        this.studentId   = studentId;
        this.visitorName = visitorName;
        this.visitorCnic = visitorCnic;
        this.purpose     = purpose;
        this.visitDate   = visitDate;
    }

    /** Convenience constructor for new entries. */
    public VisitorLog(String studentId, String visitorName,
                      String visitorCnic, String purpose) {
        this(null, studentId, visitorName, visitorCnic, purpose, LocalDateTime.now());
    }

    public String formattedDate() {
        return visitDate != null ? visitDate.format(FMT) : "—";
    }

    // ── Getters ───────────────────────────────────────────────
    public String        getVisitId()     { return visitId; }
    public String        getStudentId()   { return studentId; }
    public String        getVisitorName() { return visitorName; }
    public String        getVisitorCnic() { return visitorCnic; }
    public String        getPurpose()     { return purpose; }
    public LocalDateTime getVisitDate()   { return visitDate; }

    // ── Setters ───────────────────────────────────────────────
    public void setVisitId(String visitId)         { this.visitId     = visitId; }
    public void setStudentId(String studentId)     { this.studentId   = studentId; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }
    public void setVisitorCnic(String visitorCnic) { this.visitorCnic = visitorCnic; }
    public void setPurpose(String purpose)         { this.purpose     = purpose; }
    public void setVisitDate(LocalDateTime date)   { this.visitDate   = date; }
}
