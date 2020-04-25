public class TextTransformer {
    /**
     * Demonstrates the producer/consumer model in use in threads designed 
     * to manipulate text. The user passes a text file for the program to 
     * parse and a maximum buffer size
     * @param args the arguments passed to main: the name of the text 
     * file and the maximum buffer size
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                "Arguments must be 1 file name and 1 maximum buffer size."
            );
        }

        String fileName = args[0];
        Integer maximumBufferSize = Integer.parseInt(args[1]);
        FileThreadManager fileToBufferManager = new FileThreadManager(fileName);


        // thread 1 block
        SynchronizedBuffer<String> fileReadBuffer = 
            new SynchronizedBuffer<String>(maximumBufferSize);
        fileToBufferManager.fileParseThreadInitializer(fileReadBuffer);

        // thread 2 block
        SynchronizedBuffer<String> replacedIntegerBuffer =
            new SynchronizedBuffer<String>(maximumBufferSize);
        fileToBufferManager.digitReplacementThreadInitializer(fileReadBuffer,
            replacedIntegerBuffer);

        // thread 3 block
        SynchronizedBuffer<String> reversedCharacterBuffer =
            new SynchronizedBuffer<String>(maximumBufferSize);
        fileToBufferManager.characterReversalThreadInitializer(
            replacedIntegerBuffer, reversedCharacterBuffer);

        //thread 4 block
        fileToBufferManager.linePrintingThreadInitializer(
            reversedCharacterBuffer);
    }
}