package com.jlb;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class GeminiProvider implements IAProvider {
    private static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");
    private static final String GEMINI_MODEL = "gemini-1.5-pro-latest";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent";
    private final boolean debug;

    public GeminiProvider(boolean debug) {
        this.debug = debug;
    }

    @Override
    public String generarEscenario(String descripcionNueva, String contextoGherkin) throws IOException {
        if (debug) {
            System.out.println("[DEBUG] Valor de GEMINI_API_KEY: " + (GEMINI_API_KEY != null ? "[DEFINIDA]" : "[NO DEFINIDA]"));
            System.out.println("[DEBUG] Endpoint: " + ENDPOINT);
            System.out.println("[DEBUG] Descripción nueva: " + descripcionNueva);
            System.out.println("[DEBUG] Contexto Gherkin:");
            System.out.println(contextoGherkin);
        }

        if (GEMINI_API_KEY == null || GEMINI_API_KEY.isEmpty()) {
            throw new IllegalStateException("La variable de entorno GEMINI_API_KEY no está definida o está vacía");
        }

        OkHttpClient client = new OkHttpClient();

        JSONObject requestBody = new JSONObject()
                .put("contents", new JSONArray()
                        .put(new JSONObject()
                                .put("parts", new JSONArray()
                                        .put(new JSONObject().put("text", "Escenarios existentes:\n" + contextoGherkin + "\nSos un generador de " +
                                                "escenarios de pruebas automatizadas en lenguaje Gherkin. Tiene un listado de escenarios existentes " +
                                                "en el proyecto para conocer las sentencias existentes y las practicas del mismo. Siempre que puedas " +
                                                "reutiliza las frases que ya existen en los otros escenarios No expliques nada, solo generá el " +
                                                "escenario, en base a esta consigna: " + descripcionNueva))
                                )
                        )
                );

        if (debug) {
            System.out.println("[DEBUG] Cuerpo del request:");
            System.out.println(requestBody.toString(2));
        }

        Request request = new Request.Builder()
                .url(ENDPOINT)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-goog-api-key", GEMINI_API_KEY)
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
                throw new IOException("Error API Gemini: " + response.code() + " - " + response.message());
            }

            String responseBody = response.body().string();
            if (debug) {
                System.out.println("[DEBUG] Cuerpo de la respuesta:");
                System.out.println(responseBody);
            }

            JSONObject json = new JSONObject(responseBody);
            return json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");
        }
    }
}