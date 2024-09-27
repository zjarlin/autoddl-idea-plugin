package com.addzero.addl.autoddlstarter.tools

import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Function
import java.util.stream.Collectors

object DuplicateSuffixAdder {
    fun handleDuplicatesWithStreamAndCustomRemap(inputList: List<String>): List<String> {
        val duplicateMapper =
            BiFunction { baseStr: String, count: Int ->
                baseStr + (if (count > 0) "_" + String.format(
                    "%02d",
                    count
                ) else "")
            }
        val seen: MutableMap<String, Int> = LinkedHashMap()

        return inputList.stream()
            .map { item: String ->
                val count = seen.merge(item, 1) { a: Int?, b: Int? ->
                    Integer.sum(
                        a!!, b!!
                    )
                }!! - 1
                if (count == 0) item else duplicateMapper.apply(item, count)
            }
            .collect(Collectors.toList())
    }

    fun <T> handleDuplicatesWithStreamAndCustomRemap(
        inputList: List<T>,
        getFun: Function<T, String>,
        setfun: BiConsumer<T, String?>
    ): List<T> {
        val duplicateMapper =
            BiFunction { baseStr: String, count: Int ->
                baseStr + (if (count > 0) "_" + String.format(
                    "%02d",
                    count
                ) else "")
            }
        return handleDuplicatesWithStreamAndCustomRemap(inputList, getFun, duplicateMapper, setfun)
    }

    fun <T> handleDuplicatesWithStreamAndCustomRemap(
        inputList: List<T>, keyExtractor: Function<T, String>,
        bifunction: BiFunction<String, Int, String>,
        setfun: BiConsumer<T, String?>
    ): List<T> {
        // 使用LinkedHashMap保持插入顺序

        val seen: MutableMap<String, Int> = LinkedHashMap()

        val collect = inputList.stream()
            .map<T> { item: T ->
                val key = keyExtractor.apply(item)
                // 获取当前key的计数，如果没有则为0
                val count = seen.compute(key) { k: String?, v: Int? -> if (v == null) 1 else v + 1 }!! - 1
                // 根据是否是重复项选择不同的映射逻辑
                if (count == 0) {
                    return@map item
                }
                val apply = bifunction.apply(key, count)

                setfun.accept(item, apply)
                item
            }
            .collect(Collectors.toList<T>())
        return collect
    }

    internal class Item(var name: String) {
        companion object {
            @JvmStatic
            fun main(args: Array<String>) {
                val list: List<String> = mutableListOf("A", "A", "C")
                val strings = handleDuplicatesWithStreamAndCustomRemap(list)
                println(strings)
            }
        }
    }
}