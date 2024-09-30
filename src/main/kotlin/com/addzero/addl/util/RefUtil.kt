package com.addzero.addl.util

import cn.hutool.core.util.ReflectUtil
import com.addzero.addl.autoddlstarter.generator.defaultconfig.DefaultMetaInfoUtil.getCommentFun
import com.addzero.addl.autoddlstarter.generator.entity.JavaFieldMetaInfo
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

object RefUtil {
    /**
     * 尝试序列化不成功就是原始类型,否则视为对象类型,暂时想到的办法判断实体里是不是有嵌套实体,后续再优化
     * @param [obj]
     * @return [Boolean]
     */
    fun isT(obj: Any): Boolean {
        if (Objects.isNull(obj)) {
            return false
        }
        //非原始类型
        val javaClass = obj.javaClass
        try {
            val jsonObject: JSONObject? = JSON.parseObject(
                JSON.toJSONString(obj)
            )
        } catch (e: Exception) {
            return false
        }
        return !isPrimitiveOrWrapper(javaClass)
    }

    private fun isPrimitiveOrWrapper(aClass: Class<*>): Boolean {
// Class<?> aClass = obj.getClass();
        val primitive = aClass.isPrimitive
        return primitive
                || !Enum::class.java.isAssignableFrom(javaClass) && !JSON::class.java.isAssignableFrom(javaClass)

                || Byte::class.java.isAssignableFrom(aClass) || Short::class.java.isAssignableFrom(aClass) || Int::class.java.isAssignableFrom(
            aClass
        ) || Long::class.java.isAssignableFrom(aClass) || Float::class.java.isAssignableFrom(aClass) || Double::class.java.isAssignableFrom(
            aClass
        ) || Boolean::class.java.isAssignableFrom(aClass) || Char::class.java.isAssignableFrom(aClass)
    }


    fun isCollection(obj: Any): Boolean {
        val assignableFrom = MutableCollection::class.java.isAssignableFrom(obj.javaClass)
        return assignableFrom
    }

    fun isNonNullField(obj: Any?, field: Field?): Boolean {
        val fieldValue: Any = ReflectUtil.getFieldValue(obj, field)
        return Objects.nonNull(fieldValue)
    }

    fun isObjectField(obj: Any?, field: Field?): Boolean {
        val fieldValue: Any = ReflectUtil.getFieldValue(obj, field)
        val `object`: Boolean = isT(fieldValue)
        return `object`
    }

    fun isCollectionField(field: Field): Boolean {
        val type: Class<*> = field.type
        val assignableFrom = MutableCollection::class.java.isAssignableFrom(type)
        return assignableFrom
    }

    fun extractInterfaceMetaInfo(clazz: Class<*>): List<JavaFieldMetaInfo> {
        val fieldMetaInfoList = mutableListOf<JavaFieldMetaInfo>()

        // 遍历类中所有的方法（接口中的字段会生成 getter 方法）
        clazz.declaredMethods.forEach { method ->
            // 判断是否是 getter 方法
            if (method.name.startsWith("get") && method.parameterCount == 0) {
                val fieldName =
                    method.name.substring(3).replaceFirstChar { it.lowercase(Locale.getDefault()) } // 从 getter 方法推断属性名
                val returnType = method.returnType
                val genericType = method.genericReturnType as? Class<*>

                // 查找字段上的注解
//                val comment      DefaultMetaInfoUtil.getColComment(method)
                val comment = getCommentFunByMethod(method)

                // 将属性信息添加到列表中
                fieldMetaInfoList.add(
                    JavaFieldMetaInfo(
                        name = fieldName,
                        type = returnType,
                        genericType = genericType ?: returnType,
                        comment = comment
                    )
                )
            }
        }

        return fieldMetaInfoList
    }

    private fun getCommentFunByMethod(method: Method):String {
        TODO("Not yet implemented")
    }


    fun extractClassMetaInfo(clazz: Class<*>): List<JavaFieldMetaInfo> {
        val fieldMetaInfoList = mutableListOf<JavaFieldMetaInfo>()

        // 遍历类中的所有字段
        clazz.declaredFields.forEach { field ->
            val fieldName = field.name
            val fieldType = field.type
            val genericType = field.genericType as? Class<*>

            // 查找字段上的注解
            val comment = getCommentFun(field)


            // 将属性信息添加到列表中
            fieldMetaInfoList.add(
                JavaFieldMetaInfo(
                    name = fieldName,
                    type = fieldType,
                    genericType = genericType ?: fieldType,
                    comment = comment
                )
            )
        }

        return fieldMetaInfoList
    }







}