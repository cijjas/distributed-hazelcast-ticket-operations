package ar.edu.itba.pod.tpe2.query2;

import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class Query2Mapper implements Mapper<Long, Ticket, String, String> {


    @Override
    public void map(Long id, Ticket ticket, Context<String, String> context) {
        context.emit(ticket.getCountyName(), ticket.getInfractionCode());
    }
}
