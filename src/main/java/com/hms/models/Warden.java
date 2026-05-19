package com.hms.models;

/**
 * Domain Entity: Warden
 *
 * GRASP INFORMATION EXPERT:
 * Warden knows its own identity and provides
 * display helpers used in the dashboard header.
 */
public class Warden {

    private String userId;
    private String name;
    private String email;

    public Warden(String userId, String name, String email) {
        this.userId = userId;
        this.name   = name;
        this.email  = email;
    }

    // ── Information Expert ─────────────────────────────────
    public String displayTitle() {
        return "Warden  •  " + name;
    }

    // ── Getters & Setters ─────────────────────────────────
    public String getUserId() { return userId; }
    public String getName()   { return name; }
    public String getEmail()  { return email; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name)     { this.name   = name; }
    public void setEmail(String email)   { this.email  = email; }

    @Override
    public String toString() {
        return "Warden{userId='" + userId + "', name='" + name + "'}";
    }
}
