package com.meeting.ui.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
class SecurityConfig {

    private val indexPage = "/index"
    private val userHome = "/userHome"

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =

        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(AntPathRequestMatcher.antMatcher("/actuator/**"))
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .formLogin {
                it.loginPage(indexPage)
                    .defaultSuccessUrl(userHome)
                    .permitAll()
            }
            .oauth2Login {
                it
                    .loginPage(indexPage)
                    .defaultSuccessUrl(userHome)
                    .permitAll()
                    .redirectionEndpoint { t -> t.baseUri("/oauth2/land/*") }
            }
            .logout {
                it.logoutUrl("/logout")
                    .logoutSuccessUrl(indexPage)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .clearAuthentication(true)
            }
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
}
