package com.recipeapp;

import static spark.Spark.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WebApp {

    private static final String API_KEY = "GROQ_API_KEY";

    public static void main(String[] args) {
        staticFiles.location("/public");
        port(getHerokuAssignedPort());
        System.out.println("\n🚀 Server starting...\n");

        get("/", (req, res) -> new ModelAndView(new HashMap<>(), "index.hbs"), new HandlebarsTemplateEngine());

        post("/generate-recipe", (request, response) -> {
            String ingredientsInput = request.queryParams("ingredients");
            Map<String, Object> model = new HashMap<>();
            try {
                String rawRecipe = getRecipeFromGroq(ingredientsInput);
                model.put("title", parseSection(rawRecipe, "Recipe Name:"));
                model.put("description", parseSection(rawRecipe, "Description:"));
                model.put("prepTime", parseSection(rawRecipe, "Prep Time:"));
                model.put("cookTime", parseSection(rawRecipe, "Cook Time:"));
                model.put("servings", parseSection(rawRecipe, "Servings:"));

                String ingredientsBlock = getBlock(rawRecipe, "---Ingredients---", "---Instructions---");
                List<String> ingredientsList = Arrays.stream(ingredientsBlock.split("\\n"))
                        .map(line -> line.replaceFirst("^[-*]\\s*", "").trim())
                        .filter(line -> !line.isEmpty())
                        .map(line -> line.replaceAll("\\*\\*", ""))
                        .collect(Collectors.toList());
                model.put("ingredients", ingredientsList);

                String instructionsBlock = getBlock(rawRecipe, "---Instructions---", null);
                List<String> instructionsList = Arrays.stream(instructionsBlock.split("\\n"))
                        .map(String::trim)
                        .filter(line -> !line.isEmpty())
                        .map(line -> line.replaceFirst("^\\d+\\.\\s*", ""))
                        .map(line -> line.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>"))
                        .collect(Collectors.toList());
                model.put("instructions", instructionsList);

            } catch (Exception e) {
                model.put("title", "An Error Occurred");
                model.put("description", "There was a problem generating the recipe.");
                model.put("ingredients", Collections.singletonList("Please check the API Key on the Render server."));
                model.put("instructions", List.of("The AI server returned an error. This is often due to an invalid or missing API Key in the deployment environment variables."));
            }
            return new ModelAndView(model, "recipe.hbs");
        }, new HandlebarsTemplateEngine());

        System.out.println("✅ Server is ready and listening on port " + port());
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }

    private static String parseSection(String text, String header) {
        Pattern pattern = Pattern.compile(Pattern.quote(header) + "\\s*(.*)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim().replaceAll("\\*\\*", "");
        }
        return "N/A";
    }

    private static String getBlock(String text, String startHeader, String endHeader) {
        int startIndex = text.indexOf(startHeader);
        if (startIndex == -1) return "";
        startIndex += startHeader.length();
        int endIndex = (endHeader != null) ? text.indexOf(endHeader, startIndex) : text.length();
        if (endIndex == -1) endIndex = text.length();
        return text.substring(startIndex, endIndex).trim();
    }

    public static String getRecipeFromGroq(String ingredients) throws Exception {
        String systemPrompt = "You are an expert chef specializing in simple, delicious Indian home cooking. Your task is to create an extremely detailed, step-by-step recipe for an absolute beginner." +
                "\n\n**CRITICAL RULE:** You MUST ONLY use ingredients that are very common in a typical Indian kitchen." +
                "\n\nFormat the response exactly like this, providing every single field:\n" +
                "Recipe Name: [Create an appealing, common Indian dish name]\n" +
                "Description: [Write a short, one-paragraph, enticing description of the final dish.]\n" +
                "Prep Time: [e.g., 15 minutes]\n" +
                "Cook Time: [e.g., 25 minutes]\n" +
                "Servings: [e.g., 4 people]\n\n" +
                "---Ingredients---\n- [List ALL ingredients, including basics like oil, water, salt, haldi, masala powders, with clear Indian measurements like '1 tsp' or '1/2 cup']\n\n" +
                "---Instructions---\n1. [Step 1, be extremely detailed. Explain the 'why' behind actions. Use **bold** for emphasis on key actions.]";

        String userPrompt = "Create a recipe using the following ingredients: " + ingredients;

        Gson gson = new Gson();
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", systemPrompt);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", userPrompt);

        JsonArray messagesArray = new JsonArray();
        messagesArray.add(systemMessage);
        messagesArray.add(userMessage);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "llama-3.1-8b-instant");
        requestBody.add("messages", messagesArray);

        String jsonPayload = gson.toJson(requestBody);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("API Request FAILED! Status: " + response.statusCode() + ", Body: " + response.body());
        }

        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
        return jsonResponse.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
    }
}
