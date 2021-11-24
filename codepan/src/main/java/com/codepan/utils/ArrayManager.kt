package com.codepan.utils

class ListManager<T>(val itemList: ArrayList<T>) {

    fun removeDuplicate(): ArrayList<T> {
        val holder = arrayListOf<T>()
        for (item in itemList) {
            if (!holder.contains(item)) {
                holder.add(item)
            }
        }
        itemList.clear()
        itemList.addAll(holder)
        return itemList
    }

    fun getDuplicate(): T? {
        val holder = arrayListOf<T>()
        for (item in itemList) {
            if (holder.contains(item)) {
                return item
            } else {
                holder.add(item)
            }
        }
        return null
    }

    companion object {
        fun <T> fromArray(array: Array<T>): ListManager<T> {
            return ListManager(arrayListOf(*array))
        }

        fun <T> fromArgs(vararg args: T): ListManager<T> {
            return ListManager(arrayListOf(*args))
        }
    }
}