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

            Map.Entry<String, Integer> maxEntry = null;
            for (Map.Entry<String, Integer> licenseEntry : licenseMap.entrySet()) {
                if (maxEntry == null || licenseEntry.getValue().compareTo(maxEntry.getValue()) > 0) {
                    maxEntry = licenseEntry;
                }
            }

            if (maxEntry != null) {
                result.put(countyName, maxEntry);
            }
        }

        return result;
    }
}