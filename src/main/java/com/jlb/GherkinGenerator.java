package com.jlb;

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
    private static final int MAX_SCENARIOS = 6;
    private static final String DEFAULT_OUTPUT_FOLDER = "generated-features";

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

    public static String leerEscenariosPorNombre(String carpeta, String nombreArchivoFeature) throws IOException {
        if (nombreArchivoFeature == null || nombreArchivoFeature.isEmpty()) {
            return leerUltimosEscenarios(carpeta);
        }
        try (Stream<Path> files = Files.walk(Paths.get(carpeta))) {
            List<String> escenarios = files
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().equalsIgnoreCase(nombreArchivoFeature))
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
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8))) {
            writer.println(resultado);
        }
        System.out.println("Escenario guardado en: " + path);
    }
}
