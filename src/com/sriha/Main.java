package com.sriha;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Main {

    static int numSteps = 0;

    public static void main(String[] args) {

        runTest("infiniteRecursion()", () -> infiniteRecursion());
        runTest("infiniteRecursionAllocatingMemory(10_000)", () -> infiniteRecursionAllocatingMemory(10_000));
        runTest("infiniteRecursionAllocatingMemory(10_000_000)", () -> infiniteRecursionAllocatingMemory(10_000_000));
        runTest("exhaustNativeMemory", () -> exhaustNativeMemory());
        runTest("exhaustHeapMemory", () -> exhaustHeapMemory());
        runTest("instantiateHeapExhauster", () -> instantiateHeapExhauster());
        runTest("instantiateNativeExhauster", () -> instantiateNativeExhauster());
    }

    static void runTest(String name, Runnable run) {
        System.out.println("Starting " + name + " stress test...");

        try {
            numSteps = 0;
            run.run();
        } catch (StackOverflowError err) {
            System.out.println("ERROR: " + err);
            memoryIntensiveOperation();
        } catch (OutOfMemoryError err) {
            System.out.println("ERROR: " + err);
            memoryIntensiveOperation();
        } catch (Throwable err) {
            System.out.println("ERROR: " + err);
        }

        System.out.printf("Test %s done! NumSteps=%,d%n", name, numSteps);
        System.out.println();
    }


    /**
     * Method that should fail with StackOverflowError
     */
    static int infiniteRecursion() {
        numSteps++;
        return infiniteRecursion();
    }

    /**
     * Method that should fail with StackOverflowError or OutOfMemoryError, depending on how big {@code bufferSize} is
     */
    static int infiniteRecursionAllocatingMemory(int bufferSize) {
        numSteps++;
        char[] buffer = new char[bufferSize];
        return infiniteRecursionAllocatingMemory(bufferSize) + buffer.length;
    }

    /**
     * Method that should fail with OutOfMemoryError
     */
    static HeapMemoryExhauster instantiateHeapExhauster() {
        return new HeapMemoryExhauster();
    }

    /**
     * Method that should fail with OutOfMemoryError
     */
    static NativeMemoryExhauster instantiateNativeExhauster() {
        return new NativeMemoryExhauster();
    }

    /**
     * Method that should fail with OutOfMemoryError
     */
    public static int exhaustHeapMemory() {
        final int size = 100_000_000;
        ArrayList<char[]> bufferCollection = new ArrayList<char[]>();
        for (int i = 0; i < 1000; i++) {
            numSteps++;
            bufferCollection.add(new char[size]);
        }
        return bufferCollection.size();
    }

    /**
     * Method that should fail with OutOfMemoryError
     */
    public static int exhaustNativeMemory() {
        final int size = 100_000_000;
        ArrayList<ByteBuffer> bufferCollection = new ArrayList<ByteBuffer>();
        for (int i = 0; i < 1000; i++) {
            numSteps++;
            bufferCollection.add(ByteBuffer.allocateDirect(size));
        }
        return bufferCollection.size();
    }

    /**
     * Method that allocates a pretty big buffer
     */
    static void memoryIntensiveOperation() {
        System.out.println("Starting memory intensive operation...");
        final int bufferSize = 1000_000;
        char[] buffer = new char[bufferSize];
        System.out.printf("Successfully allocated %,d characters.%n", buffer.length);
    }
}
