package com.example.quick_reply.data.datastate

import com.example.quick_reply.data.remote.error.ApiErrorMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

private fun <T> Flow<T>.mapResultDataState(): Flow<DataState<T>> = map { DataState.Result(it) }

private fun <T> Flow<T>.mapLoadingDataState(): Flow<DataState<T>> = map { DataState.Loading(it) }

private fun <T> Flow<DataState<T>>.mapErrorDataState(errorMapper: ((Throwable) -> Throwable)? = null) = catch { cause: Throwable ->
    emit(DataState.Error(errorMapper?.invoke(cause) ?: cause))
}

fun <T> Flow<T>.convertToDataState(
    errorMapper: ((Throwable) -> Throwable)? = null,
    partialFlow: Flow<T>? = null,
    ignorePartialFlowException: Boolean = true
) = mapResultDataState()
    .mapErrorDataState(errorMapper)
    .onStart {
        partialFlow?.let { flow ->
            emitAll(
                flow.mapLoadingDataState()
                    .catch {
                        if (!ignorePartialFlowException) throw it
                    }
            )
        } ?: emit(DataState.Loading())
    }

fun <T> Flow<T>.convertToDataState(
    errorMapper: ApiErrorMapper,
    partialFlow: Flow<T>? = null,
    ignorePartialFlowException: Boolean = true
) = convertToDataState(errorMapper::mapError, partialFlow, ignorePartialFlowException)

fun <T, I> Flow<DataState<T>>.mapResult(mapper: (T) -> I): Flow<DataState<I>> = map {
    when (it) {
        is DataState.Result -> DataState.Result(mapper(it.result))
        is DataState.Loading -> DataState.Loading()
        is DataState.Error -> DataState.Error(it.throwable)
    }
}

fun <T> dataState(producer: suspend () -> T): Flow<DataState<T>> {
    return flow {
        emit(producer())
    }.convertToDataState()
}