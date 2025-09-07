package dev.poc.client;



import java.nio.ByteBuffer;
import java.time.Duration;

import org.agrona.concurrent.UnsafeBuffer;

import dev.poc.codec.MsgCodec;
import io.aeron.Aeron;
import io.aeron.cluster.client.AeronCluster;

public class TradeClient {
    public static void main(String[] args) throws Exception {
        try (Aeron aeron = Aeron.connect();
             AeronCluster cluster = AeronCluster.connect(
                 new AeronCluster.Context()
                     .ingressChannel("aeron:udp?endpoint=localhost:0")
                     .egressChannel("aeron:udp?endpoint=localhost:0")
                     .ingressEndpoints("0=localhost:9020"))) {

            send(cluster, MsgCodec.capture("T1"));
            send(cluster, MsgCodec.capture("T2"));
            send(cluster, MsgCodec.enrich("T1", "{\"sym\":\"INFY\"}"));
            send(cluster, MsgCodec.validate("T1"));
            send(cluster, MsgCodec.calc("T1"));
            send(cluster, MsgCodec.distribute("T1"));

            send(cluster, MsgCodec.enrich("T2", "{\"sym\":\"TCS\"}"));
            send(cluster, MsgCodec.validate("T2"));
            send(cluster, MsgCodec.calc("T2"));
            send(cluster, MsgCodec.distribute("T2"));

            System.out.println("Commands sent. Observe server logs.");
            Thread.sleep(Duration.ofSeconds(2).toMillis());
        }
    }

    private static void send(AeronCluster cluster, byte[] msg) {
        UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(msg.length));
        buffer.putBytes(0, msg);
        while (true) {
            long result = cluster.offer(buffer, 0, msg.length);
            if (result > 0) break;
            if (result < 0) {
                throw new IllegalStateException("Failed to send message, offer returned: " + result);
            }
            // backoff
            Thread.onSpinWait();
        }
    }
}
