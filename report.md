# Laboratory Report — Logger and Parentheses Validator

**Student:** Croitoru Artemie FAF 252\
**Programming language:** Java

## 1. Program Structure

**Logger** is responsible for printing messages. It decides if a message is shown (based on the configured level), builds the final text from the format string, and prints it with `System.out.println`. It also has the helper methods `trace`, `info`, `warn` and `error`, which just call `log` with the right level.

**ParenthesesValidator** is responsible for checking if a string of parentheses is valid. It does not print anything by itself, it only walks through the string and reports what it finds.

The two classes collaborate like this: the validator creates a `Logger` named `"Parentheses"` and calls it whenever something happens (opening parenthesis found, pair matched, result, error). So the validator *uses* the logger, it does not inherit from it. `LogLevel` is an enum with the four levels, and `Main` runs the tests.

```java
public class ParenthesesValidator {
    private final Logger logger = new Logger("Parentheses");
    ...
}
```

## 2. Classes and Objects

In my implementation a class is the blueprint that describes what data and behavior something has. I have the classes `Logger`, `ParenthesesValidator` and `Main`, plus the enum `LogLevel`.

An example of an object is the logger created inside the validator:

```java
private final Logger logger = new Logger("Parentheses");
```

State stored by each object:
- a `Logger` object stores its `name`;
- a `ParenthesesValidator` object stores a reference to its `logger`. The result of `isValid` is not stored, it is only returned.

## 3. Static and Instance Members

**Static members of `Logger`:** `level` and `format`. They are static because the level and the format should be the same for the whole application. If I want to hide trace messages, I want that for every logger, not just one. The constants `TIME_FORMAT` and `RANDOM_MESSAGES` and the object `random` are static too, because they are the same for all loggers.

**Instance member:** `name`. Each logger has its own name, so one logger can be called `Parentheses` and another one `Demo`.

```java
public class Logger {
    // static: shared by all loggers
    private static LogLevel level = LogLevel.INFO;
    private static String format = "[%t] [%l] %n: %m";

    // instance: every logger has its own name
    private final String name;

    public Logger(String name) {
        this.name = name;
    }
    ...
}
```

When the level is changed, it affects every logger instance, because they all read the same static `level` from the class, not from the object. For example, after `Logger.setLevel(LogLevel.ERROR)` both a `Parentheses` logger and a `Demo` logger will only print errors.

```java
public static void setLevel(LogLevel newLevel) {
    level = newLevel;
}
```

## 4. Methods and Responsibilities

The `log` method does three things: it compares the severity of the message with the configured level and returns if the message is too low, it builds the text (replaces `%t`, `%m`, `%n`, `%l`, `%r`), and then it prints it. The severity of a level is its position in the enum, given by `ordinal()` (TRACE is 0, INFO is 1, WARN is 2, ERROR is 3), so two levels can be compared as simple numbers.

```java
public void log(LogLevel msgLevel, String message) {
    // ignore messages that are less severe than the configured level
    if (msgLevel.ordinal() < level.ordinal()) {
        return;
    }
    System.out.println(buildText(msgLevel, message));
}
```

The method `buildText` goes through the format string only once. When it finds a `%` followed by a known letter, it adds the value of the placeholder, otherwise it adds the character unchanged. Because the format is read only once, a `%n` written inside the message is not replaced a second time.

```java
private String buildText(LogLevel msgLevel, String message) {
    String result = "";
    int i = 0;
    while (i < format.length()) {
        char c = format.charAt(i);
        String value = null;
        if (c == '%' && i + 1 < format.length()) {
            value = getValue(format.charAt(i + 1), msgLevel, message);
        }
        if (value != null) {
            result += value;
            i += 2;      // skip the % and the letter
        } else {
            result += c; // normal character, print it unchanged
            i++;
        }
    }
    return result;
}
```

`ParenthesesValidator` uses a logger instead of doing the logging itself because the two jobs are different. The validator should only care about checking parentheses. If it also handled levels and formats, the code would be mixed and hard to change. With a separate `Logger`, I can change the format or level in one place and the validator does not change at all. The logger can also be reused in other programs.

An example of a method call between objects: inside `isValid`, the validator calls the logger:

```java
logger.trace("Opening parenthesis found at index " + i);
```

and `trace` calls `log(LogLevel.TRACE, message)`.

## 5. Testing and Correctness

- Valid input: `(())((()())())`  →  `true`
- Invalid input: `)((()))`  →  `false`

The program uses a stack (`java.util.Stack`) that stores the index of every `(`. When a `(` is found it is pushed. When a `)` is found, if the stack is empty there is nothing to match, so the string is invalid (and a warning is logged). Otherwise the top of the stack is popped and a matching pair is logged. At the end, if the stack is not empty, some `(` never got closed, so the string is invalid too.

```java
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
```

One test that helped me: `)((()))`. It has the same number of `(` and `)`, so a solution that only counts them would say it is valid, but it is not, because the first `)` comes before any `(`. This test is the reason I check for an empty stack when a `)` appears instead of only counting. I also tested `(()` and `())` to check both kinds of error, and the empty string `""` (valid, nothing to match). I ran all the tests with level `TRACE` and then with level `INFO` and the results were the same (7 of 7 passed), which shows that the level only changes what is printed, not the validation.

| Input | Expected | Obtained | Status |
|---|---|---|---|
| `()` | true | true | passed |
| `)((()))` | false | false | passed |
| `(` | false | false | passed |
| `(())((()())())` | true | true | passed |
| `(()` | false | false | passed |
| `())` | false | false | passed |
| `""` (empty) | true | true | passed |

Output for `)((()))` with level `INFO`:

```
[10:11:39] [WARN] Parentheses: Closing parenthesis at index 0 has no opening parenthesis
[10:11:39] [INFO] Parentheses: Matching pair found: indexes 3 and 4
[10:11:39] [INFO] Parentheses: Matching pair found: indexes 2 and 5
[10:11:39] [INFO] Parentheses: Matching pair found: indexes 1 and 6
[10:11:39] [INFO] Parentheses: Result for ")((()))": false
[10:11:39] [ERROR] Parentheses: ")((()))" is invalid
```

## 6. Reflection

The most important OOP concept for me was the difference between static and instance members, and how it ties to separating responsibilities. `level` and `format` are shared by the class, `name` belongs to each object, and the validator just uses the logger without knowing how printing works.

With more time I would:
- let the logger write to a file or other output and not only the console, by moving the printing into a separate method or class;
- add proper unit tests with JUnit instead of my own `runTests` method that compares results manually;
- pass the logger to the `ParenthesesValidator` constructor (instead of creating it inside), so it is easier to test and to replace the logger.

I also did the extra credit: the `%r` placeholder is replaced by a random message that belongs to the level of the current log entry. The messages are kept in a 2D array with one row for each level, and the row is chosen with the `ordinal()` of the level:

```java
String[] options = RANDOM_MESSAGES[msgLevel.ordinal()];
return options[random.nextInt(options.length)];
```

## How to run

```
javac *.java
java Main
```
