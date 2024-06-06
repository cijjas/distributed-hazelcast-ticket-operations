package ar.edu.itba.pod.tpe2.query4;

import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class Query4Mapper implements Mapper<String, Ticket, String, String> {
    @Override
    public void map(String s, Ticket ticket, Context<String, String> context) {
        context.emit(ticket.getCountyName(), ticket.getPlate());
    }
}
