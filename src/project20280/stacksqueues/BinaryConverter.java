package project20280.stacksqueues;

import project20280.interfaces.Stack;

public class BinaryConverter {

    public static String convertToBinary(long dec_num) {
        if (dec_num == 0) {
            return "0";
        }

        Stack<Integer> S = new ArrayStack<>();
        long n = dec_num;

        while (n > 0) {
            S.push((int)(n % 2));
            n /= 2;
        }

        StringBuilder sb = new StringBuilder();
        while (!S.isEmpty()) {
            sb.append(S.pop());
        }

        return sb.toString();
    }
}