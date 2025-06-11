# Porting Themis to Scala

An experiment in using [Augment](https://www.augmentcode.com/) to port [Themis](https://github.com/absurdfarce/themis) to Scala.

## Chat version
First attempt was to use the chat functionality in Augment.  The output of this process can be found [here](chat).  A transcript of the entire conversation between myself and Augment can be found [here](https://absurdfarce.github.io/themis-scala/transcript/chat/themis-scala.html).

## Remaining Problems
The transcript above shows what was necessary to get to a version of Themis that would at least compile with Scala 3.5.1.  This didn't leave us with a functioning app, however... there were still multiple significant problems in place:

* Configuration process was converted from YAML to use Typesafe Config
* All commands are passed a map of all clusters so that they can operate against any target.  None of this logic is included in the Scala impl.
* Random generation of test data in InsertCommand.java is just gone.  InsertCommand.scala expects data to be passed via a command-line parameter.
* ClusterName.java defines the enum which describes which cluster commands should be executed against.  We expect to find configs at some of these names in the configuration YAML.  ClusterName.scala is just a case class.
* SchemaCommand.java is used to setup the test schema; SchemaCommand.scala is (apparently) trying to display schema information.  It doesn’t actually create any keyspaces or tables.
