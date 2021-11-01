package org.syngenta.transformer.task

import com.typesafe.config.ConfigFactory
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.syngenta.transformer.config.TransformerConfig
import org.syngenta.transformer.util.{FlinkKafkaConnector, FlinkUtil}

import java.io.File

class TransformerStreamTask(config: TransformerConfig, kafkaConnector: FlinkKafkaConnector) {
  implicit val env: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(config)
  implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])
  val kafkaConsumer: KafkaSource[String] = kafkaConnector.kafkaStringSource(config.kafkaInputTopic)

  env.fromSource(kafkaConsumer, WatermarkStrategy.noWatermarks[String](), "obs-transformer-consumer")
    .sinkTo(kafkaConnector.kafkaStringSink(config.kafkaOutputTopic))

  env.execute()
}

object TransformerStreamTask {

  def main(args: Array[String]): Unit = {
    val configFilePath = Option(ParameterTool.fromArgs(args).get("config.file.path"))
    val config = configFilePath.map {
      path => ConfigFactory.parseFile(new File(path)).resolve()
    }.getOrElse(ConfigFactory.load("transformer.conf").withFallback(ConfigFactory.systemEnvironment()))
    val transformerConfig = new TransformerConfig(config, "obs-transformer")
    val kafkaUtil = new FlinkKafkaConnector(transformerConfig)
    val task = new TransformerStreamTask(transformerConfig, kafkaUtil)
  }

 }
