package ru.baza.lab3.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class CastParser implements Parser<String> { //todo сделать соответсвие между типами парсеов (чтобы было ясно, какой тип возвращает парсер-реализация

    @Override
    public Stream<String> parse(URL url, int timeout, Function<Element, String> mapper) {
        try {
            var doc = Jsoup.parse(url, timeout);

            var castDiv = doc.selectFirst("div.div-col");
            if (castDiv == null) return Stream.empty();

            return castDiv.select("li").stream()
                    .map(mapper)
                    .filter(Objects::nonNull);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String elementToActorName(Element li) {
        var element = li.selectFirst("a[title]");
        return element != null ? element.text().trim() : null;
    }
}
