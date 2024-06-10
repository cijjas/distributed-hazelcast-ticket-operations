package ar.edu.itba.pod.tpe2.models.ticket.adapters;

import ar.edu.itba.pod.tpe2.models.City;
import ar.edu.itba.pod.tpe2.models.ticket.services.TicketFactory;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TicketCHI implements Ticket, DataSerializable {
    private LocalDate issueDate;
    private String licensePlateNumber;
    private String violationCode;
    private String unitDescription;
    private double fineLevel1Amount;
    private String communityAreaName;

    @Override
    public Ticket createTicket(String[] fields) {
        return new TicketCHI(fields);
    }


    public TicketCHI(){

    }
    public TicketCHI(String[] fields) {
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


    @Override
    public void writeData(ObjectDataOutput objectDataOutput) throws IOException {
        long dateEpochDay = issueDate.toEpochDay();
        objectDataOutput.writeLong(dateEpochDay);
        objectDataOutput.writeUTF(licensePlateNumber);
        objectDataOutput.writeUTF(violationCode);
        objectDataOutput.writeUTF(unitDescription);
        objectDataOutput.writeDouble(fineLevel1Amount);
        objectDataOutput.writeUTF(communityAreaName);
    }

    @Override
    public void readData(ObjectDataInput objectDataInput) throws IOException {
        long dateEpochDay = objectDataInput.readLong();
        issueDate = LocalDate.ofEpochDay(dateEpochDay);
        licensePlateNumber = objectDataInput.readUTF();
        violationCode = objectDataInput.readUTF();
        unitDescription = objectDataInput.readUTF();
        fineLevel1Amount = objectDataInput.readDouble();
        communityAreaName = objectDataInput.readUTF();
    }

    
}
