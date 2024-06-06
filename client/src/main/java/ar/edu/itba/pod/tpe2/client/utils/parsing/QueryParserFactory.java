package ar.edu.itba.pod.tpe2.client.utils.parsing;

import ar.edu.itba.pod.tpe2.client.query4.Query4Parser;
import ar.edu.itba.pod.tpe2.client.query3.Query3Parser;

public class QueryParserFactory {
    public static QueryParser getParser(String queryType) {
        return switch (queryType) {
            case "query1", "query2", "query5" -> new BaseParser();
            case "query3" -> new Query3Parser();
            case "query4" -> new Query4Parser();
            default -> throw new IllegalArgumentException("Unknown query type: " + queryType);
        };
    }
}

