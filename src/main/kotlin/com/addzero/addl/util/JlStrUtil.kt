package com.addzero.addl.util

import cn.hutool.core.util.StrUtil
import cn.hutool.extra.pinyin.PinyinUtil
import java.util.*

/**
 * @author zjarlin
 * @since 2023/3/17 13:12
 */
object JlStrUtil {


    fun makeSurroundWith(s: String, fix: String): String {
        // 如果 s 为空或全是空白字符，则直接返回空字符串
        if (s.isBlank()) {
            return ""
        }
        val s1 = StrUtil.addPrefixIfNot(s, fix)
        val s2 = StrUtil.addSuffixIfNot(s1, fix)
        return s2
    }

    fun removeNotChinese(str: String): String {
        if (StrUtil.isBlank(str)) {
            return ""
        }
        val regex = "[^\u4E00-\u9FA5]"
        val s1 = str.replace(regex.toRegex(), "")
        return s1
    }

    /**
     * 优化表名
     * @param tableEnglishName
     * @param tableChineseName
     * @return [String]
     */
    fun shortEng(tableEnglishName: String, tableChineseName: String?): String {
        var tableEnglishName = tableEnglishName
        if (StrUtil.length(tableEnglishName) > 15) {
            tableEnglishName = PinyinUtil.getFirstLetter(tableChineseName, "")
        }
        tableEnglishName = StrUtil.removeAny(tableEnglishName, "(", ")")
        tableEnglishName = tableEnglishName.replace("\\((.*?)\\)".toRegex(), "") // 移除括号及其内容
        tableEnglishName = tableEnglishName.replace("(_{2,})".toRegex(), "_") // 移除连续的下划线
        return tableEnglishName
    }

    /**
     * 删除多余符号
     * @param [source]
     * @param [duplicateElement]
     * @return [String?]
     */
    fun removeDuplicateSymbol(source: String, duplicateElement: String): String? {
        if (Objects.isNull(source) || source.isEmpty() || Objects.isNull(duplicateElement) || duplicateElement.isEmpty()) {
            return source
        }

        val sb = StringBuilder()
        var previous = "" // 初始化前一个元素，用于比较

        var i = 0
        while (i < source.length) {
            val elementLength = duplicateElement.length
            if (i + elementLength <= source.length && source.substring(i, i + elementLength) == duplicateElement) {
                if (previous != duplicateElement) {
                    sb.append(duplicateElement)
                    previous = duplicateElement
                }
                i += elementLength
            } else {
                sb.append(source[i])
                previous = source[i].toString()
                i++
            }
        }

        return sb.toString()
    }

    fun extractCodeBlockContent(markdown: String): String {
        val regex = Regex("```\\w*\\s*(.*?)\\s*```", RegexOption.DOT_MATCHES_ALL)
        val matchResult = regex.find(markdown)
        return matchResult?.groups?.get(1)?.value?.trim() ?: ""
    }


}