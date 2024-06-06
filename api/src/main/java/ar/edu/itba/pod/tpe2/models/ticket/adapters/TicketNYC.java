package ar.edu.itba.pod.tpe2.models.ticket.adapters;

import ar.edu.itba.pod.tpe2.models.ticket.Ticket;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TicketNYC implements Ticket, DataSerializable {
    private String plate;
    private LocalDate issueDate;
    private String infractionCode;
    private Double fineAmount;
    private String countyName;
    private String issuingAgency;

    @Override
    public Ticket createTicket(String[] fields) {
        return new TicketNYC(fields);
    }
    public TicketNYC(String[] fields) {
        this.plate = fields[0];
        this.issueDate = LocalDate.parse(fields[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.infractionCode = fields[2];
        this.fineAmount = Double.parseDouble(fields[3]);
        this.countyName = fields[4];
        this.issuingAgency = fields[5];
    }

    public TicketNYC(){

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


    @Override
    public void writeData(ObjectDataOutput objectDataOutput) throws IOException {
        objectDataOutput.writeUTF(plate);
        objectDataOutput.writeUTF(issueDate.toString());
        objectDataOutput.writeUTF(infractionCode);
        objectDataOutput.writeDouble(fineAmount);
        objectDataOutput.writeUTF(countyName);
        objectDataOutput.writeUTF(issuingAgency);
    }

    @Override
    public void readData(ObjectDataInput objectDataInput) throws IOException {
    }
}
