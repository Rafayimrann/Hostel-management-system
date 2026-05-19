package com.hms.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Centralised styling helper for the HMS UI.
 *
 * STORM COLOUR PALETTE (from design brief):
 *   NAVY    #0D1F2A  — deepest background
 *   TEAL_DK #1B3D4A  — card / panel background
 *   SAGE    #6A9090  — secondary accent / borders
 *   MINT    #7BC4BE  — primary accent (buttons, highlights)
 *   SILVER  #D4DDE0  — primary text / light elements
 */
public class HmsStyle {

    // ── Palette constants ──────────────────────────────────────
    public static final String NAVY    = "#0D1F2A";
    public static final String TEAL_DK = "#1B3D4A";
    public static final String SAGE    = "#6A9090";
    public static final String MINT    = "#7BC4BE";
    public static final String SILVER  = "#D4DDE0";
    public static final String WHITE   = "#FFFFFF";
    public static final String RED_ERR = "#E05C5C";
    public static final String GREEN_OK= "#5CBF8A";

    // ── Root / Scene background ────────────────────────────────
    public static String rootBg() {
        return "-fx-background-color: " + NAVY + ";";
    }

    // ── Card panel ─────────────────────────────────────────────
    public static VBox card(double width) {
        VBox card = new VBox(14);
        card.setPrefWidth(width);
        card.setPadding(new Insets(28));
        card.setStyle(
            "-fx-background-color: " + TEAL_DK + ";"
          + "-fx-background-radius: 12;"
          + "-fx-border-color: " + SAGE + ";"
          + "-fx-border-radius: 12;"
          + "-fx-border-width: 1;"
          + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 16, 0, 0, 4);"
        );
        return card;
    }

    // ── Title label ────────────────────────────────────────────
    public static Label title(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        lbl.setTextFill(Color.web(MINT));
        return lbl;
    }

    // ── Section heading ────────────────────────────────────────
    public static Label heading(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 15));
        lbl.setTextFill(Color.web(SILVER));
        return lbl;
    }

    // ── Body label ─────────────────────────────────────────────
    public static Label label(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", 13));
        lbl.setTextFill(Color.web(SILVER));
        return lbl;
    }

    // ── Field label ────────────────────────────────────────────
    public static Label fieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        lbl.setTextFill(Color.web(SAGE));
        return lbl;
    }

    // ── TextField ──────────────────────────────────────────────
    public static TextField textField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefHeight(38);
        tf.setStyle(
            "-fx-background-color: " + NAVY + ";"
          + "-fx-border-color: " + SAGE + ";"
          + "-fx-border-radius: 6;"
          + "-fx-background-radius: 6;"
          + "-fx-text-fill: " + SILVER + ";"
          + "-fx-prompt-text-fill: #5a7a80;"
          + "-fx-padding: 0 10 0 10;"
          + "-fx-font-size: 13;"
        );
        return tf;
    }

    // ── PasswordField ──────────────────────────────────────────
    public static PasswordField passwordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setPrefHeight(38);
        pf.setStyle(
            "-fx-background-color: " + NAVY + ";"
          + "-fx-border-color: " + SAGE + ";"
          + "-fx-border-radius: 6;"
          + "-fx-background-radius: 6;"
          + "-fx-text-fill: " + SILVER + ";"
          + "-fx-prompt-text-fill: #5a7a80;"
          + "-fx-padding: 0 10 0 10;"
          + "-fx-font-size: 13;"
        );
        return pf;
    }

    // ── TextArea ───────────────────────────────────────────────
    public static TextArea textArea(String prompt, int rows) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setPrefRowCount(rows);
        ta.setWrapText(true);
        ta.setStyle(
            "-fx-background-color: " + NAVY + ";"
          + "-fx-border-color: " + SAGE + ";"
          + "-fx-border-radius: 6;"
          + "-fx-background-radius: 6;"
          + "-fx-text-fill: " + SILVER + ";"
          + "-fx-prompt-text-fill: #5a7a80;"
          + "-fx-font-size: 13;"
          + "-fx-control-inner-background: " + NAVY + ";"
        );
        return ta;
    }

    // ── ComboBox ───────────────────────────────────────────────
    public static <T> ComboBox<T> comboBox(String prompt) {
        ComboBox<T> cb = new ComboBox<>();
        cb.setPromptText(prompt);
        cb.setPrefHeight(38);
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setStyle(
            "-fx-background-color: " + NAVY + ";"
          + "-fx-border-color: " + SAGE + ";"
          + "-fx-border-radius: 6;"
          + "-fx-background-radius: 6;"
          + "-fx-text-fill: " + SILVER + ";"
          + "-fx-font-size: 13;"
        );
        return cb;
    }

    // ── Primary button ─────────────────────────────────────────
    public static Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(40);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
            "-fx-background-color: " + MINT + ";"
          + "-fx-text-fill: " + NAVY + ";"
          + "-fx-font-weight: bold;"
          + "-fx-font-size: 13;"
          + "-fx-background-radius: 8;"
          + "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #9ad4ce;"
          + "-fx-text-fill: " + NAVY + ";"
          + "-fx-font-weight: bold;"
          + "-fx-font-size: 13;"
          + "-fx-background-radius: 8;"
          + "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: " + MINT + ";"
          + "-fx-text-fill: " + NAVY + ";"
          + "-fx-font-weight: bold;"
          + "-fx-font-size: 13;"
          + "-fx-background-radius: 8;"
          + "-fx-cursor: hand;"
        ));
        return btn;
    }

    // ── Danger / secondary button ──────────────────────────────
    public static Button outlineButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(36);
        btn.setStyle(
            "-fx-background-color: transparent;"
          + "-fx-text-fill: " + SAGE + ";"
          + "-fx-border-color: " + SAGE + ";"
          + "-fx-border-radius: 8;"
          + "-fx-background-radius: 8;"
          + "-fx-font-size: 12;"
          + "-fx-cursor: hand;"
        );
        return btn;
    }

    // ── Status badge ───────────────────────────────────────────
    public static Label statusBadge(String status) {
        Label lbl = new Label(status);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        lbl.setPadding(new Insets(3, 10, 3, 10));
        lbl.setStyle(badgeStyle(status));
        return lbl;
    }

    private static String badgeStyle(String status) {
        String bg, fg;
        switch (status.toUpperCase()) {
            case "PENDING"     -> { bg = "#2a3d1f"; fg = "#8fcc60"; }
            case "ASSIGNED"    -> { bg = "#1a2d4a"; fg = MINT; }
            case "IN_PROGRESS" -> { bg = "#3a2d0d"; fg = "#f0c050"; }
            case "RESOLVED",
                 "COMPLETED",
                 "CLOSED"      -> { bg = "#0d2a1a"; fg = GREEN_OK; }
            case "APPROVED"    -> { bg = "#0d2a1a"; fg = GREEN_OK; }
            case "REJECTED"    -> { bg = "#2a0d0d"; fg = RED_ERR; }
            default            -> { bg = TEAL_DK;   fg = SILVER; }
        }
        return "-fx-background-color: " + bg + ";"
             + "-fx-text-fill: " + fg + ";"
             + "-fx-background-radius: 20;"
             + "-fx-padding: 3 10 3 10;";
    }

    // ── Priority badge ─────────────────────────────────────────
    public static Label priorityBadge(String priority) {
        Label lbl = new Label(priority);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        lbl.setPadding(new Insets(3, 8, 3, 8));
        String style = switch (priority.toUpperCase()) {
            case "HIGH"   -> "-fx-background-color:#2a0d0d;-fx-text-fill:" + RED_ERR + ";";
            case "MEDIUM" -> "-fx-background-color:#3a2d0d;-fx-text-fill:#f0c050;";
            default       -> "-fx-background-color:#1a2d4a;-fx-text-fill:" + MINT + ";";
        };
        lbl.setStyle(style + "-fx-background-radius:20;");
        return lbl;
    }

    // ── Alert message box ─────────────────────────────────────
    public static Label alertBox(String message, boolean isError) {
        Label lbl = new Label(message);
        lbl.setWrapText(true);
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setPadding(new Insets(10, 14, 10, 14));
        lbl.setFont(Font.font("Segoe UI", 13));
        String color = isError ? RED_ERR : GREEN_OK;
        String bg    = isError ? "#2a0d0d" : "#0d2a1a";
        lbl.setStyle(
            "-fx-background-color: " + bg + ";"
          + "-fx-text-fill: " + color + ";"
          + "-fx-background-radius: 8;"
          + "-fx-border-color: " + color + ";"
          + "-fx-border-radius: 8;"
          + "-fx-border-width: 1;"
        );
        return lbl;
    }

    // ── Divider ────────────────────────────────────────────────
    public static Separator divider() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + SAGE + "; -fx-opacity: 0.3;");
        return sep;
    }

    // ── Sidebar nav button ─────────────────────────────────────
    public static Button navButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 20, 12, 20));
        btn.setFont(Font.font("Segoe UI", active ? FontWeight.BOLD : FontWeight.NORMAL, 13));
        if (active) {
            btn.setStyle(
                "-fx-background-color: " + MINT + "20;"
              + "-fx-text-fill: " + MINT + ";"
              + "-fx-background-radius: 8;"
              + "-fx-border-color: " + MINT + ";"
              + "-fx-border-width: 0 0 0 3;"
              + "-fx-cursor: hand;"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: transparent;"
              + "-fx-text-fill: " + SILVER + ";"
              + "-fx-background-radius: 8;"
              + "-fx-cursor: hand;"
            );
        }
        return btn;
    }

    // ── Table styling helper ───────────────────────────────────
    public static <T> void styleTable(TableView<T> table) {
        table.setStyle(
            "-fx-background-color: " + NAVY + ";"
          + "-fx-border-color: " + SAGE + ";"
          + "-fx-border-radius: 8;"
          + "-fx-background-radius: 8;"
          + "-fx-table-cell-border-color: transparent;"
        );
        table.setFixedCellSize(40);
    }
}
