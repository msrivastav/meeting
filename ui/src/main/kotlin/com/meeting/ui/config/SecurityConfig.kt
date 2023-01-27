package com.meeting.ui.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =

        http
            .authorizeHttpRequests {
                it.anyRequest().authenticated()
            }
            .formLogin {
                it.loginPage("/index").defaultSuccessUrl("/userHome").permitAll()
            }
/*            .oauth2Login {
                it.loginPage("/index").clientRegistrationRepository(googleClientRegistration())
            }*/
            .build()

    @Bean
    fun users(): UserDetailsService {
        val users = User.withDefaultPasswordEncoder()
        val user = users
            .username("user")
            .password("pass")
            .roles("USER")
            .build()
        val admin = users
            .username("admin")
            .password("pass")
            .roles("USER", "ADMIN")
            .build()
        return InMemoryUserDetailsManager(user, admin)
    }

    private fun googleClientRegistration() =
        InMemoryClientRegistrationRepository(
            ClientRegistration.withRegistrationId("google")
                .clientId("974211070499-360t350151qkbd38r7pa5r62ikq3ffrs.apps.googleusercontent.com")
                .clientSecret("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")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/userHome")
                .scope("openid", "profile", "email", "address", "phone")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .clientName("Google")
                .build()
        )
}
