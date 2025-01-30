import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DeepSeekApiClient {
    private static final String API_URL = "https://api.deepseek.com/chat/completions";
    private static final String MODEL_NAME = "deepseek-chat";

    private final String apiKey;
    private final Gson gson;
    private final HttpClient httpClient;

    public DeepSeekApiClient(String apiKey) {
        this.apiKey = apiKey;
        this.gson = new Gson();
        this.httpClient = HttpClient.newHttpClient();
    }

    public String sendMessage(String message) {
        try {
            String jsonBody = buildJsonBody(message, false);
            HttpRequest request = buildHttpRequest(jsonBody);
    
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return "Error: " + response.statusCode();
            }
    
            JsonObject json = gson.fromJson(response.body(), JsonObject.class);
            if (json.has("choices")) {
                JsonArray choices = json.getAsJsonArray("choices");
                if (!choices.isEmpty()) {
                    JsonObject choice = choices.get(0).getAsJsonObject();
                    return choice.getAsJsonObject("message").get("content").getAsString();
                }
            }
    
            return "Error: No valid response from DeepSeek";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    

    public void sendMessageWithStreaming(String message, boolean stream, StreamCallback callback) {
        try {
            String jsonBody = buildJsonBody(message, stream);
            HttpRequest request = buildHttpRequest(jsonBody);

            if (stream) {
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                        .thenAccept(response -> {
                            if (response.statusCode() != 200) {
                                callback.onError("Error: " + response.statusCode());
                                return;
                            }

                            response.body().forEach(line -> {
                                try {
                                    if (!line.isEmpty() && line.startsWith("data: ")) {
                                        line = line.substring(6).trim();
                                        JsonObject json = gson.fromJson(line, JsonObject.class);

                                        if (json.has("choices")) {
                                            JsonArray choices = json.getAsJsonArray("choices");
                                            if (!choices.isEmpty()) {
                                                JsonObject choice = choices.get(0).getAsJsonObject();
                                                if (choice.has("delta") && choice.getAsJsonObject("delta").has("content")) {
                                                    callback.onMessage(choice.getAsJsonObject("delta").get("content").getAsString());
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    callback.onError("Error parsing JSON: " + e.getMessage());
                                }
                            });

                            callback.onComplete();
                        })
                        .exceptionally(ex -> {
                            callback.onError("Streaming error: " + ex.getMessage());
                            return null;
                        });

            } else {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    callback.onError("Error: " + response.statusCode());
                    return;
                }

                JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                if (json.has("choices")) {
                    JsonArray choices = json.getAsJsonArray("choices");
                    if (!choices.isEmpty()) {
                        JsonObject choice = choices.get(0).getAsJsonObject();
                        callback.onMessage(choice.getAsJsonObject("message").get("content").getAsString());
                    }
                }

                callback.onComplete();
            }
        } catch (Exception e) {
            callback.onError("Error: " + e.getMessage());
        }
    }

    private String buildJsonBody(String message, boolean stream) {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("model", MODEL_NAME);
        jsonBody.addProperty("stream", stream);

        JsonArray messages = new JsonArray();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "You are a helpful assistant.");
        messages.add(systemMessage);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", message);
        messages.add(userMessage);

        jsonBody.add("messages", messages);

        return gson.toJson(jsonBody);
    }

    private HttpRequest buildHttpRequest(String jsonBody) {
        return HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
    }

    public interface StreamCallback {
        void onMessage(String message);
        void onError(String error);
        void onComplete();
    }
}
