package com.datastax.themis.cli

import com.datastax.themis.cli.commands.{InsertCommand, QueryCommand, SchemaCommand}
import picocli.CommandLine
import picocli.CommandLine.{Command, HelpCommand}

@Command(
  name = "themis",
  description = Array("Themis CLI for Cassandra/DSE/Astra DB"),
  subcommands = Array(
    classOf[QueryCommand],
    classOf[SchemaCommand],
    classOf[InsertCommand],
    classOf[HelpCommand]
  )
)
class ThemisCli

object ThemisCli:
  def main(args: Array[String]): Unit =
    val exitCode = new CommandLine(ThemisCli()).execute(args: _*)
    System.exit(exitCode)