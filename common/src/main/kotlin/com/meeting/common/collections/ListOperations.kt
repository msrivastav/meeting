package com.meeting.common.collections

inline fun <reified K> List<K>.combine(otherList: List<K>): List<K> {
    return mutableListOf(*this.toTypedArray())
        .also { it.addAll(otherList) }
        .toList()
}
