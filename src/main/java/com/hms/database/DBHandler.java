package com.hms.database;

import com.hms.models.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * GRASP PURE FABRICATION PATTERN
 * ────────────────────────────────
 * DBHandler is a "fabricated" class with no real-world counterpart.
 * It exists solely to handle SQL operations, keeping domain classes
 * (Student, Complaint, etc.) free from database coupling.
 *
 * Architecture layer: Data / Persistence Layer
 * Called by: SystemController (Business Logic Layer)
 */
public class DBHandler {

    private final Connection conn;

    public DBHandler() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // ══════════════════════════════════════════════════════════
    // UC-01 ── AUTHENTICATION
    // ══════════════════════════════════════════════════════════

    /**
     * Authenticates a user by email and password.
     *
     * @return User object if found, null otherwise
     */
    public User authenticate(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("[DBHandler] authenticate error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Loads the Student profile linked to a given userId.
     */
    public Student getStudentByUserId(String userId) {
        String sql = "SELECT s.*, u.name FROM students s "
                   + "JOIN users u ON s.user_id = u.user_id "
                   + "WHERE s.user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapStudent(rs);
        } catch (SQLException e) {
            System.err.println("[DBHandler] getStudentByUserId error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Loads the MaintenanceStaff profile linked to a given userId.
     */
    public MaintenanceStaff getStaffByUserId(String userId) {
        String sql = "SELECT ms.*, u.name FROM maintenance_staff ms "
                   + "JOIN users u ON ms.user_id = u.user_id "
                   + "WHERE ms.user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapStaff(rs);
        } catch (SQLException e) {
            System.err.println("[DBHandler] getStaffByUserId error: " + e.getMessage());
        }
        return null;
    }

    // ══════════════════════════════════════════════════════════
    // UC-02 ── SUBMIT COMPLAINT
    // ══════════════════════════════════════════════════════════

    /**
     * Inserts a new complaint into the database.
     *
     * @param complaint Complaint object (complaintId auto-generated)
     * @return true if insert succeeded
     */
    public boolean submitComplaint(Complaint complaint) {
        String id  = "CMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String sql = "INSERT INTO complaints "
                   + "(complaint_id, student_id, description, category, priority, status) "
                   + "VALUES (?, ?, ?, ?, ?, 'PENDING')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, complaint.getStudentId());
            ps.setString(3, complaint.getDescription());
            ps.setString(4, complaint.getCategory());
            ps.setString(5, complaint.getPriority());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DBHandler] submitComplaint error: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════════════════
    // UC-05 ── VIEW COMPLAINT STATUS
    // ══════════════════════════════════════════════════════════

    /**
     * Returns all complaints for a given student.
     */
    public List<Complaint> getComplaintsByStudent(String studentId) {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE student_id = ? ORDER BY submitted_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapComplaint(rs));
        } catch (SQLException e) {
            System.err.println("[DBHandler] getComplaintsByStudent error: " + e.getMessage());
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    // UC-03 ── REQUEST ROOM SWAP
    // ══════════════════════════════════════════════════════════

    /**
     * Submits a room swap request for a student.
     */
    public boolean submitRoomSwapRequest(RoomSwapRequest req) {
        String id  = "RSR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String sql = "INSERT INTO room_swap_requests "
                   + "(request_id, student_id, current_room, requested_room, reason, status) "
                   + "VALUES (?, ?, ?, ?, ?, 'PENDING')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, req.getStudentId());
            ps.setString(3, req.getCurrentRoom());
            ps.setString(4, req.getRequestedRoom());
            ps.setString(5, req.getReason());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DBHandler] submitRoomSwapRequest error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns all room swap requests for a student.
     */
    public List<RoomSwapRequest> getRoomSwapsByStudent(String studentId) {
        List<RoomSwapRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM room_swap_requests WHERE student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRoomSwap(rs));
        } catch (SQLException e) {
            System.err.println("[DBHandler] getRoomSwapsByStudent error: " + e.getMessage());
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    // UC-04 ── REGISTER VISITOR
    // ══════════════════════════════════════════════════════════

    /**
     * Logs a new visitor entry for a student.
     */
    public boolean registerVisitor(VisitorLog visitor) {
        String id  = "VIS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String sql = "INSERT INTO visitor_log "
                   + "(visit_id, student_id, visitor_name, visitor_cnic, purpose) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, visitor.getStudentId());
            ps.setString(3, visitor.getVisitorName());
            ps.setString(4, visitor.getVisitorCnic());
            ps.setString(5, visitor.getPurpose());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DBHandler] registerVisitor error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns all visitor logs for a student.
     */
    public List<VisitorLog> getVisitorsByStudent(String studentId) {
        List<VisitorLog> list = new ArrayList<>();
        String sql = "SELECT * FROM visitor_log WHERE student_id = ? ORDER BY visit_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapVisitor(rs));
        } catch (SQLException e) {
            System.err.println("[DBHandler] getVisitorsByStudent error: " + e.getMessage());
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    // UC-11 ── VIEW ASSIGNED TASKS (Maintenance Staff)
    // ══════════════════════════════════════════════════════════

    /**
     * Returns all maintenance tasks assigned to a staff member,
     * with complaint details via JOIN.
     */
    public List<MaintenanceTask> getTasksByStaff(String staffId) {
        List<MaintenanceTask> list = new ArrayList<>();
        String sql = "SELECT mt.*, c.description AS comp_desc, c.category, c.priority "
                   + "FROM maintenance_tasks mt "
                   + "JOIN complaints c ON mt.complaint_id = c.complaint_id "
                   + "WHERE mt.staff_id = ? "
                   + "ORDER BY mt.assigned_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staffId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapTask(rs));
        } catch (SQLException e) {
            System.err.println("[DBHandler] getTasksByStaff error: " + e.getMessage());
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    // UC-12/13 ── UPDATE TASK STATUS / RESOLVE COMPLAINT
    // ══════════════════════════════════════════════════════════

    /**
     * Updates a maintenance task status (IN_PROGRESS / COMPLETED / CLOSED).
     * When COMPLETED, also sets the linked complaint to RESOLVED.
     *
     * @param taskId    The task to update
     * @param newStatus One of: IN_PROGRESS, COMPLETED, CLOSED
     * @return true if update succeeded
     */
    public boolean updateTaskStatus(String taskId, String newStatus) {
        String updateTask = "UPDATE maintenance_tasks SET status = ? WHERE task_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateTask)) {
            ps.setString(1, newStatus);
            ps.setString(2, taskId);
            boolean ok = ps.executeUpdate() > 0;

            // UC-13: Auto-resolve the linked complaint when task is COMPLETED
            if (ok && "COMPLETED".equals(newStatus)) {
                resolveLinkedComplaint(taskId);
            }
            return ok;
        } catch (SQLException e) {
            System.err.println("[DBHandler] updateTaskStatus error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sets the complaint linked to a task as RESOLVED.
     */
    private void resolveLinkedComplaint(String taskId) {
        String sql = "UPDATE complaints c "
                   + "JOIN maintenance_tasks mt ON c.complaint_id = mt.complaint_id "
                   + "SET c.status = 'RESOLVED' "
                   + "WHERE mt.task_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, taskId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DBHandler] resolveLinkedComplaint error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    // OBJECT MAPPERS  (ResultSet → Domain Object)
    // ══════════════════════════════════════════════════════════

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getString("user_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("role")
        );
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        return new Student(
            rs.getString("student_id"),
            rs.getString("user_id"),
            rs.getString("room_number"),
            rs.getString("program"),
            rs.getString("contact_number"),
            rs.getDouble("outstanding_dues"),
            rs.getString("name")
        );
    }

    private MaintenanceStaff mapStaff(ResultSet rs) throws SQLException {
        return new MaintenanceStaff(
            rs.getString("staff_id"),
            rs.getString("user_id"),
            rs.getString("specialization"),
            rs.getString("contact_number"),
            rs.getBoolean("is_available"),
            rs.getString("name")
        );
    }

    private Complaint mapComplaint(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("submitted_at");
        return new Complaint(
            rs.getString("complaint_id"),
            rs.getString("student_id"),
            rs.getString("description"),
            rs.getString("category"),
            rs.getString("priority"),
            rs.getString("status"),
            ts != null ? ts.toLocalDateTime() : LocalDateTime.now()
        );
    }

    private MaintenanceTask mapTask(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("assigned_at");
        MaintenanceTask task = new MaintenanceTask(
            rs.getString("task_id"),
            rs.getString("complaint_id"),
            rs.getString("staff_id"),
            rs.getString("status"),
            ts != null ? ts.toLocalDateTime() : LocalDateTime.now()
        );
        // Enrich with complaint details from JOIN
        task.setComplaintDescription(rs.getString("comp_desc"));
        task.setComplaintCategory(rs.getString("category"));
        task.setComplaintPriority(rs.getString("priority"));
        return task;
    }

    // ══════════════════════════════════════════════════════════
    // WARDEN ── UC-06  VIEW / FILTER ALL COMPLAINTS
    // ══════════════════════════════════════════════════════════

    /**
     * Returns all complaints, optionally filtered by priority and/or status.
     * Pass null to skip that filter.
     */
    public List<Complaint> getAllComplaints(String priorityFilter, String statusFilter) {
        List<Complaint> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT c.*, s.student_id FROM complaints c "
          + "JOIN students s ON c.student_id = s.student_id WHERE 1=1"
        );
        if (priorityFilter != null && !priorityFilter.isBlank())
            sql.append(" AND c.priority = '").append(priorityFilter).append("'");
        if (statusFilter != null && !statusFilter.isBlank())
            sql.append(" AND c.status = '").append(statusFilter).append("'");
        sql.append(" ORDER BY c.submitted_at DESC");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapComplaint(rs));
        } catch (SQLException e) {
            System.err.println("[DBHandler] getAllComplaints error: " + e.getMessage());
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    // WARDEN ── UC-07  ASSIGN COMPLAINT
    // ══════════════════════════════════════════════════════════

    /**
     * Sets complaint status to ASSIGNED, creates a MaintenanceTask,
     * and logs a notification for the assigned staff member.
     *
     * @return true if all three operations succeeded
     */
    public boolean assignComplaint(String complaintId, String staffId, String wardenUserId) {
        // 1. Update complaint status → ASSIGNED
        String updateComplaint = "UPDATE complaints SET status = 'ASSIGNED' WHERE complaint_id = ?";
        // 2. Create maintenance task
        String insertTask =
            "INSERT INTO maintenance_tasks (task_id, complaint_id, staff_id, status) "
          + "VALUES (?, ?, ?, 'ASSIGNED')";
        // 3. Notify staff member
        String insertNotif =
            "INSERT INTO notifications (notif_id, recipient, message) VALUES (?, ?, ?)";

        try {
            conn.setAutoCommit(false);

            // Step 1
            try (PreparedStatement ps = conn.prepareStatement(updateComplaint)) {
                ps.setString(1, complaintId);
                if (ps.executeUpdate() == 0) { conn.rollback(); return false; }
            }

            // Step 2 — generate task ID
            String taskId = "TSK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            try (PreparedStatement ps = conn.prepareStatement(insertTask)) {
                ps.setString(1, taskId);
                ps.setString(2, complaintId);
                ps.setString(3, staffId);
                ps.executeUpdate();
            }

            // Step 3 — look up staff user_id for notification recipient
            String staffUserId = getStaffUserId(staffId);
            if (staffUserId != null) {
                String notifId  = "NTF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String message  = "You have been assigned a new maintenance task for complaint "
                                + complaintId + ".";
                try (PreparedStatement ps = conn.prepareStatement(insertNotif)) {
                    ps.setString(1, notifId);
                    ps.setString(2, staffUserId);
                    ps.setString(3, message);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            System.err.println("[DBHandler] assignComplaint error: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { /* ignore */ }
        }
    }

    /** Helper: get user_id for a staff member. */
    private String getStaffUserId(String staffId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT user_id FROM maintenance_staff WHERE staff_id = ?")) {
            ps.setString(1, staffId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("user_id");
        } catch (SQLException e) { /* ignore */ }
        return null;
    }

    /** Returns all maintenance staff members. */
    public List<MaintenanceStaff> getAllStaff() {
        List<MaintenanceStaff> list = new ArrayList<>();
        String sql = "SELECT ms.*, u.name FROM maintenance_staff ms "
                   + "JOIN users u ON ms.user_id = u.user_id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapStaff(rs));
        } catch (SQLException e) {
            System.err.println("[DBHandler] getAllStaff error: " + e.getMessage());
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    // WARDEN ── UC-08  APPROVE / REJECT ROOM SWAP
    // ══════════════════════════════════════════════════════════

    /** Returns all pending room swap requests. */
    public List<RoomSwapRequest> getAllPendingRoomSwaps() {
        List<RoomSwapRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM room_swap_requests WHERE status = 'PENDING'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRoomSwap(rs));
        } catch (SQLException e) {
            System.err.println("[DBHandler] getAllPendingRoomSwaps error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Checks whether a room exists in the rooms table and is AVAILABLE.
     */
    public boolean isRoomAvailable(String roomNumber) {
        String sql = "SELECT status FROM rooms WHERE room_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return "AVAILABLE".equalsIgnoreCase(rs.getString("status"));
        } catch (SQLException e) {
            System.err.println("[DBHandler] isRoomAvailable error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Approves a room swap: updates request status, updates the student's
     * room_number, marks old room AVAILABLE and new room OCCUPIED,
     * and notifies the student.
     */
    public boolean approveRoomSwap(RoomSwapRequest req, String wardenUserId) {
        String updateReq    = "UPDATE room_swap_requests SET status = 'APPROVED' WHERE request_id = ?";
        String updateStudent= "UPDATE students SET room_number = ? WHERE student_id = ?";
        String updateOldRoom= "UPDATE rooms SET status = 'AVAILABLE' WHERE room_number = ?";
        String updateNewRoom= "UPDATE rooms SET status = 'OCCUPIED'  WHERE room_number = ?";
        String insertNotif  = "INSERT INTO notifications (notif_id, recipient, message) VALUES (?,?,?)";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(updateReq)) {
                ps.setString(1, req.getRequestId()); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(updateStudent)) {
                ps.setString(1, req.getRequestedRoom());
                ps.setString(2, req.getStudentId());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(updateOldRoom)) {
                ps.setString(1, req.getCurrentRoom()); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(updateNewRoom)) {
                ps.setString(1, req.getRequestedRoom()); ps.executeUpdate();
            }
            // Notify the student
            String studentUserId = getStudentUserId(req.getStudentId());
            if (studentUserId != null) {
                String notifId = "NTF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String msg = "Your room swap request from Room " + req.getCurrentRoom()
                           + " to Room " + req.getRequestedRoom() + " has been APPROVED.";
                try (PreparedStatement ps = conn.prepareStatement(insertNotif)) {
                    ps.setString(1, notifId);
                    ps.setString(2, studentUserId);
                    ps.setString(3, msg);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            System.err.println("[DBHandler] approveRoomSwap error: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { /* ignore */ }
        }
    }

    /**
     * Rejects a room swap request and notifies the student.
     */
    public boolean rejectRoomSwap(RoomSwapRequest req, String wardenUserId) {
        String updateReq   = "UPDATE room_swap_requests SET status = 'REJECTED' WHERE request_id = ?";
        String insertNotif = "INSERT INTO notifications (notif_id, recipient, message) VALUES (?,?,?)";
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(updateReq)) {
                ps.setString(1, req.getRequestId()); ps.executeUpdate();
            }
            String studentUserId = getStudentUserId(req.getStudentId());
            if (studentUserId != null) {
                String notifId = "NTF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String msg = "Your room swap request from Room " + req.getCurrentRoom()
                           + " to Room " + req.getRequestedRoom() + " has been REJECTED.";
                try (PreparedStatement ps = conn.prepareStatement(insertNotif)) {
                    ps.setString(1, notifId);
                    ps.setString(2, studentUserId);
                    ps.setString(3, msg);
                    ps.executeUpdate();
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            System.err.println("[DBHandler] rejectRoomSwap error: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { /* ignore */ }
        }
    }

    /** Helper: get user_id for a student. */
    private String getStudentUserId(String studentId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT user_id FROM students WHERE student_id = ?")) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("user_id");
        } catch (SQLException e) { /* ignore */ }
        return null;
    }

    // ══════════════════════════════════════════════════════════
    // WARDEN ── UC-09  GENERATE FINE
    // ══════════════════════════════════════════════════════════

    /** Returns all students (for the warden's selection list). */
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, u.name FROM students s "
                   + "JOIN users u ON s.user_id = u.user_id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapStudent(rs));
        } catch (SQLException e) {
            System.err.println("[DBHandler] getAllStudents error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns the fine_policy table as a list of String[2] arrays
     * where [0] = violation_type and [1] = amount.
     */
    public List<String[]> getFinePolicies() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT violation_type, amount, description FROM fine_policy ORDER BY violation_type";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("violation_type"),
                    String.valueOf(rs.getDouble("amount")),
                    rs.getString("description")
                });
            }
        } catch (SQLException e) {
            System.err.println("[DBHandler] getFinePolicies error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Issues a fine: inserts into fines table, increments student outstanding_dues,
     * and notifies the student.
     */
    public boolean issueFine(String studentId, String violationType,
                             double amount, String wardenUserId) {
        String insertFine  = "INSERT INTO fines (fine_id, student_id, violation_type, amount, issued_by) "
                           + "VALUES (?, ?, ?, ?, ?)";
        String updateDues  = "UPDATE students SET outstanding_dues = outstanding_dues + ? "
                           + "WHERE student_id = ?";
        String insertNotif = "INSERT INTO notifications (notif_id, recipient, message) VALUES (?,?,?)";

        try {
            conn.setAutoCommit(false);

            String fineId = "FIN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            try (PreparedStatement ps = conn.prepareStatement(insertFine)) {
                ps.setString(1, fineId);
                ps.setString(2, studentId);
                ps.setString(3, violationType);
                ps.setDouble(4, amount);
                ps.setString(5, wardenUserId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(updateDues)) {
                ps.setDouble(1, amount);
                ps.setString(2, studentId);
                ps.executeUpdate();
            }
            String studentUserId = getStudentUserId(studentId);
            if (studentUserId != null) {
                String notifId = "NTF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String msg = "A fine of PKR " + String.format("%.2f", amount)
                           + " has been issued for: " + violationType + ".";
                try (PreparedStatement ps = conn.prepareStatement(insertNotif)) {
                    ps.setString(1, notifId);
                    ps.setString(2, studentUserId);
                    ps.setString(3, msg);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            System.err.println("[DBHandler] issueFine error: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { /* ignore */ }
        }
    }

    /** Returns all fines issued, ordered newest first. */
    public List<Fine> getAllFines() {
        List<Fine> list = new ArrayList<>();
        String sql = "SELECT f.*, u.name AS student_name "
                   + "FROM fines f JOIN students s ON f.student_id = s.student_id "
                   + "JOIN users u ON s.user_id = u.user_id "
                   + "ORDER BY f.issued_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapFine(rs));
        } catch (SQLException e) {
            System.err.println("[DBHandler] getAllFines error: " + e.getMessage());
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    // WARDEN ── UC-10  EMERGENCY REALLOCATION
    // ══════════════════════════════════════════════════════════

    /** Returns all rooms in the system. */
    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRoom(rs));
        } catch (SQLException e) {
            System.err.println("[DBHandler] getAllRooms error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Emergency reallocation: moves student to new room immediately,
     * marks old room UNAVAILABLE (emergency), marks new room OCCUPIED,
     * logs the event, and notifies the student.
     */
    public boolean emergencyReallocate(String studentId, String newRoom,
                                       String reason, String wardenUserId) {
        // Fetch current room first
        String getCurrentRoom = "SELECT room_number FROM students WHERE student_id = ?";
        String updateStudent  = "UPDATE students SET room_number = ? WHERE student_id = ?";
        String markOldRoom    = "UPDATE rooms SET status = 'UNAVAILABLE' WHERE room_number = ?";
        String markNewRoom    = "UPDATE rooms SET status = 'OCCUPIED'   WHERE room_number = ?";
        String logRealloc     =
            "INSERT INTO emergency_reallocations "
          + "(realloc_id, student_id, old_room, new_room, reason, reallocated_by) "
          + "VALUES (?, ?, ?, ?, ?, ?)";
        String insertNotif = "INSERT INTO notifications (notif_id, recipient, message) VALUES (?,?,?)";

        try {
            conn.setAutoCommit(false);

            // Get old room
            String oldRoom = null;
            try (PreparedStatement ps = conn.prepareStatement(getCurrentRoom)) {
                ps.setString(1, studentId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) oldRoom = rs.getString("room_number");
            }
            if (oldRoom == null) { conn.rollback(); return false; }

            // Move student
            try (PreparedStatement ps = conn.prepareStatement(updateStudent)) {
                ps.setString(1, newRoom); ps.setString(2, studentId); ps.executeUpdate();
            }
            // Mark old room unavailable
            try (PreparedStatement ps = conn.prepareStatement(markOldRoom)) {
                ps.setString(1, oldRoom); ps.executeUpdate();
            }
            // Mark new room occupied
            try (PreparedStatement ps = conn.prepareStatement(markNewRoom)) {
                ps.setString(1, newRoom); ps.executeUpdate();
            }
            // Log
            String reallocId = "REA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            try (PreparedStatement ps = conn.prepareStatement(logRealloc)) {
                ps.setString(1, reallocId);
                ps.setString(2, studentId);
                ps.setString(3, oldRoom);
                ps.setString(4, newRoom);
                ps.setString(5, reason);
                ps.setString(6, wardenUserId);
                ps.executeUpdate();
            }
            // Notify
            String studentUserId = getStudentUserId(studentId);
            if (studentUserId != null) {
                String notifId = "NTF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String msg = "EMERGENCY: You have been reallocated from Room " + oldRoom
                           + " to Room " + newRoom + ". Reason: " + reason;
                try (PreparedStatement ps = conn.prepareStatement(insertNotif)) {
                    ps.setString(1, notifId);
                    ps.setString(2, studentUserId);
                    ps.setString(3, msg);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            System.err.println("[DBHandler] emergencyReallocate error: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { /* ignore */ }
        }
    }

    // ══════════════════════════════════════════════════════════
    // WARDEN ── SESSION LOAD
    // ══════════════════════════════════════════════════════════

    /** Loads Warden profile by userId. */
    public com.hms.models.Warden getWardenByUserId(String userId) {
        String sql = "SELECT user_id, name, email FROM users WHERE user_id = ? AND role = 'WARDEN'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new com.hms.models.Warden(
                    rs.getString("user_id"),
                    rs.getString("name"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            System.err.println("[DBHandler] getWardenByUserId error: " + e.getMessage());
        }
        return null;
    }

    // ══════════════════════════════════════════════════════════
    // OBJECT MAPPERS (new)
    // ══════════════════════════════════════════════════════════

    private Room mapRoom(ResultSet rs) throws SQLException {
        return new Room(
            rs.getString("room_number"),
            rs.getInt("capacity"),
            rs.getString("status"),
            rs.getString("block")
        );
    }

    private Fine mapFine(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("issued_at");
        return new Fine(
            rs.getString("fine_id"),
            rs.getString("student_id"),
            rs.getString("student_name"),
            rs.getString("violation_type"),
            rs.getDouble("amount"),
            rs.getString("issued_by"),
            ts != null ? ts.toLocalDateTime() : java.time.LocalDateTime.now()
        );
    }

    private RoomSwapRequest mapRoomSwap(ResultSet rs) throws SQLException {
        return new RoomSwapRequest(
            rs.getString("request_id"),
            rs.getString("student_id"),
            rs.getString("current_room"),
            rs.getString("requested_room"),
            rs.getString("reason"),
            rs.getString("status")
        );
    }

    private VisitorLog mapVisitor(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("visit_date");
        return new VisitorLog(
            rs.getString("visit_id"),
            rs.getString("student_id"),
            rs.getString("visitor_name"),
            rs.getString("visitor_cnic"),
            rs.getString("purpose"),
            ts != null ? ts.toLocalDateTime() : LocalDateTime.now()
        );
    }
}
