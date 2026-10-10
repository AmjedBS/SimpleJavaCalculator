public class EngineTest {
    public static void main(String[] args) {
        String[] testCases = {
            "2+3", "2(3+4)", "5/0", "5++2", "*3", "5(", "abc", "2*)"
        };

        System.out.println("==================================================");
        System.out.println("         ADVANCED ENGINE DIAGNOSTIC TEST          ");
        System.out.println("==================================================");

        for (String test : testCases) {
            CalculatorEngine engine = new CalculatorEngine();
            for (int i = 0; i < test.length(); i++) {
                engine.inputToken(String.valueOf(test.charAt(i)));
            }
            engine.inputToken("=");
            System.out.println(String.format("Input: %-10s --> Result: %s", test, engine.getExpression()));
        }
        System.out.println("==================================================");
    }
}