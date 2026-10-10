import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HP35Calculator extends JFrame implements ActionListener {
    private JTextField display;
    private JButton modeButton;
    private JButton delButton;
    
    // Calculator States
    private boolean fullEquationMode = false;
    private double currentVal = 0;
    private String pendingOperation = "";
    private boolean freshInput = true;
    private String fullExpression = "";

    public HP35Calculator() {
        setTitle("HP-35 Scientific Calculator (1972)");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Classic HP-35 color scheme: Dark mahogany/brown chassis
        Color chassisColor = new Color(42, 30, 22);
        Color displayBg = Color.BLACK;
        Color displayFg = new Color(255, 30, 30); // Retro Red LED

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(chassisColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // LED Display Screen
        display = new JTextField("0");
        display.setEditable(false);
        display.setBackground(displayBg);
        display.setForeground(displayFg);
        display.setFont(new Font("Monospaced", Font.BOLD, 22));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY, 4),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        mainPanel.add(display, BorderLayout.NORTH);

        // Keypad Panel (6 rows x 5 cols)
        JPanel buttonPanel = new JPanel(new GridLayout(6, 5, 6, 6));
        buttonPanel.setBackground(chassisColor);

        // HP-35 Key arrangement
        String[] keys = {
            "1/x", "sqrt", "(",    ")",    "tan",
            "PI",  "x^y",  "LOG",  "LN",   "e^x",
            "CHS", "7",    "8",    "9",    "/",
            "sin", "4",    "5",    "6",    "*",
            "cos", "1",    "2",    "3",    "-",
            "CLR", "0",    ".",    "=",    "+"
        };

        // Button Color Definitions
        Color btnWhite = new Color(230, 230, 230);
        Color btnGold  = new Color(218, 165, 32);
        Color btnBlue  = new Color(70, 130, 180);
        Color btnDark  = new Color(60, 60, 60);

        for (String text : keys) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));
            btn.setFocusPainted(false);
            
            if (text.matches("[0-9.]")) {
                btn.setBackground(btnWhite);
                btn.setForeground(Color.BLACK);
            } else if (text.equals("=") || text.equals("+") || text.equals("-") || text.equals("*") || text.equals("/")) {
                btn.setBackground(btnGold);
                btn.setForeground(Color.BLACK);
            } else if (text.equals("sin") || text.equals("cos") || text.equals("tan") || text.equals("LOG") || text.equals("LN") || text.equals("(") || text.equals(")")) {
                btn.setBackground(btnBlue);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(btnDark);
                btn.setForeground(Color.WHITE);
            }

            btn.addActionListener(this);
            buttonPanel.add(btn);
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Bottom Control Bar (Small Mode Button & Delete Button)
        JPanel bottomControlPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        bottomControlPanel.setBackground(chassisColor);

        modeButton = new JButton();
        updateModeButtonText();
        modeButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        modeButton.setFocusPainted(false);
        modeButton.setBackground(new Color(60, 60, 60));
        modeButton.setForeground(Color.WHITE);
        modeButton.addActionListener(e -> toggleMode());

        delButton = new JButton("DEL ⌫");
        delButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        delButton.setFocusPainted(false);
        delButton.setBackground(new Color(120, 40, 40));
        delButton.setForeground(Color.WHITE);
        delButton.addActionListener(this);

        bottomControlPanel.add(modeButton);
        bottomControlPanel.add(delButton);

        mainPanel.add(bottomControlPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void toggleMode() {
        fullEquationMode = !fullEquationMode;
        updateModeButtonText();
        resetCalculator();
    }

    private void updateModeButtonText() {
        if (fullEquationMode) {
            modeButton.setText("<html>Mode &nbsp; <span style='color:red; font-size:14px;'><b>●</b></span></html>");
        } else {
            modeButton.setText("<html>Mode &nbsp; <span style='color:gray; font-size:14px;'>⚪</span></html>");
        }
    }

    private void resetCalculator() {
        display.setText("0");
        currentVal = 0;
        pendingOperation = "";
        fullExpression = "";
        freshInput = true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        try {
            if (cmd.equals("DEL ⌫")) {
                handleDelete();
                return;
            }

            if (fullEquationMode) {
                handleEquationMode(cmd);
            } else {
                handleDirectMode(cmd);
            }
        } catch (Exception ex) {
            display.setText("Error");
            freshInput = true;
            fullExpression = "";
        }
    }

    private void handleDelete() {
        if (fullEquationMode) {
            if (fullExpression.length() > 0) {
                fullExpression = fullExpression.substring(0, fullExpression.length() - 1);
                display.setText(fullExpression.isEmpty() ? "0" : fullExpression);
            }
        } else {
            String currentText = display.getText();
            if (currentText.length() > 1 && !currentText.equals("Error")) {
                display.setText(currentText.substring(0, currentText.length() - 1));
            } else {
                display.setText("0");
                freshInput = true;
            }
        }
    }

    // --- MODE 1: Write Full Equation then Calculate ---
    private void handleEquationMode(String cmd) {
        if (cmd.matches("[0-9.]") || cmd.equals("(") || cmd.equals(")")) {
            if (freshInput || display.getText().equals("0")) {
                fullExpression = cmd;
                freshInput = false;
            } else {
                fullExpression += cmd;
            }
            display.setText(fullExpression);
        } else if (cmd.equals("+") || cmd.equals("-") || cmd.equals("*") || cmd.equals("/") || cmd.equals("x^y")) {
            String opSymbol = cmd.equals("x^y") ? "^" : cmd;
            fullExpression += " " + opSymbol + " ";
            display.setText(fullExpression);
            freshInput = false;
        } else if (cmd.equals("sin") || cmd.equals("cos") || cmd.equals("tan") || cmd.equals("sqrt") || cmd.equals("LOG") || cmd.equals("LN") || cmd.equals("e^x")) {
            if (freshInput || display.getText().equals("0")) {
                fullExpression = cmd.toLowerCase() + "(";
                freshInput = false;
            } else {
                fullExpression += cmd.toLowerCase() + "(";
            }
            display.setText(fullExpression);
        } else if (cmd.equals("PI")) {
            fullExpression += "PI";
            display.setText(fullExpression);
            freshInput = false;
        } else if (cmd.equals("CLR")) {
            resetCalculator();
        } else if (cmd.equals("CLx")) {
            handleDelete();
        } else if (cmd.equals("=")) {
            double result = evaluateExpression(fullExpression);
            display.setText(formatResult(result));
            fullExpression = String.valueOf(result);
            freshInput = true;
        }
    }

    // --- MODE 2: Calculate Directly (Standard Accumulator) ---
    private void handleDirectMode(String cmd) {
        if (cmd.matches("[0-9.]")) {
            if (freshInput) {
                display.setText(cmd.equals(".") ? "0." : cmd);
                freshInput = false;
            } else {
                if (cmd.equals(".") && display.getText().contains(".")) return;
                display.setText(display.getText() + cmd);
            }
        } else if (cmd.equals("CLR")) {
            resetCalculator();
        } else if (cmd.equals("CLx")) {
            display.setText("0");
            freshInput = true;
        } else if (cmd.equals("CHS")) {
            double val = Double.parseDouble(display.getText());
            display.setText(formatResult(-val));
        } else if (cmd.equals("PI")) {
            display.setText(String.valueOf(Math.PI));
            freshInput = true;
        } else if (cmd.equals("sqrt")) {
            double val = Double.parseDouble(display.getText());
            display.setText(formatResult(Math.sqrt(val)));
            freshInput = true;
        } else if (cmd.equals("1/x")) {
            double val = Double.parseDouble(display.getText());
            display.setText(formatResult(1.0 / val));
            freshInput = true;
        } else if (cmd.equals("sin")) {
            double val = Double.parseDouble(display.getText());
            display.setText(formatResult(Math.sin(Math.toRadians(val))));
            freshInput = true;
        } else if (cmd.equals("cos")) {
            double val = Double.parseDouble(display.getText());
            display.setText(formatResult(Math.cos(Math.toRadians(val))));
            freshInput = true;
        } else if (cmd.equals("tan")) {
            double val = Double.parseDouble(display.getText());
            display.setText(formatResult(Math.tan(Math.toRadians(val))));
            freshInput = true;
        } else if (cmd.equals("LOG")) {
            double val = Double.parseDouble(display.getText());
            display.setText(formatResult(Math.log10(val)));
            freshInput = true;
        } else if (cmd.equals("LN")) {
            double val = Double.parseDouble(display.getText());
            display.setText(formatResult(Math.log(val)));
            freshInput = true;
        } else if (cmd.equals("e^x")) {
            double val = Double.parseDouble(display.getText());
            display.setText(formatResult(Math.exp(val)));
            freshInput = true;
        } else if (cmd.equals("+") || cmd.equals("-") || cmd.equals("*") || cmd.equals("/") || cmd.equals("x^y")) {
            currentVal = Double.parseDouble(display.getText());
            pendingOperation = cmd;
            freshInput = true;
        } else if (cmd.equals("=")) {
            double secondVal = Double.parseDouble(display.getText());
            double result = 0;
            switch (pendingOperation) {
                case "+": result = currentVal + secondVal; break;
                case "-": result = currentVal - secondVal; break;
                case "*": result = currentVal * secondVal; break;
                case "/": result = secondVal != 0 ? currentVal / secondVal : 0; break;
                case "x^y": result = Math.pow(currentVal, secondVal); break;
                default: result = secondVal; break;
            }
            display.setText(formatResult(result));
            pendingOperation = "";
            freshInput = true;
        }
    }

    // --- Expression Parser with Implicit Multiplication Support ---
    private double evaluateExpression(String str) {
        // Preprocess to handle implicit multiplication like "30(5...)" -> "30*(5...)" or ")(pi" -> ")*pi"
        str = str.replaceAll("(?<=[0-9\\)])\\s*(?=[a-zA-Z\\(])", "*");
        str = str.replaceAll("(?<=PI)\\s*(?=[0-9a-zA-Z\\(])", "*");

        final String processedStr = str;

        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < processedStr.length()) ? processedStr.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < processedStr.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(processedStr.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z') {
                    while (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9') nextChar();
                    String func = processedStr.substring(startPos, this.pos);
                    if (func.equalsIgnoreCase("PI")) {
                        x = Math.PI;
                    } else {
                        eat('(');
                        double val = parseExpression();
                        eat(')');
                        switch (func.toLowerCase()) {
                            case "sqrt": x = Math.sqrt(val); break;
                            case "sin": x = Math.sin(Math.toRadians(val)); break;
                            case "cos": x = Math.cos(Math.toRadians(val)); break;
                            case "tan": x = Math.tan(Math.toRadians(val)); break;
                            case "log": x = Math.log10(val); break;
                            case "ln": x = Math.log(val); break;
                            case "e^x": x = Math.exp(val); break;
                            default: throw new RuntimeException("Unknown function: " + func);
                        }
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }

    private String formatResult(double val) {
        if (Double.isNaN(val) || Double.isInfinite(val)) return "Error";
        if (val == (long) val) {
            return String.format("%d", (long) val);
        } else {
            return String.format("%.8f", val).replaceAll("0*$", "").replaceAll("\\.$", "");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HP35Calculator().setVisible(true);
        });
    }
}