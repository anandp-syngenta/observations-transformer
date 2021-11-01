package org.syngenta.transformer.util

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup
import org.syngenta.transformer.config.TransformerConfig

object FlinkUtil {

  def getExecutionContext(config: TransformerConfig): StreamExecutionEnvironment = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.getConfig.setUseSnapshotCompression(config.enableCompressedCheckpointing)
    env.enableCheckpointing(config.checkpointingInterval)

    /**
     * Use S3 storage as distributed state backend if enabled
     */
    config.enableFileSystemCheckpointing match {
      case Some(true) =>
        val checkpointStorage = new FileSystemCheckpointStorage(s"${config.checkpointingBaseUrl.getOrElse("")}/${config.jobName}")
        val checkpointConfig: CheckpointConfig = env.getCheckpointConfig
        checkpointConfig.setCheckpointStorage(checkpointStorage)
        checkpointConfig.enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
        checkpointConfig.setMinPauseBetweenCheckpoints(config.checkpointingPauseSeconds)
      case _ => env.setStateBackend(new HashMapStateBackend())
    }

    // env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime)
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(config.restartAttempts, config.delayBetweenAttempts))
    env
  }
}
