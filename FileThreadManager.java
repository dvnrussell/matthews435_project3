import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * An object constructed with the name a file and contains methods for
 * manipulating the contents of that file
 * @author dvnrussell
 */
public class FileThreadManager {

    File textFile;
    final String FLAG = "%%EOF%%";

    /**
     * Constructor.
     * 
     * @param fileName name of the file to be used in the class's methods
     */
    public FileThreadManager(String fileName) {
        this.textFile = new File(fileName);
    }

    /**
     * Takes each line of the class's file and pushes onto a buffer
     * @param buffer the buffer that will accept all of the file's lines (FIFO)
     */
    private void enqueueParsedLines(SynchronizedBuffer<String> buffer) {
        try {
            Scanner scnr = new Scanner(textFile);
            while (scnr.hasNextLine()) {
                String line = scnr.nextLine();
                buffer.add(line);
            }
            scnr.close();
            buffer.add(FLAG);
        } catch (FileNotFoundException e) {
            System.out.println(
                "No file of this name found. " + 
                "You might want to double check the spelling or your directory."
            );
            System.out.println(e);
            return;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Takes each string in a buffer and replaces all of the digits with the 
     * phonetic spelling of that digit
     * @param inputBuffer the buffer containing the strings to change
     * @param outputBuffer the buffer where the changed strings will be stored
     */
    private void replaceDigitsWithText(SynchronizedBuffer<String> inputBuffer,
        SynchronizedBuffer<String> outputBuffer) {
        try {
            String line = inputBuffer.remove();
            while (line != FLAG) {
                line = 
                    line.replace("0", "zero")
                        .replace("1", "one")
                        .replace("2", "two")
                        .replace("3", "three")
                        .replace("4", "four")
                        .replace("5", "five")
                        .replace("6", "six")
                        .replace("7", "seven")
                        .replace("8", "eight")
                        .replace("9", "nine");
                outputBuffer.add(line);
                line = inputBuffer.remove();
            }
            outputBuffer.add(FLAG);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Takes each string in a buffer and reverses all of the characters
     * @param inputBuffer the buffer containing the strings to change
     * @param outputBuffer the buffer where the reversed strings will be stored
     */
    private void reverseLineCharacters(SynchronizedBuffer<String> inputBuffer,
        SynchronizedBuffer<String> outputBuffer) {

        try {
            String line = inputBuffer.remove();
            while (line != FLAG) {
                StringBuilder input = new StringBuilder(line);
                line = input.reverse().toString();
                outputBuffer.add(line);
                line = inputBuffer.remove();
            }
            outputBuffer.add(FLAG);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes each string from a buffer and prints to the console
     * @param buffer the buffer containing the strings to be printed
     */
    private void printLinesFromBuffer(SynchronizedBuffer<String> buffer) {

        try {
            String line = buffer.remove();
            while (line != FLAG) {
                System.out.println(line);
                line = buffer.remove();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts the thread that will perform the file parsing function
     * on a buffer
     * @param buffer the buffer to pass to the file parsing function
     */
    public synchronized void fileParseThreadInitializer(
        SynchronizedBuffer<String> buffer) {

        Thread t = new Thread(() -> enqueueParsedLines(buffer));
        t.start();
    }

    /**
     * Starts the thread that will perform the digit replacement function
     * on a buffer
     * @param inputBuffer the buffer with the strings to replace digits for
     * @param outputBuffer the buffer where the new strings will be stored
     */
    public synchronized void digitReplacementThreadInitializer(
        SynchronizedBuffer<String> inputBuffer, 
        SynchronizedBuffer<String> outputBuffer) {

        Thread t = new Thread(() -> replaceDigitsWithText(
            inputBuffer, outputBuffer));
        t.start();
    }

    /**
     * Starts the thread that will perform the character reversal function
     * on a buffer
     * @param inputBuffer the buffer with the strings to reverse
     * @param outputBuffer the buffer where the new strings will be stored
     */
    public synchronized void characterReversalThreadInitializer(
        SynchronizedBuffer<String> inputBuffer,
        SynchronizedBuffer<String> outputBuffer) {

        Thread t = new Thread(() -> reverseLineCharacters(
            inputBuffer, outputBuffer));
        t.start();
    }

    /**
     * Starts the thread that will print each string in a buffer
     * @param buffer
     */
    public synchronized void linePrintingThreadInitializer(
        SynchronizedBuffer<String> buffer) {

        Thread t = new Thread(() -> printLinesFromBuffer(buffer));
        t.start();
    }
}