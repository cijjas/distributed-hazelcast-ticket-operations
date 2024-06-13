package ar.edu.itba.pod.tpe2.query1;

import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import com.hazelcast.mapreduce.Collator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Query1Collator implements Collator<Map.Entry<String, Integer>, Map<String, Integer>> {
    private final Map<String, Infraction> infractions;

    public Query1Collator(Map<String, Infraction> infractions) {
        this.infractions = infractions;
    }

    @Override
    public Map<String, Integer> collate(Iterable<Map.Entry<String, Integer>> values) {
        Map<String, String> descriptions = new HashMap<>();
        values.forEach(entry -> {
            Infraction infraction = infractions.get(entry.getKey());
            if (infraction != null) {
                descriptions.put(entry.getKey(), infraction.getDescription());
            }
        });

        return StreamSupport.stream(values.spliterator(), false)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(entry -> descriptions.getOrDefault(entry.getKey(), "")))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}