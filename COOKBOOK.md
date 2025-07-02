# Building a Java Document Translator with the DeepL API

Streamline document localization workflows with automated translation for businesses and developers

## Why Build a Document Translator?

In today's global business environment, organizations frequently need to translate various document types - from technical manuals and legal contracts to marketing materials and internal communications. Manually uploading files to web-based translation services can be time-consuming and inefficient, especially when dealing with multiple documents.

By building a command-line Java application with the DeepL API, you can automate document translation processes, integrate them into CI/CD pipelines, and provide a reliable solution for bulk document processing. This approach is particularly valuable for:

- **Development teams** who need to localize documentation and user manuals
- **Content teams** managing multilingual marketing materials
- **Businesses** requiring regular translation of contracts, reports, and communications
- **Automation workflows** where translation needs to be triggered programmatically

## Setting Up Your Document Translator

### Prerequisites

Before you begin, you'll need:

- Java Development Kit (JDK) 8 or higher
- Apache Maven for dependency management
- A DeepL API key (get one at [DeepL API](https://www.deepl.com/pro-api))
- Basic familiarity with Java and command-line tools

### Project Overview

Our Java application leverages the official DeepL Java SDK to translate documents across multiple formats. It provides a simple command-line interface that takes an input file and target language, automatically generating appropriately named output files.

Here's the complete implementation breakdown:

#### 1. Project Setup and Dependencies

First, let's set up the Maven project structure with the necessary dependencies:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>java-document-translator</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.deepl.api</groupId>
            <artifactId>deepl-java</artifactId>
            <version>1.10.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.0.0</version>
                <configuration>
                    <mainClass>App</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

The `deepl-java` dependency provides all the necessary functionality for interacting with the DeepL API, including document translation capabilities.

#### 2. Supported File Types Definition

We start by defining the supported file formats to provide clear feedback to users:

```java
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
```

This static initialization ensures our application can quickly validate file types before attempting translation, providing immediate feedback for unsupported formats.

#### 3. Command Line Argument Processing

The application expects two command-line arguments: the input file path and target language code. The source language is automatically detected by the DeepL API.

```java
public static void main(String[] args) {
    String inputFilePath = args[0];
    String targetLang = args[1];
    
    // Validate file extension
    String extension = "";
    int i = inputFilePath.lastIndexOf('.');
    if (i > 0 && i < inputFilePath.length() - 1) {
        extension = inputFilePath.substring(i + 1);
        String lowerCaseExtension = extension.toLowerCase();
        if (SUPPORTED_EXTENSIONS.containsKey(lowerCaseExtension)) {
            System.out.println("File type: " + SUPPORTED_EXTENSIONS.get(lowerCaseExtension));
        } else {
            System.err.println("Error: Unsupported file extension '" + extension + "'");
            return;
        }
    }
}
```

#### 4. Output File Path Generation

One key feature is automatic output file naming, which prevents accidental overwrites and clearly identifies translated versions:

```java
// Auto-generate output file path
Path inputPathObject = Paths.get(inputFilePath);
String originalFileName = inputPathObject.getFileName().toString();
String newFileName = targetLang.toUpperCase() + "_" + originalFileName;
Path outputFilePathObject = inputPathObject.resolveSibling(newFileName);
String outputFilePath = outputFilePathObject.toString();
```

This approach transforms `document.pdf` with target language `DE` into `DE_document.pdf`, making it easy to identify translated versions.

#### 5. DeepL API Integration

The core translation functionality uses the DeepL Java SDK:

```java
// Initialize DeepL client
String authKey = System.getenv("DEEPL_AUTH_KEY");
if (authKey == null || authKey.isEmpty()) {
    System.err.println("Error: DEEPL_AUTH_KEY environment variable not set.");
    return;
}

File inputFile = Paths.get(inputFilePath).toFile();
File outputFile = Paths.get(outputFilePath).toFile();
DeepLClient client = new DeepLClient(authKey);

// Perform translation
DocumentStatus status = client.translateDocument(inputFile, outputFile, null, targetLang);
System.out.println("Document translation initiated. Document ID: " + status.getDocumentId());
```

#### 6. Translation Status Monitoring

Document translation is asynchronous, so we need to monitor the process:

```java
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
```

This polling mechanism ensures the application waits for translation completion and provides appropriate feedback.

### Building and Running

1. **Set up your environment:**
```bash
export DEEPL_AUTH_KEY="your_deepl_api_key_here"
```

2. **Build the project:**
```bash
mvn compile
```

3. **Run translations:**

Here are some practical examples of using the translator:

**Translating Technical Documentation:**
```bash
mvn exec:java -Dexec.args="./api-documentation.pdf EN-US"
```

**Localizing Marketing Materials:**
```bash
mvn exec:java -Dexec.args="./brochure.docx JA"
```

**Processing Subtitle Files:**
```bash
mvn exec:java -Dexec.args="./movie-subtitles.srt DE"
```

### Wrapping Up

This Java document translator provides a solid foundation for automating document localization workflows. By combining the reliability of Java with DeepL's translation quality, you can build scalable solutions for various business needs.

The command-line interface makes it easy to integrate into existing automation scripts, CI/CD pipelines, or batch processing workflows. Whether you're a developer localizing documentation or a business automating multilingual content creation, this approach offers a practical solution for programmatic document translation.

The extensible design allows for easy customization and enhancement, making it adaptable to specific organizational requirements while maintaining the core functionality of reliable, high-quality document translation.

## References

- [DeepL API Documentation](https://developers.deepl.com/docs)
- [DeepL Java SDK](https://github.com/DeepLcom/deepl-java)
- [Maven Exec Plugin](https://www.mojohaus.org/exec-maven-plugin/)
- [DeepL Supported Languages](https://www.deepl.com/docs-api/translate-text/target-language/) 