package dev.poc.cluster;

import java.io.File;
import java.util.UUID;

import org.agrona.concurrent.NoOpIdleStrategy;

import io.aeron.archive.Archive;
import io.aeron.archive.client.AeronArchive;
import io.aeron.cluster.ConsensusModule;
import io.aeron.cluster.service.ClusteredServiceContainer;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;

public class SingleNodeLauncher {
    public static void main(String[] args) {
        // Each run uses a fresh directory to avoid residue between runs
        String baseDir = "cluster-node-" + UUID.randomUUID();
        new File(baseDir).mkdirs();

        // MediaDriver
        final MediaDriver.Context mediaCtx = new MediaDriver.Context()
            .threadingMode(ThreadingMode.SHARED)
            .dirDeleteOnStart(true)
            .spiesSimulateConnection(true)
            .termBufferSparseFile(true)
            .ipcTermBufferLength(64 * 1024);

        final MediaDriver mediaDriver = MediaDriver.launch(mediaCtx);

        // Archive
        final Archive.Context archiveCtx = new Archive.Context()
            .aeronDirectoryName(mediaCtx.aeronDirectoryName())
            .controlChannel("aeron:udp?endpoint=localhost:8010")
            //.controlChannel("aeron:ipc")
            .archiveDir(new File(baseDir, "archive"))
            .deleteArchiveOnStart(true)
            .idleStrategySupplier(NoOpIdleStrategy::new)
            //.replicationChannel("aeron:udp?endpoint=localhost:8020"); 
            .replicationChannel("aeron:ipc");

        System.out.println("Replication channel: " + archiveCtx.replicationChannel());

       
        final Archive archive = Archive.launch(archiveCtx);

        

        // Consensus Module
    


        final ConsensusModule.Context consensusCtx = new ConsensusModule.Context()
            .aeronDirectoryName(mediaCtx.aeronDirectoryName())
            .archiveContext(new AeronArchive.Context()
                //.controlRequestChannel("aeron:udp?endpoint=localhost:8010")
                //.controlResponseChannel("aeron:udp?endpoint=localhost:0"))
                .controlRequestChannel("aeron:ipc")
                .controlResponseChannel("aeron:ipc"))
            .clusterDir(new File(baseDir, "consensus"))
            .ingressChannel("aeron:udp?endpoint=localhost:9020")
            .logChannel("aeron:ipc")
            .replicationChannel("aeron:udp?endpoint=localhost:0")
            .clusterMemberId(0)
            .clusterMembers("0,localhost:9020,localhost:0,localhost:0")
            .appointedLeaderId(0) // single-node appointed leader
            .deleteDirOnStart(true);

        final ConsensusModule consensusModule = ConsensusModule.launch(consensusCtx);

        // Clustered Service
        final ClusteredServiceContainer.Context serviceCtx = new ClusteredServiceContainer.Context()
            .aeronDirectoryName(mediaCtx.aeronDirectoryName())
            .archiveContext(new AeronArchive.Context()
                //.controlRequestChannel("aeron:udp?endpoint=localhost:8010")
                //.controlResponseChannel("aeron:udp?endpoint=localhost:0"))
                .controlRequestChannel("aeron:ipc")
                .controlResponseChannel("aeron:ipc"))
            .clusterDir(new File(baseDir, "service"))
            .clusteredService(new TradeClusterService());

        final ClusteredServiceContainer serviceContainer = ClusteredServiceContainer.launch(serviceCtx);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            serviceContainer.close();
            consensusModule.close();
            archive.close();
            mediaDriver.close();
        }));

        System.out.println("Single-node cluster started. Ingress on udp://localhost:9020");
        System.out.println("Run the client to send commands.");
        // Keep running
        try { Thread.currentThread().join(); } catch (InterruptedException ignored) {}
    }
}
