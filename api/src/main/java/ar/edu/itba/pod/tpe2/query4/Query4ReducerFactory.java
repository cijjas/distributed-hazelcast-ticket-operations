package ar.edu.itba.pod.tpe2.query4;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.HashMap;
import java.util.Map;

// Identica a Query2ReducerFactory
public class Query4ReducerFactory implements ReducerFactory<String, Map<String, Integer>, Map<String, Integer>> {

    @Override
    public Reducer< Map<String, Integer>, Map<String, Integer>> newReducer(String s) {
        return new Reducer<>() {
            private final Map<String, Integer> countMap = new HashMap<>();

            @Override
            public void reduce(Map<String, Integer> counts) {
                counts.forEach((key, value) -> countMap.merge(key, value, Integer::sum));
            }

            @Override
            public Map<String, Integer> finalizeReduce() {
                return countMap;
            }

        };
    }

}
