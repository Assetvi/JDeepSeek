# JDeepSeek
# DeepSeek API Java Client

The **DeepSeek API Java Client** is a lightweight library that allows you to integrate the DeepSeek search capabilities into your Java applications with ease. This library includes streaming support to efficiently handle large datasets and real-time data streams.

## Features

- **Easy Integration:** Simple API to connect to the DeepSeek service.
- **Streaming Support:** Handle large data streams with an efficient streaming API.
- **Minimal Dependencies:** Only requires [Gson 2.11.0](https://github.com/google/gson) for JSON processing.
- **Example Demo:** Comes with a `DeepSeekApiDemo.java` to illustrate usage.

## Prerequisites

- **Java 8 or higher**
- **Gson Library:** `lib/gson-2.11.0.jar` must be added to your classpath.
- **Source Files:** 
  - `src/DeepSeekApiClient.java` – The main client implementation.
  - `src/DeepSeekApiDemo.java` – A demo illustrating the usage of the API.

## Installation

1. **Download/Clone the Repository**  
   Ensure you have the `lib` and `src` directories.

2. **Include the Gson Library**  
   Download [gson-2.11.0.jar](https://repo1.maven.org/maven2/com/google/code/gson/gson/2.11.0/gson-2.11.0.jar) and place it in the `lib` directory.

3. **Compile the Source Files**  
   From the root directory, run:
   ```bash
   javac -cp lib/gson-2.11.0.jar -d bin src/DeepSeekApiClient.java src/DeepSeekApiDemo.java

## License
This project is licensed under the MIT License.
