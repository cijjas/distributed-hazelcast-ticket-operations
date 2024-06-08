package ar.edu.itba.pod.tpe2.client;

import lombok.Getter;

@Getter
public enum QueryConfigEnum {
    QUERY1("query1", "time1.txt", "Infraction;Tickets"),
    QUERY2("query2", "time2.txt", "County;InfractionTop1;InfractionTop2;InfractionTop3"),
    QUERY3("query3", "time3.txt", "Issuing Agency;Percentage"),
    QUERY4("query4", "time4.txt", "County;Plate;Tickets"),
    QUERY5("query5", "time5.txt", "Group;Infraction A;Infraction B"),
    ;

    private final String queryName;
    private final String timeOutputFile;
    private final String resultHeader;

    QueryConfigEnum(String queryName, String timeOutputFileName, String resultHeader) {
        this.queryName = queryName;
        this.timeOutputFile = timeOutputFileName;
        this.resultHeader = resultHeader;
    }

    public String getQueryOutputFile() {
        return getQueryName() + ".csv";
    }
}