package ar.edu.itba.pod.tpe2.query1;

import com.hazelcast.mapreduce.Collator;

import java.util.*;

public class Q1Collator implements Collator<Map.Entry<String, Long>, Map<String, Long>> {
    @Override
    public Map<String, Long> collate(Iterable<Map.Entry<String, Long>> values) {
        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>();
        values.forEach(sortedEntries::add);

        sortedEntries.sort(Comparator.comparing((Map.Entry<String, Long> entry) -> entry.getValue()).reversed()
                .thenComparing(Map.Entry::getKey));

        Map<String, Long> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : sortedEntries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
