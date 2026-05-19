package com.hms.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Domain Entity: Fine
 *
 * Represents a financial penalty issued to a student by the Warden.
 * GRASP INFORMATION EXPERT: owns its own formatting logic.
 */
public class Fine {

    private String        fineId;
    private String        studentId;
    private String        studentName;   // denormalised for display
    private String        violationType;
    private double        amount;
    private String        issuedBy;      // warden user_id
    private LocalDateTime issuedAt;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public Fine(String fineId, String studentId, String studentName,
                String violationType, double amount,
                String issuedBy, LocalDateTime issuedAt) {
        this.fineId        = fineId;
        this.studentId     = studentId;
        this.studentName   = studentName;
        this.violationType = violationType;
        this.amount        = amount;
        this.issuedBy      = issuedBy;
        this.issuedAt      = issuedAt;
    }

    // ── Information Expert ─────────────────────────────────
    public String formattedAmount() { return String.format("PKR %.2f", amount); }
    public String formattedDate()   { return issuedAt != null ? issuedAt.format(FMT) : "—"; }

    // ── Getters & Setters ─────────────────────────────────
    public String        getFineId()        { return fineId; }
    public String        getStudentId()     { return studentId; }
    public String        getStudentName()   { return studentName; }
    public String        getViolationType() { return violationType; }
    public double        getAmount()        { return amount; }
    public String        getIssuedBy()      { return issuedBy; }
    public LocalDateTime getIssuedAt()      { return issuedAt; }

    public void setFineId(String fineId)               { this.fineId        = fineId; }
    public void setStudentId(String studentId)         { this.studentId     = studentId; }
    public void setStudentName(String studentName)     { this.studentName   = studentName; }
    public void setViolationType(String violationType) { this.violationType = violationType; }
    public void setAmount(double amount)               { this.amount        = amount; }
    public void setIssuedBy(String issuedBy)           { this.issuedBy      = issuedBy; }
    public void setIssuedAt(LocalDateTime issuedAt)    { this.issuedAt      = issuedAt; }
}
