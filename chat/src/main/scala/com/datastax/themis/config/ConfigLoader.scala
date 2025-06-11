package com.datastax.themis.config

import com.typesafe.config.{Config, ConfigFactory}
import com.datastax.themis.ThemisException
import scala.jdk.CollectionConverters.*
import scala.util.Try

class ConfigLoader:
  private val config: Config = ConfigFactory.load()

  def getClusterNames(): List[ClusterName] =
    if config.hasPath("clusters") then
      config.getObject("clusters").keySet().asScala.toList.map(ClusterName(_))
    else
      List.empty

  def getClusterConfig(clusterName: ClusterName): Map[ClusterConfigKey, String] =
    val clusterPath = s"clusters.${clusterName.value}"
    if !config.hasPath(clusterPath) then
      throw ThemisException(s"Cluster ${clusterName.value} not found in configuration")
    
    val clusterConfig = config.getConfig(clusterPath)
    ClusterConfigKey.values.flatMap { key =>
      Try(clusterConfig.getString(key.key)).toOption.map(value => key -> value)
    }.toMap
