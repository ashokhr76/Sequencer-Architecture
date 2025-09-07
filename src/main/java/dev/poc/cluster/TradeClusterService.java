package dev.poc.cluster;

import java.util.LinkedHashMap;
import java.util.Map;

import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.aeron.ExclusivePublication;
import io.aeron.Image;
import io.aeron.cluster.codecs.CloseReason;
import io.aeron.cluster.service.ClientSession;
import io.aeron.cluster.service.Cluster;
import io.aeron.cluster.service.Cluster.Role;
import io.aeron.cluster.service.ClusteredService;
import io.aeron.logbuffer.Header;

public class TradeClusterService implements ClusteredService {

    private static final Logger log = LoggerFactory.getLogger(TradeClusterService.class);

    private final Map<String, String> tradeState = new LinkedHashMap<>();

    @Override
    public void onStart(Cluster cluster, Image snapshotImage) {
        log.info("Cluster service started. Role: {}", cluster.role());
        // Restore state from snapshotImage if needed
    }

    @Override
    public void onSessionOpen(ClientSession session, long timestamp) {
        log.info("Session opened: {} at {}", session.id(), timestamp);
    }

    @Override
    public void onSessionClose(ClientSession session, long timestamp, CloseReason closeReason) {
        log.info("Session closed: {} at {}. Reason: {}", session.id(), timestamp, closeReason);
    }

    @Override
    public void onSessionMessage(ClientSession session, long timestamp, DirectBuffer buffer, int offset, int length, Header header) {
        byte[] msgBytes = new byte[length];
        buffer.getBytes(offset, msgBytes);
        String message = new String(msgBytes);
        log.info("Received message from session {}: {}", session.id(), message);

        // Example: Store message in tradeState
        tradeState.put(String.valueOf(session.id()), message);

        // Optionally, send a response
        String response = "Ack: " + message;
        org.agrona.concurrent.UnsafeBuffer responseBuffer = new org.agrona.concurrent.UnsafeBuffer(response.getBytes());
        session.offer(responseBuffer, 0, responseBuffer.capacity());
    }

    @Override
    public void onTimerEvent(long correlationId, long timestamp) {
        log.info("Timer event: correlationId={}, timestamp={}", correlationId, timestamp);
    }

    @Override
    public void onTakeSnapshot(ExclusivePublication snapshotPublication) {
        log.info("Taking snapshot of trade state...");
        // Serialize tradeState and publish to snapshotPublication if needed
    }

    @Override
    public void onRoleChange(Role newRole) {
        log.info("Cluster role changed to {}", newRole);
    }

    @Override
    public void onTerminate(Cluster cluster) {
        log.info("Cluster service terminating. Role: {}", cluster.role());
    }
}