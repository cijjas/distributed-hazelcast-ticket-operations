package ar.edu.itba.pod.tpe2.query1;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class Q1CombinerFactory implements CombinerFactory<String, Integer, Integer> {

    @Override
    public Combiner<Integer, Integer> newCombiner(String s) {
        return new Combiner<>() {
            private int sum = 0;

            @Override
            public void combine(Integer value) {
                sum += value;
            }

            @Override
            public Integer finalizeChunk() {
                int result = sum;
                sum = 0;
                return result;
            }

            @Override
            public void reset() {
                sum = 0;
            }
        };
    }

}
