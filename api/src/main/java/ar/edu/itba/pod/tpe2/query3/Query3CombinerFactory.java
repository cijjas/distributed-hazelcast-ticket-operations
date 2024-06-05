package ar.edu.itba.pod.tpe2.query3;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class Query3CombinerFactory implements CombinerFactory<String, Double, Double> {

    @Override
    public Combiner<Double, Double> newCombiner(String s) {
        return new Combiner<>() {
            private double sum = 0;

            @Override
            public void combine(Double value) {
                sum += value;
            }

            @Override
            public Double finalizeChunk() {
                return sum;
            }

            @Override
            public void reset() {
                sum = 0;
            }
        };
    }

}
