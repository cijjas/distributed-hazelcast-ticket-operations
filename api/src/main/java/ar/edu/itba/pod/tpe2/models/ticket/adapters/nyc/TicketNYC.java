package ar.edu.itba.pod.tpe2.models.ticket.adapters.nyc;

import ar.edu.itba.pod.tpe2.models.ticket.Ticket;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TicketNYC implements Ticket {
    private final String plate;
    private final LocalDate issueDate;
    private final String infractionCode;
    private final Double fineAmount;
    private final String countyName;
    private final String issuingAgency;

    public TicketNYC(String[] fields) {
        this.plate = fields[0];
        this.issueDate = LocalDate.parse(fields[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.infractionCode = fields[2];
        this.fineAmount = Double.parseDouble(fields[3]);
        this.countyName = fields[4];
        this.issuingAgency = fields[5];
    }


    @Override
    public String getPlate() {
        return plate;
    }

    @Override
    public LocalDate getIssueDate() {
        return issueDate;
    }

    @Override
    public String getInfractionCode() {
        return infractionCode;
    }

    @Override
    public Double getFineAmount() {
        return fineAmount;
    }

    @Override
    public String getCountyName() {
        return countyName;
    }

    @Override
    public String getIssuingAgency() {
        return issuingAgency;
    }


}
