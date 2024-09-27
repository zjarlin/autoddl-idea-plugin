//package com.addzero.addl.autoddlstarter.testentity
//
//import com.addzero.addl.autoddlstarter.anno.Comment
//import io.swagger.v3.oas.annotations.media.Schema
//import org.babyfish.jimmer.sql.*
//import org.babyfish.jimmer.sql.meta.UUIDIdGenerator
//
//@Entity
//@Table(name = "biz_book")
//@Comment("书")
//interface Book {
//
//    @Id
//    @GeneratedValue(generatorType = UUIDIdGenerator::class)
//    val id: String
//
//    /**
//     *书名
//     */
//    @Key
//    @get:Schema(description = "书名")
//    val bookName: String
//
//    /**
//    书号
//     */
//    @Key
//    @get:Schema(description = "书号")
//    val bookNo: Long
//
//}