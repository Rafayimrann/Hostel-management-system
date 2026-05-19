package com.hms.ui;

import com.hms.controllers.SystemController;
import com.hms.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Presentation Layer: Login Screen
 *
 * UC-01: Login to System
 * Supports both Student and Maintenance Staff authentication.
 * On success, routes to the appropriate dashboard.
 */
public class LoginScreen {

    private final Stage           stage;
    private final SystemController controller;

    // ── UI Fields ──────────────────────────────────────────────
    private TextField             emailField;
    private PasswordField         passwordField;
    private Label                 messageLabel;

    public LoginScreen(Stage stage) {
        this.stage      = stage;
        this.controller = new SystemController();
    }

    public void show() {
        stage.setTitle("HMS — Login");
        stage.setWidth(460);
        stage.setHeight(600);
        stage.setResizable(false);
        stage.centerOnScreen();

        Scene scene = new Scene(buildRoot(), 460, 600);
        stage.setScene(scene);
        stage.show();
    }

    // ── Layout builder ─────────────────────────────────────────
    private StackPane buildRoot() {
        StackPane root = new StackPane();
        root.setStyle(HmsStyle.rootBg());

        // Decorative background circles
        root.getChildren().add(buildBackground());

        // Centre card
        VBox card = HmsStyle.card(360);
        card.setAlignment(Pos.CENTER);
        card.setSpacing(18);
        card.getChildren().addAll(
            buildLogo(),
            buildForm()
        );

        root.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);
        return root;
    }

    private Pane buildBackground() {
        Pane bg = new Pane();
        bg.setPrefSize(460, 600);

        // Decorative circles using Rectangles/Circles via CSS
        for (int i = 0; i < 3; i++) {
            javafx.scene.shape.Circle c = new javafx.scene.shape.Circle();
            c.setRadius(80 + i * 40);
            c.setFill(Color.web(HmsStyle.TEAL_DK, 0.3));
            c.setStroke(Color.web(HmsStyle.SAGE, 0.15));
            c.setCenterX(i % 2 == 0 ? 60 : 400);
            c.setCenterY(i == 1 ? 500 : 100 + i * 50);
            bg.getChildren().add(c);
        }
        return bg;
    }

    private VBox buildLogo() {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);

        // Icon
        Label icon = new Label("🏨");
        icon.setFont(Font.font(48));

        Label title = new Label("Hostel Management");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(HmsStyle.MINT));

        Label sub = new Label("System");
        sub.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        sub.setTextFill(Color.web(HmsStyle.MINT));

        Label tagline = new Label("Student  •  Maintenance  •  Warden");
        tagline.setFont(Font.font("Segoe UI", 12));
        tagline.setTextFill(Color.web(HmsStyle.SAGE));

        box.getChildren().addAll(icon, title, sub, tagline);
        return box;
    }

    private VBox buildForm() {
        VBox form = new VBox(12);
        form.setMaxWidth(320);

        // ── Email ──
        VBox emailBox = new VBox(5);
        emailBox.getChildren().addAll(
            HmsStyle.fieldLabel("Email Address"),
            emailField = HmsStyle.textField("Enter your email")
        );

        // ── Password ──
        VBox passBox = new VBox(5);
        passBox.getChildren().addAll(
            HmsStyle.fieldLabel("Password"),
            passwordField = HmsStyle.passwordField("Enter your password")
        );

        // ── Message ──
        messageLabel = new Label("");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(320);
        messageLabel.setFont(Font.font("Segoe UI", 12));
        messageLabel.setTextFill(Color.web(HmsStyle.RED_ERR));
        messageLabel.setTextAlignment(TextAlignment.CENTER);

        // ── Login Button ──
        Button loginBtn = HmsStyle.primaryButton("Sign In");
        loginBtn.setOnAction(e -> handleLogin());

        // Allow Enter key to trigger login
        passwordField.setOnAction(e -> handleLogin());
        emailField.setOnAction(e -> passwordField.requestFocus());

        // ── Demo hint ──
        Label hint = new Label("Demo: ali@hms.com / pass123  (Student)");
        hint.setFont(Font.font("Segoe UI", 11));
        hint.setTextFill(Color.web(HmsStyle.SAGE));
        hint.setTextAlignment(TextAlignment.CENTER);
        hint.setMaxWidth(Double.MAX_VALUE);

        Label hint2 = new Label("Demo: zaid@hms.com / pass123  (Maintenance)");

        Label hint3 = new Label("Demo: warden@hms.com / pass123  (Warden)");
        hint3.setFont(Font.font("Segoe UI", 11));
        hint3.setTextFill(Color.web(HmsStyle.SAGE));
        hint3.setTextAlignment(TextAlignment.CENTER);
        hint3.setMaxWidth(Double.MAX_VALUE);
        hint2.setFont(Font.font("Segoe UI", 11));
        hint2.setTextFill(Color.web(HmsStyle.SAGE));
        hint2.setTextAlignment(TextAlignment.CENTER);
        hint2.setMaxWidth(Double.MAX_VALUE);

        form.getChildren().addAll(
            emailBox,
            passBox,
            messageLabel,
            loginBtn,
            HmsStyle.divider(),
            hint,
            hint2,
            hint3
        );
        return form;
    }

    // ── Event Handler ──────────────────────────────────────────
    private void handleLogin() {
        messageLabel.setTextFill(Color.web(HmsStyle.RED_ERR));
        String email = emailField.getText();
        String pass  = passwordField.getText();

        if (email.isBlank() || pass.isBlank()) {
            messageLabel.setText("⚠  Please enter both email and password.");
            return;
        }

        User user = controller.login(email, pass);

        if (user == null) {
            messageLabel.setText("✗  Invalid credentials. Please try again.");
            passwordField.clear();
            return;
        }

        // Route to correct dashboard
        if (user.isStudent()) {
            StudentDashboard dash = new StudentDashboard(stage, controller);
            dash.show();
        } else if (user.isMaintenance()) {
            MaintenanceDashboard dash = new MaintenanceDashboard(stage, controller);
            dash.show();
        } else if (user.isWarden()) {
            WardenDashboard dash = new WardenDashboard(stage, controller);
            dash.show();
        } else {
            messageLabel.setText("⚠  Unknown role. Please contact admin.");
        }
    }
}
