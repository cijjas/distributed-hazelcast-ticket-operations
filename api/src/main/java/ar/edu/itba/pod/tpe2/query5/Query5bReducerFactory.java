package ar.edu.itba.pod.tpe2.query5;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.*;

public class Query5bReducerFactory implements ReducerFactory<Integer, String, String> {

    @Override
    public Reducer<String, String> newReducer(Integer key) {
        return new Reducer<>() {
            private final List<String> infractions = new ArrayList<>();

            @Override
            public void reduce(String value) {
                infractions.add(value);
            }

            @Override
            public String finalizeReduce() {
                Collections.sort(infractions);
                List<String> pairs = new ArrayList<>();
                for (int i = 0; i < infractions.size(); i++) {
                    for (int j = i + 1; j < infractions.size(); j++) {
                        pairs.add(key + ";" + infractions.get(i) + ";" + infractions.get(j));
                    }
                }
                return String.join("\n", pairs);
            }
        };
    }
}
