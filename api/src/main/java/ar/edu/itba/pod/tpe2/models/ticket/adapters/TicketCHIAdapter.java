package ar.edu.itba.pod.tpe2.models.ticket.adapters;

import ar.edu.itba.pod.tpe2.models.ticket.Ticket;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TicketCHIAdapter  implements Ticket {
    private final LocalDate issueDate;
    private final String licensePlateNumber;
    private final String violationCode;
    private final String unitDescription;
    private final Double fineLevel1Amount;
    private final String communityAreaName;

    public TicketCHIAdapter(String[] fields) {
        this.issueDate = LocalDate.parse(fields[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.licensePlateNumber = fields[1];
        this.violationCode = fields[2];
        this.unitDescription = fields[3];
        this.fineLevel1Amount = Double.parseDouble(fields[4]);
        this.communityAreaName = fields[5];
    }

    @Override
    public String getPlate() {
        return licensePlateNumber;
    }

    @Override
    public LocalDate getIssueDate() {
        return issueDate;
    }

    @Override
    public String getInfractionCode() {
        return violationCode;
    }

    @Override
    public Double getFineAmount() {
        return fineLevel1Amount;
    }

    @Override
    public String getCountyName() {
        return communityAreaName;
    }

    @Override
    public String getIssuingAgency() {
        return unitDescription;
    }

}
