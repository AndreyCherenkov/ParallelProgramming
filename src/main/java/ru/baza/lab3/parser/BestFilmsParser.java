package ru.baza.lab3.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class BestFilmsParser implements Parser<URL> {

    @Override
    public Stream<URL> parse(URL url, int timeout, Function<Element, URL> mapper) {
        try {
            var document = Jsoup.parse(url, timeout);

            return document
                    .select("tr[style*=#FAEB86]")
                    .stream()
                    .map(row -> row.selectFirst("td i b a[href]"))
                    .filter(Objects::nonNull)
                    .map(mapper);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL toURL(Element element) {
        try {
            return URI.create(element.absUrl("href")).toURL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
