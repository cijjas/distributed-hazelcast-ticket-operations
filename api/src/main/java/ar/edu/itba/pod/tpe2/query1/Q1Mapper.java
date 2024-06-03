package ar.edu.itba.pod.tpe2.query1;

import ar.edu.itba.pod.tpe2.models.Ticket;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.util.HashSet;
import java.util.Set;

public class Q1Mapper implements Mapper<String, Ticket, String, Integer> {


    @Override
    public void map(String integer, Ticket ticket, Context<String, Integer> context) {
        context.emit(ticket.getInfractionCode(), 1);
    }
}
