package com.datastax.themis.cluster

import com.datastax.themis.config.{ClusterConfigKey, ClusterName, ConfigLoader}

object ClusterFactory:
  def createCluster(name: ClusterName): Cluster =
    val configLoader = new ConfigLoader()
    val config = configLoader.getClusterConfig(name)
    
    if config.contains(ClusterConfigKey.SecureConnectBundle) then
      new AstraCluster(name, config)
    else
      new DefaultCluster(name, config)
