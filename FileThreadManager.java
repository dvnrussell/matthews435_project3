import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileThreadManager {

    File textFile;
    final String FLAG = "%%EOF%%";

    public FileThreadManager(String fileName) {
        this.textFile = new File(fileName);
    }

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
            System.out.println("No file of this name found. You might want to double check the spelling or your directory.");
            System.out.println(e);
            return;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void replaceDigitsWithText(SynchronizedBuffer<String> inputBuffer, SynchronizedBuffer<String> outputBuffer) {
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

    private void reverseLineCharacters(SynchronizedBuffer<String> inputBuffer, SynchronizedBuffer<String> outputBuffer) {
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

    public synchronized void fileParseThreadInitializer(SynchronizedBuffer<String> buffer) {
        Thread t = new Thread(() -> enqueueParsedLines(buffer));
        t.start();
    }

    public synchronized void digitReplacementThreadInitializer(SynchronizedBuffer<String> inputBuffer, SynchronizedBuffer<String> outputBuffer) {
        Thread t = new Thread(() -> replaceDigitsWithText(inputBuffer, outputBuffer));
        t.start();
    }

    public synchronized void characterReversalThreadInitializer(SynchronizedBuffer<String> inputBuffer, SynchronizedBuffer<String> outputBuffer) {
        Thread t = new Thread(() -> reverseLineCharacters(inputBuffer, outputBuffer));
        t.start();
    }

    public synchronized void linePrintingThreadInitializer(SynchronizedBuffer<String> buffer) {
        Thread t = new Thread(() -> printLinesFromBuffer(buffer));
        t.start();
    }
}