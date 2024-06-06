package ar.edu.itba.pod.tpe2.query5;

import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import com.hazelcast.mapreduce.Collator;

import java.util.*;

public class Query5bCollator implements Collator<Map.Entry<Integer, String>, Map<Integer, List<String>>> {
    private final Map<String, Infraction> infractions;

    public Query5bCollator(Map<String, Infraction> infractions) {
        this.infractions = infractions;
    }

    @Override
    public Map<Integer, List<String>> collate(Iterable<Map.Entry<Integer, String>> values) {
        Map<Integer, List<String>> result = new TreeMap<>(Collections.reverseOrder());

        for (Map.Entry<Integer, String> entry : values) {
            Integer group = entry.getKey();
            String[] pairs = entry.getValue().split("\n");
            result.computeIfAbsent(group, k -> new ArrayList<>()).addAll(Arrays.asList(pairs));
        }

        return result;
    }
}
