import cn.hutool.core.util.ReflectUtil
import cn.hutool.http.HttpRequest
import com.addzero.addl.FieldDTO
import com.addzero.addl.FormDTO
import com.addzero.addl.ktututil.parseObject
import com.addzero.addl.ktututil.toJson
import com.addzero.addl.util.Dba
import com.addzero.addl.util.JlStrUtil.extractCodeBlockContent
import com.addzero.addl.util.fieldinfo.getSimpleFieldInfoStr


fun buildStructureOutPutPrompt(clazz: Class<*>?): String {
    if (clazz == null) {
        return ""
    }
    val fieldInfosRecursive = getSimpleFieldInfoStr(clazz)
    // 收集所有字段及其描述
    val fieldDescriptions = StringBuilder()
    val fields = ReflectUtil.getFields(clazz)
    // 过滤带有 @field:JsonPropertyDescription 注解的字段
    val ret = emptyList<String>()
    // 返回生成的描述信息
    val prompt = """
        结构化输出字段定义 内容如下:
        $fieldInfosRecursive
    """.trimIndent()
    return prompt
}
data class Qwendto(
  val model: String,
val   messages: List<MyMessage>,
)



data class MyMessage(
    val role: String="",
    val   content: String=""
)

fun getResponse(question: String, prompt: String): String? {

    // 读取环境变量中的API Key
    val name = "DASHSCOPE_API_KEY"
    val apiKey = System.getenv(name) ?: "your_api_key_here"  // 替换成你的API Key
    val baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions"
    // 构建请求内容
    val qwendto = Qwendto("qwen-max", listOf(MyMessage("system", prompt), MyMessage("user", question)))
    val toJson = qwendto.toJson()


//    val requestBody = """
//        {
//          "model": "$model",
//          "messages": [
//            {"role": "system", "content": "$prompt"},
//            {"role": "user", "content": "$question"}
//          ]
//        }
//    """.trimIndent()

    // 发送POST请求，包含Authorization和Content-Type头
    val response = HttpRequest.post(baseUrl)
        .header("Authorization", "Bearer $apiKey")  // 设置Authorization头
        .header("Content-Type", "application/json")  // 设置Content-Type头
        .body(toJson)  // 设置请求体
        .execute()
    // 返回响应内容
    return response.body()
}

private fun dbask(question: String): String? {
    val role = "你是一个 DBA 工程师，负责设计表。请根据我的内容,输出结构化的json数据,区分大小写,没有偏差"

    val trimIndent1 = """
        结构化输出字段定义 内容如下:
        -----------
        ${buildStructureOutPutPrompt(FormDTO::class.java)}
    """.trimIndent()


    val trimIndent = """
      期望最终返回的结果,即: 结构化的json数据格式如下,最终结果移除开头```json,和结尾```没有偏差 
        ------------
   {
  "tableName": "",
  "tableEnglishName": "",
  "dbType": "",
  "dbName": "",
  "fields": [
    {
      "javaType": "",
      "fieldName": "",
      "fieldChineseName": ""
    }
  ]
} 
""".trimIndent()


    val   promtTempla="""
        $role
     $trimIndent1
     ${trimIndent}
 """.trimIndent()
    val response = getResponse(
        question,
       promtTempla
    )
    return response
}

 fun quesDba(string: String): FormDTO? {
    try {
        val dbask = dbask(string)
        val parseObject = dbask?.parseObject(Dba::class.java)

        val joinToString = parseObject?.choices?.map {
            val content = it?.message?.content
            content
        }?.joinToString()
        val let = joinToString?.let { extractCodeBlockContent(it) }
        val parseObject1 = let?.parseObject(FormDTO::class.java)
        return parseObject1
    } catch (e: Exception) {
        return defaultdTO()
    }
}

fun defaultdTO(): FormDTO {
    val fieldDTO = FieldDTO("String", "字段名", "字段注释")
    return FormDTO("示例表名", "示例英文名", "示例数据库类型", "示例数据库名称", listOf(fieldDTO))
}