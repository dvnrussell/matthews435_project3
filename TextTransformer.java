/**
 * Hello
 */
public class TextTransformer {
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("One and only one file must be be named.");
        }

        String fileName = args[0];
        FileThreadManager fileToBufferManager = new FileThreadManager(fileName);


        // thread 1 block
        SynchronizedBuffer<String> fileReadBuffer = new SynchronizedBuffer<String>(16);
        fileToBufferManager.fileParseThreadInitializer(fileReadBuffer);

        // thread 2 block
        SynchronizedBuffer<String> replacedIntegerBuffer = new SynchronizedBuffer<String>(16);
        fileToBufferManager.digitReplacementThreadInitializer(fileReadBuffer, replacedIntegerBuffer);

        // thread 3 block
        SynchronizedBuffer<String> reversedCharacterBuffer = new SynchronizedBuffer<String>(16);
        fileToBufferManager.characterReversalThreadInitializer(replacedIntegerBuffer, reversedCharacterBuffer);

        //thread 4 block
        fileToBufferManager.linePrintingThreadInitializer(reversedCharacterBuffer);
    }
}