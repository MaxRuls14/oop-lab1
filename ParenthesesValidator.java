import java.util.Stack;

public class ParenthesesValidator {
    private final Logger logger = new Logger("Parentheses");

    // returns true if every "(" has a matching ")" in the correct order
    public boolean isValid(String input) {
        Stack<Integer> openIndexes = new Stack<>(); // index of every "(" not matched yet
        boolean valid = true;

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (ch == '(') {
                openIndexes.push(i);
                logger.trace("Opening parenthesis found at index " + i);
            } else if (ch == ')') {
                if (openIndexes.isEmpty()) {
                    // there is no "(" before this one
                    valid = false;
                    logger.warn("Closing parenthesis at index " + i + " has no opening parenthesis");
                } else {
                    int start = openIndexes.pop();
                    logger.info("Matching pair found: indexes " + start + " and " + i);
                }
            }
        }

        // a "(" that is still on the stack was never closed
        if (!openIndexes.isEmpty()) {
            valid = false;
        }

        logger.info("Result for \"" + input + "\": " + valid);
        if (!valid) {
            logger.error("\"" + input + "\" is invalid");
        }
        return valid;
    }
}
