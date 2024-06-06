package ar.edu.itba.pod.tpe2.query5;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class Query5aReducerFactory implements ReducerFactory<String, String, String> {

    @Override
    public Reducer<String, String> newReducer(String key) {
        return new Reducer<>() {
            private double totalAmount = 0;
            private int count = 0;

            @Override
            public void reduce(String value) {
                String[] parts = value.split(",");
                double amount = Double.parseDouble(parts[0]);
                int cnt = Integer.parseInt(parts[1]);
                totalAmount += amount;
                count += cnt;
            }

            @Override
            public String finalizeReduce() {
                double average = totalAmount / count;
                return key + ";" + average;
            }
        };
    }
}
