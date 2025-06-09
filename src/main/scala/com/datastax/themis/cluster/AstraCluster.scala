package com.datastax.themis.cluster

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{ResultSet, SimpleStatement}
import com.datastax.themis.config.{ClusterConfigKey, ClusterName}
import com.datastax.themis.ThemisException
import java.nio.file.Paths

class AstraCluster(name: ClusterName, config: Map[ClusterConfigKey, String]) extends Cluster:
  private var session: Option[CqlSession] = None

  override def connect(): CqlSession =
    session.getOrElse {
      val builder = CqlSession.builder()
      
      // Get secure connect bundle path
      val bundlePath = config.getOrElse(
        ClusterConfigKey.SecureConnectBundle,
        throw ThemisException(s"Secure connect bundle path is required for Astra cluster ${name.value}")
      )
      
      builder.withCloudSecureConnectBundle(Paths.get(bundlePath))
      
      // Set credentials
      val clientId = config.getOrElse(
        ClusterConfigKey.ClientId,
        throw ThemisException(s"Client ID is required for Astra cluster ${name.value}")
      )
      
      val clientSecret = config.getOrElse(
        ClusterConfigKey.ClientSecret,
        throw ThemisException(s"Client secret is required for Astra cluster ${name.value}")
      )
      
      builder.withAuthCredentials(clientId, clientSecret)
      
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
