import java.io.FileNotFoundException;
import java.io.IOException;

public class MagicSquareDriver {
    /*
     * Print an error message in the format "ERROR: {@code errorMsg_}!
     * Aborting." This method handles punctuation for you, and does not ignore
     * the supplied punctuation.
     *
     * @param errorMsg_ The error message you would like to deliver to the user,
     * sans "ERROR: " and "Aborting."
     */
    private static void printErrorMsg(String errorMsg_) {
        String errorMsg = errorMsg_.strip();

        // Append punctuation (if missing) to the message to delineate
        // "Aborting" from the message.
        String punctuation = "!";
        String[] validPunctuations = {".", "!", "?"};
        for (String validPunctuation : validPunctuations) {
            if (errorMsg.endsWith(validPunctuation)) {
                punctuation = "";
                break;
            }
        }

        System.err.println("ERROR: " + errorMsg + punctuation + " Aborting.");
    }

    /**
     * Print out the proper usage of MagicSquareDriver.
     *
     * <p>Primarily useful for aborting due to invalid command line arguments
     * and reminding the user of the correct arguments</p>
     *
     * @param errorMsg An optional error message to display to the user. If this
     * string is equal to "", nothing will be sent to stderr.
     */
    private static void printUsage(String errorMsg) {
        if (errorMsg != null && !errorMsg.equals("")) {
            printErrorMsg(errorMsg);
        }

        String[] lines = {
            "MagicSquareDriver usage:",
            "java MagicSquareDriver <-check | -create> <filename> < |size>",
            "",
            "\t-check <filename>:\tVerify whether or not the data within <filename> is a magic square",
            "\t-create <filename> <size>:\tWrite out a <size>x<size> magic square to <filename>.",
            "\t\tNote that <size> cannot be an even number, and this program will not accept an even <size>."
        };
        for (String line : lines) {
            System.out.println(line);
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage("Please provide arguments to the program");
            return;
        }

        if (args[0].equals("-check")) {
            if (args.length != 2) {
                // subtract 1 from args.length since we're not counting -check itself here
                printUsage("Invalid number of arguments supplied to -check. Expected: 1, Received: " + (args.length - 1) + ".");
                return;
            }

            String filename = args[1];
            try {
                MagicSquareInterface magicSquare = new MagicSquare(filename);
                System.out.println(magicSquare);
            } catch (FileNotFoundException e) {
                printErrorMsg(filename + " could not be read or was formatted improperly");
                return;
            }
        } else if (args[0].equals("-create")) {
            if (args.length != 3) {
                // subtract 1 from args.length since we're not counting -create itself here
                printUsage("Invalid number of arguments for -create. Expected: 2, Received: " + (args.length - 1) + ".");
                return;
            }

            String filename = args[1];
            int squareSize;
            try {
                squareSize = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                printErrorMsg(args[2] + " is not an integer");
                return;
            }

            boolean squareSizeIsEven = squareSize % 2 == 0;
            if (squareSizeIsEven) {
                printUsage(squareSize + " is an even number, which this program can't handle");
                return;
            }

            try {
                MagicSquareInterface magicSquare = new MagicSquare(filename, squareSize);
                System.out.println(magicSquare);
            } catch (IOException e) {
                printErrorMsg("Failed to write to " + filename);
                return;
            }
        } else {
            printUsage("Invalid argument. Please specify either -check or -create.");
            return;
        }
    }
}
