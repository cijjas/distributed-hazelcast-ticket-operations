package ar.edu.itba.pod.tpe2.query2;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.HashMap;
import java.util.Map;

public class Query2ReducerFactory implements ReducerFactory<String, Map<String, Integer>, Map<String, Integer>> {

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
