package com.hms.models;

/**
 * Domain Entity: Room
 *
 * GRASP INFORMATION EXPERT:
 * The Room class owns availability and status logic,
 * providing helpers the Warden UI consumes directly.
 */
public class Room {

    private String roomNumber;
    private int    capacity;
    private String status;   // AVAILABLE | OCCUPIED | UNAVAILABLE
    private String block;

    public Room(String roomNumber, int capacity, String status, String block) {
        this.roomNumber = roomNumber;
        this.capacity   = capacity;
        this.status     = status;
        this.block      = block;
    }

    // ── Information Expert ─────────────────────────────────
    public boolean isAvailable()   { return "AVAILABLE".equalsIgnoreCase(status); }
    public boolean isOccupied()    { return "OCCUPIED".equalsIgnoreCase(status); }
    public boolean isUnavailable() { return "UNAVAILABLE".equalsIgnoreCase(status); }

    public String displayLabel() {
        return "Room " + roomNumber + " (Block " + block + ") — " + status;
    }

    // ── Getters & Setters ─────────────────────────────────
    public String getRoomNumber() { return roomNumber; }
    public int    getCapacity()   { return capacity; }
    public String getStatus()     { return status; }
    public String getBlock()      { return block; }

    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setCapacity(int capacity)        { this.capacity   = capacity; }
    public void setStatus(String status)         { this.status     = status; }
    public void setBlock(String block)           { this.block      = block; }

    @Override
    public String toString() {
        return "Room{number='" + roomNumber + "', block='" + block
             + "', status='" + status + "'}";
    }
}
