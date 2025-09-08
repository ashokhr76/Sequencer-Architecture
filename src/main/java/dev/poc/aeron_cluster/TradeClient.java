package dev.poc.aeron_cluster;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.agrona.concurrent.UnsafeBuffer;

import dev.poc.aeron_cluster.trade.ExecutedTrade;
import io.aeron.Aeron;
import io.aeron.cluster.client.AeronCluster;

public class TradeClient {
    public static void main(String[] args) throws Exception {
        try (Aeron aeron = Aeron.connect();
             AeronCluster cluster = AeronCluster.connect(
                 new AeronCluster.Context()
                     .ingressChannel("aeron:udp?endpoint=localhost:9020")
                     .egressChannel("aeron:udp?endpoint=localhost:0")
                     .ingressEndpoints("0=localhost:9020"))) {

            ExecutedTrade trade = new ExecutedTrade(
                "T123", "ACCT001", "INFY", "123456789", 100,
                LocalDate.now().plusDays(2), LocalDateTime.now(),
                "NSE", new BigDecimal("1450.25"), "CounterpartyX"
            );

            byte[] msg = TradeCommandCodec.encodeCapture(trade);
            UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(msg.length));
            buffer.putBytes(0, msg);

            while (cluster.offer(buffer, 0, msg.length) < 0) {
                Thread.onSpinWait();
            }

            System.out.println("Trade sent: " + trade.tradeId);
        }
    }
}
