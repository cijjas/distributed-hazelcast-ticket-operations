package ar.edu.itba.pod.tpe2.client.query3;

import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import ar.edu.itba.pod.tpe2.models.City;
import lombok.Getter;

import java.nio.file.Path;

@Getter
public class Query3Arguments extends BaseArguments {
    private final int n;

    public Query3Arguments(String addresses, City city, Path inPath, Path outPath, String clusterName, String clusterPass, int n) {
        super(addresses, city, inPath, outPath, clusterName, clusterPass);
        this.n = n;
    }

}