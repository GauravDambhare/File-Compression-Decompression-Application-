import java.io.*;
import java.nio.file.*;
import java.util.zip.*;
import java.util.*;

public class ZipFileUtility {

    /**
     * Compresses a directory into a ZIP file.
     * @param inputPath The path to the directory to compress.
     * @throws IOException If an I/O error occurs.
     */
    public static void compressDirectory(String inputPath) throws IOException {
        Path inputDir = Paths.get(inputPath);
        if (!Files.isDirectory(inputDir)) {
            throw new IllegalArgumentException("The input path must be a directory.");
        }

        String zipFilePath = inputDir.toString() + ".zip";
        zipFiles(inputPath, zipFilePath);
    }

    /**
     * Utility method to create a zip file.
     * @param source The source directory or file to compress.
     * @param zipFilePath The path where the zip file will be created.
     * @throws IOException If an I/O error occurs.
     */
    public static void zipFiles(String source, String zipFilePath) throws IOException {
        Path sourcePath = Paths.get(source);
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            if (Files.isDirectory(sourcePath)) {
                Files.walk(sourcePath)
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(path).toString());
                            try {
                                zos.putNextEntry(zipEntry);
                                Files.copy(path, zos);
                                zos.closeEntry();
                            } catch (IOException e) {
                                System.err.println("Error compressing file: " + path);
                            }
                        });
            } else {
                ZipEntry zipEntry = new ZipEntry(sourcePath.getFileName().toString());
                zos.putNextEntry(zipEntry);
                Files.copy(sourcePath, zos);
                zos.closeEntry();
            }
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter operation (compress/decompress): ");
        String operation = scanner.next();

        System.out.print("Enter input file path: ");
        String inputPath = scanner.nextLine();

        System.out.print("Enter output file path: ");
        String outputPath = scanner.nextLine();

        ZipFileUtility utility = new ZipFileUtility();

        try {
            if (operation.equalsIgnoreCase("compress")) {
                utility.compressDirectory(inputPath);
                System.out.println("Compressed directory: " + inputPath + " to " + outputPath);
            } else if (operation.equalsIgnoreCase("decompress")) {
                UnzipFile.unzipFiles(inputPath, outputPath);
                System.out.println("Decompressed file: " + inputPath + " to " + outputPath);
            } else {
                System.out.println("Invalid operation. Please enter 'compress' or 'decompress'.");
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
