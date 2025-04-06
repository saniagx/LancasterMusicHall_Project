package com.venueOps.lancastermusichallproject.operations;

import java.math.BigDecimal;

public class ClientOverviewTable {

    private final String clientCompanyName;
    private final String clientName;
    private final String clientTel;
    private final String clientEmail;

        public ClientOverviewTable(String clientCompanyName, String clientName, String clientTel, String clientEmail) {
            this.clientCompanyName = clientCompanyName;
            this.clientName = clientName;
            this.clientTel = clientTel;
            this.clientEmail = clientEmail;
        }

    public String getClientCompanyName() {
        return clientCompanyName;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientTel() {
        return clientTel;
    }

    public String getClientEmail() {
        return clientEmail;
    }
}
