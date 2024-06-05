import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipFile {

    /**
     * Decompresses a ZIP file.
     * @param compressedFilePath The path to the ZIP file to decompress.
     * @param outputDir The directory where the decompressed files should be saved.
     * @throws IOException If an I/O error occurs.
     */
    public static void decompressFile(String compressedFilePath, String outputDir) throws IOException {
        unzipFiles(compressedFilePath, outputDir);
    }

    /**
     * Unzips a ZIP file to a specified output directory.
     * @param zipFilePath The path to the ZIP file to decompress.
     * @param outputDir The directory where the decompressed files should be saved.
     * @throws IOException If an I/O error occurs.
     */
    public static void unzipFiles(String zipFilePath, String outputDir) throws IOException {
        File destinationDir = new File(outputDir);
        if (!destinationDir.exists()) {
            if (!destinationDir.mkdirs()) {
                throw new IOException("Failed to create output directory: " + outputDir);
            }
        }

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                File newFile = newFile(destinationDir, entry);
                if (entry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }
                    extractFile(zipIn, newFile);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }

    /**
     * Ensures the file is within the destination directory and prevents ZIP slip vulnerability.
     * @param destinationDir The directory where files are to be extracted.
     * @param zipEntry The ZIP entry to be extracted.
     * @return A new File object for the extracted file.
     * @throws IOException If a file path traversal vulnerability is detected.
     */
    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    /**
     * Extracts a file from a ZIP input stream.
     * @param zipIn The ZIP input stream.
     * @param filePath The file path where the file will be extracted.
     * @throws IOException If an I/O error occurs.
     */
    private static void extractFile(ZipInputStream zipIn, File filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = zipIn.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
        }
    }

}
