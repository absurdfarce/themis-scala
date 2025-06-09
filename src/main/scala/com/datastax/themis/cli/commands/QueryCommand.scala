package com.datastax.themis.cli.commands

import com.datastax.oss.driver.api.core.cql.{Row, SimpleStatement}
import picocli.CommandLine.{Command, Parameters}
import scala.jdk.CollectionConverters.*

@Command(name = "query", description = Array("Execute a CQL query"))
class QueryCommand extends AbstractCommand:
  @Parameters(index = "0", description = Array("CQL query to execute"))
  private var query: String = _
  
  override def call(): Integer =
    val cluster = getCluster()
    try
      val result = cluster.execute(query)
      val columnDefinitions = result.getColumnDefinitions
      val columnNames = columnDefinitions.asScala.map(_.getName.toString).toList
      
      // Print header
      println(columnNames.mkString("\t"))
      println("-" * columnNames.mkString("\t").length)
      
      // Print rows
      result.asScala.foreach { row =>
        val values = columnNames.map(name => getValueAsString(row, name))
        println(values.mkString("\t"))
      }
      
      0
    catch
      case e: Exception =>
        System.err.println(s"Error executing query: ${e.getMessage}")
        1
    finally
      cluster.close()
  
  private def getValueAsString(row: Row, columnName: String): String =
    val columnDef = row.getColumnDefinitions.get(columnName)
    if row.isNull(columnName) then "NULL"
    else row.getObject(columnName).toString
