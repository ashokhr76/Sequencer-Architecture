package dev.poc.aeron_cluster;

import java.util.HashMap;
import java.util.Map;

import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;

import dev.poc.aeron_cluster.trade.ExecutedTrade;
import io.aeron.ExclusivePublication;
import io.aeron.Image;
import io.aeron.cluster.codecs.CloseReason;
import io.aeron.cluster.service.ClientSession;
import io.aeron.cluster.service.Cluster;
import io.aeron.cluster.service.Cluster.Role;
import io.aeron.cluster.service.ClusteredService;
import io.aeron.cluster.service.ClusteredServiceContainer;
import io.aeron.logbuffer.Header;

public class TradeClusterService implements ClusteredService    {

        private final Map<String, ExecutedTrade> trades = new HashMap<>();

        private Cluster cluster;

        @Override
        public void onRoleChange(Role newRole) {           
            System.out.println("Role changed to: " + newRole);
        }

        @Override
        public void onSessionClose(ClientSession session, long arg1, CloseReason reason) {
            System.out.println("Session closed: " + session.id() + " Reason: " + reason);
        }

        @Override
        public void onSessionMessage(ClientSession session, long timestamp, DirectBuffer buffer, int offset, int length, Header header) {
            byte[] data = new byte[length];
            buffer.getBytes(offset, data);
            ExecutedTrade trade = TradeCommandCodec.decodeCapture(data);
            trades.put(trade.tradeId, trade);
            System.out.println("Captured trade: " + trade.tradeId);
        }

       
        @Override
        public void onSessionOpen(ClientSession session, long arg1) {
            System.out.println("Session opened: " + session.id());
         
          
        }

        private IdleStrategy idleStrategy;  

        @Override
        public void onStart(Cluster cluster, Image snapShotImage) {

             this.cluster = cluster;
             this.idleStrategy = cluster.idleStrategy();

             System.err.println("TradeClusterService started"+ cluster.role());

        }

        @Override
        public void onTakeSnapshot(ExclusivePublication arg0) {
            System.out.println("Snapshot requested.");
        }

        @Override
        public void onTerminate(Cluster arg0) {
            System.out.println("TradeClusterService terminating.");
        }

        @Override
        public void onTimerEvent(long arg0, long arg1) {
           System.out.println("TonTimerEvent.");
        }


}
