import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * A concrete representation of a "magic square."
 *
 * A square is considered "magic" if the its rows, columns, and diagonals, all
 * sum to the same number. For example, this is a valid 4x4 magic square:
 *
 * |----|----|----|----|
 * | 16 |  3 |  2 | 13 |
 * |  5 | 10 | 11 |  8 |
 * |  9 |  6 |  7 | 12 |
 * |  4 | 15 | 14 |  1 |
 * |----|----|----|----|
 *
 * Note that this class does not necessitate that its data be "magic," it only
 * offers validation of whether it is.
 *
 * @author Cedar Piehl
 */
public class MagicSquare implements MagicSquareInterface {
    private int[][] data;
    private boolean dataIsMagic;

    /**
     * Constructs a {@link MagicSquare} from a file that should have the
     * following format:
     *
     * <dimension size>
     * <magic square data, columns separated by spaces, rows separated by newlines>
     *
     * For example, the 4x4 magic square would be stored as:
     *
     * 4
     * 16 3 2 13
     * 5 10 11 8
     * 9 6 7 12
     * 4 15 14 1
     *
     * @param filename The file to read the magic square data from. The contents
     * of this file should conform to the specification above.
     *
     * @return A {@link MagicSquare} with the data from {@code filename}
     *
     * @throws {@link FileNotFoundException} when the supplied {@code filename}
     * cannot be read or is formatted incorrectly.
     */
    public MagicSquare(String filename) throws FileNotFoundException {
        data = readMatrix(filename);
        dataIsMagic = isMagicSquare();
    }

    /*
     * Constructs a {@link MagicSquare} calculated from the {@code
     * dimensionality} provided. Will write out data to the supplied {@code
     * filename}
     *
     * @param filename The destination for where this data should be written to.
     * 
     * @param dimensionality The dimensionality of the magic square. The magic
     * square will be {@code dimensionality}x{@code dimensionality}.
     * 
     * @throws IOException when the supplied file cannot be written to for
     * whatever reason.
     */
    public MagicSquare(String filename, int dimensionality) throws IOException {
        data = new int[dimensionality][dimensionality];
        int row = dimensionality - 1;
        int col = dimensionality / 2;
        for (int i = 1; i <= dimensionality * dimensionality; i++) {
            data[row][col] = i;

            int oldRow = row;
            int oldCol = col;
            row++;
            col++;

            if (row == dimensionality) {
                row = 0;
            }
            if (col == dimensionality) {
                col = 0;
            }

            // 0 is the default value, and no filled cell will be 0 either.
            if (data[row][col] != 0) {
                row = oldRow;
                col = oldCol;
                row--;
            }
        }

        dataIsMagic = isMagicSquare();
        writeMatrix(data, filename);
    }

    /**
     * Write out the supplied {@code matrix} to the file named {@code filename}
     *
     * <p>This data will be written out in the same format specified in the
     * javadocs for the {@link MagicSquare#MagicSquare(String) MagicSquare
     * constructor} that reads data from a file.</p>
     *
     * @param matrix The contents of the matrix to be written out
     *
     * @param filename The path to the file that should contain the matrix data.
     *
     * @throws IOException when the file cannot be opened or written to.
     */
    private void writeMatrix(int[][] matrix, String filename) throws IOException {
        File outFile = new File(filename);
        String dataStr = stringifyData();
        try (PrintWriter printer = new PrintWriter(outFile)) {
            printer.printf("%d\n", matrix.length);
            printer.print(dataStr);
        }
    }

    /*
     * Read matrix data from a file.
     *
     * <p>This data should be formatted as specified in the javadocs for the
     * {@link MagicSquare#MagicSquare(String) MagicSquare constructor} that
     * reads data from a file.
     *
     * @param filename Path to the file that contains the matrix data.
     *
     * @return The matrix data located within the file at {@code filename} in
     * the form of an int[][]
     *
     * @throws FileNotFoundException when the supplied {@code filename} cannot
     * be found or the user has insufficient read access to the file. This can
     * also be thrown when the file is formatted incorrectly.
     */
    private int[][] readMatrix(String filename) throws FileNotFoundException {
        // TODO: need to build in more robust validation, handle improperly formatted files

        File file = new File(filename);
        // may throw FileNotFoundException; don't need to plumb that logic manually
        Scanner scanner = new Scanner(file);

        int dimensionality = scanner.nextInt();

        int[][] ret = new int[dimensionality][dimensionality];
        for (int i = 0; i < dimensionality; i++) {
            for (int j = 0; j < dimensionality; j++) {
                ret[i][j] = scanner.nextInt();
            }
        }

        scanner.close();

        return ret;
    }

    public int[][] getMatrix() {
        int[][] ret = new int[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                ret[i][j] = data[i][j];
            }
        }
        return ret;
    }

    public boolean isMagicSquare() {
        if (data.length <= 0) {
            return true;
        }

        // first check for number correctness (all numbers 1..n^2 present)
        List<Integer> expectedSet = new LinkedList<>();
        for (int i = 1; i <= data.length * data.length; i++) {
            expectedSet.add(i);
        }
        boolean setIsCorrect = Arrays.stream(data)
                .flatMapToInt(Arrays::stream)
                .distinct()
                .sorted()
                .boxed()
                .collect(Collectors.toList())
                .equals(expectedSet);
        if (!setIsCorrect) return false;

        int sum = data.length * (data.length * data.length + 1) / 2;

        int[] columnSums = new int[data.length];
        int[] diagonalSums = new int[2]; // only 2 diagonals in a square
        
        for (int i = 0; i < data.length; i++) {
            int[] row = data[i];

            // check row sums
            int rowSum = Arrays.stream(row).sum();
            if (rowSum != sum) return false;

            // build up column sums
            for (int j = 0; j < row.length; j++) {
                columnSums[j] += row[j];

                // build up diagonal sums
                if (i == j) { // top-left to bottom-right diagonal
                    diagonalSums[0] += row[j];
                }
                // -1 accounts for zero-based indexing vs 1-based length
                if (i == row.length - 1 - j) {  // top-right to bottom-left diagonal
                    diagonalSums[1] += row[j];
                }
            }
        }

        // check row sums
        for (int columnSum : columnSums) {
            if (columnSum != sum) return false;
        }

        // check diagonal sums
        for (int diagonalSum : diagonalSums) {
            if (diagonalSum != sum) return false;
        }

        return true;
    }

    /*
     * Transform only the data in this object into a string representation.
     * Helper method for simplifying file writing & {@code toString()}
     * representation
     *
     * @param padding How much padding should be on the left of the matrix data.
     * Will add this many spaces before each row in the String.
     *
     * @return A representation of this square's data as a string, where each
     * column is separated by a space, and each row is separated by a newline.
     */
    private String stringifyData() {
        String ret = "";
        for (int[] row : data) {
            String rowStr = "";

            for (int element : row) {
                rowStr += String.format("%d ", element);
            }
            // chop off the dangling whitespace from iteration
            rowStr = rowStr.substring(0, rowStr.length()-1);

            rowStr += "\n";

            ret += rowStr;
        }

        return ret;
    }

    public String toString() {
        String ret = "The matrix\n";
        ret += stringifyData();
        ret += "is ";
        if (!dataIsMagic) {
            ret += "not ";
        }
        ret += "a magic square.";
        return ret;
    }
}
