package com.vanshika.bfhl.service;

import okhttp3.*;
import org.json.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    public String ask(String question) {

        OkHttpClient client = new OkHttpClient();

        JSONObject text = new JSONObject();
        text.put("text", question);

        JSONObject content = new JSONObject();
        content.put("parts", new JSONArray().put(text));

        JSONObject body = new JSONObject();
        body.put("contents", new JSONArray().put(content));

        Request request = new Request.Builder()
                .url(GEMINI_URL + apiKey)
                .post(RequestBody.create(
                        body.toString(),
                        MediaType.parse("application/json")
                ))
                .build();

        try (Response response = client.newCall(request).execute()) {

            // 🔍 DEBUG
            if (!response.isSuccessful()) {
                return "Gemini HTTP error: " + response.code();
            }

            String json = response.body().string();
            JSONObject res = new JSONObject(json);

            JSONArray candidates = res.getJSONArray("candidates");
            JSONObject contentObj = candidates.getJSONObject(0).getJSONObject("content");
            JSONArray parts = contentObj.getJSONArray("parts");

            return parts.getJSONObject(0).getString("text");

        } catch (Exception e) {
            return "Gemini exception: " + e.getMessage();
        }
    }
}
