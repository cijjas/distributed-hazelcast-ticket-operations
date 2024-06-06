package ar.edu.itba.pod.tpe2.models.ticket.adapters;

import java.time.LocalDate;

public interface Ticket  {
    String getPlate();
    LocalDate getIssueDate();
    String getInfractionCode();
    Double getFineAmount();
    String getCountyName();
    String getIssuingAgency();
    Ticket createTicket(String[] fields);

}
