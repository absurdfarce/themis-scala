package com.datastax.themis.cli.commands

import picocli.CommandLine.Command
import scala.jdk.CollectionConverters.*

@Command(name = "schema", description = Array("Display schema information"))
class SchemaCommand extends AbstractCommand:
  override def call(): Integer =
    val cluster = getCluster()
    try
      // Get keyspaces
      val keyspacesResult = cluster.execute("SELECT keyspace_name FROM system_schema.keyspaces")
      val keyspaces = keyspacesResult.asScala.map(_.getString("keyspace_name")).toList
      
      keyspaces.filterNot(k => k.startsWith("system")).foreach { keyspace =>
        println(s"Keyspace: $keyspace")
        
        // Get tables for this keyspace
        val tablesResult = cluster.execute(s"SELECT table_name FROM system_schema.tables WHERE keyspace_name = '$keyspace'")
        val tables = tablesResult.asScala.map(_.getString("table_name")).toList
        
        tables.foreach { table =>
          println(s"  Table: $table")
          
          // Get columns for this table
          val columnsResult = cluster.execute(
            s"SELECT column_name, type FROM system_schema.columns WHERE keyspace_name = '$keyspace' AND table_name = '$table'"
          )
          
          columnsResult.asScala.foreach { row =>
            val columnName = row.getString("column_name")
            val columnType = row.getString("type")
            println(s"    Column: $columnName ($columnType)")
          }
        }
      }
      
      0
    catch
      case e: Exception =>
        System.err.println(s"Error retrieving schema: ${e.getMessage}")
        1
    finally
      cluster.close()