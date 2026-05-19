package com.hms.ui;

import com.hms.controllers.SystemController;
import com.hms.models.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

/**
 * Presentation Layer: Warden Dashboard
 *
 * Implements:
 *   UC-06  View and Filter All Complaints
 *   UC-07  Assign Complaint to Maintenance Staff
 *   UC-08  Approve / Reject Room Swap Requests
 *   UC-09  Generate Fine (auto-calculated from policy)
 *   UC-10  Emergency Room Reallocation
 */
public class WardenDashboard {

    private final Stage            stage;
    private final SystemController controller;
    private final Warden           warden;

    private BorderPane mainLayout;
    private String     activeSection = "overview";

    public WardenDashboard(Stage stage, SystemController controller) {
        this.stage      = stage;
        this.controller = controller;
        this.warden     = controller.getCurrentWarden();
    }

    public void show() {
        stage.setTitle("HMS — Warden Dashboard");
        stage.setWidth(1200);
        stage.setHeight(750);
        stage.setResizable(true);
        stage.centerOnScreen();

        mainLayout = new BorderPane();
        mainLayout.setStyle(HmsStyle.rootBg());
        mainLayout.setLeft(buildSidebar());
        mainLayout.setCenter(buildSection("overview"));

        Scene scene = new Scene(mainLayout, 1200, 750);
        stage.setScene(scene);
        stage.show();
    }

    // ══════════════════════════════════════════════════════════
    // SIDEBAR
    // ══════════════════════════════════════════════════════════

    private VBox buildSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.setPrefWidth(240);
        sidebar.setPadding(new Insets(24, 12, 24, 12));
        sidebar.setStyle(
            "-fx-background-color: " + HmsStyle.TEAL_DK + ";"
          + "-fx-border-color: " + HmsStyle.SAGE + " transparent " + HmsStyle.SAGE + " transparent;"
          + "-fx-border-width: 0 1 0 0;"
        );

