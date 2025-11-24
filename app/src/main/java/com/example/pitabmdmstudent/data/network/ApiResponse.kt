package com.example.pitabmdmstudent.data.network

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: T,
    @SerializedName("message")
    val message: String,
    @SerializedName("error")
    val error: Error? = null,
    @SerializedName("paginate")
    var paginate: PagingResponse? = null,
    @SerializedName("statusCode")
    var statusCode : String = "",
)

data class PagingResponse(
    @SerializedName("totalCount") var totalCount: Int? = null,
    @SerializedName("totalResolved") var totalResolved: Int? = null,
    @SerializedName("totalPending") var totalPending: Int? = null,
    @SerializedName("limit") var limit: Int? = null,
    @SerializedName("page") var page: Int? = null
)