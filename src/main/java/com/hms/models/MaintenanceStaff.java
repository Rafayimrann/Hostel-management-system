package com.hms.models;

/**
 * Domain Entity: MaintenanceStaff
 *
 * GRASP INFORMATION EXPERT:
 * This class knows its own availability and specialization,
 * so availability-related logic lives here.
 */
public class MaintenanceStaff {

    private String  staffId;
    private String  userId;
    private String  specialization;
    private String  contactNumber;
    private boolean isAvailable;
    private String  name; // denormalised from users table

    public MaintenanceStaff(String staffId, String userId, String specialization,
                            String contactNumber, boolean isAvailable, String name) {
        this.staffId        = staffId;
        this.userId         = userId;
        this.specialization = specialization;
        this.contactNumber  = contactNumber;
        this.isAvailable    = isAvailable;
        this.name           = name;
    }

    // ── Information Expert: business logic ────────────────────

    /** Returns true if this staff member is free to take new tasks. */
    public boolean isAvailableForTask() {
        return isAvailable;
    }

    public String availabilityLabel() {
        return isAvailable ? "Available" : "Busy";
    }

    // ── Getters & Setters ─────────────────────────────────────
    public String  getStaffId()        { return staffId; }
    public String  getUserId()         { return userId; }
    public String  getSpecialization() { return specialization; }
    public String  getContactNumber()  { return contactNumber; }
    public boolean isAvailable()       { return isAvailable; }
    public String  getName()           { return name; }

    public void setStaffId(String staffId)               { this.staffId        = staffId; }
    public void setUserId(String userId)                 { this.userId         = userId; }
    public void setSpecialization(String spec)           { this.specialization = spec; }
    public void setContactNumber(String contactNumber)   { this.contactNumber  = contactNumber; }
    public void setAvailable(boolean available)          { this.isAvailable    = available; }
    public void setName(String name)                     { this.name           = name; }

    @Override
    public String toString() {
        return "MaintenanceStaff{id='" + staffId + "', spec='" + specialization
             + "', available=" + isAvailable + "}";
    }
}
