package edu.psu.se411.lab02;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Exercise 1: PrintableList
        String[] courses = {
            "SE411",
            "Software Engineering",
            "Java Generics"
        };

        PrintableList<String> printableList =
                new PrintableList<>(courses);

        System.out.println("PrintableList:");
        printableList.printItems();

        // Exercise 2: NumberBox with Integer
        NumberBox<Integer> integerBox = new NumberBox<>();
        integerBox.setItem(10);

        System.out.println("\nInteger NumberBox:");
        System.out.println("Stored item: " + integerBox.getItem());
        System.out.println("10 + 5 = " + integerBox.add(5));
        System.out.println(
                "Integer list sum: "
                        + integerBox.sumNumbers(List.of(1, 2, 3, 4))
        );

        // Exercise 2: NumberBox with Double
        NumberBox<Double> doubleBox = new NumberBox<>();
        doubleBox.setItem(4.5);

        System.out.println("\nDouble NumberBox:");
        System.out.println("Stored item: " + doubleBox.getItem());
        System.out.println("4.5 + 1.5 = " + doubleBox.add(1.5));
        System.out.println(
                "Double list sum: "
                        + doubleBox.sumNumbers(List.of(1.5, 2.5, 3.0))
        );

                // Exercise 3: Generic Pipeline
        Pipeline<String, String> pipeline = Pipeline.<String>start()
                .add(String::trim)
                .add(String::toUpperCase)
                .add(String::length)
                .add(length -> "Final length: " + length);

        String pipelineResult =
                pipeline.execute("  Java Generics  ");

        System.out.println("\nPipeline:");
        System.out.println(pipelineResult);

        // Exercise 4: Wildcards
        List<Object> mixedList =
                List.of("Java", 42, 3.14);

        System.out.println("\nWildcard printList:");
        printList(mixedList);

        System.out.println(
                "Wildcard integer sum: "
                        + sumNumbers(List.of(10, 20, 30))
        );

        System.out.println(
                "Wildcard double sum: "
                        + sumNumbers(List.of(1.5, 2.5, 3.5))
        );
    }

    public static void printList(List<?> items) {
        for (Object item : items) {
            System.out.println(item);
        }
    }

    public static double sumNumbers(
            List<? extends Number> numbers) {
        double sum = 0;

        for (Number number : numbers) {
            sum += number.doubleValue();
        }

        return sum;
    }
}