import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Args {
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
        if (args.length == 0) {
            System.out.println("Please provide a filename as an argument.");
            return;
        }

        String fileName = args[0];
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < fileName.length() - 1) {
            extension = fileName.substring(i + 1);
            String lowerCaseExtension = extension.toLowerCase();

            if (SUPPORTED_EXTENSIONS.containsKey(lowerCaseExtension)) {
                System.out.println("File type: " + SUPPORTED_EXTENSIONS.get(lowerCaseExtension));
            } else {
                System.out.println("Error: Unsupported file extension '" + extension + "'.");
            }
        } else {
            System.out.println("No extension found for file: " + fileName);
        }
    }
}