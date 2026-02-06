package project20280.stacksqueues;

import project20280.interfaces.Stack;

class BracketChecker {
    private String input;

    public BracketChecker(String in) {
        input = in;
    }

    public void check() {
        Stack<Character> stk = new ArrayStack<>();

        for (int i = 0; i < input.length(); i++) {
            char charOfInput = input.charAt(i);

            if (charOfInput == '(' || charOfInput == '[' || charOfInput == '{') {
                stk.push(charOfInput);
            } else if (charOfInput == ')' || charOfInput == ']' || charOfInput == '}') {
                if (stk.isEmpty()) {
                    System.out.println("not correct; Nothing matches final " + charOfInput);
                    return;
                }

                char left = stk.pop();
                if (!delimiterMatch(left, charOfInput)) {
                    System.out.println("not correct, " + charOfInput + " does not match " + left);
                    return;
                }
            }
        }

        if (!stk.isEmpty()) {
            System.out.println("not correct; Nothing matches opening");
        } else {
            System.out.println("Correct");
        }
    }

    private boolean delimiterMatch(char left, char right) {
        if (left == '(' && right == ')') {
            return true;
        } else if (left == '[' && right == ']') {
            return true;
        } else if (left == '{' && right == '}') {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        String[] inputs = {
                "[]]()()", // not correct
                "c[d]", // correct\n" +
                "a{b[c]d}e", // correct\n" +
                "a{b(c]d}e", // not correct; ] doesn't match (\n" +
                "a[b{c}d]e}", // not correct; nothing matches final }\n" +
                "a{b(c) ", // // not correct; Nothing matches opening {
        };

        for (String input : inputs) {
            BracketChecker checker = new BracketChecker(input);
            System.out.println("checking: " + input);
            checker.check();
            System.out.println();
        }
    }
}