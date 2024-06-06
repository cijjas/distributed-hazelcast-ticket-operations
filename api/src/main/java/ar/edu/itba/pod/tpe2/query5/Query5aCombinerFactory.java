package ar.edu.itba.pod.tpe2.query5;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import java.util.HashMap;
import java.util.Map;

public class Query5aCombinerFactory implements CombinerFactory<String, String, String> {

    @Override
    public Combiner<String, String> newCombiner(String key) {
        return new Combiner<>() {
            private double totalAmount = 0;
            private int count = 0;

            @Override
            public void combine(String value) {
                String[] parts = value.split(",");
                double amount = Double.parseDouble(parts[0]);
                int cnt = Integer.parseInt(parts[1]);
                totalAmount += amount;
                count += cnt;
            }

            @Override
            public String finalizeChunk() {
                return totalAmount + "," + count;
            }

            @Override
            public void reset() {
                totalAmount = 0;
                count = 0;
            }
        };
    }
}
