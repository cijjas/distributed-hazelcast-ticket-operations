package ar.edu.itba.pod.tpe2.query5;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class Query5bMapper implements Mapper<String, String, Integer, String> {

    @Override
    public void map(String key, String value, Context<Integer, String> context) {
        String[] parts = value.split(";");
        String infraction = parts[0];
        double averageFine = Double.parseDouble(parts[1]);
        int group = ((int) averageFine / 100) * 100;

        if (group >= 100) {
            context.emit(group, infraction);
        }
    }
}
