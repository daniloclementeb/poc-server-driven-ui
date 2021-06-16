package com.example.poc.ui.login

import java.io.Serializable

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
        val displayName: Object?,
        val token: String,
        val cpf: String

        //... other data fields that may be accessible to the UI
): Serializable