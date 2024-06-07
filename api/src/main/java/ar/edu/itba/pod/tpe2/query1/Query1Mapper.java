package ar.edu.itba.pod.tpe2.query1;

import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.io.Serial;
import java.util.UUID;

public class Query1Mapper implements Mapper<Long, Ticket, String, Integer> {
    @Serial
    private static final long serialVersionUID = 1L;


    @Override
    public void map(Long id, Ticket ticket, Context<String, Integer> context) {
        context.emit(ticket.getInfractionCode(), 1);
    }
}
