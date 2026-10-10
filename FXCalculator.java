import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FXCalculator extends Application {

    private CalculatorEngine engine = new CalculatorEngine();

    @Override
    public void start(Stage primaryStage) {
        // 1. AMOLED Dark Mode Display Screen
        TextField display = new TextField("0");
        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);
        display.setStyle(
            "-fx-font-size: 34px; " +
            "-fx-font-family: 'Segoe UI', Helvetica, sans-serif; " +
            "-fx-background-color: #141414; " +
            "-fx-background-radius: 14px; " +
            "-fx-padding: 16px; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-border-color: #262626; " +
            "-fx-border-radius: 14px;"
        );

        // 2. Button layout grid matching the matrix
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        String[][] buttons = {
            {"AC", "()", "%", "÷"},
            {"7",  "8",  "9", "×"},
            {"4",  "5",  "6", "-"},
            {"1",  "2",  "3", "+"},
            {"0",  ".",  "⌫", "="}
        };

        // Subtle tactile drop shadow for physical key depth
        DropShadow tactileShadow = new DropShadow();
        tactileShadow.setRadius(4);
        tactileShadow.setOffsetY(3);
        tactileShadow.setColor(Color.rgb(0, 0, 0, 0.5));

        // 3. Populate grid with tactile button dynamics
        for (int row = 0; row < buttons.length; row++) {
            for (int col = 0; col < buttons[row].length; col++) {
                String text = buttons[row][col];
                if (text.isEmpty()) continue;

                Button btn = new Button(text);
                btn.setPrefSize(68, 62);
                btn.setEffect(tactileShadow);

                // Apply initial styles & interactive hover/press listeners
                applyTactileStyling(btn, text);

                // --- MAP UI SYMBOLS TO ENGINE TOKENS ---
                btn.setOnAction(e -> {
                    String engineToken = text;
                    if (text.equals("AC")) engineToken = "C";
                    else if (text.equals("÷")) engineToken = "/";
                    else if (text.equals("×")) engineToken = "*";
                    else if (text.equals("⌫")) engineToken = "DEL";

                    engine.inputToken(engineToken);
                    display.setText(engine.getExpression());
                });

                grid.add(btn, col, row);
            }
        }

        // 4. Main container with true-black AMOLED background
        VBox root = new VBox(20, display, grid);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0a0a0a;"); // True Black AMOLED

        // 5. Scene and Stage setup
        Scene scene = new Scene(root, 360, 520);
        primaryStage.setTitle("AMOLED Tactile Calculator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- Tactile Styling & Interactive Hover Engine ---
    private void applyTactileStyling(Button btn, String text) {
        String baseStyle = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI', Helvetica, sans-serif; -fx-background-radius: 16px; -fx-cursor: hand; ";
        
        String normalColor;
        String hoverColor;
        String pressedColor;
        String textColor;

        if (text.equals("AC") || text.equals("⌫")) {
            // Muted Coral / Red for Controls
            normalColor = "#3d1f24";
            hoverColor = "#4e272e";
            pressedColor = "#2c151a";
            textColor = "#ff6b6b";
        } else if (text.equals("()")) {
            // Neutral Muted Slate for Parentheses
            normalColor = "#262626";
            hoverColor = "#333333";
            pressedColor = "#1a1a1a";
            textColor = "#b0b0b0";
        } else if (text.equals("%") || text.equals("÷") || text.equals("×") || text.equals("-") || text.equals("+")) {
            // Warm Amber/Gold for Operators (High contrast against black)
            normalColor = "#3d2e1a";
            hoverColor = "#4e3b22";
            pressedColor = "#2c2112";
            textColor = "#ffb74d";
        } else if (text.equals("=")) {
            // Vibrant Accent Blue/Cyan for Equals
            normalColor = "#113f48";
            hoverColor = "#16525e";
            pressedColor = "#0d2e35";
            textColor = "#4dd0e1";
        } else {
            // Dark Grey Tactile Keys for Numbers
            normalColor = "#1f1f1f";
            hoverColor = "#2b2b2b";
            pressedColor = "#141414";
            textColor = "#ffffff";
        }

        String normalStyleFull = baseStyle + "-fx-background-color: " + normalColor + "; -fx-text-fill: " + textColor + ";";
        String hoverStyleFull = baseStyle + "-fx-background-color: " + hoverColor + "; -fx-text-fill: " + textColor + ";";
        String pressedStyleFull = baseStyle + "-fx-background-color: " + pressedColor + "; -fx-text-fill: " + textColor + "; -fx-translate-y: 1px;";

        btn.setStyle(normalStyleFull);

        // Add physical tactile feedback listeners
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyleFull));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyleFull));
        btn.setOnMousePressed(e -> btn.setStyle(pressedStyleFull));
        btn.setOnMouseReleased(e -> btn.setStyle(hoverStyleFull));
    }

    public static void main(String[] args) {
        launch(args);
    }
}