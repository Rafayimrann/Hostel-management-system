package com.hms.ui;

import com.hms.controllers.SystemController;
import com.hms.models.MaintenanceStaff;
import com.hms.models.MaintenanceTask;
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
 * Presentation Layer: Maintenance Staff Dashboard
 *
 * Implements:
 *   UC-11 View Assigned Tasks
 *   UC-12 Update Task Status
 *   UC-13 Resolve Complaint (auto-triggered on COMPLETED)
 *   UC-14 Close Maintenance Ticket
 */
public class MaintenanceDashboard {

    private final Stage            stage;
    private final SystemController controller;
    private final MaintenanceStaff staff;

    private BorderPane mainLayout;
    private String     activeSection = "overview";

    public MaintenanceDashboard(Stage stage, SystemController controller) {
        this.stage      = stage;
        this.controller = controller;
        this.staff      = controller.getCurrentStaff();
    }

    public void show() {
        stage.setTitle("HMS — Maintenance Dashboard");
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

        Label avatar = new Label("🔧");
        avatar.setFont(Font.font(36));
        Label nameLabel = new Label(staff.getName());
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.web(HmsStyle.SILVER));
        Label roleLabel = new Label("Maintenance  •  " + staff.getSpecialization());
        roleLabel.setFont(Font.font("Segoe UI", 12));
        roleLabel.setTextFill(Color.web(HmsStyle.SAGE));

        Label availBadge = HmsStyle.statusBadge(staff.isAvailable() ? "AVAILABLE" : "BUSY");

        VBox userBox = new VBox(4, avatar, nameLabel, roleLabel, availBadge);
        userBox.setAlignment(Pos.CENTER);
        userBox.setPadding(new Insets(0, 0, 20, 0));

