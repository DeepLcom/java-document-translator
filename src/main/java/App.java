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
        String newFileName = targetLang.toUpperCase() + "_" + originalFileName;
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

            DocumentStatus status = client.translateDocument(inputFile, outputFile, null, targetLang);
            System.out.println("Document translation initiated. Document ID: " + status.getDocumentId());
            System.out.println("Waiting for translation to complete...");

            while (true) {
                StatusCode statusCode = status.getStatus();
                if (statusCode == StatusCode.Done) {
                    System.out.println("Translation completed successfully.");
                    break;
                }
                if (statusCode == StatusCode.Error) {
                    System.err.println("Error during translation: " + status.getErrorMessage());
                    break;
                }

                Thread.sleep(1000); // Wait 1 second before checking status again
            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}