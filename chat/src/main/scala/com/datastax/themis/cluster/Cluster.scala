package com.datastax.themis.cluster

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{ResultSet, SimpleStatement}

trait Cluster:
  def connect(): CqlSession
  def execute(query: String): ResultSet
  def close(): Unit