        // Avatar
        Label avatar = new Label("🎓");
        avatar.setFont(Font.font(36));
        Label nameLabel = new Label(warden.getName());
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.web(HmsStyle.SILVER));
        Label roleLabel = new Label("Hostel Warden");
        roleLabel.setFont(Font.font("Segoe UI", 12));
        roleLabel.setTextFill(Color.web(HmsStyle.SAGE));

        VBox userBox = new VBox(4, avatar, nameLabel, roleLabel);
        userBox.setAlignment(Pos.CENTER);
        userBox.setPadding(new Insets(0, 0, 20, 0));

        String[][] navItems = {
            { "overview",   "🏠  Overview"              },
            { "complaints", "📋  View Complaints"        },
            { "assign",     "⚙   Assign Complaint"      },
            { "roomSwap",   "🔄  Room Swap Approvals"   },
            { "fines",      "💰  Issue Fine"             },
            { "emergency",  "🚨  Emergency Reallocation" },
        };

        VBox navBox = new VBox(4);
        for (String[] item : navItems) {
            String key   = item[0];
            String label = item[1];
            Button btn   = HmsStyle.navButton(label, key.equals(activeSection));
            btn.setOnAction(e -> navigate(key));
            navBox.getChildren().add(btn);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(userBox, navBox, spacer, buildLogoutBtn());
        return sidebar;
    }

    private Button buildLogoutBtn() {
        Button btn = HmsStyle.outlineButton("⎋  Logout");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            controller.logout();
            new LoginScreen(stage).show();
        });
        return btn;
    }

    private void navigate(String section) {
        activeSection = section;
        mainLayout.setLeft(buildSidebar());
        mainLayout.setCenter(buildSection(section));
    }

    private Node buildSection(String key) {
        return switch (key) {
            case "complaints" -> buildComplaintsSection();
            case "assign"     -> buildAssignComplaintSection();
            case "roomSwap"   -> buildRoomSwapSection();
            case "fines"      -> buildFinesSection();
            case "emergency"  -> buildEmergencySection();
            default           -> buildOverviewSection();
        };
    }

    // ══════════════════════════════════════════════════════════
    // OVERVIEW
    // ══════════════════════════════════════════════════════════

    private ScrollPane buildOverviewSection() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(28));

        Label pageTitle = HmsStyle.title("Welcome, " + warden.getName() + " 🎓");

        // Live stats
        List<Complaint>      allComplaints = controller.getAllComplaints(null, null);
        List<RoomSwapRequest> pendingSwaps = controller.getAllPendingRoomSwaps();
        List<Room>           rooms         = controller.getAllRooms();
        List<Fine>           fines         = controller.getAllFines();

        long pendingComplaints = allComplaints.stream()
            .filter(Complaint::isPending).count();
        long availableRooms = rooms.stream()
            .filter(Room::isAvailable).count();

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            statCard("📋", "Pending Complaints", String.valueOf(pendingComplaints)),
            statCard("🔄", "Pending Swaps",      String.valueOf(pendingSwaps.size())),
            statCard("🏠", "Available Rooms",    String.valueOf(availableRooms)),
            statCard("💰", "Fines Issued",        String.valueOf(fines.size()))
        );

        // Quick action buttons
        Label quickTitle = HmsStyle.heading("Quick Actions");
        HBox quickRow = new HBox(12);
        String[][] actions = {
            {"complaints", "📋 View Complaints"},
            {"assign",     "⚙  Assign Complaint"},
            {"roomSwap",   "🔄 Swap Approvals"},
            {"fines",      "💰 Issue Fine"},
            {"emergency",  "🚨 Emergency"},
        };
        for (String[] a : actions) {
            Button btn = HmsStyle.primaryButton(a[1]);
            btn.setPrefWidth(160);
            String key = a[0];
            btn.setOnAction(e -> navigate(key));
            quickRow.getChildren().add(btn);
        }

        // Recent high-priority complaints
        Label recentTitle = HmsStyle.heading("High-Priority Complaints");
        List<Complaint> highPriority = allComplaints.stream()
            .filter(Complaint::isHighPriority)
            .filter(c -> !c.isResolved())
            .limit(4)
            .toList();
        VBox recentBox = buildComplaintRows(highPriority, false);

        content.getChildren().addAll(
            pageTitle,
            statsRow,
            HmsStyle.divider(),
            quickTitle,
            quickRow,
            HmsStyle.divider(),
            recentTitle,
            recentBox.getChildren().isEmpty()
                ? wrapEmpty("No unresolved high-priority complaints.")
                : recentBox
        );

        return wrapScroll(content);
    }

    private VBox statCard(String icon, String title, String value) {
        VBox card = HmsStyle.card(170);
        card.setAlignment(Pos.CENTER);
        card.setSpacing(6);
        Label ico = new Label(icon);
        ico.setFont(Font.font(28));
        Label val = new Label(value);
        val.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        val.setTextFill(Color.web(HmsStyle.MINT));
        Label ttl = new Label(title);
        ttl.setFont(Font.font("Segoe UI", 12));
        ttl.setTextFill(Color.web(HmsStyle.SAGE));
        card.getChildren().addAll(ico, val, ttl);
        return card;
    }

    // ══════════════════════════════════════════════════════════
    // UC-06 ── VIEW & FILTER ALL COMPLAINTS
    // ══════════════════════════════════════════════════════════

    private ScrollPane buildComplaintsSection() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(28));

        Label pageTitle = HmsStyle.title("📋 All Complaints");

        // Filter bar
        ComboBox<String> priorityFilter = HmsStyle.comboBox("All Priorities");
        priorityFilter.getItems().addAll("ALL", "HIGH", "MEDIUM", "LOW");
        priorityFilter.setValue("ALL");
        priorityFilter.setPrefWidth(160);

        ComboBox<String> statusFilter = HmsStyle.comboBox("All Statuses");
        statusFilter.getItems().addAll("ALL", "PENDING", "ASSIGNED", "IN_PROGRESS", "RESOLVED");
        statusFilter.setValue("ALL");
        statusFilter.setPrefWidth(160);

        Button applyBtn = HmsStyle.primaryButton("Apply Filter");
        applyBtn.setPrefWidth(130);

        Button refreshBtn = HmsStyle.outlineButton("↻  Refresh");
        refreshBtn.setOnAction(e -> navigate("complaints"));

        HBox filterRow = new HBox(12, priorityFilter, statusFilter, applyBtn, new Region(), refreshBtn);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(filterRow.getChildren().get(3), Priority.ALWAYS);

        // Results container (replaced on filter apply)
        VBox[] resultsHolder = { buildComplaintRows(
            controller.getAllComplaints(null, null), false) };

        ScrollPane innerScroll = new ScrollPane(resultsHolder[0]);
        innerScroll.setFitToWidth(true);
        innerScroll.setPrefHeight(480);
        innerScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        applyBtn.setOnAction(e -> {
            String p = "ALL".equals(priorityFilter.getValue()) ? null : priorityFilter.getValue();
            String s = "ALL".equals(statusFilter.getValue())   ? null : statusFilter.getValue();
            List<Complaint> filtered = controller.getAllComplaints(p, s);
            VBox rows = buildComplaintRows(filtered, false);
            innerScroll.setContent(rows);
        });

        content.getChildren().addAll(
            pageTitle,
            filterRow,
            HmsStyle.divider(),
            innerScroll
        );

        return wrapScroll(content);
    }

    // ══════════════════════════════════════════════════════════
    // UC-07 ── ASSIGN COMPLAINT TO MAINTENANCE STAFF
    // ══════════════════════════════════════════════════════════

    private ScrollPane buildAssignComplaintSection() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(28));

        Label pageTitle = HmsStyle.title("⚙ Assign Complaint");
        Label subtitle  = HmsStyle.label(
            "Select a PENDING complaint and assign it to an available maintenance staff member.");
        subtitle.setTextFill(Color.web(HmsStyle.SAGE));

        VBox card = HmsStyle.card(700);
        card.setSpacing(16);

        // Complaint picker (shows only PENDING)
        List<Complaint>       pendingList = controller.getAllComplaints(null, "PENDING");
        List<MaintenanceStaff> staffList  = controller.getAllStaff();

        VBox complaintBox = new VBox(5);
        ComboBox<String> complaintCombo = HmsStyle.comboBox("Select a pending complaint…");
        complaintCombo.setMaxWidth(Double.MAX_VALUE);
        for (Complaint c : pendingList) {
            String label = "[" + c.getPriority() + "] " + c.getComplaintId()
                         + " — " + truncate(c.getDescription(), 60);
            complaintCombo.getItems().add(label);
        }
        complaintBox.getChildren().addAll(
            HmsStyle.fieldLabel("Pending Complaint *"), complaintCombo);

        // Staff picker
        VBox staffBox = new VBox(5);
        ComboBox<String> staffCombo = HmsStyle.comboBox("Select maintenance staff…");
        staffCombo.setMaxWidth(Double.MAX_VALUE);
        for (MaintenanceStaff s : staffList) {
            staffCombo.getItems().add(s.getStaffId() + " — " + s.getName()
                + " (" + s.getSpecialization() + ")  " + s.availabilityLabel());
        }
        staffBox.getChildren().addAll(HmsStyle.fieldLabel("Assign To *"), staffCombo);

        // Detail panel (shown when complaint selected)
        VBox detailBox = new VBox(6);
        detailBox.setPadding(new Insets(12));
        detailBox.setStyle(
            "-fx-background-color: #0D1F2A;"
          + "-fx-background-radius: 8;"
          + "-fx-border-color: " + HmsStyle.SAGE + "44;"
          + "-fx-border-radius: 8;"
        );
        detailBox.setVisible(false);
        Label detailTitle = HmsStyle.fieldLabel("Complaint Details");
        Label detailText  = HmsStyle.label("");
        detailText.setWrapText(true);
        detailBox.getChildren().addAll(detailTitle, detailText);

        // When complaint selected, fill detail panel
        complaintCombo.setOnAction(e -> {
            int idx = complaintCombo.getSelectionModel().getSelectedIndex();
            if (idx >= 0 && idx < pendingList.size()) {
                Complaint c = pendingList.get(idx);
                detailText.setText(
                    "ID: " + c.getComplaintId() + "\n"
                  + "Description: " + c.getDescription() + "\n"
                  + "Category: " + c.getCategory()
                  + "   Priority: " + c.getPriority()
                  + "   Submitted: " + c.formattedDate()
                );
                detailBox.setVisible(true);
            }
        });

        Label msgLabel = new Label();
        msgLabel.setWrapText(true);
        msgLabel.setVisible(false);

        Button assignBtn = HmsStyle.primaryButton("Assign Complaint");
        assignBtn.setPrefWidth(200);
        assignBtn.setOnAction(e -> {
            int ci = complaintCombo.getSelectionModel().getSelectedIndex();
            int si = staffCombo.getSelectionModel().getSelectedIndex();
            if (ci < 0) {
                showMsg(msgLabel, "Error: Please select a complaint.", true); return;
            }
            if (si < 0) {
                showMsg(msgLabel, "Error: Please select a staff member.", true); return;
            }
            String complaintId = pendingList.get(ci).getComplaintId();
            String staffId     = staffList.get(si).getStaffId();
            String result      = controller.assignComplaint(complaintId, staffId);
            boolean isError    = result.startsWith("Error");
            showMsg(msgLabel, result, isError);
            if (!isError) {
                // Refresh so the assigned complaint disappears from the list
                navigate("assign");
            }
        });

        if (pendingList.isEmpty()) {
            card.getChildren().add(wrapEmpty("No pending complaints to assign."));
        } else {
            card.getChildren().addAll(complaintBox, detailBox, staffBox, assignBtn, msgLabel);
        }

        content.getChildren().addAll(pageTitle, subtitle, card);
        return wrapScroll(content);
    }

    // ══════════════════════════════════════════════════════════
    // UC-08 ── APPROVE / REJECT ROOM SWAP REQUESTS
    // ══════════════════════════════════════════════════════════

    private ScrollPane buildRoomSwapSection() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(28));

        Label pageTitle = HmsStyle.title("🔄 Room Swap Approvals");
        Label subtitle  = HmsStyle.label(
            "Review pending requests. The system verifies room availability before approving.");
        subtitle.setTextFill(Color.web(HmsStyle.SAGE));

        Button refreshBtn = HmsStyle.outlineButton("↻  Refresh");
        refreshBtn.setOnAction(e -> navigate("roomSwap"));

        HBox header = new HBox(12, pageTitle, new Region(), refreshBtn);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        List<RoomSwapRequest> pendingSwaps = controller.getAllPendingRoomSwaps();

        VBox swapCards = new VBox(12);
        if (pendingSwaps.isEmpty()) {
            swapCards.getChildren().add(wrapEmpty("No pending room swap requests."));
        } else {
            for (RoomSwapRequest req : pendingSwaps) {
                swapCards.getChildren().add(buildSwapCard(req));
            }
        }

        content.getChildren().addAll(header, subtitle, HmsStyle.divider(), swapCards);
        return wrapScroll(content);
    }

    private VBox buildSwapCard(RoomSwapRequest req) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color: " + HmsStyle.TEAL_DK + ";"
          + "-fx-background-radius: 12;"
          + "-fx-border-color: " + HmsStyle.SAGE + "40;"
          + "-fx-border-radius: 12;"
        );

        // Info row
        HBox infoRow = new HBox(12);
        infoRow.setAlignment(Pos.CENTER_LEFT);

        Label roomLabel = HmsStyle.label("Room " + req.getCurrentRoom()
            + "  →  Room " + req.getRequestedRoom());
        roomLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        roomLabel.setTextFill(Color.web(HmsStyle.MINT));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusBadge = HmsStyle.statusBadge("PENDING");
        infoRow.getChildren().addAll(roomLabel, spacer, statusBadge);

        Label studentLabel = HmsStyle.label("Student ID: " + req.getStudentId());
        studentLabel.setTextFill(Color.web(HmsStyle.SAGE));

        Label reasonLabel = HmsStyle.label("Reason: " + req.getReason());
        reasonLabel.setWrapText(true);

        // Availability indicator
        List<Room> rooms = controller.getAllRooms();
        boolean targetAvailable = rooms.stream()
            .anyMatch(r -> r.getRoomNumber().equals(req.getRequestedRoom()) && r.isAvailable());

        Label availLabel = new Label(
            targetAvailable
            ? "✓ Room " + req.getRequestedRoom() + " is AVAILABLE"
            : "✗ Room " + req.getRequestedRoom() + " is NOT available"
        );
        availLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        availLabel.setTextFill(Color.web(targetAvailable ? HmsStyle.GREEN_OK : HmsStyle.RED_ERR));

        // Action buttons
        Label msgLabel = new Label();
        msgLabel.setVisible(false);
        msgLabel.setWrapText(true);

        Button approveBtn = HmsStyle.primaryButton("✔ Approve");
        approveBtn.setPrefWidth(130);
        approveBtn.setDisable(!targetAvailable);
        approveBtn.setOnAction(e -> {
            String result = controller.approveRoomSwap(req);
            showMsg(msgLabel, result, result.startsWith("Error"));
            if (!result.startsWith("Error")) navigate("roomSwap");
        });

        Button rejectBtn = HmsStyle.outlineButton("✗ Reject");
        rejectBtn.setPrefWidth(110);
        rejectBtn.setOnAction(e -> {
            String result = controller.rejectRoomSwap(req);
            showMsg(msgLabel, result, result.startsWith("Error"));
            if (!result.startsWith("Error")) navigate("roomSwap");
        });

        HBox actions = new HBox(10, approveBtn, rejectBtn, msgLabel);
        actions.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(infoRow, studentLabel, reasonLabel, availLabel,
                                   HmsStyle.divider(), actions);
        return card;
    }

    // ══════════════════════════════════════════════════════════
    // UC-09 ── GENERATE FINE
    // ══════════════════════════════════════════════════════════

    private ScrollPane buildFinesSection() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(28));

        Label pageTitle = HmsStyle.title("💰 Generate Fine");
        Label subtitle  = HmsStyle.label(
            "Select a student and violation type. Fine amount is auto-calculated from the policy table.");
        subtitle.setTextFill(Color.web(HmsStyle.SAGE));

        VBox issueCard = HmsStyle.card(700);
        issueCard.setSpacing(14);

        List<Student>  students = controller.getAllStudents();
        List<String[]> policies = controller.getFinePolicies();

        // Student picker
        VBox studentBox = new VBox(5);
        ComboBox<String> studentCombo = HmsStyle.comboBox("Select student…");
        studentCombo.setMaxWidth(Double.MAX_VALUE);
        for (Student s : students) {
            studentCombo.getItems().add(
                s.getStudentId() + " — " + s.getName() + "  (Room " + s.getRoomNumber() + ")");
        }
        studentBox.getChildren().addAll(HmsStyle.fieldLabel("Student *"), studentCombo);

        // Violation picker
        VBox violationBox = new VBox(5);
        ComboBox<String> violationCombo = HmsStyle.comboBox("Select violation type…");
        violationCombo.setMaxWidth(Double.MAX_VALUE);
        // Key: display label, fine amount in parallel list
        double[] selectedAmount = {0.0};
        for (String[] pol : policies) {
            violationCombo.getItems().add(pol[0] + "  —  PKR " + pol[1]);
        }
        violationBox.getChildren().addAll(HmsStyle.fieldLabel("Violation Type *"), violationCombo);

        // Auto-calculated amount display
        Label amountLabel = new Label("Fine Amount: —");
        amountLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        amountLabel.setTextFill(Color.web(HmsStyle.MINT));

        violationCombo.setOnAction(e -> {
            int idx = violationCombo.getSelectionModel().getSelectedIndex();
            if (idx >= 0 && idx < policies.size()) {
                double amt = Double.parseDouble(policies.get(idx)[1]);
                selectedAmount[0] = amt;
                amountLabel.setText("Fine Amount: PKR " + String.format("%.2f", amt)
                    + "  —  " + policies.get(idx)[2]);
            }
        });

        Label msgLabel = new Label();
        msgLabel.setWrapText(true);
        msgLabel.setVisible(false);

        Button issueBtn = HmsStyle.primaryButton("Issue Fine");
        issueBtn.setPrefWidth(180);
        issueBtn.setOnAction(e -> {
            int si = studentCombo.getSelectionModel().getSelectedIndex();
            int vi = violationCombo.getSelectionModel().getSelectedIndex();
            if (si < 0) { showMsg(msgLabel, "Error: Select a student.", true); return; }
            if (vi < 0) { showMsg(msgLabel, "Error: Select a violation type.", true); return; }
            String studentId     = students.get(si).getStudentId();
            String violationType = policies.get(vi)[0];
            double amount        = selectedAmount[0];
            String result = controller.issueFine(studentId, violationType, amount);
            showMsg(msgLabel, result, result.startsWith("Error"));
            if (!result.startsWith("Error")) {
                studentCombo.setValue(null);
                violationCombo.setValue(null);
                amountLabel.setText("Fine Amount: —");
                selectedAmount[0] = 0.0;
                // Refresh fine log below
                navigate("fines");
            }
        });

        issueCard.getChildren().addAll(
            studentBox, violationBox, amountLabel, issueBtn, msgLabel);

        // Fine history log
        Label histTitle = HmsStyle.heading("Fine History");
        List<Fine> allFines = controller.getAllFines();
        VBox fineRows = buildFineRows(allFines);

        content.getChildren().addAll(
            pageTitle, subtitle, issueCard,
            HmsStyle.divider(), histTitle, fineRows
        );
        return wrapScroll(content);
    }

    private VBox buildFineRows(List<Fine> fines) {
        VBox box = new VBox(8);
        if (fines.isEmpty()) {
            box.getChildren().add(wrapEmpty("No fines issued yet."));
            return box;
        }
        for (Fine f : fines) {
            HBox row = new HBox(12);
            row.setPadding(new Insets(12, 14, 12, 14));
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle(
                "-fx-background-color: " + HmsStyle.TEAL_DK + ";"
              + "-fx-background-radius: 8;"
              + "-fx-border-color: " + HmsStyle.SAGE + "33;"
              + "-fx-border-radius: 8;"
            );

            VBox info = new VBox(3);
            Label name = new Label(f.getStudentName() + "  (ID: " + f.getStudentId() + ")");
            name.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
            name.setTextFill(Color.web(HmsStyle.SILVER));
            Label violation = new Label(f.getViolationType() + "   " + f.formattedDate());
            violation.setFont(Font.font("Segoe UI", 11));
            violation.setTextFill(Color.web(HmsStyle.SAGE));
            info.getChildren().addAll(name, violation);
            HBox.setHgrow(info, Priority.ALWAYS);

            Label amount = new Label(f.formattedAmount());
            amount.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            amount.setTextFill(Color.web(HmsStyle.RED_ERR));

            row.getChildren().addAll(info, amount);
            box.getChildren().add(row);
        }
        return box;
    }

    // ══════════════════════════════════════════════════════════
    // UC-10 ── EMERGENCY ROOM REALLOCATION
    // ══════════════════════════════════════════════════════════

    private ScrollPane buildEmergencySection() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(28));

        Label pageTitle = HmsStyle.title("🚨 Emergency Reallocation");
        Label subtitle  = HmsStyle.label(
            "Immediately reassign a student to an available room. "
          + "The vacated room will be marked UNAVAILABLE.");
        subtitle.setTextFill(Color.web(HmsStyle.SAGE));

        // Warning banner
        Label warning = HmsStyle.alertBox(
            "⚠  This action is immediate and irreversible. "
          + "The student's old room will be marked UNAVAILABLE until reviewed.", true);

        VBox card = HmsStyle.card(700);
        card.setSpacing(14);

        List<Student> students     = controller.getAllStudents();
        List<Room>    availRooms   = controller.getAllRooms().stream()
            .filter(Room::isAvailable).toList();

        // Student picker
        VBox studentBox = new VBox(5);
        ComboBox<String> studentCombo = HmsStyle.comboBox("Select student to reallocate…");
        studentCombo.setMaxWidth(Double.MAX_VALUE);
        for (Student s : students) {
            studentCombo.getItems().add(
                s.getStudentId() + " — " + s.getName()
              + "  (Current Room: " + s.getRoomNumber() + ")");
        }
        studentBox.getChildren().addAll(HmsStyle.fieldLabel("Student *"), studentCombo);

        // Room picker (only available rooms)
        VBox roomBox = new VBox(5);
        ComboBox<String> roomCombo = HmsStyle.comboBox("Select target room…");
        roomCombo.setMaxWidth(Double.MAX_VALUE);
        for (Room r : availRooms) {
            roomCombo.getItems().add(
                "Room " + r.getRoomNumber() + "  (Block " + r.getBlock()
              + ", Capacity: " + r.getCapacity() + ")");
        }
        roomBox.getChildren().addAll(HmsStyle.fieldLabel("Target Room (Available Only) *"), roomCombo);

        // Reason
        VBox reasonBox = new VBox(5);
        TextArea reasonArea = HmsStyle.textArea("State the emergency reason…", 3);
        reasonBox.getChildren().addAll(HmsStyle.fieldLabel("Reason *"), reasonArea);

        Label msgLabel = new Label();
        msgLabel.setWrapText(true);
        msgLabel.setVisible(false);

        Button reallocBtn = HmsStyle.primaryButton("🚨 Perform Emergency Reallocation");
        reallocBtn.setPrefWidth(300);
        // Style as danger (red) to signal severity
        reallocBtn.setStyle(
            "-fx-background-color: " + HmsStyle.RED_ERR + ";"
          + "-fx-text-fill: #ffffff;"
          + "-fx-font-weight: bold;"
          + "-fx-font-size: 13;"
          + "-fx-background-radius: 8;"
          + "-fx-cursor: hand;"
        );
        reallocBtn.setDisable(availRooms.isEmpty());

        reallocBtn.setOnAction(e -> {
            int si = studentCombo.getSelectionModel().getSelectedIndex();
            int ri = roomCombo.getSelectionModel().getSelectedIndex();
            if (si < 0) { showMsg(msgLabel, "Error: Select a student.", true); return; }
            if (ri < 0) { showMsg(msgLabel, "Error: Select a target room.", true); return; }
            if (reasonArea.getText().isBlank()) {
                showMsg(msgLabel, "Error: Reason cannot be empty.", true); return;
            }
            String studentId = students.get(si).getStudentId();
            String newRoom   = availRooms.get(ri).getRoomNumber();
            String result    = controller.emergencyReallocate(studentId, newRoom, reasonArea.getText());
            showMsg(msgLabel, result, result.startsWith("Error"));
            if (!result.startsWith("Error")) {
                navigate("emergency");
            }
        });

        if (availRooms.isEmpty()) {
            card.getChildren().add(HmsStyle.alertBox(
                "No available rooms for reallocation. All rooms are occupied or unavailable.", true));
        } else {
            card.getChildren().addAll(
                studentBox, roomBox, reasonBox, reallocBtn, msgLabel);
        }

        // Room status board
        Label boardTitle = HmsStyle.heading("Room Status Board");
        VBox roomBoard   = buildRoomBoard(controller.getAllRooms());

        content.getChildren().addAll(
            pageTitle, subtitle, warning, card,
            HmsStyle.divider(), boardTitle, roomBoard
        );
        return wrapScroll(content);
    }

    private VBox buildRoomBoard(List<Room> rooms) {
        VBox box = new VBox(0);
        // Header row
        HBox header = new HBox();
        header.setPadding(new Insets(8, 12, 8, 12));
        header.setStyle("-fx-background-color: " + HmsStyle.NAVY + ";");
        Label[] headers = {
            HmsStyle.fieldLabel("Room"), HmsStyle.fieldLabel("Block"),
            HmsStyle.fieldLabel("Capacity"), HmsStyle.fieldLabel("Status")
        };
        double[] widths = {100, 100, 120, 200};
        for (int i = 0; i < headers.length; i++) {
            headers[i].setPrefWidth(widths[i]);
            header.getChildren().add(headers[i]);
        }
        box.getChildren().add(header);

        for (Room r : rooms) {
            HBox row = new HBox();
            row.setPadding(new Insets(8, 12, 8, 12));
            row.setStyle(
                "-fx-background-color: " + HmsStyle.TEAL_DK + ";"
              + "-fx-border-color: " + HmsStyle.NAVY + " transparent transparent transparent;"
              + "-fx-border-width: 1 0 0 0;"
            );
            Label rNum = HmsStyle.label("Room " + r.getRoomNumber()); rNum.setPrefWidth(100);
            Label blk  = HmsStyle.label("Block " + r.getBlock());    blk.setPrefWidth(100);
            Label cap  = HmsStyle.label(r.getCapacity() + " persons"); cap.setPrefWidth(120);
            Label status = HmsStyle.statusBadge(r.getStatus());
            row.getChildren().addAll(rNum, blk, cap, status);
            box.getChildren().add(row);
        }
        return box;
    }

    // ══════════════════════════════════════════════════════════
    // SHARED HELPERS
    // ══════════════════════════════════════════════════════════

    /** Builds a list of complaint rows (used in overview and complaints view). */
    private VBox buildComplaintRows(List<Complaint> complaints, boolean compact) {
        VBox box = new VBox(8);
        if (complaints.isEmpty()) {
            box.getChildren().add(wrapEmpty("No complaints found."));
            return box;
        }
        for (Complaint c : complaints) {
            HBox row = new HBox(12);
            row.setPadding(new Insets(12, 14, 12, 14));
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle(
                "-fx-background-color: " + HmsStyle.TEAL_DK + ";"
              + "-fx-background-radius: 10;"
              + "-fx-border-color: " + HmsStyle.SAGE + "33;"
              + "-fx-border-radius: 10;"
            );

            VBox info = new VBox(4);
            Label desc = new Label(truncate(c.getDescription(), 80));
            desc.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
            desc.setTextFill(Color.web(HmsStyle.SILVER));
            Label meta = new Label(
                "ID: " + c.getComplaintId()
              + "   Category: " + c.getCategory()
              + "   " + c.formattedDate()
            );
            meta.setFont(Font.font("Segoe UI", 11));
            meta.setTextFill(Color.web(HmsStyle.SAGE));
            info.getChildren().addAll(desc, meta);
            HBox.setHgrow(info, Priority.ALWAYS);

            VBox badges = new VBox(4);
            badges.setAlignment(Pos.CENTER_RIGHT);
            badges.getChildren().addAll(
                HmsStyle.statusBadge(c.getStatus()),
                HmsStyle.priorityBadge(c.getPriority())
            );

            row.getChildren().addAll(info, badges);
            box.getChildren().add(row);
        }
        return box;
    }

    private void showMsg(Label label, String message, boolean isError) {
        label.setTextFill(Color.web(isError ? HmsStyle.RED_ERR : HmsStyle.GREEN_OK));
        label.setText(message);
        label.setVisible(true);
    }

    private ScrollPane wrapScroll(VBox content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return sp;
    }

    private Label wrapEmpty(String text) {
        Label lbl = HmsStyle.label(text);
        lbl.setTextFill(Color.web(HmsStyle.SAGE));
        lbl.setPadding(new Insets(20, 0, 0, 0));
        return lbl;
    }

    private String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max) + "…" : s;
    }
}
