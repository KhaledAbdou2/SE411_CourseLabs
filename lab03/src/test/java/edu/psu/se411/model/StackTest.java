package edu.psu.se411.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class StackTest {

    @Test
    public void pushPushPop_returnsLatestElement() {
        Stack<String> stringStack = new Stack<>();

        stringStack.push("Z");
        stringStack.push("A");

        assertEquals("A", stringStack.pop());
    }

    @Test
    public void popEmptyStack_throwsException() {
        Stack<String> stringStack = new Stack<>();

        NoSuchElementException thrown = assertThrows(
                NoSuchElementException.class,
                () -> stringStack.pop(),
                "Expected pop from empty Stack to throw"
        );

        assertTrue(
                thrown.getMessage().equals(
                        "Stack is empty, cannot pop"
                )
        );
    }

    @Test
    public void pushedElements_arePoppedInReverseOrder() {
        Stack<String> stringStack = new Stack<>();

        stringStack.push("Z");
        stringStack.push("A");
        stringStack.push("B");

        assertEquals("B", stringStack.pop());
        assertEquals("A", stringStack.pop());
        assertEquals("Z", stringStack.pop());
    }
}