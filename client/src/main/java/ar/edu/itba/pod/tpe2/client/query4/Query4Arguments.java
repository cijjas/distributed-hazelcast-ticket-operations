package ar.edu.itba.pod.tpe2.client.query4;

import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import lombok.Getter;

import java.nio.file.Path;
import java.time.LocalDate;

@Getter
public class Query4Arguments extends BaseArguments {
    private final LocalDate from;
    private final LocalDate to;

    public Query4Arguments(String addresses, String city, Path inPath, Path outPath, String clusterName, String clusterPass, LocalDate from, LocalDate to) {
        super(addresses, city, inPath, outPath, clusterName, clusterPass);
        this.from = from;
        this.to = to;
    }

}