        String[][] navItems = {
            { "overview", "🏠  Overview"       },
            { "tasks",    "📋  Assigned Tasks"  },
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
            case "tasks" -> buildTasksSection();
            default      -> buildOverviewSection();
        };
    }

    // ══════════════════════════════════════════════════════════
    // OVERVIEW
    // ══════════════════════════════════════════════════════════

    private ScrollPane buildOverviewSection() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(28));

        Label pageTitle = HmsStyle.title("Welcome, " + staff.getName() + " 🔧");

        List<MaintenanceTask> tasks = controller.getMyTasks();
        long assigned   = tasks.stream().filter(t -> "ASSIGNED".equals(t.getStatus())).count();
        long inProgress = tasks.stream().filter(MaintenanceTask::isInProgress).count();
        long completed  = tasks.stream().filter(MaintenanceTask::isCompleted).count();

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            statCard("📋", "Total Tasks",    String.valueOf(tasks.size())),
            statCard("🕐", "Assigned",       String.valueOf(assigned)),
            statCard("⚙",  "In Progress",    String.valueOf(inProgress)),
            statCard("✅", "Completed",      String.valueOf(completed))
        );

        Label quickTitle = HmsStyle.heading("Quick Access");
        Button viewBtn = HmsStyle.primaryButton("📋 View All Tasks");
        viewBtn.setPrefWidth(200);
        viewBtn.setOnAction(e -> navigate("tasks"));

        // Recent tasks preview
        Label recentTitle = HmsStyle.heading("Recent Tasks");
        VBox recent = buildTaskCards(tasks.stream().limit(3).toList(), false);

        content.getChildren().addAll(
            pageTitle, statsRow,
            HmsStyle.divider(), quickTitle, viewBtn,
            HmsStyle.divider(), recentTitle, recent
        );

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
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
    // UC-11 / 12 / 13 / 14  TASKS
    // ══════════════════════════════════════════════════════════

    private ScrollPane buildTasksSection() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(28));

        Button refreshBtn = HmsStyle.outlineButton("↻  Refresh");
        refreshBtn.setOnAction(e -> navigate("tasks"));

        HBox header = new HBox(12,
            HmsStyle.title("📋 My Assigned Tasks"),
            new Region(),
            refreshBtn
        );
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        List<MaintenanceTask> tasks = controller.getMyTasks();

        if (tasks.isEmpty()) {
            Label empty = HmsStyle.label("No tasks assigned to you at this time.");
            content.getChildren().addAll(header, empty);
        } else {
            VBox taskCards = buildTaskCards(tasks, true);
            content.getChildren().addAll(header, taskCards);
        }

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return sp;
    }

    /**
     * Renders task cards.
     * @param showActions whether to show update-status buttons (full view vs preview)
     */
    private VBox buildTaskCards(List<MaintenanceTask> tasks, boolean showActions) {
        VBox box = new VBox(12);
        for (MaintenanceTask task : tasks) {
            box.getChildren().add(buildTaskCard(task, showActions));
        }
        return box;
    }

    private VBox buildTaskCard(MaintenanceTask task, boolean showActions) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color: " + HmsStyle.TEAL_DK + ";"
          + "-fx-background-radius: 12;"
          + "-fx-border-color: " + HmsStyle.SAGE + "40;"
          + "-fx-border-radius: 12;"
          + "-fx-border-width: 1;"
        );

        // ── Header row ─────────────────────────────────────────
        HBox headerRow = new HBox(10);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label taskIdLabel = new Label("Task: " + task.getTaskId());
        taskIdLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        taskIdLabel.setTextFill(Color.web(HmsStyle.MINT));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox badges = new HBox(6,
            HmsStyle.statusBadge(task.getStatus()),
            HmsStyle.priorityBadge(
                task.getComplaintPriority() != null ? task.getComplaintPriority() : "LOW"
            )
        );
        badges.setAlignment(Pos.CENTER_RIGHT);
        headerRow.getChildren().addAll(taskIdLabel, spacer, badges);

        // ── Complaint details ──────────────────────────────────
        VBox details = new VBox(4);
        String desc = task.getComplaintDescription() != null
            ? task.getComplaintDescription()
            : "(No description)";
        Label descLabel = HmsStyle.label(desc.length() > 100
            ? desc.substring(0, 100) + "…" : desc);

        Label metaLabel = new Label(
            "Category: " + (task.getComplaintCategory() != null ? task.getComplaintCategory() : "—")
          + "   •   Assigned: " + task.formattedDate()
        );
        metaLabel.setFont(Font.font("Segoe UI", 11));
        metaLabel.setTextFill(Color.web(HmsStyle.SAGE));

        details.getChildren().addAll(descLabel, metaLabel);

        card.getChildren().addAll(headerRow, details);

        // ── Action buttons (UC-12, UC-13, UC-14) ──────────────
        if (showActions && task.canUpdate()) {
            HBox actions = new HBox(10);
            actions.setAlignment(Pos.CENTER_LEFT);

            // Message label for this card
            Label msgLabel = new Label();
            msgLabel.setFont(Font.font("Segoe UI", 12));
            msgLabel.setVisible(false);

            if ("ASSIGNED".equals(task.getStatus())) {
                Button startBtn = HmsStyle.primaryButton("▶  Start Task");
                startBtn.setPrefWidth(150);
                startBtn.setOnAction(e -> {
                    String result = controller.updateTaskStatus(task.getTaskId(), "IN_PROGRESS");
                    showActionResult(msgLabel, result);
                    // Refresh the tasks view
                    navigate("tasks");
                });
                actions.getChildren().add(startBtn);
            }

            if ("IN_PROGRESS".equals(task.getStatus())) {
                Button completeBtn = HmsStyle.primaryButton("✔  Mark Complete");
                completeBtn.setPrefWidth(170);
                completeBtn.setStyle(completeBtn.getStyle()
                    .replace(HmsStyle.MINT, HmsStyle.GREEN_OK));
                completeBtn.setOnAction(e -> {
                    String result = controller.updateTaskStatus(task.getTaskId(), "COMPLETED");
                    showActionResult(msgLabel, result);
                    navigate("tasks");
                });
                actions.getChildren().add(completeBtn);
            }

            if ("COMPLETED".equals(task.getStatus())) {
                Button closeBtn = HmsStyle.primaryButton("🔒  Close Ticket");
                closeBtn.setPrefWidth(160);
                closeBtn.setOnAction(e -> {
                    String result = controller.updateTaskStatus(task.getTaskId(), "CLOSED");
                    showActionResult(msgLabel, result);
                    navigate("tasks");
                });
                actions.getChildren().add(closeBtn);
            }

            actions.getChildren().add(msgLabel);
            card.getChildren().add(actions);
        }

        return card;
    }

    private void showActionResult(Label label, String message) {
        boolean isError = message.startsWith("Error");
        label.setTextFill(Color.web(isError ? HmsStyle.RED_ERR : HmsStyle.GREEN_OK));
        label.setText(message);
        label.setVisible(true);
    }
}
