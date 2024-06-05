package ar.edu.itba.pod.tpe2.query3;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class Query3ReducerFactory implements ReducerFactory<String, Double, Double> {

    @Override
    public Reducer<Double, Double> newReducer(String s) {
        return new Query3Reducer();
    }

    private static class Query3Reducer extends Reducer<Double, Double> {
        private double sum = 0;

        @Override
        public void reduce(Double value) {
            sum += value;
        }

        @Override
        public Double finalizeReduce() {
            return sum;
        }
    }
}