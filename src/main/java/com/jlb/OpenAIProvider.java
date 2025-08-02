package com.jlb;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class OpenAIProvider implements IAProvider {
    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o-mini";
    private final boolean debug;

    public OpenAIProvider(boolean debug) {
        this.debug = debug;
    }

    @Override
    public String generarEscenario(String descripcionNueva, String contextoGherkin) throws IOException {
        if (debug) {
            System.out.println("[DEBUG] Valor de OPENAI_API_KEY: " + (OPENAI_API_KEY != null ? "[DEFINIDA]" : "[NO DEFINIDA]"));
            System.out.println("[DEBUG] Endpoint: " + ENDPOINT);
            System.out.println("[DEBUG] Descripción nueva: " + descripcionNueva);
            System.out.println("[DEBUG] Contexto Gherkin:");
            System.out.println(contextoGherkin);
        }

        if (OPENAI_API_KEY == null || OPENAI_API_KEY.isEmpty()) {
            throw new IllegalStateException("La variable de entorno OPENAI_API_KEY no está definida o está vacía");
        }

        OkHttpClient client = new OkHttpClient();

        JSONObject requestBody = new JSONObject()
                .put("model", MODEL)
                .put("messages", new JSONArray()
                        .put(new JSONObject()
                                .put("role", "system")
                                .put("content", "Sos un generador de escenarios de pruebas automatizadas en lenguaje Gherkin. " +
                                        "Tiene un listado de escenarios existentes en el proyecto para conocer las sentencias " +
                                        "existentes y las practicas del mismo. Siempre que puedas reutiliza las frases que ya " +
                                        "existen en los otros escenarios No expliques nada, solo generá el escenario.")
                        )
                        .put(new JSONObject()
                                .put("role", "user")
                                .put("content", "Escenarios existentes:\n" + contextoGherkin + "\n\nGenerá un escenario Gherkin para: " + descripcionNueva)
                        )
                );

        if (debug) {
            System.out.println("[DEBUG] Cuerpo del request:");
            System.out.println(requestBody.toString(2));
        }

        Request request = new Request.Builder()
                .url(ENDPOINT)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (debug) {
                System.out.println("[DEBUG] Código de respuesta: " + response.code());
                System.out.println("[DEBUG] Mensaje de respuesta: " + response.message());
            }

            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "[Sin contenido]";
                if (debug) {
                    System.out.println("[DEBUG] Cuerpo del error: " + errorBody);
                }
                throw new IOException("Error API OpenAI: " + response.code() + " - " + response.message());
            }

            String responseBody = response.body().string();
            if (debug) {
                System.out.println("[DEBUG] Cuerpo de la respuesta:");
                System.out.println(responseBody);
            }

            JSONObject json = new JSONObject(responseBody);
            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();
        }
    }
}