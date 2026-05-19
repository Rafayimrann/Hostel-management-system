package com.hms.models;

/**
 * Domain Entity: Student
 *
 * GRASP INFORMATION EXPERT:
 * The Student class holds all student-specific data and therefore
 * contains methods that reason about that data (e.g., hasDues).
 */
public class Student {

    private String studentId;
    private String userId;
    private String roomNumber;
    private String program;
    private String contactNumber;
    private double outstandingDues;
    private String name; // denormalised from users table

    public Student(String studentId, String userId, String roomNumber,
                   String program, String contactNumber,
                   double outstandingDues, String name) {
        this.studentId       = studentId;
        this.userId          = userId;
        this.roomNumber      = roomNumber;
        this.program         = program;
        this.contactNumber   = contactNumber;
        this.outstandingDues = outstandingDues;
        this.name            = name;
    }

    // ── Information Expert: business logic ────────────────────

    /** Returns true if the student has outstanding dues. */
    public boolean hasDues() {
        return outstandingDues > 0.0;
    }

    /** Returns a formatted dues string. */
    public String formattedDues() {
        return String.format("PKR %.2f", outstandingDues);
    }

    // ── Getters & Setters ─────────────────────────────────────
    public String getStudentId()       { return studentId; }
    public String getUserId()          { return userId; }
    public String getRoomNumber()      { return roomNumber; }
    public String getProgram()         { return program; }
    public String getContactNumber()   { return contactNumber; }
    public double getOutstandingDues() { return outstandingDues; }
    public String getName()            { return name; }

    public void setStudentId(String studentId)           { this.studentId       = studentId; }
    public void setUserId(String userId)                 { this.userId          = userId; }
    public void setRoomNumber(String roomNumber)         { this.roomNumber      = roomNumber; }
    public void setProgram(String program)               { this.program         = program; }
    public void setContactNumber(String contactNumber)   { this.contactNumber   = contactNumber; }
    public void setOutstandingDues(double outstanding)   { this.outstandingDues = outstanding; }
    public void setName(String name)                     { this.name            = name; }

    @Override
    public String toString() {
        return "Student{id='" + studentId + "', room='" + roomNumber + "', name='" + name + "'}";
    }
}
