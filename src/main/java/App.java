import com.deepl.api.*;
import com.deepl.api.DocumentStatus.StatusCode;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class App {
    private static final Map<String, String> SUPPORTED_EXTENSIONS;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("docx", "Microsoft Word Document");
        map.put("doc", "Microsoft Word Document");
        map.put("pptx", "Microsoft PowerPoint Document");
        map.put("xlsx", "Microsoft Excel Document");
        map.put("pdf", "Portable Document Format");
        map.put("htm", "HTML Document");
        map.put("html", "HTML Document");
        map.put("txt", "Plain Text Document");
        map.put("xlf", "XLIFF Document, version 2.1");
        map.put("xliff", "XLIFF Document, version 2.1");
        map.put("srt", "SubRip Subtitle file");
        SUPPORTED_EXTENSIONS = Collections.unmodifiableMap(map);
    }


    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java App <inputFile> <targetLang>");
            System.err.println("Example: java App input.docx DE");
            System.err.println("Output file will be auto-generated (e.g., input_DE.docx).");
            System.err.println("Ensure DEEPL_AUTH_KEY environment variable is set.");
            return;
        }

        String inputFilePath = args[0];
        String targetLang = args[1];

        // Perform file extension check first
        String extension = "";
        int i = inputFilePath.lastIndexOf('.');
        if (i > 0 && i < inputFilePath.length() - 1) {
            extension = inputFilePath.substring(i + 1);
            String lowerCaseExtension = extension.toLowerCase();
            if (SUPPORTED_EXTENSIONS.containsKey(lowerCaseExtension)) {
                System.out.println("File type: " + SUPPORTED_EXTENSIONS.get(lowerCaseExtension));
            } else {
                System.err.println("Error: Unsupported file extension '" + extension + "' based on user-defined list.");
                return;
            }
        } else {
            System.err.println("No extension found for input file: " + inputFilePath);
            return;
        }

        // Auto-generate output file path
        Path inputPathObject = Paths.get(inputFilePath);
        String originalFileName = inputPathObject.getFileName().toString();
        String newFileName = originalFileName + "_" + targetLang.toUpperCase();
        Path outputFilePathObject = inputPathObject.resolveSibling(newFileName);
        String outputFilePath = outputFilePathObject.toString();

        // Proceed with DeepL specific checks and translation
        String authKey = System.getenv("DEEPL_AUTH_KEY");
        if (authKey == null || authKey.isEmpty()) {
            System.err.println("Error: DEEPL_AUTH_KEY environment variable not set.");
            return;
        }

        File inputFile = Paths.get(inputFilePath).toFile();
        File outputFile = Paths.get(outputFilePath).toFile();

        DeepLClient client = new DeepLClient(authKey);

        try {
            System.out.println("Starting document translation...");
            System.out.println("Input file: " + inputFile.getAbsolutePath());
            System.out.println("Output file: " + outputFile.getAbsolutePath());
            System.out.println("Target language: " + targetLang);

            // Ensure output directory exists
            File outputDir = outputFile.getParentFile();
            if (outputDir != null && !outputDir.exists()) {
                if (!outputDir.mkdirs()) {
                    System.err.println("Error: Could not create output directory: " + outputDir.getAbsolutePath());
                    return;
                }
            }

            DocumentStatus status = client.translateDocument(inputFile, outputFile, null, targetLang);
            System.out.println("Document translation initiated. Document ID: " + status.getDocumentId());
            System.out.println("Waiting for translation to complete...");

            // Wait for translation to complete (this is a simplified wait, DeepL might offer more robust status checking)
            // For production, you might want to poll the status using handle.getStatus() or use callbacks if available.
            // This example uses a simple loop based on the example from deepl-java documentation.
            long startTime = System.currentTimeMillis();
            long timeoutMillis = 600000; // 10 minutes timeout for translation

            while (true) {
                StatusCode statusCode = status.getStatus();
                if (statusCode == StatusCode.Done) {
                    System.out.println("Translation completed successfully.");
                    if (status.getBilledCharacters() != null) {
                         System.out.println("Billed characters: " + status.getBilledCharacters());
                    }
                    break;
                }
                if (statusCode == StatusCode.Error) {
                    System.err.println("Error during translation: " + status.getErrorMessage());
                    break;
                }
                if (System.currentTimeMillis() - startTime > timeoutMillis) {
                    System.err.println("Error: Translation timed out after " + (timeoutMillis / 60000) + " minutes.");
                    break;
                }
                Thread.sleep(5000); // Wait 5 seconds before checking status again
            }

        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}