package ar.edu.itba.pod.tpe2.query4;

import com.hazelcast.mapreduce.Collator;

import java.util.Map;
import java.util.TreeMap;

public class Query4Collator implements Collator<Map.Entry<String, Map<String, Integer>>, Map<String, Map.Entry<String, Integer>>> {
    @Override
    public Map<String, Map.Entry<String, Integer>> collate(Iterable<Map.Entry<String, Map<String, Integer>>> values) {
        Map<String, Map.Entry<String, Integer>> result = new TreeMap<>();

        for (Map.Entry<String, Map<String, Integer>> entry : values) {
            String countyName = entry.getKey();
            Map<String, Integer> licenseMap = entry.getValue();

            licenseMap.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .ifPresent(maxEntry -> result.put(countyName, maxEntry));

        }

        return result;
    }
}