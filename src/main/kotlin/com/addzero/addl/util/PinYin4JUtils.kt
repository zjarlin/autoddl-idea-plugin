package com.addzero.addl.util

import com.addzero.addl.util.PinYin4JUtils.getHeadByString
import com.addzero.addl.util.PinYin4JUtils.stringToPinyin
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination

/**
 * PinYin4j工具类
 */
object PinYin4JUtils {
    /**
     * 将字符串转换成拼音数组
     *
     * @param src       字符串
     * @param separator 多音字拼音之间的分隔符
     * @return
     */
    fun stringToPinyin(src: String?, separator: String?): Array<String?>? {
        return stringToPinyin(src, true, separator)
    }

    /**
     * 将字符串转换成拼音数组
     *
     * @param src         字符串
     * @param isPolyphone 是否查出多音字的所有拼音
     * @param separator   多音字拼音之间的分隔符
     * @return
     */
    /**
     * 将字符串转换成拼音数组
     *
     * @param src 字符串
     * @return
     */
    @JvmOverloads
    fun stringToPinyin(src: String?, isPolyphone: Boolean = false, separator: String? = null): Array<String?>? {
        // 判断字符串是否为空

        if (src == null) {
            return null
        }

        val srcChar = src.toCharArray()

        val srcCount = srcChar.size

        val srcStr = arrayOfNulls<String>(srcCount)

        for (i in 0 until srcCount) {
            srcStr[i] = charToPinyin(srcChar[i], isPolyphone, separator)
        }

        return srcStr
    }

    /**
     * 将单个字符转换成拼音
     *
     * @param src         被转换的字符
     * @param isPolyphone 是否查出多音字的所有拼音
     * @param separator   多音字拼音之间的分隔符
     * @return
     */
    fun charToPinyin(src: Char, isPolyphone: Boolean, separator: String?): String {
        // 创建汉语拼音处理类

        val defaultFormat = HanyuPinyinOutputFormat()

        // 输出设置，大小写，音标方式
        defaultFormat.caseType = HanyuPinyinCaseType.LOWERCASE

        defaultFormat.toneType = HanyuPinyinToneType.WITHOUT_TONE

        val tempPinying = StringBuffer()

        // 如果是中文
        if (src.code > 128) {
            try {
                // 转换得出结果

                val strs = PinyinHelper.toHanyuPinyinStringArray(src, defaultFormat)

                // 是否查出多音字，默认是查出多音字的第一个字符
                if (isPolyphone && null != separator) {
                    for (i in strs.indices) {
                        tempPinying.append(strs[i])

                        if (strs.size != (i + 1)) {
                            // 多音字之间用特殊符号间隔起来

                            tempPinying.append(separator)
                        }
                    }
                } else {
                    tempPinying.append(strs[0])
                }
            } catch (e: BadHanyuPinyinOutputFormatCombination) {
                e.printStackTrace()
            }
        } else {
            tempPinying.append(src)
        }

        return tempPinying.toString()
    }

    /**
     * 将汉字转换成拼音
     *
     * @param hanzi
     * @return
     */
    fun hanziToPinyin(hanzi: String): String {
        return hanziToPinyin(hanzi, " ")
    }

    /**
     * 将汉字转换成拼音
     *
     * @param hanzi     汉字
     * @param separator 分隔符
     * @return
     */
    fun hanziToPinyin(hanzi: String, separator: String?): String {
        // 创建汉语拼音处理类

        val defaultFormat = HanyuPinyinOutputFormat()

        // 输出设置，大小写，音标方式
        defaultFormat.caseType = HanyuPinyinCaseType.LOWERCASE

        defaultFormat.toneType = HanyuPinyinToneType.WITHOUT_TONE

        var pinyingStr = ""

        try {
            pinyingStr = PinyinHelper.toHanYuPinyinString(hanzi, defaultFormat, separator, true)
        } catch (e: BadHanyuPinyinOutputFormatCombination) {
            // TODO Auto-generated catch block

            e.printStackTrace()
        }

        return pinyingStr
    }

