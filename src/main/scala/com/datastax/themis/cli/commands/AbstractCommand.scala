package com.datastax.themis.cli.commands

import com.datastax.themis.cluster.{Cluster, ClusterFactory}
import com.datastax.themis.config.ClusterName
import picocli.CommandLine.{Option => CliOption}
import java.util.concurrent.Callable

abstract class AbstractCommand extends Callable[Integer]:
  @CliOption(names = Array("-c", "--cluster"), description = Array("Cluster name"), required = true)
  private var clusterName: String = _
  
  protected def getCluster(): Cluster =
    ClusterFactory.createCluster(ClusterName(clusterName))