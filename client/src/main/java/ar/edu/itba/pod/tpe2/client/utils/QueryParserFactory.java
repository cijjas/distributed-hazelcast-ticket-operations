package ar.edu.itba.pod.tpe2.client.utils;

import ar.edu.itba.pod.tpe2.client.query4.Q4Parser;
import ar.edu.itba.pod.tpe2.client.query3.Q3Parser;

public class QueryParserFactory {
    public static QueryParser getParser(String queryType) {
        return switch (queryType) {
            case "q1", "q2", "q5" -> new GenericParser();
            case "q3" -> new Q3Parser();
            case "q4" -> new Q4Parser();
            default -> throw new IllegalArgumentException("Unknown query type: " + queryType);
        };
    }
}

