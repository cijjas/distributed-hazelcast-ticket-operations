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
        double total = StreamSupport.stream(values.spliterator(), false)
                .mapToDouble(Map.Entry::getValue)
                .sum();

        return StreamSupport.stream(values.spliterator(), false)
                .map(entry -> {
                    BigDecimal percentage = new BigDecimal(entry.getValue() / total * 100)
                            .setScale(2, RoundingMode.DOWN);
                    return new HashMap.SimpleEntry<>(entry.getKey(), percentage);
                })
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue()
                        .reversed()
                        .thenComparing(Map.Entry::getKey))
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().toString() + "%",
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}