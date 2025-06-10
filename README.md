# Java Document Translator

This is a command-line Java application for translating documents using the DeepL API. It leverages the official [DeepL Java SDK (`deepl-java`)](https://github.com/DeepLcom/deepl-java) for its core translation functionalities. The application supports a variety of file types and allows users to specify an input file and a target language, automatically generating an output file with the translation.

## Features

*   Translate documents to various languages supported by DeepL.
*   Auto-generates output filename (e.g., `input.txt` translated to German (`DE`) becomes `input.txt_DE`).
*   Checks for a predefined list of supported input file extensions before attempting translation.
*   Requires DeepL API authentication key to be set as an environment variable (`DEEPL_AUTH_KEY`).

## Prerequisites

*   Java Development Kit (JDK) 8 or higher.
*   Apache Maven (for building and managing dependencies).
*   A DeepL API Pro account (or a Free account with API access) and an authentication key.

## Setup

1.  **Clone the repository (or set up the project manually):**
    ```bash
    git clone https://github.com/DeepLcom/java-document-translator.git
    cd java-document-translator
    ```

2.  **Set your DeepL Authentication Key:**
    Set the `DEEPL_AUTH_KEY` environment variable to your DeepL API key.
    ```bash
    export DEEPL_AUTH_KEY="your_deepl_api_key_here"
    ```
    For persistent storage, add this line to your shell's configuration file (e.g., `~/.zshrc`, `~/.bash_profile`).

3.  **Build the project using Maven:**
    This will compile the source code and download the necessary dependencies (including the `deepl-java` library).
    ```bash
    mvn compile
    ```

## Usage

Run the application from the project's root directory using Maven:

```bash
mvn exec:java -Dexec.args="<inputFile> <targetLang>"
```

**Arguments:**

*   `<inputFile>`: Path to the document you want to translate (e.g., `./report.docx` or `data/my_text.txt`).
*   `<targetLang>`: The target language code (e.g., `DE` for German, `FR` for French, `EN-US` for English (US), `ES` for Spanish). Refer to the [DeepL API documentation](https://www.deepl.com/docs-api/translate-text/target-language/) for a full list of supported target language codes.

**Example:**

To translate a file named `mydocument.pdf` located in your Downloads folder to German:

```bash
mvn exec:java -Dexec.args="./mydocument.pdf DE"
```

The translated file will be saved in the same directory as the input file, with `_DE` appended to its name (e.g., `./mydocument.pdf_DE`).

## Supported File Types (Custom Check)

The application performs a preliminary check based on the input file's extension. The following extensions and their descriptions are currently recognized by this custom check:

*   `docx`: Microsoft Word Document
*   `doc`: Microsoft Word Document
*   `pptx`: Microsoft PowerPoint Document
*   `xlsx`: Microsoft Excel Document
*   `pdf`: Portable Document Format
*   `htm`: HTML Document
*   `html`: HTML Document
*   `txt`: Plain Text Document
*   `xlf`: XLIFF Document, version 2.1
*   `xliff`: XLIFF Document, version 2.1
*   `srt`: SubRip Subtitle file

**Note:** While this application performs a preliminary extension check, the actual document translation capability and supported formats are determined by the DeepL API. Refer to the official [DeepL documentation for document translation](https://www.deepl.com/docs-api/translating-documents/) for the most up-to-date list of supported file types by the API itself.

## Project Structure

*   `pom.xml`: Maven project configuration, including dependencies like `deepl-java`.
*   `src/main/java/App.java`: Main application class containing the translation logic.
*   `src/main/java/HelloWorld.java`: A simple Hello World program (can be removed if not needed).
*   `src/main/java/Args.java`: An older version of the argument parsing logic (can be removed or refactored if `App.java` is the primary entry point).

## License

MIT

## Links

- [DeepL API Documentation](https://developers.deepl.com/docs)
- [DeepL API Java SDK](https://github.com/DeepLcom/deepl-java)

