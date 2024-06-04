package ar.edu.itba.pod.tpe2.client.utils;

import lombok.Getter;

@Getter
public class QueryConfig {
    private final String queryOutputFile;
    private final String timeOutputFile;

    public QueryConfig(String queryOutputFile, String timeOutputFile) {
        this.queryOutputFile = queryOutputFile;
        this.timeOutputFile = timeOutputFile;
    }

}

