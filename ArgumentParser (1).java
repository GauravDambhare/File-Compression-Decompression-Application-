import java.io.*;
import java.util.Scanner;

public class ArgumentParser {

    /**
     * Validates the command-line arguments.
     * @param args The command-line arguments.
     * @return true if the arguments are valid, false otherwise.
     */
    public boolean validateInput(String[] args) {
        // Check if the number of arguments is correct (3 arguments: operation, input file, output file)
        if (args.length != 3) {
            System.out.println("Error: Incorrect number of arguments. Expected 3 arguments: <operation> <inputFile> <outputFile>");
            return false;
        }

        // Validate the first argument (operation type)
        if (!args[0].equalsIgnoreCase("compress") && !args[0].equalsIgnoreCase("decompress")) {
            System.out.println("Error: Invalid operation. Expected 'compress' or 'decompress'");
            return false;
        }

        // Validate the second argument (input file path)
        if (!isValidFilePath(args[1], true)) {
            System.out.println("Error: Invalid input file path: " + args[1]);
            return false;
        }

        // Validate the third argument (output file path)
        if (!isValidFilePath(args[2], false)) {
            System.out.println("Error: Invalid output file path: " + args[2]);
            return false;
        }

        // If all checks pass, return true
        return true;
    }

    /**
     * Checks if a given file path is valid.
     * @param path The file path to check.
     * @param isInput Indicates whether the file path is for an input file.
     * @return true if the file path is valid, false otherwise.
     */
    private boolean isValidFilePath(String path, boolean isInput) {
        // Basic check: the path should not be empty
        if (path == null || path.trim().isEmpty()) {
            return false;
        }

        File file = new File(path);

        if (isInput) {
            // Check if the input file exists and is readable
            if (!file.exists() || !file.canRead()) {
                return false;
            }
        } else {
            // For output directory, check if the parent directory is writable
            File parentDir = file.isDirectory() ? file : file.getParentFile();
            return parentDir != null && parentDir.exists() && parentDir.canWrite();
        }

        return true;
    }

    /**
     * Method for compressing a file or directory.
     * @param inputFile The input file path.
     * @param outputFile The output file path.
     */
    private void compressFile(String inputFile, String outputFile) {
        try {
            ZipFileUtility zipUtility = new ZipFileUtility();
            zipUtility.compressDirectory(inputFile);
            System.out.println("File compressed successfully from " + inputFile + " to " + outputFile);
        } catch (IOException e) {
            System.err.println("Error during compression: " + e.getMessage());
        }
    }

    /**
     * Method for decompressing a file.
     * @param inputFile The input file path.
     * @param outputFile The output file path.
     */
    private void decompressFile(String inputFile, String outputFile) {
        try {
            UnzipFile.decompressFile(inputFile, outputFile);
            System.out.println("File decompressed successfully from " + inputFile + " to " + outputFile);
        } catch (IOException e) {
            System.err.println("Error during decompression: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the operation (compress/decompress): ");
        String operation = scanner.nextLine();

        System.out.print("Enter the input file path: ");
        String inputFile = scanner.nextLine();

        System.out.print("Enter the output file path: ");
        String outputFile = scanner.nextLine();

        // Creating an array to pass to the validateInput method
        String[] arguments = {operation, inputFile, outputFile};

        ArgumentParser parser = new ArgumentParser();
        boolean isValid = parser.validateInput(arguments);

        if (isValid) {
            if (operation.equalsIgnoreCase("compress")) {
                parser.compressFile(inputFile, outputFile);
            } else if (operation.equalsIgnoreCase("decompress")) {
                parser.decompressFile(inputFile, outputFile);
            }
        } else {
            System.out.println("Invalid arguments. Usage: <operation> <inputFile> <outputFile>");
        }

        scanner.close();
    }
}
