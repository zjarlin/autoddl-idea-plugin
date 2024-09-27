package com.addzero.addl.translator

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.text.CharSequenceUtil
import cn.hutool.core.thread.ThreadUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.extra.pinyin.PinyinUtil
import com.addzero.addl.autoddlstarter.tools.JlStrUtil
import com.alibaba.fastjson.JSON
import groovy.transform.ToString
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors

/**
 * @author zjarlin
 */
object TransUtil {
    private var APP_ID = "20221102001431448"

    private var SECURITY_KEY = "jDe2X9lkQtXnuSuXtd5V"

    /**
     * 翻译成下划线
     *
     * @param chinese 中国人 入参
     * @return [String]
     * @author addzero
     * @since 2022/11/09
     */
    /**
     * 翻译成下划线英文
     *
     * @param chinese    [String] 中文字符串
     * @return [String]
     * @author addzero
     * @since 2022/11/09
     */
    fun chinese2English(chinese: String, consumer: Consumer<String>? = null): String? {
        if (CharSequenceUtil.isBlank(chinese)) {
            return ""
        }
        var apply: String? = null
        try {
            val api = TransApi(APP_ID, SECURITY_KEY)
            val transResult = api.getTransResult(chinese, "auto", "en")
            val transOutVO = JSON.parseObject(transResult, TransOutVO::class.java)

            var transResultVO: List<TransResultDTO>? = transOutVO.getTransResult()
            //这里是百度翻译QPS限制1s访问一次,transResultVO会空
            while (CollUtil.isEmpty(transResultVO)) {
                ThreadUtil.sleep(1, TimeUnit.SECONDS)
                val transResult2 = api.getTransResult(chinese, "auto", "en")
                val transOutVO2 = JSON.parseObject(transResult2, TransOutVO::class.java)
                val transResultVO2: List<TransResultDTO>? = transOutVO2.getTransResult()
                transResultVO = transResultVO2
            }

            val dst = transResultVO?.map{
                val dst = it.getDst()
                consumer?.accept(dst.toString())
                dst
            }
                ?.joinToString("_")
            val let = dst?.let { JlStrUtil.removeDuplicateSymbol(it, "_") }

            apply = let
        } catch (e: Exception) {
            val firstLetter: String = PinyinUtil.getFirstLetter(chinese, "")
            return firstLetter
        }
        return apply
    }

    /**
     * 批量翻译成下划线英文
     *
     * @param chinese 中国人 入参
     * @return [List]<[String]>
     * @author addzero
     * @since 2022/11/09
     */
    fun chinese2English(chinese: List<String>): List<String?> {
        return chinese.stream().map { e: String -> toUnderLine(chinese2English(e)) }.collect(Collectors.toList())
    }

    private fun chinese2EnglishByInputColumn(chinese: String): String {
        return chinese2EnglishByInput(chinese, System.lineSeparator())
    }

    private fun chinese2EnglishByInputRow(chinese: String): String {
        return chinese2EnglishByInput(chinese, "\t")
    }

    private fun chinese2EnglishByInput(chinese: String, lineSeparator: String): String {
        val split: List<String> = StrUtil.split(chinese, lineSeparator)
        val strings = chinese2English(split)
        val collect = strings.stream().collect(Collectors.joining(lineSeparator))
        return collect
    }

    //    public static void main(String[] args) {
    //        String s = "字段名\t类型\t长度,小数点\t是否为主键\t是否自增\t注释\t追加注释\t示例值";
    //        String s1 = "主键\n" +
    //                "创建者\n" +
    //                "创建时间\n" +
    //                "更新者\n" +
    //                "更新时间\n" +
    //                "逻辑删除字段";
    //        String s2 = chinese2EnglishByInputRow(s);
    //        String s3 = chinese2EnglishByInputColumn(s);
    //        System.out.println(s3);
    //
    //    }
    private fun toUpperCase(charSequence: CharSequence): String? {
        val s = toUnderLine(charSequence)
        val s1: String = StrUtil.toCamelCase(s)
        val s2: String = StrUtil.upperFirst(s1)
        return s2
    }

    private fun toCamelCase(charSequence: CharSequence): String? {
        val s = toUnderLine(charSequence)
        return StrUtil.toCamelCase(s)
    }

    private fun toUnderLine(s: CharSequence?): String? {
        val replace: String = StrUtil.replace(s, " ", "_", true)
        val s1: String = StrUtil.toUnderlineCase(replace)
        return s1
    }

}