package com.datastax.themis.config

enum ClusterConfigKey(val key: String):
  case ContactPoints extends ClusterConfigKey("contact-points")
  case Port extends ClusterConfigKey("port")
  case LocalDatacenter extends ClusterConfigKey("local-datacenter")
  case Username extends ClusterConfigKey("username")
  case Password extends ClusterConfigKey("password")
  case Keyspace extends ClusterConfigKey("keyspace")
  case SecureConnectBundle extends ClusterConfigKey("secure-connect-bundle")
  case ClientId extends ClusterConfigKey("client-id")
  case ClientSecret extends ClusterConfigKey("client-secret")