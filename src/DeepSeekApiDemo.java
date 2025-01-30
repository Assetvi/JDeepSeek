

import java.util.Scanner;

public class DeepSeekApiDemo {
    public static void main(String[] args) {
        String apiKey = ""; // Replace with your actual DeepSeek API key

        try {
            DeepSeekApiClient deepSeekApi = new DeepSeekApiClient(apiKey);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Enter your message to DeepSeek (or 'quit' to exit): ");
                String userInput = scanner.nextLine();
                if (userInput.equalsIgnoreCase("quit")) {
                    break;
                }

                System.out.print("Enable streaming? (yes/no): ");
                String streamChoice = scanner.nextLine();
                boolean stream = streamChoice.equalsIgnoreCase("yes");

                if (stream) {
                    deepSeekApi.sendMessageWithStreaming(userInput, true, new DeepSeekApiClient.StreamCallback() {
                        @Override
                        public void onMessage(String message) {
                            System.out.print(message);
                        }

                        @Override
                        public void onError(String error) {
                            System.err.println("\nError: " + error);
                        }

                        @Override
                        public void onComplete() {
                            System.out.println("\n\n--- Response complete ---");
                        }
                    });
                } else {
                    deepSeekApi.sendMessageWithStreaming(userInput, false, new DeepSeekApiClient.StreamCallback() {
                        @Override
                        public void onMessage(String message) {
                            System.out.println("\nDeepSeek response:\n" + message);
                        }

                        @Override
                        public void onError(String error) {
                            System.err.println("\nError: " + error);
                        }

                        @Override
                        public void onComplete() {
                            System.out.println("\n--- Response complete ---");
                        }
                    });
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
