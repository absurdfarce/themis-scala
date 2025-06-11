package com.datastax.themis.cluster

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{ResultSet, SimpleStatement}
import com.datastax.themis.config.{ClusterConfigKey, ClusterName}
import java.net.InetSocketAddress
import scala.jdk.CollectionConverters.*

class DefaultCluster(name: ClusterName, config: Map[ClusterConfigKey, String]) extends Cluster:
  private var session: Option[CqlSession] = None

  override def connect(): CqlSession =
    session.getOrElse {
      val builder = CqlSession.builder()
      
      // Set contact points and port
      config.get(ClusterConfigKey.ContactPoints).foreach { points =>
        val port = config.get(ClusterConfigKey.Port).map(_.toInt).getOrElse(9042)
        val addresses = points.split(",").map(_.trim).map(host => InetSocketAddress(host, port))
        builder.addContactPoints(addresses.toList.asJava)
      }
      
      // Set local datacenter
      config.get(ClusterConfigKey.LocalDatacenter).foreach(builder.withLocalDatacenter)
      
      // Set credentials if provided
      for {
        username <- config.get(ClusterConfigKey.Username)
        password <- config.get(ClusterConfigKey.Password)
      } yield builder.withAuthCredentials(username, password)
      
      // Set keyspace if provided
      config.get(ClusterConfigKey.Keyspace).foreach(builder.withKeyspace)
      
      val newSession = builder.build()
      session = Some(newSession)
      newSession
    }

  override def execute(query: String): ResultSet =
    connect().execute(SimpleStatement.newInstance(query))

  override def close(): Unit =
    session.foreach(_.close())
    session = None