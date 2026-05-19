package com.hms.ui;

import com.hms.controllers.SystemController;
import com.hms.models.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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
 * Presentation Layer: Student Dashboard
 *
 * Implements:
 *   UC-02 Submit Complaint
 *   UC-03 Request Room Swap
 *   UC-04 Register Visitor
 *   UC-05 View Complaint Status
 */
public class StudentDashboard {

    private final Stage            stage;
    private final SystemController controller;
    private final Student          student;

    // ── Sidebar navigation state ───────────────────────────────
    private BorderPane  mainLayout;
    private String      activeSection = "overview";

    public StudentDashboard(Stage stage, SystemController controller) {
        this.stage      = stage;
        this.controller = controller;
        this.student    = controller.getCurrentStudent();
    }

    public void show() {
        stage.setTitle("HMS — Student Dashboard");
        stage.setWidth(1100);
        stage.setHeight(700);
        stage.setResizable(true);
        stage.centerOnScreen();

        mainLayout = new BorderPane();
        mainLayout.setStyle(HmsStyle.rootBg());
        mainLayout.setLeft(buildSidebar());
        mainLayout.setCenter(buildSection("overview"));

        Scene scene = new Scene(mainLayout, 1100, 700);
        stage.setScene(scene);
        stage.show();
    }

    // ══════════════════════════════════════════════════════════
    // SIDEBAR
    // ══════════════════════════════════════════════════════════

    private VBox buildSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.setPrefWidth(230);
        sidebar.setPadding(new Insets(24, 12, 24, 12));
        sidebar.setStyle(
            "-fx-background-color: " + HmsStyle.TEAL_DK + ";"
          + "-fx-border-color: " + HmsStyle.SAGE + " transparent " + HmsStyle.SAGE + " transparent;"
          + "-fx-border-width: 0 1 0 0;"
        );

