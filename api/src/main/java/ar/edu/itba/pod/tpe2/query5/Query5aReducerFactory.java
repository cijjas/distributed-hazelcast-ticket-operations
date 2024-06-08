package ar.edu.itba.pod.tpe2.query5;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class Query5aReducerFactory implements ReducerFactory<String, String, String> {

    @Override
    public Reducer<String, String> newReducer(String key) {
        return new Reducer<>() {
            private double totalAmount = 0.0;
            private int count = 0;

            @Override
            public void reduce(String value) {
                String[] parts = value.split(",");
                totalAmount += Double.parseDouble(parts[0]);
                count += Integer.parseInt(parts[1]);
            }

            @Override
            public String finalizeReduce() {
                return key + ";" +  totalAmount / count;
            }
        };
    }
}
