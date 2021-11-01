package org.syngenta.transformer.util

import org.apache.flink.connector.base.DeliveryGuarantee
import org.apache.flink.connector.kafka.sink.KafkaSink
import org.apache.flink.connector.kafka.source.KafkaSource

import java.{util => jutil}
import org.syngenta.transformer.config.TransformerConfig
import org.syngenta.transformer.serde.{ByteDeserializationSchema, ByteSerializationSchema, MapDeserializationSchema,
  MapSerializationSchema, StringDeserializationSchema, StringSerializationSchema}

class FlinkKafkaConnector(config: TransformerConfig) {
  def kafkaMapSource(kafkaTopic: String): KafkaSource[jutil.Map[String, AnyRef]] = {
    KafkaSource.builder[jutil.Map[String, AnyRef]]()
      .setTopics(kafkaTopic)
      .setDeserializer(new MapDeserializationSchema)
      .setProperties(config.kafkaConsumerProperties)
      .build()
  }

  def kafkaMapSink(kafkaTopic: String): KafkaSink[jutil.Map[String, AnyRef]] = {
    KafkaSink.builder[jutil.Map[String, AnyRef]]()
      .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
      .setRecordSerializer(new MapSerializationSchema(kafkaTopic))
      .setKafkaProducerConfig(config.kafkaProducerProperties)
      .build()
  }

  def kafkaStringSource(kafkaTopic: String): KafkaSource[String] = {
    KafkaSource.builder[String]()
      .setTopics(kafkaTopic)
      .setDeserializer(new StringDeserializationSchema)
      .setProperties(config.kafkaConsumerProperties)
      .setBootstrapServers(config.kafkaConsumerBrokerServers)
      .build()
  }

  def kafkaBytesSource(kafkaTopic: String): KafkaSource[Array[Byte]] = {
    KafkaSource.builder[Array[Byte]]()
      .setTopics(kafkaTopic)
      .setDeserializer(new ByteDeserializationSchema)
      .setProperties(config.kafkaConsumerProperties)
      .build()
  }

  def kafkaStringSink(kafkaTopic: String): KafkaSink[String] = {
    KafkaSink.builder[String]()
      .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
      .setRecordSerializer(new StringSerializationSchema(kafkaTopic))
      .setKafkaProducerConfig(config.kafkaProducerProperties)
      .setBootstrapServers(config.kafkaProducerBrokerServers)
      .build()
  }

  /*
  def kafkaEventSource[T <: Events](kafkaTopic: String)(implicit m: Manifest[T]): SourceFunction[T] = {
    new FlinkKafkaConsumer[T](kafkaTopic, new EventDeserializationSchema[T], config.kafkaConsumerProperties)
  }

  def kafkaEventSink[T <: Events](kafkaTopic: String)(implicit m: Manifest[T]): SinkFunction[T] = {
    new FlinkKafkaProducer[T](kafkaTopic,
      new EventSerializationSchema[T](kafkaTopic), config.kafkaProducerProperties, Semantic.AT_LEAST_ONCE)
  }
  */

  def kafkaBytesSink(kafkaTopic: String): KafkaSink[Array[Byte]] = {
    KafkaSink.builder[Array[Byte]]()
      .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
      .setRecordSerializer(new ByteSerializationSchema(kafkaTopic))
      .setKafkaProducerConfig(config.kafkaProducerProperties)
      .build()
  }
}
