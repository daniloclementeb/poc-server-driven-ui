package com.danilo.poc.data

import com.danilo.data.api.ApiAdapter


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    suspend fun login(username: String, password: String): Result<Map<String,Object>> {
        var mapa = HashMap<String, String>()
        mapa.put("cpf", username)
        mapa.put("password", password)
        val response = ApiAdapter().apiClient.login(mapa)
        if (response.isSuccessful)
            return Result.Success(response.body())
        else
            return  Result.Error(response.body())

        /*try {
                // TODO: handle loggedInUser authentication
                val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
                return Result.Success(fakeUser)
            } catch (e: Throwable) {
                return Result.Error(IOException("Error logging in", e))
            }*/
    }

    fun logout() {
        // TODO: revoke authentication
    }
}