package ar.edu.itba.pod.tpe2.query3;

import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import com.hazelcast.mapreduce.Collator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Query3Collator implements Collator<Map.Entry<String, Double>, Map<String, String>> {
    private final int limit;

    public Query3Collator(int limit) {
        this.limit = limit;
    }

    @Override
    public Map<String, String> collate(Iterable<Map.Entry<String, Double>> values) {
        List<Map.Entry<String, Double>> entries = StreamSupport.stream(values.spliterator(), false)
                .toList();

        double total = entries.stream()
                .mapToDouble(Map.Entry::getValue)
                .sum();

        return entries.stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(
                        entry.getKey(),
                        new BigDecimal(entry.getValue() / total * 100).setScale(2, RoundingMode.HALF_DOWN) + "%"
                ))
                .sorted(Comparator.comparing((Map.Entry<String, String> entry) -> new BigDecimal(entry.getValue().replace("%", "")))
                        .reversed()
                        .thenComparing(Map.Entry::getKey))
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}