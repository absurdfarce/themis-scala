package com.datastax.themis.cluster

import com.datastax.themis.config.{ClusterConfigKey, ClusterName, ConfigLoader}

object ClusterFactory:
  def createCluster(name: ClusterName): Cluster =
    val configLoader = ConfigLoader()
    val config = configLoader.getClusterConfig(name)
    
    if config.contains(ClusterConfigKey.SecureConnectBundle) then
      AstraCluster(name, config)
    else
      DefaultCluster(name, config)