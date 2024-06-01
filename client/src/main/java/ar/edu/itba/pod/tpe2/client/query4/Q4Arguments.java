package ar.edu.itba.pod.tpe2.client.query4;

import ar.edu.itba.pod.tpe2.client.utils.BaseArguments;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Q4Arguments extends BaseArguments {
    private final LocalDate from;
    private final LocalDate to;

    public Q4Arguments(String addresses, String city, String inPath, String outPath, LocalDate from, LocalDate to) {
        super(addresses, city, inPath, outPath);
        this.from = from;
        this.to = to;
    }

}

