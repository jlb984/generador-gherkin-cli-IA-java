package com.jlb;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class OpenAIProvider implements IAProvider {
    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";


    @Override
    public  String generarEscenario(String descripcionNueva, String contextoGherkin) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONArray messages = new JSONArray()
                .put(new JSONObject().put("role", "system")
                        .put("content", "Sos un generador de escenarios de pruebas automatizadas en lenguaje Gherkin. " +
                                "Tiene un listado de escenarios existentes en el proyecto para conocer las sentencias " +
                                "existentes y las practicas del mismo. Siempre que puedas reutiliza las frases que ya " +
                                "existen en los otros escenarios No expliques nada, solo generá el escenario."))
                .put(new JSONObject().put("role", "user")
                        .put("content", "Escenarios existentes:\n" + contextoGherkin))
                .put(new JSONObject().put("role", "user")
                        .put("content", "Generá un nuevo escenario para: " + descripcionNueva));

        JSONObject requestBody = new JSONObject()
                .put("model", "gpt-4o-mini")
                .put("temperature", 0.4)
                .put("messages", messages);

        Request request = new Request.Builder()
                .url(ENDPOINT)
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Error API: " + response);
            JSONObject jsonResponse = new JSONObject(response.body().string());
            return jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        }
    }
}