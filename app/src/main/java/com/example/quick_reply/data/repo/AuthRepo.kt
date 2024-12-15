package com.example.quick_reply.data.repo

import com.example.quick_reply.data.datastate.convertToDataState
import com.example.quick_reply.data.entity.request.RegisterRequest
import com.example.quick_reply.data.remote.MainApiService
import com.example.quick_reply.data.remote.error.ApiErrorMapper
import kotlinx.coroutines.flow.flow

class AuthRepo(
    private val mainApiService: MainApiService,
    private val apiErrorMapper: ApiErrorMapper
) {

    fun register(request: RegisterRequest) = flow {
        emit(mainApiService.register(request))
    }.convertToDataState(apiErrorMapper)
}