    /**
     * 将字符串数组转换成字符串
     *
     * @param str
     * @param separator 各个字符串之间的分隔符
     * @return
     */
    /**
     * 简单的将各个字符数组之间连接起来
     *
     * @param str
     * @return
     */
    @JvmOverloads
    fun stringArrayToString(str: Array<String?>, separator: String? = ""): String {
        val sb = StringBuffer()

        for (i in str.indices) {
            sb.append(str[i])

            if (str.size != (i + 1)) {
                sb.append(separator)
            }
        }

        return sb.toString()
    }

    /**
     * 将字符数组转换成字符串
     *
     * @param ch        字符数组
     * @param separator 各个字符串之间的分隔符
     * @return
     */
    /**
     * 将字符数组转换成字符串
     *
     * @param ch 字符数组
     * @return
     */
    @JvmOverloads
    fun charArrayToString(ch: CharArray, separator: String? = " "): String {
        val sb = StringBuffer()

        for (i in ch.indices) {
            sb.append(ch[i])

            if (ch.size != (i + 1)) {
                sb.append(separator)
            }
        }

        return sb.toString()
    }

    /**
     * 取汉字的首字母
     *
     * @param src
     * @param isCapital 是否是大写
     * @return
     */
    fun getHeadByChar(src: Char, isCapital: Boolean): CharArray {
        // 如果不是汉字直接返回

        if (src.code <= 128) {
            return charArrayOf(src)
        }

        // 获取所有的拼音
        val pinyingStr = PinyinHelper.toHanyuPinyinStringArray(src)

        // 创建返回对象
        val polyphoneSize = pinyingStr.size

        val headChars = CharArray(polyphoneSize)

        var i = 0

        // 截取首字符
        for (s in pinyingStr) {
            val headChar = s[0]

            // 首字母是否大写，默认是小写
            if (isCapital) {
                headChars[i] = headChar.uppercaseChar()
            } else {
                headChars[i] = headChar
            }

            i++
        }

        return headChars
    }

    /**
     * 取汉字的首字母(默认是大写)
     *
     * @param src
     * @return
     */
    fun getHeadByChar(src: Char): CharArray {
        return getHeadByChar(src, true)
    }

    /**
     * 查找字符串首字母
     *
     * @param src
     * @return
     */
    fun getHeadByString(src: String): Array<String?> {
        return getHeadByString(src, true)
    }

    /**
     * 查找字符串首字母
     *
     * @param src
     * @param isCapital 是否大写
     * @return
     */
    fun getHeadByString(src: String, isCapital: Boolean): Array<String?> {
        return getHeadByString(src, isCapital, null)
    }

    /**
     * 查找字符串首字母
     *
     * @param src       汉字字符串
     * @param isCapital 是否大写
     * @param separator 分隔符
     * @return
     */
    fun getHeadByString(src: String, isCapital: Boolean, separator: String?): Array<String?> {
        val chars = src.toCharArray()

        val headString = arrayOfNulls<String>(chars.size)

        var i = 0

        for (ch in chars) {
            val chs = getHeadByChar(ch, isCapital)

            val sb = StringBuffer()

            if (null != separator) {
                var j = 1

                for (ch1 in chs) {
                    sb.append(ch1)

                    if (j != chs.size) {
                        sb.append(separator)
                    }

                    j++
                }
            } else {
                sb.append(chs[0])
            }

            headString[i] = sb.toString()

            i++
        }

        return headString
    }


}

fun main(args: Array<String>) {
    // pin4j 简码 和 城市编码

    val s1 = "中华人民共和国"

    val headArray = getHeadByString(s1) // 获得每个汉字拼音首字母

    println(headArray.contentToString())

    val s2 = "长城"

    println(stringToPinyin(s2, true, ",").contentToString())

    val s3 = "长"

    println(stringToPinyin(s3, true, ",").contentToString())
}