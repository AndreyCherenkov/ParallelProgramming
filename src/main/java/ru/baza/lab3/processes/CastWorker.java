package ru.baza.lab3.processes;

import ru.baza.lab3.parser.CastParser;
import ru.baza.lab3.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

public class CastWorker {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private static final Parser<String> PARSER = new CastParser();

    public static void main(String[] args) throws IOException {
        processFilm();
    }

    private static void processFilm() throws IOException {
        String url;
        while ((url = reader.readLine()) != null) {
            try {
                PARSER.parse(URI.create(url).toURL(), 5000, CastParser::elementToActorName)
                        .forEach(System.out::println);
            } catch (Exception e) {
                System.err.println("Failed to process URL: " + url + " -> " + e.getMessage());
            }
        }
        System.out.flush();
    }
}
