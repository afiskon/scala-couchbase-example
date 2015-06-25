package me.eax.couchbase_example

import java.net.URI

import com.couchbase.client._
import scala.collection._
import scala.collection.JavaConverters._

import scala.compat.Platform

object CouchbaseExample extends App {
  val bucketName = "default"
  val password = ""
  val nodesList = Seq("172.31.16.68", "172.31.30.104", "172.31.23.121")
                    .map(ip => URI.create(s"http://$ip:8091/pools"))
                    .toBuffer.asJava
  val client = new CouchbaseClient(nodesList, bucketName, password)

  val startTime = Platform.currentTime
  println(s"Start time: $startTime")

  val counterName = s"test-counter-$startTime"
  client.set(counterName, "0").get()
  for (i <- 1 to 32) {
    try {
      val key = s"test-key-$i"
      client.set(key, s"test-value-$i-$startTime").get()
      val value = client.get(key).asInstanceOf[String]
      val counter = client.incr(counterName, 1L)
      println(s"key $i - OK: $value, counter: $counter")
    } catch {
      case e: Exception =>
        println(s"key $i - FAILED: ${e.getMessage}")
    }
  }

  val readRes = {
    client.getBulk(Seq("test-key-1", "test-key-2", "no-such-key").asJavaCollection)
      .asScala
      .map { case (k, v) => k -> v.asInstanceOf[String] }
  }

  // resulting Map will not contain no-such-key
  println(s"readRes = $readRes")

  val endTime = Platform.currentTime
  println(s"Total: ${endTime - startTime}")

  System.exit(0)
}
