package ar.edu.itba.pod.tpe2.server.utils.parsing;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class ServerArguments {
    private final String clusterName;
    private final String clusterPassword;
    private final Collection<String> interfaces;
}
