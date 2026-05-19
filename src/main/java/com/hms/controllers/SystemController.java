package com.hms.controllers;

import com.hms.database.DBHandler;
import com.hms.models.*;

import java.util.Map;

import java.util.List;

/**
 * GRASP CONTROLLER PATTERN
 * ──────────────────────────
 * SystemController is the single point of entry for ALL system use-cases.
 * The JavaFX UI classes call ONLY this controller — never DBHandler directly.
 *
 * Architecture layer: Business Logic / Application Layer
 * Delegates persistence to: DBHandler (Data Layer)
 * Called by: LoginScreen, StudentDashboard, MaintenanceDashboard (Presentation Layer)
 *
 * This enforces the layered architecture:
 *   UI → SystemController → DBHandler → MySQL
 */
public class SystemController {

    private final DBHandler db;

    // Currently logged-in user context
    private User             currentUser;
    private Student          currentStudent;
    private MaintenanceStaff currentStaff;
    private Warden           currentWarden;

    public SystemController() {
        this.db = new DBHandler();
    }

    // ══════════════════════════════════════════════════════════
    // UC-01 ── LOGIN
    // ══════════════════════════════════════════════════════════

    /**
     * Authenticates the user and loads their profile.
     *
     * @param email    User email
     * @param password Plain-text password
     * @return The authenticated User, or null on failure
     */
    public User login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        User user = db.authenticate(email.trim(), password.trim());
        if (user != null) {
            this.currentUser = user;
            loadProfile(user);
        }
        return user;
    }

    /** Clears session on logout. */
    public void logout() {
        currentUser    = null;
        currentStudent = null;
        currentStaff   = null;
        currentWarden  = null;
    }

    /** Loads the role-specific profile after login. */
    private void loadProfile(User user) {
        if (user.isStudent()) {
            currentStudent = db.getStudentByUserId(user.getUserId());
        } else if (user.isMaintenance()) {
            currentStaff = db.getStaffByUserId(user.getUserId());
        } else if (user.isWarden()) {
            currentWarden = db.getWardenByUserId(user.getUserId());
        }
    }

    // ══════════════════════════════════════════════════════════
    // UC-02 ── SUBMIT COMPLAINT
    // ══════════════════════════════════════════════════════════

    /**
     * Validates and submits a new complaint for the current student.
     *
     * @param description Complaint description (max 500 chars)
     * @param category    E.g. "Plumbing", "Electrical"
     * @param priority    LOW | MEDIUM | HIGH
     * @return Result message string
     */
    public String submitComplaint(String description, String category, String priority) {
        if (currentStudent == null) return "Error: No student session found.";
        if (description == null || description.isBlank())
            return "Error: Description cannot be empty.";
        if (category == null || category.isBlank())
            return "Error: Category must be selected.";
        if (priority == null || priority.isBlank())
            return "Error: Priority must be selected.";

        Complaint complaint = new Complaint(
            currentStudent.getStudentId(),
            description.trim(),
            category.trim(),
            priority.trim()
        );
        boolean ok = db.submitComplaint(complaint);
        return ok ? "Complaint submitted successfully!" : "Error: Failed to submit complaint.";
    }

    // ══════════════════════════════════════════════════════════
    // UC-05 ── VIEW COMPLAINT STATUS
    // ══════════════════════════════════════════════════════════

    /**
     * Returns all complaints for the logged-in student.
     */
    public List<Complaint> getMyComplaints() {
        if (currentStudent == null) return List.of();
        return db.getComplaintsByStudent(currentStudent.getStudentId());
    }

    // ══════════════════════════════════════════════════════════
    // UC-03 ── REQUEST ROOM SWAP
    // ══════════════════════════════════════════════════════════

    /**
     * Validates and submits a room swap request.
     *
     * @param requestedRoom Target room number
     * @param reason        Reason for the swap
     * @return Result message string
     */
    public String requestRoomSwap(String requestedRoom, String reason) {
        if (currentStudent == null) return "Error: No student session found.";
        if (requestedRoom == null || requestedRoom.isBlank())
            return "Error: Requested room cannot be empty.";
        if (reason == null || reason.isBlank())
            return "Error: Reason cannot be empty.";
        if (requestedRoom.trim().equals(currentStudent.getRoomNumber()))
            return "Error: Requested room is the same as your current room.";

        RoomSwapRequest req = new RoomSwapRequest(
            currentStudent.getStudentId(),
            currentStudent.getRoomNumber(),
            requestedRoom.trim(),
            reason.trim()
        );
        boolean ok = db.submitRoomSwapRequest(req);
        return ok ? "Room swap request submitted!" : "Error: Failed to submit request.";
    }

    /**
     * Returns all room swap requests for the logged-in student.
     */
    public List<RoomSwapRequest> getMyRoomSwapRequests() {
        if (currentStudent == null) return List.of();
        return db.getRoomSwapsByStudent(currentStudent.getStudentId());
    }

    // ══════════════════════════════════════════════════════════
    // UC-04 ── REGISTER VISITOR
    // ══════════════════════════════════════════════════════════

    /**
     * Validates and registers a visitor for the logged-in student.
     *
     * @param visitorName Visitor's full name
     * @param cnic        Visitor's CNIC (13–15 chars)
     * @param purpose     Purpose of visit
     * @return Result message string
     */
    public String registerVisitor(String visitorName, String cnic, String purpose) {
        if (currentStudent == null) return "Error: No student session found.";
        if (visitorName == null || visitorName.isBlank())
            return "Error: Visitor name cannot be empty.";
        if (cnic == null || cnic.isBlank())
            return "Error: CNIC cannot be empty.";
        if (!cnic.trim().matches("\\d{13,15}"))
            return "Error: CNIC must be 13–15 digits.";
        if (purpose == null || purpose.isBlank())
            return "Error: Purpose cannot be empty.";

        VisitorLog visitor = new VisitorLog(
            currentStudent.getStudentId(),
            visitorName.trim(),
            cnic.trim(),
            purpose.trim()
        );
        boolean ok = db.registerVisitor(visitor);
        return ok ? "Visitor registered successfully!" : "Error: Failed to register visitor.";
    }

    /**
     * Returns all visitor logs for the logged-in student.
     */
    public List<VisitorLog> getMyVisitorLogs() {
        if (currentStudent == null) return List.of();
        return db.getVisitorsByStudent(currentStudent.getStudentId());
    }

    // ══════════════════════════════════════════════════════════
    // UC-11 ── VIEW ASSIGNED TASKS
    // ══════════════════════════════════════════════════════════

    /**
     * Returns all tasks assigned to the logged-in maintenance staff member.
     */
    public List<MaintenanceTask> getMyTasks() {
        if (currentStaff == null) return List.of();
        return db.getTasksByStaff(currentStaff.getStaffId());
    }

    // ══════════════════════════════════════════════════════════
    // UC-12 / UC-13 ── UPDATE TASK STATUS / RESOLVE COMPLAINT
    // ══════════════════════════════════════════════════════════

    /**
     * Updates the status of a maintenance task.
     * When status is COMPLETED, linked complaint is auto-resolved.
     *
     * @param taskId    Target task ID
     * @param newStatus IN_PROGRESS | COMPLETED | CLOSED
     * @return Result message string
     */
    public String updateTaskStatus(String taskId, String newStatus) {
        if (currentStaff == null) return "Error: No staff session found.";
        if (taskId == null || taskId.isBlank())
            return "Error: Task ID required.";

        boolean ok = db.updateTaskStatus(taskId, newStatus);
        if (ok && "COMPLETED".equals(newStatus))
            return "Task completed and complaint resolved!";
        return ok ? "Task status updated to " + newStatus + "." : "Error: Update failed.";
    }

    // ══════════════════════════════════════════════════════════
    // UC-06 ── VIEW / FILTER ALL COMPLAINTS (Warden)
    // ══════════════════════════════════════════════════════════

    /**
     * Returns all complaints, filtered by priority and/or status.
     * Pass null or blank to skip a filter.
     */
    public List<Complaint> getAllComplaints(String priority, String status) {
        if (currentWarden == null) return List.of();
        return db.getAllComplaints(priority, status);
    }

    // ══════════════════════════════════════════════════════════
    // UC-07 ── ASSIGN COMPLAINT TO MAINTENANCE STAFF (Warden)
    // ══════════════════════════════════════════════════════════

    /**
     * Assigns a PENDING complaint to a maintenance staff member.
     * Creates a task and sends a notification.
     */
    public String assignComplaint(String complaintId, String staffId) {
        if (currentWarden == null) return "Error: No warden session found.";
        if (complaintId == null || complaintId.isBlank()) return "Error: Complaint ID required.";
        if (staffId == null || staffId.isBlank()) return "Error: Staff member must be selected.";
        boolean ok = db.assignComplaint(complaintId, staffId, currentWarden.getUserId());
        return ok ? "Complaint assigned and task created successfully!"
                  : "Error: Failed to assign complaint.";
    }

    /** Returns all maintenance staff (for assignment dropdown). */
    public List<MaintenanceStaff> getAllStaff() {
        if (currentWarden == null) return List.of();
        return db.getAllStaff();
    }

    // ══════════════════════════════════════════════════════════
    // UC-08 ── APPROVE / REJECT ROOM SWAP (Warden)
    // ══════════════════════════════════════════════════════════

    /** Returns all pending room swap requests. */
    public List<RoomSwapRequest> getAllPendingRoomSwaps() {
        if (currentWarden == null) return List.of();
        return db.getAllPendingRoomSwaps();
    }

    /**
     * Approves a room swap after verifying room availability.
     */
    public String approveRoomSwap(RoomSwapRequest req) {
        if (currentWarden == null) return "Error: No warden session found.";
        if (!db.isRoomAvailable(req.getRequestedRoom()))
            return "Error: Room " + req.getRequestedRoom() + " is not available.";
        boolean ok = db.approveRoomSwap(req, currentWarden.getUserId());
        return ok ? "Room swap approved. Student notified."
                  : "Error: Failed to approve room swap.";
    }

    /**
     * Rejects a room swap request.
     */
    public String rejectRoomSwap(RoomSwapRequest req) {
        if (currentWarden == null) return "Error: No warden session found.";
        boolean ok = db.rejectRoomSwap(req, currentWarden.getUserId());
        return ok ? "Room swap rejected. Student notified."
                  : "Error: Failed to reject room swap.";
    }

    // ══════════════════════════════════════════════════════════
    // UC-09 ── GENERATE FINE (Warden)
    // ══════════════════════════════════════════════════════════

    /** Returns all students for the warden's selection. */
    public List<Student> getAllStudents() {
        if (currentWarden == null) return List.of();
        return db.getAllStudents();
    }

    /** Returns fine policy entries: violation type → amount. */
    public List<String[]> getFinePolicies() {
        if (currentWarden == null) return List.of();
        return db.getFinePolicies();
    }

    /**
     * Issues a fine to a student, auto-calculated from the policy table.
     *
     * @param studentId     Target student
     * @param violationType Violation type (must match fine_policy)
     * @param amount        Pre-looked-up amount from policy
     */
    public String issueFine(String studentId, String violationType, double amount) {
        if (currentWarden == null) return "Error: No warden session found.";
        if (studentId == null || studentId.isBlank()) return "Error: Student must be selected.";
        if (violationType == null || violationType.isBlank()) return "Error: Violation type must be selected.";
        if (amount <= 0) return "Error: Fine amount must be greater than zero.";
        boolean ok = db.issueFine(studentId, violationType, amount, currentWarden.getUserId());
        return ok ? "Fine of PKR " + String.format("%.2f", amount) + " issued successfully!"
                  : "Error: Failed to issue fine.";
    }

    /** Returns all fines for the warden log. */
    public List<Fine> getAllFines() {
        if (currentWarden == null) return List.of();
        return db.getAllFines();
    }

    // ══════════════════════════════════════════════════════════
    // UC-10 ── EMERGENCY REALLOCATION (Warden)
    // ══════════════════════════════════════════════════════════

    /** Returns all rooms for the warden's selection. */
    public List<Room> getAllRooms() {
        if (currentWarden == null) return List.of();
        return db.getAllRooms();
    }

    /**
     * Immediately reallocates a student to an available room.
     * Marks the old room as UNAVAILABLE (emergency condition).
     */
    public String emergencyReallocate(String studentId, String newRoom, String reason) {
        if (currentWarden == null) return "Error: No warden session found.";
        if (studentId == null || studentId.isBlank()) return "Error: Student must be selected.";
        if (newRoom == null || newRoom.isBlank()) return "Error: Target room must be selected.";
        if (reason == null || reason.isBlank()) return "Error: Reason cannot be empty.";
        if (!db.isRoomAvailable(newRoom))
            return "Error: Room " + newRoom + " is not available for reallocation.";
        boolean ok = db.emergencyReallocate(studentId, newRoom, reason.trim(),
                                            currentWarden.getUserId());
        return ok ? "Emergency reallocation completed. Student notified."
                  : "Error: Failed to perform reallocation.";
    }

    // ══════════════════════════════════════════════════════════
    // SESSION ACCESSORS
    // ══════════════════════════════════════════════════════════

    public User             getCurrentUser()    { return currentUser; }
    public Student          getCurrentStudent() { return currentStudent; }
    public MaintenanceStaff getCurrentStaff()   { return currentStaff; }
    public Warden           getCurrentWarden()  { return currentWarden; }
    public boolean          isLoggedIn()        { return currentUser != null; }
}
