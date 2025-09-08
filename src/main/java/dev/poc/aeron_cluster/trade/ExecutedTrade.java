package dev.poc.aeron_cluster.trade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExecutedTrade {
    public String tradeId;
    public String acctNo;
    public String security;
    public String cusip;
    public int quantity;
    public LocalDate settlementDate;
    public LocalDateTime executionTimestamp;
    public String tradeVenue;
    public BigDecimal price;
    public String counterparty;

    public String state = "Captured"; // Initial state

    public ExecutedTrade(String tradeId, String acctNo, String security, String cusip, int quantity,
                         LocalDate settlementDate, LocalDateTime executionTimestamp,
                         String tradeVenue, BigDecimal price, String counterparty) {
        this.tradeId = tradeId;
        this.acctNo = acctNo;
        this.security = security;
        this.cusip = cusip;
        this.quantity = quantity;
        this.settlementDate = settlementDate;
        this.executionTimestamp = executionTimestamp;
        this.tradeVenue = tradeVenue;
        this.price = price;
        this.counterparty = counterparty;
    }
}
