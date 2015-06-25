package me.eax.couchbase_example

import rx._
import rx.functions._
import com.couchbase.client.java._
import com.couchbase.client.java.document._

import scala.collection.JavaConverters._
import scala.collection._
import scala.compat.Platform

object CouchbaseExample extends App {
  val cluster = CouchbaseCluster.create("172.31.16.68", "172.31.30.104", "172.31.23.121")
  val bucket = cluster.openBucket("default", "")

  val startTime = Platform.currentTime
  println(s"Start time: $startTime")

  val counterName = s"test-counter-$startTime"
  for (i <- 1 to 32) {
    try {
      val key = s"test-key-$i"
      // .insert does not replace existing document
      // .replace replaces document only if it exists
      bucket.upsert(StringDocument.create(key, s"test-value-$i-$startTime"))
      val value = bucket.get(StringDocument.create(key)).content()
      val counter = bucket.counter(counterName, +1L).content()
      println(s"key $i - OK: $value, counter: $counter")
    } catch {
      case e: Exception =>
        println(s"key $i - FAILED: ${e.getMessage}")
    }
  }

  val readRes = {
    Observable
      .from(Seq("test-key-1", "test-key-2", "no-such-key").asJavaCollection)
      .flatMap(
        new Func1[String, Observable[StringDocument]]() {
          override def call(id: String): Observable[StringDocument] = {
            bucket.async().get(StringDocument.create(id))
          }
        })
      .toList.toBlocking.single.asScala
      .map(doc => doc.id -> doc.content).toMap
  }

  // resulting Map will not contain no-such-key
  println(s"readRes = $readRes")

  val endTime = Platform.currentTime
  println(s"Total: ${endTime - startTime}")
}
