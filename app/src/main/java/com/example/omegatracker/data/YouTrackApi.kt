package com.example.omegatracker.data

import com.example.omegatracker.entity.User
import com.example.omegatracker.entity.task.TaskFromJson
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Url


interface YouTrackApi {
    //tasks
    @GET("/api/issues/?fields=id,summary,description,idReadable,project(shortName,iconUrl),created,customFields(value(minutes,name),minutes,name,id,projectCustomField(field(value())))")
    suspend fun getTasks(@Header("Authorization") authToken: String?): List<TaskFromJson>

    //id
    @GET("/api/users/me/?fields=id,avatarUrl")
    suspend fun signIn(@Header("Authorization") authToken: String?): User

    @GET("/api/issues/{id}?fields=id,summary,description,project(shortName),created,customFields(value(minutes,name),minutes,iconUrl,name,id,projectCustomField(field(value())))")
    suspend fun readTask(
        @Path("id") taskId: String,
        @Header("Authorization") authToken: String?
    ): TaskFromJson

    @GET
    suspend fun getImageForTask(
        @Url imageUrl: String?,
        @Header("Authorization") authToken: String?
    ): String?
}

