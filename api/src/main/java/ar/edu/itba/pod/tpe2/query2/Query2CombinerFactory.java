package ar.edu.itba.pod.tpe2.query2;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import java.util.HashMap;
import java.util.Map;

public class Query2CombinerFactory implements CombinerFactory<String, String, Map<String, Integer>> {

    @Override
    public Combiner<String, Map<String, Integer>> newCombiner(String s) {
        return new Combiner<>() {
            private final Map<String, Integer> countMap = new HashMap<>();

            @Override
            public void combine(String infractionCode) {
                countMap.merge(infractionCode, 1, Integer::sum);
            }

            @Override
            public Map<String, Integer> finalizeChunk() {
                return new HashMap<>(countMap);
            }

            @Override
            public void reset() {
                countMap.clear();
            }
        };
    }

}
