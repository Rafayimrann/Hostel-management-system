package com.hms.models;

/**
 * Domain Entity: RoomSwapRequest
 */
public class RoomSwapRequest {

    private String requestId;
    private String studentId;
    private String currentRoom;
    private String requestedRoom;
    private String reason;
    private String status; // PENDING | APPROVED | REJECTED

    public RoomSwapRequest(String requestId, String studentId, String currentRoom,
                           String requestedRoom, String reason, String status) {
        this.requestId     = requestId;
        this.studentId     = studentId;
        this.currentRoom   = currentRoom;
        this.requestedRoom = requestedRoom;
        this.reason        = reason;
        this.status        = status;
    }

    /** Convenience constructor for new requests. */
    public RoomSwapRequest(String studentId, String currentRoom,
                           String requestedRoom, String reason) {
        this(null, studentId, currentRoom, requestedRoom, reason, "PENDING");
    }

    public boolean isPending()  { return "PENDING".equalsIgnoreCase(status); }
    public boolean isApproved() { return "APPROVED".equalsIgnoreCase(status); }
    public boolean isRejected() { return "REJECTED".equalsIgnoreCase(status); }

    // ── Getters ───────────────────────────────────────────────
    public String getRequestId()     { return requestId; }
    public String getStudentId()     { return studentId; }
    public String getCurrentRoom()   { return currentRoom; }
    public String getRequestedRoom() { return requestedRoom; }
    public String getReason()        { return reason; }
    public String getStatus()        { return status; }

    // ── Setters ───────────────────────────────────────────────
    public void setRequestId(String id)          { this.requestId     = id; }
    public void setStudentId(String id)          { this.studentId     = id; }
    public void setCurrentRoom(String room)      { this.currentRoom   = room; }
    public void setRequestedRoom(String room)    { this.requestedRoom = room; }
    public void setReason(String reason)         { this.reason        = reason; }
    public void setStatus(String status)         { this.status        = status; }
}
