package com.example.quick_reply.data.remote.error

import android.content.res.Resources
import com.example.quick_reply.R
import com.example.quick_reply.data.entity.response.ApiResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException

interface ApiErrorMapper {

    fun mapError(throwable: Throwable): ApiError
}

class ApiErrorMapperImpl(
    private val resources: Resources
) : ApiErrorMapper {

    override fun mapError(throwable: Throwable): ApiError {
        val apiResponse = getError(throwable, ApiResponse::class.java)
        return ApiError(message = apiResponse.message ?: ErrorType.UNKNOWN.message, code = apiResponse.code ?: ErrorType.UNKNOWN.code)
    }

    private fun <P : ApiResponse<*>> getError(throwable: Throwable, clazz: Class<P>): P {
        when (throwable) {
            is UnknownHostException, is SocketTimeoutException, is IOException -> {
                return getError(ErrorType.NETWORK.code, resources.getString(R.string.zla_network_error_message), clazz)
            }
            is JsonSyntaxException -> {
                return getError(ErrorType.DATA.code, resources.getString(R.string.zla_error_message), clazz)
            }
            is HttpException -> {
                val httpCode: Int = throwable.code()
                return try {
                    getErrorFromHttpException(throwable, clazz)
                } catch (e: Exception) {
                    Timber.e(e)
                    if (httpCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        getError(ErrorType.HTTP.code, resources.getString(R.string.zla_error_message), clazz)
                    } else {
                        getError(ErrorType.HTTP.code, resources.getString(R.string.zla_error_message), clazz)
                    }
                }
            }
            else -> {
                return getError(ErrorType.HTTP.code, resources.getString(R.string.zla_error_message), clazz)
            }
        }
    }

    private fun <P : ApiResponse<*>> getError(code: Int, message: String, clazz: Class<P>): P {
        val gson = Gson()
        val body = gson.toJson(ApiResponse<Any>(success = false, message = message, code = code))
        return body.getApiErrorOrNull(clazz) ?: getDefaultError(clazz)
    }

    private fun <P : ApiResponse<*>> getDefaultError(clazz: Class<P>): P {
        val gson = Gson()
        val body = gson.toJson(ApiResponse<Any>(success = false, message = ErrorType.UNKNOWN.message, code = ErrorType.UNKNOWN.code))
        return body.getApiErrorOrNull(clazz)!!
    }

    private fun <P : ApiResponse<*>> getErrorFromHttpException(throwable: HttpException, clazz: Class<P>): P {
        val errorBody: String? = throwable.response()?.errorBody()?.string()
        return errorBody?.getApiErrorOrNull(clazz) ?: getDefaultError(clazz)
    }

    private fun <T : ApiResponse<*>> String.getApiErrorOrNull(clazz: Class<T>): T? =
        try {
            Gson().fromJson(this, clazz)
        } catch (exception: Exception) {
            Timber.e(exception)
            null
        }
}