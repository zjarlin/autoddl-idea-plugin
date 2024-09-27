//package com.addzero.addl.autoddlstarter
//
//import com.addzero.addl.autoddlstarter.generator.DatabaseDDLGenerator.Companion.getDatabaseDDLGenerator
//import com.addzero.addl.autoddlstarter.generator.defaultconfig.BaseMetaInfoUtil
//import com.addzero.addl.autoddlstarter.generator.defaultconfig.DefaultMetaInfoUtil
//import com.addzero.addl.autoddlstarter.generator.entity.DDLContext
//import com.addzero.addl.autoddlstarter.testentity.SysUser
//import org.babyfish.jimmer.client.EnableImplicitApi
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.boot.autoconfigure.SpringBootApplication
//import java.util.concurrent.ConcurrentHashMap
//
//val loggerMap = ConcurrentHashMap<Class<*>, Logger>()
//inline val <reified T> T.log: Logger get() = loggerMap.computeIfAbsent(T::class.java) { LoggerFactory.getLogger(it) }
//
//@EnableImplicitApi
//@SpringBootApplication
//class AutoddlStarterApplication
//
//fun main(args: Array<String>) {
//    val dbType = DefaultMetaInfoUtil.mydbType()
//    val sysUserClass = SysUser::class.java
//    //获取sql生成器
//    val databaseDDLGenerator = getDatabaseDDLGenerator(dbType)
//
//    //获取元数据提取器
//    val ddlContexts: List<DDLContext> = BaseMetaInfoUtil.extractDDLContext(sysUserClass)
//
//    ddlContexts.forEach {
//        val generateDDL = databaseDDLGenerator.generateCreateTableDDL(it)
//        println(generateDDL)
//        val generateAddColDDL = databaseDDLGenerator.generateAddColDDL(it)
//        println(generateAddColDDL)
//    }
//}