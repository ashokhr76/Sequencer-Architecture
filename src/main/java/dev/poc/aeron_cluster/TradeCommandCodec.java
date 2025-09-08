package dev.poc.aeron_cluster;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

import dev.poc.aeron_cluster.trade.ExecutedTrade;

public class TradeCommandCodec {
    public static final byte CMD_CAPTURE = 1;
    public static final byte CMD_ENRICH = 2;

    public static byte[] encodeCapture(ExecutedTrade trade) {
        String payload = trade.tradeId + "|" + trade.acctNo + "|" + trade.security + "|" + trade.cusip + "|" +
                         trade.quantity + "|" + trade.settlementDate + "|" + trade.executionTimestamp + "|" +
                         trade.tradeVenue + "|" + trade.price + "|" + trade.counterparty;
        return payload.getBytes(StandardCharsets.UTF_8);
    }

    public static ExecutedTrade decodeCapture(byte[] data) {
        String[] parts = new String(data, StandardCharsets.UTF_8).split("\\|");
        return new ExecutedTrade(
            parts[0], parts[1], parts[2], parts[3], Integer.parseInt(parts[4]),
            LocalDate.parse(parts[5]), LocalDateTime.parse(parts[6]),
            parts[7], new BigDecimal(parts[8]), parts[9]
        );
    }
}

