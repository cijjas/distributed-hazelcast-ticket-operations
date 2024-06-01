package ar.edu.itba.pod.tpe2.client.query3;

import ar.edu.itba.pod.tpe2.client.utils.BaseArguments;
import lombok.Getter;

@Getter
public class Q3Arguments extends BaseArguments {
    private final int n;

    public Q3Arguments(String addresses, String city, String inPath, String outPath, int n) {
        super(addresses, city, inPath, outPath);
        this.n = n;
    }

}