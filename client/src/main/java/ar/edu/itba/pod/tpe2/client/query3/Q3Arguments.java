package ar.edu.itba.pod.tpe2.client.query3;

import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import lombok.Getter;

import java.nio.file.Path;

@Getter
public class Q3Arguments extends BaseArguments {
    private final int n;

    public Q3Arguments(String addresses, String city, Path inPath, Path outPath,String clusterName, String clusterPass, int n) {
        super(addresses, city, inPath, outPath, clusterName, clusterPass);
        this.n = n;
    }

}