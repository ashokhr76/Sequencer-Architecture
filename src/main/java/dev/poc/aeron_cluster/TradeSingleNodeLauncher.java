package dev.poc.aeron_cluster;

import io.aeron.archive.Archive;
import io.aeron.cluster.ConsensusModule;
import io.aeron.cluster.service.ClusteredServiceContainer;
import io.aeron.driver.MediaDriver;

import java.io.File;
import java.util.UUID;

public class TradeSingleNodeLauncher {

    public static void main(String[] args) {
        String baseDir = "trade-cluster-node-" + UUID.randomUUID();
        new File(baseDir).mkdirs();

        MediaDriver.launch(
            new MediaDriver.Context()
                .dirDeleteOnStart(true)
                .aeronDirectoryName(baseDir + "/media")
        );
        Archive.launch(
            new Archive.Context()
                .deleteArchiveOnStart(true)
                .aeronDirectoryName(baseDir + "/media")
                .archiveDir(new File(baseDir, "archive"))
                .controlChannel("aeron:ipc")
        );
        ConsensusModule.launch(
            new ConsensusModule.Context()
                .deleteDirOnStart(true)
                .aeronDirectoryName(baseDir + "/media")
                .clusterDir(new File(baseDir, "consensus"))
        );
        ClusteredServiceContainer.launch(
            new ClusteredServiceContainer.Context()
                .aeronDirectoryName(baseDir + "/media")
                .clusterDir(new File(baseDir, "service"))
                .clusteredService(new TradeClusterService())
        );
    }
}
