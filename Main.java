public class Main {

    private static void runTests() {
        ParenthesesValidator validator = new ParenthesesValidator();
        String[] inputs = {"()", ")((()))", "(", "(())((()())())", "(()", "())", ""};
        boolean[] expected = {true, false, false, true, false, false, true};

        int passed = 0;
        for (int i = 0; i < inputs.length; i++) {
            boolean result = validator.isValid(inputs[i]);
            String status;
            if (result == expected[i]) {
                passed++;
                status = "PASS";
            } else {
                status = "FAIL";
            }
            System.out.println(status + " input=\"" + inputs[i]
                    + "\" expected=" + expected[i] + " got=" + result + "\n");
        }
        System.out.println(passed + "/" + inputs.length + " tests passed\n");
    }

    public static void main(String[] args) {
        System.out.println("=== Level TRACE ===");
        Logger.setLevel(LogLevel.TRACE);
        runTests();

        System.out.println("=== Level INFO (no trace messages) ===");
        Logger.setLevel(LogLevel.INFO);
        runTests();

        System.out.println("=== Level ERROR, custom format ===");
        Logger.setLevel(LogLevel.ERROR);
        Logger.setFormat("%l - %n - %m");
        new ParenthesesValidator().isValid("(()");

        System.out.println("\n=== Extra credit: %r ===");
        Logger.setLevel(LogLevel.TRACE);
        Logger.setFormat("[%t] [%l] %r");
        Logger demo = new Logger("Demo");
        demo.trace("ignored text");
        demo.info("ignored text");
        demo.warn("ignored text");
        demo.error("ignored text");
    }
}
