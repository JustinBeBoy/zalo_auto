package com.example.quick_reply.data.datastate

sealed class DataState<T> {

    data class Result<T>(val result: T) : DataState<T>()
    data class Loading<T>(val partialData: T? = null) : DataState<T>()
    data class Error<T>(val throwable: Throwable) : DataState<T>()

    val extractData: T?
        get() = when (this) {
            is Result -> result
            is Loading -> partialData
            is Error -> null
        }

    fun <Y> mapData(transform: (T) -> Y): DataState<Y> = try {
        when (this) {
            is Result<T> -> Result(transform(result))
            is Loading<T> -> Loading(partialData?.let { transform(it) })
            is Error<T> -> Error(throwable)
        }
    } catch (e: Throwable) {
        Error(e)
    }

    fun onResult(onResult: (result: T) -> Unit): DataState<T> {
        if (this is Result) onResult(result)
        return this
    }

    fun onLoading(onLoading: (partialData: T?) -> Unit): DataState<T> {
        if (this is Loading) onLoading(partialData)
        return this
    }

    fun onError(onError: (throwable: Throwable) -> Unit): DataState<T> {
        if (this is Error) onError(throwable)
        return this
    }

    fun isLoading() = this is Loading
}