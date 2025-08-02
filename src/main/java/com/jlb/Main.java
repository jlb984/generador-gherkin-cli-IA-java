package com.jlb;

import org.apache.commons.cli.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();

        options.addOption("f", "feature", true, "Ruta a carpeta con archivos .feature");
        options.addOption("n", "funcionalidad", true, "Nombre del archivo .feature a utilizar (ej: emision.feature)");
        options.addOption("d", "descripcion", true, "Descripción del nuevo caso de prueba");
        options.addOption("p", "provider", true, "Proveedor de IA: openai (default) o gemini");
        options.addOption("h", "help", false, "Mostrar ayuda");
        options.addOption(null, "debug", false, "Mostrar información de depuración");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("help") || !cmd.hasOption("feature") || !cmd.hasOption("descripcion")) {
                formatter.printHelp("gherkin-generator", options);
                return;
            }

            String featureFolder = cmd.getOptionValue("feature");
            String descripcion = cmd.getOptionValue("descripcion");
            String nombreFeature = cmd.getOptionValue("funcionalidad");
            String provider = cmd.getOptionValue("provider", "openai");
            boolean debug = cmd.hasOption("debug");

            String contexto = GherkinGenerator.leerEscenariosPorNombre(featureFolder, nombreFeature);

            if (debug) {
                System.out.println("[DEBUG] Contexto de escenarios existentes:\n");
                System.out.println(contexto);
            } else {
                System.out.println("Contexto de escenarios existentes:\n");
                System.out.println(contexto);
            }

            IAProvider iaProvider = switch (provider.toLowerCase()) {
                case "gemini" -> new GeminiProvider(debug);
                case "openai" -> new OpenAIProvider(debug);
                default -> throw new IllegalArgumentException("Proveedor no válido: " + provider);
            };

            String escenarioGenerado = iaProvider.generarEscenario(descripcion, contexto);
            System.out.println("\nEscenario generado por IA:\n\n" + escenarioGenerado);

            GherkinGenerator.guardarEscenario(escenarioGenerado);

        } catch (ParseException e) {
            System.err.println("Error en los parámetros: " + e.getMessage());
            formatter.printHelp("gherkin-generator", options);
        } catch (IOException e) {
            System.err.println("Error al leer/escribir archivos: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}