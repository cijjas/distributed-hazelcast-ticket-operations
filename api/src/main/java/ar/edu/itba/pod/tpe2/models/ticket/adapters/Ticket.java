package ar.edu.itba.pod.tpe2.models.ticket.adapters;

import java.time.LocalDate;
import java.util.UUID;

public interface Ticket  {
    String getPlate();
    LocalDate getIssueDate();
    String getInfractionCode();
    Double getFineAmount();
    String getCountyName();
    String getIssuingAgency();
    Ticket createTicket(String[] fields);
}
