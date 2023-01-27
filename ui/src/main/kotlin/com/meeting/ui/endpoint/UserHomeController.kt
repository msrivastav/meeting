package com.meeting.ui.endpoint

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
class UserHomeController {

    @PostMapping("/userHome")
    @GetMapping("/userHome")
    fun getUserHomePage1(auth: Authentication): String {
        log.info(auth.name)
        return "userHome"
    }

    @GetMapping("/userHome")
    fun getUserHomePage2(auth: Authentication): String {
        log.info(auth.name)
        return "userHome"
    }

    // @PostMapping("/userHome")
    fun getUserHomePage(
        @RequestBody req: String,
        result: BindingResult,
        model: Model
    ): String {
        req.split("&").forEach(log::info)
        log.info(result.toString())
        log.info(model.asMap().toString())

        val token = req.split("&").filter { it.contains("credential") }.map { it.split("=") }.map { it[1] }[0]
        log.info("ttkknn: $token")

        val idToken: GoogleIdToken = verifier.verify(token)
        if (idToken != null) {
            val payload: GoogleIdToken.Payload = idToken.payload

            // Print user identifier
            val userId: String = payload.subject
            println("User ID: $userId")

            // Get profile information from payload
            val email: String = payload.email
            val emailVerified: Boolean = java.lang.Boolean.valueOf(payload.emailVerified)
            val name = payload["name"] as String
            val pictureUrl = payload["picture"] as String
            // val locale = payload["locale"] as String
            // val familyName = payload["family_name"] as String
            // val givenName = payload["given_name"] as String

            log.info(payload.toString())
        } else {
            println("Invalid ID token.")
        }
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(null, null)
        return "userHome"
    }

    private companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)

        private val jsonFactory = GsonFactory.getDefaultInstance()
        private val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

        val verifier: GoogleIdTokenVerifier =
            GoogleIdTokenVerifier.Builder(
                httpTransport,
                jsonFactory
            )
                .setAudience(Collections.singletonList("974211070499-360t350151qkbd38r7pa5r62ikq3ffrs.apps.googleusercontent.com"))
                .build()
    }
}
