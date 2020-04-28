# Sclera - Proprietary API Example

This example application shows how an application can interface with Sclera using [Sclera's proprietary API](https://www.scleradb.com/api/sclera-core/).

To use Sclera through the proprietary API, the application needs to:

- specify the Sclera home directory by setting the `SCLERA_ROOT` environment variable (if not set, the default is `$HOME/.sclera`)
- add the following dependencies:
    - Sclera Configuration Manager, [sclera-config](https://github.com/scleradb/sclera/tree/master/modules/config),
    - Sclera Core Engine, [sclera-core](https://github.com/scleradb/sclera/tree/master/modules/core),
    - Sclera plugins needed (if any).

The example application described below is a command line tool to initialize Sclera, and execute queries. See [here](#executable-script) for details on the usage.

## Specify Sclera Root Directory

We need to specify a directory where Sclera can keep its configuration, metadata, and internal database. This is done by setting the environment variable `SCLERA_ROOT`. If not specified, the default is `$HOME/.sclera`.

## Add Package Dependencies

This example uses [SBT](https://scala-sbt.org) as the build tool, and the build file is [`build.sbt`](build.sbt).

The required dependencies are added as:

```scala
    libraryDependencies ++= Seq(
        "com.scleradb" %% "sclera-config" % "4.0-SNAPSHOT",
        "com.scleradb" %% "sclera-core" % "4.0-SNAPSHOT"
    )
```

This is a minimal example, and does not include any Sclera plugins. If your example needs a Sclera Plugin, it should be added to the `libraryDependencies` as well.

## Interface with Sclera using the Proprietary API

This application consists of a single source file, [`ApiExample.scala`](src/main/scala/ApiExample.scala).

There are two procedures:

- `initialize()`: This initializes Sclera's schema (metadata). This is called when `--init` is specified on the command line.
- `runQueries()`: This executes queries provided on the command line and displays the results.

### Code Details: `initialize()`

- Creates and initializes an instance of Sclera [`Processor`](https://www.scleradb.com/api/sclera-core/com/scleradb/exec/Processor.html)
- Executes the statement `create schema` on Sclera using the `Processor` instance.

When the `Processor` instance is initialized, Sclera first checks the sanity of its Schema and issues a warning if anything is wrong. Since we are initializing the schema, we bypass this step by passing a flag `checkSchema` in the properties while creating the `Processor` instance.

### Code Details: `runQueries(...)`

- Creates and initializes an instance of Sclera [`Processor`](https://www.scleradb.com/api/sclera-core/com/scleradb/exec/Processor.html)
- For each query in the list passed as the parameter,
    - Executes the query using the `Processor` instance, getting the result as an instance of type [`TableResult`](https://www.scleradb.com/api/sclera-core/com/scleradb/sql/result/TableResult.html). This result contains the column list (of type [`Column`](https://www.scleradb.com/api/sclera-core/com/scleradb/sql/datatypes/Column.html) and an iterator over rows of type [`TableRow`](https://www.scleradb.com/api/sclera-core/com/scleradb/sql/result/TableRow.html).
    - Output the column names, followed by the result values one row at a time.

## Executable Script

The build file contains a task `mkscript` that generates an executable script for the application, called `scleraexample` in the `bin` subdirectory. You can generate the script using the command:

    > sbt mkscript

The script is run as follows:

    > bin/scleraexample --init

    > bin/scleraexample "select 'Hello' as greeting1, 'World!' as greeting2"
    GREETING1, GREETING2
    Hello, World!

