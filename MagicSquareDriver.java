import java.io.FileNotFoundException;
import java.io.IOException;

public class MagicSquareDriver {
    /**
     * Print out the proper usage of MagicSquareDriver.
     *
     * <p>Primarily useful for aborting due to invalid command line arguments
     * and reminding the user of the correct arguments</p>
     */
    private static void printUsage() {
        String[] lines = {
            "MagicSquareDriver usage:",
            "java MagicSquareDriver <-check | -create> <filename> < |size>",
            "",
            "\t-check <filename>:\t\tVerify whether or not the data within <filename> is a magic square",
            "\t-create <filename> <size>:\tWrite out a <size>x<size> magic square to <filename>.",
            "\t\tNote that <size> cannot be an even number, and this program will not accept an even <size>."
        };
        for (String line : lines) {
            System.out.println(line);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            printUsage();
            return;
        }

        if (args[0].equals("-check")) {
            if (args.length != 2) {
                // subtract 1 from args.length since we're not counting -check itself here
                printUsage();
                return;
            }

            String filename = args[1];
            MagicSquareInterface magicSquare = new MagicSquare(filename);
            System.out.println(magicSquare);
        } else if (args[0].equals("-create")) {
            if (args.length != 3) {
                // subtract 1 from args.length since we're not counting -create itself here
                printUsage();
                return;
            }

            String filename = args[1];
            int squareSize;
            try {
                squareSize = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                printUsage();
                return;
            }

            boolean squareSizeIsEven = squareSize % 2 == 0;
            if (squareSizeIsEven) {
                printUsage();
                return;
            }

            MagicSquareInterface magicSquare = new MagicSquare(filename, squareSize);
            System.out.println(magicSquare);
        } else {
            printUsage();
            return;
        }
    }
}
