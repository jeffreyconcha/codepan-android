package com.codepan.utils


typealias Creator<K, V> = (key: K) -> V


class CacheManager<K, V>(private val creator: Creator<K, V>) {

    private var map = hashMapOf<K, V>();

    fun add(key: K, value: V) {
        map[key] = value;
    }

    fun getCached(key: K): V? {
        if (map.containsKey(key)) {
            return map[key];
        }
        val value = creator.invoke(key)
        map[key] = value;
        return value;
    }
}