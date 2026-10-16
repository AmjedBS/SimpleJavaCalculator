public final class EngineTest {
    private static int testsRun;

    public static void main(String[] args) {
        testEvaluation("addition and precedence", "14", "2+3*4");
        testEvaluation("parentheses", "20", "(2+3)*4");
        testEvaluation("implicit multiplication", "14", "2(3+4)");
        testEvaluation("left-associative division", "2", "12/3/2");
        testEvaluation("decimal arithmetic", "3.75", "1.5+2.25");
        testEvaluation("unary minus", "-10", "5*-2");
        testEvaluation("modulo", "1", "10%3");
        testParenthesisButton();
        testClear();
        testDelete();
        testDivisionByZero();
        testErrorRecovery();
        testDeleteOnError();

        System.out.println("Passed " + testsRun + " CalculatorEngine tests.");
    }

    private static void testEvaluation(String name, String expected, String expression) {
        CalculatorEngine engine = new CalculatorEngine();
        typeExpression(engine, expression);
        engine.inputToken("=");
        check(name, expected, engine.getExpression());
    }

    private static void testParenthesisButton() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputToken("2");
        engine.inputToken("()");
        typeExpression(engine, "3+4");
        engine.inputToken("()");
        engine.inputToken("=");
        check("parenthesis button and implicit multiplication", "14", engine.getExpression());
    }

    private static void testClear() {
        CalculatorEngine engine = new CalculatorEngine();
        typeExpression(engine, "123");
        engine.inputToken("C");
        check("clear resets the expression", "0", engine.getExpression());
    }

    private static void testDelete() {
        CalculatorEngine engine = new CalculatorEngine();
        typeExpression(engine, "123");
        engine.inputToken("DEL");
        engine.inputToken("=");
        check("delete removes the last digit", "12", engine.getExpression());
    }

    private static void testDivisionByZero() {
        testEvaluation("division by zero reports an error",
                "Error at pos 2: Division by zero", "5/0");
    }

    private static void testErrorRecovery() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputToken("1/0");
        engine.inputToken("=");
        engine.inputToken("7");
        engine.inputToken("=");
        check("typing after an error starts a new expression", "7", engine.getExpression());
    }

    private static void testDeleteOnError() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputToken("1/0");
        engine.inputToken("=");
        engine.inputToken("DEL");
        check("delete on an error resets the expression", "0", engine.getExpression());
    }

    private static void check(String name, String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(name + ": expected \"" + expected
                    + "\" but got \"" + actual + "\"");
        }
        testsRun++;
        System.out.println("PASS " + name);
    }

    private static void typeExpression(CalculatorEngine engine, String expression) {
        for (int i = 0; i < expression.length(); i++) {
            engine.inputToken(String.valueOf(expression.charAt(i)));
        }
    }
}
