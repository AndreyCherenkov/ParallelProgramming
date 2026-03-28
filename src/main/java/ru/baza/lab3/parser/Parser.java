package ru.baza.lab3.parser;

import org.jsoup.nodes.Element;

import java.net.URL;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Parser<T> {

    Stream<T> parse(URL url, int timeout, Function<Element, T> mapper);

}
