package com.jlb;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("f", "feature", true, "Ruta a carpeta con archivos .feature");
        options.addOption("d", "descripcion", true, "Descripción del nuevo caso de prueba");
        options.addOption("h", "help", false, "Mostrar ayuda");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h") || !cmd.hasOption("f") || !cmd.hasOption("d")) {
                formatter.printHelp("gherkin-generator", options);
                return;
            }

            String featureFolder = cmd.getOptionValue("feature");
            String descripcion = cmd.getOptionValue("descripcion");

            String contexto = Files.walk(Paths.get(featureFolder))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".feature"))
                    .map(p -> {
                        try {
                            return Files.readString(p);
                        } catch (IOException e) {
                            return "";
                        }
                    })
                    .collect(Collectors.joining("\n\n"));

            String resultado = GherkinGenerator.generarEscenario(descripcion, contexto);
            System.out.println("Escenario generado:\n\n" + resultado);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            formatter.printHelp("gherkin-generator", options);
        }
    }
}

