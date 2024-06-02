package ar.edu.itba.pod.tpe2.client.utils.parsing;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;

@Getter
@AllArgsConstructor
public class BaseArguments {
    private final String addresses;
    private final String city;
    private final Path inPath;
    private final Path outPath;
    private final String clusterName;
    private final String clusterPass;
}



