public class CalculatorEngine {
    private StringBuilder expression;
    private int parenthesisCount;

    public CalculatorEngine() {
        reset();
    }

    public String getExpression() {
        return expression.toString();
    }

    public void inputToken(String token) {
        // Case 1 of DEL: If currently displaying an error, DEL resets to "0"
        if (expression.toString().startsWith("Error") && token.equals("DEL")) {
            reset();
            return;
        }

        switch (token) {
            case "C":
                reset();
                break;
            case "DEL":
                removeLastChar();
                break;
            case "=":
                evaluateExpression();
                break;
            case "()":
                handleParenthesis();
                break;
            default:
                // Clear error automatically if user starts typing a new expression
                if (expression.toString().startsWith("Error")) {
                    reset();
                }
                appendToken(token);
                break;
        }
    }

    private void reset() {
        expression = new StringBuilder("0");
        parenthesisCount = 0;
    }

    private void removeLastChar() {
        // Case 2 of DEL: Normal character deletion
        if (expression.length() > 1) {
            char lastChar = expression.charAt(expression.length() - 1);
            if (lastChar == '(') parenthesisCount--;
            if (lastChar == ')') parenthesisCount++;
            expression.deleteCharAt(expression.length() - 1);
        } else {
            expression.setLength(0);
            expression.append("0");
        }
    }

    private void appendToken(String token) {
        if (expression.toString().equals("0") && !isOperator(token)) {
            expression.setLength(0);
        }
        expression.append(token);
    }

    private void handleParenthesis() {
        char lastChar = expression.charAt(expression.length() - 1);
        if (parenthesisCount > 0 && !isOperator(lastChar) && lastChar != '(') {
            expression.append(")");
            parenthesisCount--;
        } else {
            if (expression.toString().equals("0")) {
                expression.setLength(0);
            } else if (Character.isDigit(lastChar) || lastChar == ')') {
                expression.append("*");
            }
            expression.append("(");
            parenthesisCount++;
        }
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("%");
    }

    private void evaluateExpression() {
        try {
            String expr = expression.toString();
            double result = parse(expr);
            
            String finalResult;
            if (result == (long) result) {
                finalResult = String.format("%d", (long) result);
            } else {
                finalResult = String.valueOf(result);
            }

            expression.setLength(0);
            expression.append(finalResult);
            parenthesisCount = 0;
        } catch (Exception e) {
            expression.setLength(0);
            expression.append(e.getMessage()); // Returns precise error with position
            parenthesisCount = 0;
        }
    }

    // --- Advanced Position-Aware Parser with Modulo Support ---
    private double parse(String str) {
        ParserContext ctx = new ParserContext(str);
        double result = parseExpression(ctx);
        if (ctx.pos < ctx.str.length()) {
            throw new RuntimeException("Error at pos " + ctx.pos + ": Unexpected token '" + ctx.str.charAt(ctx.pos) + "'");
        }
        if (ctx.parenthesisBalance != 0) {
            throw new RuntimeException("Error: Mismatched parentheses");
        }
        return result;
    }

    private static class ParserContext {
        String str;
        int pos = 0;
        int parenthesisBalance = 0;
        ParserContext(String str) { this.str = str; }
        char peek() { return pos < str.length() ? str.charAt(pos) : '\0'; }
        char next() { return pos < str.length() ? str.charAt(pos++) : '\0'; }
    }

    private double parseExpression(ParserContext ctx) {
        double x = parseTerm(ctx);
        for (;;) {
            if (ctx.peek() == '+') {
                ctx.next();
                x += parseTerm(ctx);
            } else if (ctx.peek() == '-') {
                ctx.next();
                x -= parseTerm(ctx);
            } else {
                return x;
            }
        }
    }

    private double parseTerm(ParserContext ctx) {
        double x = parseFactor(ctx);
        for (;;) {
            if (ctx.peek() == '*') {
                ctx.next();
                x *= parseFactor(ctx);
            } else if (ctx.peek() == '/') {
                ctx.next();
                double denominator = parseFactor(ctx);
                if (denominator == 0) throw new RuntimeException("Error at pos " + (ctx.pos - 1) + ": Division by zero");
                x /= denominator;
            } else if (ctx.peek() == '%') {
                ctx.next();
                double divisor = parseFactor(ctx);
                if (divisor == 0) throw new RuntimeException("Error at pos " + (ctx.pos - 1) + ": Modulo by zero");
                x %= divisor;
            } else if (ctx.peek() == '(') {
                x *= parseFactor(ctx);
            } else {
                return x;
            }
        }
    }

    private double parseFactor(ParserContext ctx) {
        if (ctx.peek() == '+') {
            ctx.next();
            return parseFactor(ctx);
        }
        if (ctx.peek() == '-') {
            ctx.next();
            return -parseFactor(ctx);
        }

        double x;
        int startPos = ctx.pos;
        if (ctx.peek() == '(') {
            ctx.next();
            ctx.parenthesisBalance++;
            x = parseExpression(ctx);
            if (ctx.peek() == ')') {
                ctx.next();
                ctx.parenthesisBalance--;
            } else {
                throw new RuntimeException("Error at pos " + ctx.pos + ": Missing ')'");
            }
        } else if ((ctx.peek() >= '0' && ctx.peek() <= '9') || ctx.peek() == '.') {
            while ((ctx.peek() >= '0' && ctx.peek() <= '9') || ctx.peek() == '.') {
                ctx.next();
            }
            try {
                x = Double.parseDouble(ctx.str.substring(startPos, ctx.pos));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Error at pos " + startPos + ": Invalid number format");
            }
        } else {
            throw new RuntimeException("Error at pos " + ctx.pos + ": Unexpected character '" + ctx.peek() + "'");
        }

        return x;
    }
}