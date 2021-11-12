package ru.red;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/*
 * Just a little joke program
 * I consider supporting it for a bit
 * I have a crazy idea of making declarative-support via annotation parsing and generating code in the filesystem
 * Peace :) V
 *
 * -Daniil Shreyder,
 */
public class General {
    public static void main(String[] args) throws IOException {
        String template = """
                import java.util.Scanner;
                                
                public class %s {
                    public static void main(String[] args) {
                        Scanner scanner = new Scanner(System.in);
                        System.out.print("Enter a number: ");
                        System.out.printf("Number is even? %s", isEven(scanner.nextInt()));
                    }
                    
                    public static boolean isEven(int n) {
                %s
                    }
                }
                """;

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter output .java class name: ");
        String className = scanner.nextLine();

        System.out.print("How many if statements do you want?");
        int repeats = scanner.nextInt();

        BufferedWriter bufferedWriter = defaultFunnyBufferedWriter(className);
        long start = System.nanoTime();
        bufferedWriter.write(
                String.format(template,
                        className,
                        "%s",
                        Stream.of(new StringBuilder())
                                .peek(builder ->
                                        Stream.iterate(0, i -> i + 1)
                                                .limit(repeats)
                                                .map(i -> String.format(
                                                        """
                                                                        if (n == %s) {
                                                                            return %s;
                                                                        }
                                                                """,
                                                        i,
                                                        i % 2 == 0
                                                ))
                                                .forEach(builder::append)
                                )
                                .collect(Collectors.joining())
                ));
        long finish = System.nanoTime();
        bufferedWriter.flush();
        long flushed = System.nanoTime();
        System.out.printf("Took %s NANOS to complete iterating and %s NANOS to flush\n", finish - start, flushed - finish);
        System.out.print("Output file is located in the working directory");

    }

    public static BufferedWriter defaultFunnyBufferedWriter(String className) throws IOException {
        Path resolve = Paths.get("")
                .toAbsolutePath()
                .resolve(className + ".java");
        Files.deleteIfExists(resolve);

        return Files.newBufferedWriter(Files.createFile(resolve));
    }
}
