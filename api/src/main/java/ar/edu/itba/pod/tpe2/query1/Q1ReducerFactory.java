package ar.edu.itba.pod.tpe2.query1;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class Q1ReducerFactory implements ReducerFactory<String, Integer, Integer> {

    @Override
    public Reducer<Integer, Integer> newReducer(String s) {
        return new Q1Reducer();
    }

    private static class Q1Reducer extends Reducer<Integer, Integer> {
        private Integer sum = 0;

        @Override
        public void reduce(Integer value) {
            sum += value;
        }

        @Override
        public Integer finalizeReduce() {
            return sum;
        }
    }
}
