package com.datastax.themis.cli.commands

import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.datastax.themis.ThemisException
import picocli.CommandLine.{Command, Option => CliOption, Parameters}

@Command(name = "insert", description = Array("Insert data into a table"))
class InsertCommand extends AbstractCommand:
  @Parameters(index = "0", description = Array("Table name"))
  private var table: String = null
  
  @CliOption(names = Array("-k", "--keyspace"), description = Array("Keyspace name"))
  private var keyspace: String = null
  
  @CliOption(names = Array("-d", "--data"), description = Array("Column data in format 'col1=val1,col2=val2'"), required = true)
  private var data: String = null
  
  override def call(): Integer =
    val cluster = getCluster()
    try
      val keyspacePrefix = if keyspace != null then s"$keyspace." else ""
      val fullTableName = s"$keyspacePrefix$table"
      
      // Parse column data
      val columnData = data.split(",").map { pair =>
        val parts = pair.split("=", 2)
        if parts.length != 2 then
          throw ThemisException(s"Invalid data format: $pair. Expected format: column=value")
        (parts(0), parts(1))
      }.toMap
      
      if columnData.isEmpty then
        throw ThemisException("No data provided for insertion")
      
      // Build INSERT query with values directly in the query
      val columns = columnData.keys.mkString(", ")
      val values = columnData.values.map(formatValue).mkString(", ")
      val query = s"INSERT INTO $fullTableName ($columns) VALUES ($values)"
      
      // Execute query
      cluster.execute(query)
      
      println(s"Data inserted into $fullTableName")
      0
    catch
      case e: Exception =>
        System.err.println(s"Error inserting data: ${e.getMessage}")
        1
    finally
      cluster.close()
  
  private def formatValue(value: String): String =
    if value == "null" then "null"
    else if value.toLowerCase == "true" || value.toLowerCase == "false" then value
    else if value.matches("-?\\d+") || value.matches("-?\\d+\\.\\d+") then value
    else s"'${value.replace("'", "''")}'" // Escape single quotes for string values
