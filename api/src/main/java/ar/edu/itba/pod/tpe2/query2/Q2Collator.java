package ar.edu.itba.pod.tpe2.query2;

import ar.edu.itba.pod.tpe2.models.Infraction;
import com.hazelcast.mapreduce.Collator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Q2Collator implements Collator<Map.Entry<String, Map<String, Integer>>, Map<String, List<String>>> {
    private final Map<String, Infraction> infractions;

    public Q2Collator(Map<String, Infraction> infractions) {
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