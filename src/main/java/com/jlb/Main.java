package com.jlb;

import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("f", "feature", true, "Ruta a carpeta con archivos .feature");
        options.addOption("d", "descripcion", true, "Descripción del nuevo caso de prueba");
        options.addOption("n", "funcionalidad", true, "Nombre del archivo .feature a utilizar (ej: emision.feature)");
        options.addOption("p", "provider", true, "Proveedor de IA: openai (default) o gemini");
        options.addOption("h", "help", false, "Mostrar ayuda");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h") || !cmd.hasOption("f") || !cmd.hasOption("d") || !cmd.hasOption("n")) {
                formatter.printHelp("gherkin-generator", options);
                return;
            }

            String featureFolder = cmd.getOptionValue("feature");
            String descripcion = cmd.getOptionValue("descripcion");
            String nombreFeature = cmd.getOptionValue("funcionalidad");
            String proveedor = cmd.getOptionValue("provider", "openai").toLowerCase();

            String contexto = GherkinGenerator.leerEscenariosPorNombre(featureFolder, nombreFeature);

            IAProvider ia;
            if (proveedor.equals("gemini")) {
                ia = new GeminiProvider();
            } else {
                ia = new OpenAIProvider();
            }

            System.out.println("\nContexto de escenarios existentes:\n");
            System.out.println(contexto);
            String resultado = ia.generarEscenario(descripcion, contexto);
            System.out.println("\nEscenario generado:\n\n" + resultado);
            GherkinGenerator.guardarEscenario(resultado);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            formatter.printHelp("gherkin-generator", options);
        }
    }
}

