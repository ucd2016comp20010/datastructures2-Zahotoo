package project20280.stacksqueues;

import org.junit.jupiter.api.Test;
import project20280.interfaces.Stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static project20280.stacksqueues.BinaryConverter.convertToBinary;

public class BinaryConverterTest {
    @Test
    void testConvertToBinary() {
        assertEquals("10111", convertToBinary(23));
        assertEquals("111001000000101011000010011101010110110001100010000000000000",
                convertToBinary(1027010000000000000L));
    }

}