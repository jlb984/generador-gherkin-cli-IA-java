package com.jlb;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GherkinGenerator {
    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final int MAX_SCENARIOS = 6;
    private static final String DEFAULT_OUTPUT_FOLDER = "generated-features";

    public static String generarEscenario(String descripcionNueva, String contextoGherkin) throws IOException {
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

    public static String leerUltimosEscenarios(String carpeta) throws IOException {
        try (Stream<Path> files = Files.walk(Paths.get(carpeta))) {
            List<String> escenarios = files
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".feature"))
                    .flatMap(p -> {
                        try {
                            String contenido = Files.readString(p);
                            String[] bloques = contenido.split("(\r?\n){2,}");
                            List<String> escenariosEnArchivo = new ArrayList<>();
                            for (String bloque : bloques) {
                                String[] lineas = bloque.strip().split("\\R");
                                if (lineas.length >= 2 && lineas[0].trim().startsWith("@") && lineas[1].trim().startsWith("Escenario:")) {
                                    escenariosEnArchivo.add(bloque.strip());
                                } else if (lineas.length >= 1 && lineas[0].trim().startsWith("Escenario:")) {
                                    escenariosEnArchivo.add(bloque.strip());
                                }
                            }
                            return escenariosEnArchivo.stream();
                        } catch (IOException e) {
                            return Stream.empty();
                        }
                    })
                    .limit(MAX_SCENARIOS)
                    .collect(Collectors.toList());

            return String.join("\n\n", escenarios);
        }
    }

    public static void guardarEscenario(String resultado) throws IOException {
        Path outputDir = Paths.get(DEFAULT_OUTPUT_FOLDER);
        Files.createDirectories(outputDir);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path path = outputDir.resolve("escenario_generado_" + timestamp + ".feature");
        try (PrintWriter writer = new PrintWriter(path.toFile())) {
            writer.println(resultado);
        }
        System.out.println("Escenario guardado en: " + path);
    }
}