        // Avatar / user info
        Label avatar = new Label("👤");
        avatar.setFont(Font.font(36));
        Label nameLabel = new Label(student.getName());
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.web(HmsStyle.SILVER));
        Label roleLabel = new Label("Student  •  Room " + student.getRoomNumber());
        roleLabel.setFont(Font.font("Segoe UI", 12));
        roleLabel.setTextFill(Color.web(HmsStyle.SAGE));

        VBox userBox = new VBox(3, avatar, nameLabel, roleLabel);
        userBox.setAlignment(Pos.CENTER);
        userBox.setPadding(new Insets(0, 0, 20, 0));

        // Nav items
        String[][] navItems = {
            { "overview",    "🏠  Overview"          },
            { "complaint",   "📋  Submit Complaint"  },
            { "myComplaints","🔍  My Complaints"     },
            { "roomSwap",    "🔄  Room Swap Request" },
            { "visitor",     "👥  Register Visitor"  },
        };

        VBox navBox = new VBox(4);
        for (String[] item : navItems) {
            String key   = item[0];
            String label = item[1];
            Button btn   = HmsStyle.navButton(label, key.equals(activeSection));
            btn.setOnAction(e -> navigate(key));
            navBox.getChildren().add(btn);
        }

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Dues warning
        if (student.hasDues()) {
            Label duesWarn = new Label("⚠ Outstanding: " + student.formattedDues());
            duesWarn.setWrapText(true);
            duesWarn.setPadding(new Insets(8, 12, 8, 12));
            duesWarn.setFont(Font.font("Segoe UI", 12));
            duesWarn.setStyle(
                "-fx-background-color: #2a1a0a;"
              + "-fx-text-fill: #f0a050;"
              + "-fx-background-radius: 8;"
            );
            sidebar.getChildren().addAll(userBox, navBox, spacer, duesWarn, buildLogoutBtn());
        } else {
            sidebar.getChildren().addAll(userBox, navBox, spacer, buildLogoutBtn());
        }

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

    // ══════════════════════════════════════════════════════════
    // SECTION ROUTER
    // ══════════════════════════════════════════════════════════

    private Node buildSection(String key) {
        return switch (key) {
            case "complaint"    -> buildSubmitComplaintSection();
            case "myComplaints" -> buildMyComplaintsSection();
            case "roomSwap"     -> buildRoomSwapSection();
            case "visitor"      -> buildVisitorSection();
            default             -> buildOverviewSection();
        };
    }

    // ══════════════════════════════════════════════════════════
    // OVERVIEW
    // ══════════════════════════════════════════════════════════

    private ScrollPane buildOverviewSection() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(28));

        Label pageTitle = HmsStyle.title("Welcome, " + student.getName() + " 👋");

        // Stats row
        HBox statsRow = new HBox(16);
        statsRow.setFillHeight(false);

        List<Complaint> complaints = controller.getMyComplaints();
        long pending  = complaints.stream().filter(Complaint::isPending).count();
        long resolved = complaints.stream().filter(Complaint::isResolved).count();

        statsRow.getChildren().addAll(
            statCard("📋", "Total Complaints",  String.valueOf(complaints.size())),
            statCard("⏳", "Pending",           String.valueOf(pending)),
            statCard("✅", "Resolved",          String.valueOf(resolved)),
            statCard("🏠", "Room Number",       student.getRoomNumber())
        );

        // Quick links
        Label quickTitle = HmsStyle.heading("Quick Actions");
        HBox quickRow = new HBox(12);
        String[][] actions = {
            {"complaint", "📋 Submit Complaint"},
            {"roomSwap",  "🔄 Room Swap"},
            {"visitor",   "👥 Register Visitor"},
        };
        for (String[] a : actions) {
            Button btn = HmsStyle.primaryButton(a[1]);
            btn.setPrefWidth(180);
            String key = a[0];
            btn.setOnAction(e -> navigate(key));
            quickRow.getChildren().add(btn);
        }

        // Recent complaints preview
        Label recentTitle = HmsStyle.heading("Recent Complaints");
        VBox recentBox = buildCompactComplaintList(
            complaints.stream().limit(3).toList()
        );

        content.getChildren().addAll(
            pageTitle,
            statsRow,
            HmsStyle.divider(),
            quickTitle,
            quickRow,
            HmsStyle.divider(),
            recentTitle,
            recentBox
        );

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    private VBox statCard(String icon, String title, String value) {
        VBox card = HmsStyle.card(160);
        card.setAlignment(Pos.CENTER);
        card.setSpacing(6);

        Label ico = new Label(icon);
        ico.setFont(Font.font(28));

        Label val = new Label(value);
        val.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        val.setTextFill(Color.web(HmsStyle.MINT));

        Label ttl = new Label(title);
        ttl.setFont(Font.font("Segoe UI", 12));
        ttl.setTextFill(Color.web(HmsStyle.SAGE));

        card.getChildren().addAll(ico, val, ttl);
        return card;
    }

    // ══════════════════════════════════════════════════════════
    // UC-02  SUBMIT COMPLAINT
    // ══════════════════════════════════════════════════════════

    private ScrollPane buildSubmitComplaintSection() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(28));
        content.setMaxWidth(620);

        Label pageTitle = HmsStyle.title("📋 Submit a Complaint");
        Label subtitle  = HmsStyle.label("Report an issue with your room or hostel facilities.");

        VBox card = HmsStyle.card(580);
        card.setSpacing(14);

        // Description
        VBox descBox = new VBox(5);
        TextArea descArea = HmsStyle.textArea("Describe the issue in detail…", 4);
        descBox.getChildren().addAll(HmsStyle.fieldLabel("Description *"), descArea);

        // Category
        VBox catBox = new VBox(5);
        ComboBox<String> categoryCombo = HmsStyle.<String>comboBox("Select category");
        categoryCombo.getItems().addAll(
            "Plumbing", "Electrical", "Furniture", "Cleaning",
            "Internet/WiFi", "Pest Control", "Structural", "Other"
        );
        catBox.getChildren().addAll(HmsStyle.fieldLabel("Category *"), categoryCombo);

        // Priority
        VBox priBox = new VBox(5);
        ComboBox<String> priorityCombo = HmsStyle.<String>comboBox("Select priority");
        priorityCombo.getItems().addAll("LOW", "MEDIUM", "HIGH");
        priBox.getChildren().addAll(HmsStyle.fieldLabel("Priority *"), priorityCombo);

        // Message area
        Label msgLabel = new Label();
        msgLabel.setWrapText(true);
        msgLabel.setVisible(false);

        // Submit button
        Button submitBtn = HmsStyle.primaryButton("Submit Complaint");
        submitBtn.setPrefWidth(220);
        submitBtn.setOnAction(e -> {
            String result = controller.submitComplaint(
                descArea.getText(),
                categoryCombo.getValue(),
                priorityCombo.getValue()
            );
            boolean error = result.startsWith("Error");
            msgLabel.setStyle(
                error
                ? "-fx-text-fill:" + HmsStyle.RED_ERR + ";"
                : "-fx-text-fill:" + HmsStyle.GREEN_OK + ";"
            );
            msgLabel.setText(result);
            msgLabel.setVisible(true);
            if (!error) {
                descArea.clear();
                categoryCombo.setValue(null);
                priorityCombo.setValue(null);
            }
        });

        card.getChildren().addAll(descBox, catBox, priBox, submitBtn, msgLabel);
        content.getChildren().addAll(pageTitle, subtitle, card);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return sp;
    }

    // ══════════════════════════════════════════════════════════
    // UC-05  MY COMPLAINTS (status view)
    // ══════════════════════════════════════════════════════════

    private ScrollPane buildMyComplaintsSection() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(28));

        Label pageTitle = HmsStyle.title("🔍 My Complaints");

        Button refreshBtn = HmsStyle.outlineButton("↻  Refresh");

        List<Complaint> complaints = controller.getMyComplaints();
        VBox listBox = buildCompactComplaintList(complaints);

        refreshBtn.setOnAction(e -> navigate("myComplaints"));

        HBox header = new HBox(12, pageTitle, new Region(), refreshBtn);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        if (complaints.isEmpty()) {
            Label empty = HmsStyle.label("No complaints found. Submit your first complaint!");
            empty.setTextFill(Color.web(HmsStyle.SAGE));
            content.getChildren().addAll(header, empty);
        } else {
            content.getChildren().addAll(header, listBox);
        }

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return sp;
    }

    private VBox buildCompactComplaintList(List<Complaint> complaints) {
        VBox box = new VBox(10);
        for (Complaint c : complaints) {
            HBox row = new HBox(12);
            row.setPadding(new Insets(14));
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle(
                "-fx-background-color: " + HmsStyle.TEAL_DK + ";"
              + "-fx-background-radius: 10;"
              + "-fx-border-color: " + HmsStyle.SAGE + "33;"
              + "-fx-border-radius: 10;"
            );

            VBox info = new VBox(4);
            Label desc = new Label(c.getDescription().length() > 70
                ? c.getDescription().substring(0, 70) + "…"
                : c.getDescription());
            desc.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
            desc.setTextFill(Color.web(HmsStyle.SILVER));

            Label meta = new Label(c.getCategory() + "  •  " + c.formattedDate());
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

    // ══════════════════════════════════════════════════════════
    // UC-03  ROOM SWAP REQUEST
    // ══════════════════════════════════════════════════════════

    private ScrollPane buildRoomSwapSection() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(28));

        Label pageTitle = HmsStyle.title("🔄 Room Swap Request");
        Label subtitle  = HmsStyle.label(
            "Current Room: " + student.getRoomNumber()
          + "   •   Program: " + student.getProgram()
        );
        subtitle.setTextFill(Color.web(HmsStyle.SAGE));

        VBox card = HmsStyle.card(560);
        card.setSpacing(14);

        // Requested room field
        VBox roomBox = new VBox(5);
        TextField roomField = HmsStyle.textField("e.g. 205");
        roomBox.getChildren().addAll(HmsStyle.fieldLabel("Requested Room Number *"), roomField);

        // Reason
        VBox reasonBox = new VBox(5);
        TextArea reasonArea = HmsStyle.textArea("Explain why you need this swap…", 3);
        reasonBox.getChildren().addAll(HmsStyle.fieldLabel("Reason *"), reasonArea);

        // Message
        Label msgLabel = new Label();
        msgLabel.setWrapText(true);
        msgLabel.setVisible(false);

        Button submitBtn = HmsStyle.primaryButton("Submit Request");
        submitBtn.setPrefWidth(200);
        submitBtn.setOnAction(e -> {
            String result = controller.requestRoomSwap(roomField.getText(), reasonArea.getText());
            boolean error = result.startsWith("Error");
            msgLabel.setStyle(error
                ? "-fx-text-fill:" + HmsStyle.RED_ERR + ";"
                : "-fx-text-fill:" + HmsStyle.GREEN_OK + ";"
            );
            msgLabel.setText(result);
            msgLabel.setVisible(true);
            if (!error) { roomField.clear(); reasonArea.clear(); }
        });

        card.getChildren().addAll(roomBox, reasonBox, submitBtn, msgLabel);

        // Previous requests
        Label prevTitle = HmsStyle.heading("Previous Requests");
        List<RoomSwapRequest> reqs = controller.getMyRoomSwapRequests();
        VBox prevBox = new VBox(8);
        if (reqs.isEmpty()) {
            prevBox.getChildren().add(HmsStyle.label("No previous requests."));
        } else {
            for (RoomSwapRequest r : reqs) {
                HBox row = new HBox(12);
                row.setPadding(new Insets(12));
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle(
                    "-fx-background-color:" + HmsStyle.TEAL_DK + ";"
                  + "-fx-background-radius:8;"
                );
                Label info = HmsStyle.label(
                    r.getCurrentRoom() + " → " + r.getRequestedRoom()
                  + "  |  " + r.getReason()
                );
                info.setWrapText(true);
                HBox.setHgrow(info, Priority.ALWAYS);
                row.getChildren().addAll(info, HmsStyle.statusBadge(r.getStatus()));
                prevBox.getChildren().add(row);
            }
        }

        content.getChildren().addAll(pageTitle, subtitle, card, HmsStyle.divider(), prevTitle, prevBox);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return sp;
    }

    // ══════════════════════════════════════════════════════════
    // UC-04  REGISTER VISITOR
    // ══════════════════════════════════════════════════════════

    private ScrollPane buildVisitorSection() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(28));

        Label pageTitle = HmsStyle.title("👥 Register Visitor");
        Label subtitle  = HmsStyle.label("Log visitor details for hostel security records.");

        VBox card = HmsStyle.card(560);
        card.setSpacing(14);

        VBox nameBox = new VBox(5);
        TextField nameField = HmsStyle.textField("Full name of visitor");
        nameBox.getChildren().addAll(HmsStyle.fieldLabel("Visitor Name *"), nameField);

        VBox cnicBox = new VBox(5);
        TextField cnicField = HmsStyle.textField("13-digit CNIC number");
        cnicBox.getChildren().addAll(HmsStyle.fieldLabel("Visitor CNIC *"), cnicField);

        VBox purposeBox = new VBox(5);
        TextArea purposeArea = HmsStyle.textArea("Purpose of visit…", 2);
        purposeBox.getChildren().addAll(HmsStyle.fieldLabel("Purpose *"), purposeArea);

        Label msgLabel = new Label();
        msgLabel.setWrapText(true);
        msgLabel.setVisible(false);

        Button submitBtn = HmsStyle.primaryButton("Register Visitor");
        submitBtn.setPrefWidth(200);
        submitBtn.setOnAction(e -> {
            String result = controller.registerVisitor(
                nameField.getText(), cnicField.getText(), purposeArea.getText()
            );
            boolean error = result.startsWith("Error");
            msgLabel.setStyle(error
                ? "-fx-text-fill:" + HmsStyle.RED_ERR + ";"
                : "-fx-text-fill:" + HmsStyle.GREEN_OK + ";"
            );
            msgLabel.setText(result);
            msgLabel.setVisible(true);
            if (!error) { nameField.clear(); cnicField.clear(); purposeArea.clear(); }
        });

        card.getChildren().addAll(nameBox, cnicBox, purposeBox, submitBtn, msgLabel);

        // Visitor history
        Label histTitle = HmsStyle.heading("Visitor History");
        List<VisitorLog> logs = controller.getMyVisitorLogs();
        VBox histBox = new VBox(8);
        if (logs.isEmpty()) {
            histBox.getChildren().add(HmsStyle.label("No visitor records yet."));
        } else {
            for (VisitorLog v : logs) {
                HBox row = new HBox(12);
                row.setPadding(new Insets(12));
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-background-color:" + HmsStyle.TEAL_DK + ";-fx-background-radius:8;");

                VBox info = new VBox(3);
                Label name = new Label(v.getVisitorName() + "  •  CNIC: " + v.getVisitorCnic());
                name.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
                name.setTextFill(Color.web(HmsStyle.SILVER));
                Label purpose = new Label("Purpose: " + v.getPurpose() + "   " + v.formattedDate());
                purpose.setFont(Font.font("Segoe UI", 11));
                purpose.setTextFill(Color.web(HmsStyle.SAGE));
                info.getChildren().addAll(name, purpose);
                row.getChildren().add(info);
                histBox.getChildren().add(row);
            }
        }

        content.getChildren().addAll(pageTitle, subtitle, card, HmsStyle.divider(), histTitle, histBox);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return sp;
    }
}
