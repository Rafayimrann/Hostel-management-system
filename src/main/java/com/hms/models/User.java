package com.hms.models;

/**
 * Domain Entity: User
 * Represents an authenticated system user (Student, Warden, or Maintenance Staff).
 */
public class User {

    private String userId;
    private String name;
    private String email;
    private String password;
    private String role; // STUDENT | WARDEN | MAINTENANCE

    public User(String userId, String name, String email, String password, String role) {
        this.userId   = userId;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    // ── Information Expert: role checks ───────────────────────
    public boolean isStudent()     { return "STUDENT".equalsIgnoreCase(role); }
    public boolean isMaintenance() { return "MAINTENANCE".equalsIgnoreCase(role); }
    public boolean isWarden()      { return "WARDEN".equalsIgnoreCase(role); }

    // ── Getters & Setters ─────────────────────────────────────
    public String getUserId()   { return userId; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }

    public void setUserId(String userId)     { this.userId   = userId; }
    public void setName(String name)         { this.name     = name; }
    public void setEmail(String email)       { this.email    = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role)         { this.role     = role; }

    @Override
    public String toString() {
        return "User{id='" + userId + "', name='" + name + "', role='" + role + "'}";
    }
}
