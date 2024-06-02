package ar.edu.itba.pod.tpe2.query1;

import ar.edu.itba.pod.tpe2.models.Ticket;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.util.HashSet;
import java.util.Set;

public class Q1Mapper implements Mapper<String, Ticket, String, Long> {

    private final transient Set<Ticket> uniqueTickets =  new HashSet<>();


    @Override
    public void map(String key, Ticket ticket, Context<String, Long> context) {
        if (uniqueTickets.add(ticket)) {
            context.emit(ticket.getInfractionCode(), 1L);
        }
    }

}
