package com.gymondo.xrciser.data

data class PagedResult<T> (
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)