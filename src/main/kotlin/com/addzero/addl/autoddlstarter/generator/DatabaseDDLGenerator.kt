package com.addzero.addl.autoddlstarter.generator

import com.addzero.addl.autoddlstarter.generator.entity.DDLContext


abstract class DatabaseDDLGenerator : IDatabaseGenerator {
    /**建表语句
     * @param [ddlContext]
     * @return [String]
     */
    abstract fun generateCreateTableDDL(ddlContext: DDLContext): String

    /**
     * 加列语句
     * @param [ddlContext]
     * @return [String]
     */
    abstract fun generateAddColDDL(ddlContext: DDLContext): String



//    abstract fun printChangeDML(dmlContext: DMLContext): String
}