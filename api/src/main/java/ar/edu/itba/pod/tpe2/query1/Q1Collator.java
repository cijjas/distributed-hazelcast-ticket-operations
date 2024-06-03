package ar.edu.itba.pod.tpe2.query1;

import com.hazelcast.mapreduce.Collator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Q1Collator implements Collator<Map.Entry<String, Integer>, Map<String, Integer>> {
    @Override
    public Map<String, Integer> collate(Iterable<Map.Entry<String, Integer>> values) {
        return StreamSupport.stream(values.spliterator(), false)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}