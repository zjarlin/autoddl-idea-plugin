//package com.addzero.addl.autoddlstarter.testentity
//
//import com.addzero.addl.autoddlstarter.anno.Comment
//import io.swagger.v3.oas.annotations.media.Schema
//import org.babyfish.jimmer.sql.*
//import org.babyfish.jimmer.sql.meta.UUIDIdGenerator
//import kotlin.reflect.full.memberProperties
//
//@Entity
//@Table(name = "sys_user")
//@Comment("系统用户")
//interface SysUser {
////    val children: List<SysUser>
//
//    @Id
//    @GeneratedValue(generatorType = UUIDIdGenerator::class)
//    val id: String
//
//    /**
//     * 手机号
//     */
//    @Key
//    @get:Schema(description = "手机号")
//    val phone: String
//
//    /**
//     * 密码
//     */
//    @get:Schema(description = "密码")
//    val password: String
//
//    /**
//     * 头像
//     */
//    @get:Schema(description = "头像")
//    val avatar: String?
//
//    /**
//     * 昵称
//     */
//    @get:Schema(description = "昵称")
//    val nickname: String?
//
//    /**
//     * 性别
//     */
//    @get:Schema(description = "性别")
//    val gender: String?
//}
//
//fun main() {
//    val sysUserClass = SysUser::class
//    val memberProperties = sysUserClass.memberProperties
//
//    sysUserClass.java.declaredMethods.forEach {
//        val ano = it .getAnnotation(Schema::class.java)
//        val description = ano?.description
//        println(description)
//    }
//}