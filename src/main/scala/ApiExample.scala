package com.example.scleradb.api

import com.scleradb.exec.Processor

object ApiExample {
    def main(args: Array[String]): Unit = args match {
        case Array("--init") => initialize() // initialize Sclera
        case queries => runQueries(queries)  // execute queries
    }

    // initialize Sclera
    private def initialize(): Unit = {
        // SQL processor
        // we are initializing the schema, no need to check and validate
        val processor: Processor = Processor(checkSchema = false)

        try {
            // initialize
            processor.init()
            // execute statement -- create schema
            processor.handleStatement("create schema")
        } finally processor.close()
    }

    // run queries on Sclera
    private def runQueries(queries: Array[String]): Unit = {
        // SQL processor
        val processor: Processor = Processor()

        try {
            // initialize
            processor.init()

            // iterate over the input queries
            queries.foreach { query =>
                // execute query, and process the result
                processor.handleStatement(query, { result =>
                    val colNames: Seq[String] =
                        result.columns.map { c => c.name }
                    println(colNames.mkString(", "))

                    result.rows.foreach { row =>
                        val rowVals: Seq[String] = colNames.map { cname =>
                            row.getStringOpt(cname) getOrElse "[not found]"
                        }

                        println(rowVals.mkString(", "))
                    }
                })
            }
        } finally processor.close()
    }
}
