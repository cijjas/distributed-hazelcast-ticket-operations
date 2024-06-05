package ar.edu.itba.pod.tpe2.query2;

import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import com.hazelcast.mapreduce.Collator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Query2Collator implements Collator<Map.Entry<String, Map<String, Integer>>, Map<String, List<String>>> {
    private final Map<String, Infraction> infractions;

    public Query2Collator(Map<String, Infraction> infractions) {
        this.infractions = infractions;
    }
    @Override
    public Map<String, List<String>> collate(Iterable<Map.Entry<String, Map<String, Integer>>> values) {
        Map<String, List<String>> result = new HashMap<>();

        for (Map.Entry<String, Map<String, Integer>> entry : values) {
            String county = entry.getKey();
            Map<String, Integer> infractionCounts = entry.getValue();

            List<String> topInfractions = infractionCounts.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(3)
                    .map(e -> infractions.containsKey(e.getKey()) ? infractions.get(e.getKey()).getDescription() : "-")
                    .collect(Collectors.toList());

            while (topInfractions.size() < 3) {
                topInfractions.add("-");
            }

            result.put(county, topInfractions);
        }

        return result;
    }
}