package org.syngenta.transformer.serde

import java.nio.charset.StandardCharsets
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema
import org.apache.flink.streaming.connectors.kafka.{KafkaDeserializationSchema, KafkaSerializationSchema}
import org.apache.flink.util.Collector
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord

class ByteDeserializationSchema extends KafkaRecordDeserializationSchema[Array[Byte]] {

  override def getProducedType: TypeInformation[Array[Byte]] = TypeExtractor.getForClass(classOf[Array[Byte]])

  override def deserialize(record: ConsumerRecord[Array[Byte], Array[Byte]], out: Collector[Array[Byte]]): Unit = {
    out.collect(record.value())
  }

}

class ByteSerializationSchema(topic: String, key: Option[String] = None) extends KafkaRecordSerializationSchema[Array[Byte]] {

  private val serialVersionUID = -4284080856874185929L

  override def serialize(element: Array[Byte], context: KafkaRecordSerializationSchema.KafkaSinkContext, timestamp: java.lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
    key.map { kafkaKey =>
      new ProducerRecord[Array[Byte], Array[Byte]](topic, kafkaKey.getBytes(StandardCharsets.UTF_8), element)
    }.getOrElse(new ProducerRecord[Array[Byte], Array[Byte]](topic, element))
  }
}
