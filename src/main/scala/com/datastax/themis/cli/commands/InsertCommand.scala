package com.datastax.themis.cli.commands

import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.datastax.themis.ThemisException
import picocli.CommandLine.{Command, Option => CliOption, Parameters}

@Command(name = "insert", description = Array("Insert data into a table"))
class InsertCommand extends AbstractCommand:
  @Parameters(index = "0", description = Array("Table name"))
  private var table: String = _
  
  @CliOption(names = Array("-k", "--keyspace"), description = Array("Keyspace name"))
  private var keyspace: String = _
  
  @CliOption(names = Array("-d", "--data"), description = Array("Column data in format 'col1=val1,col2=val2'"), required = true)
  private var data: String = _
  
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
      
      // Build INSERT query
      val columns = columnData.keys.mkString(", ")
      val placeholders = columnData.keys.map(_ => "?").mkString(", ")
      val query = s"INSERT INTO $fullTableName ($columns) VALUES ($placeholders)"
      
      // Execute query with values
      val statement = SimpleStatement.newInstance(query, columnData.values.map(toTypedValue).toSeq*)
      cluster.execute(statement)
      
      println(s"Data inserted into $fullTableName")
      0
    catch
      case e: Exception =>
        System.err.println(s"Error inserting data: ${e.getMessage}")
        1
    finally
      cluster.close()
  
  private def toTypedValue(value: String): Object =
    // Simple conversion - in a real app, you'd want more sophisticated type handling
    if value == "null" then null
    else if value.toLowerCase == "true" || value.toLowerCase == "false" then java.lang.Boolean.valueOf(value)
    else if value.matches("-?\\d+") then java.lang.Integer.valueOf(value)
    else if value.matches("-?\\d+\\.\\d+") then java.lang.Double.valueOf(value)
    else value
