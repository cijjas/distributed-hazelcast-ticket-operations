package ar.edu.itba.pod.tpe2.query5;

import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import com.hazelcast.mapreduce.Collator;

import java.util.*;
import java.util.stream.Collectors;

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
            String[] infractionEntries = entry.getValue().split("\n");

            List<String> descriptions = Arrays.stream(infractionEntries)
                    .map(infractionEntry -> {
                        String[] parts = infractionEntry.split(";");
                        if (parts.length >= 2) {
                            String groupPart = parts[0];
                            String descriptionsPart = Arrays.stream(parts)
                                    .skip(1)
                                    .map(String::trim)
                                    .map(infractions::get)
                                    .filter(Objects::nonNull)
                                    .map(Infraction::getDescription)
                                    .collect(Collectors.joining(";"));
                            return groupPart + ";" + descriptionsPart;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .sorted()
                    .toList();

            result.computeIfAbsent(group, k -> new ArrayList<>()).addAll(descriptions);
        }

        return result;
    }
}
