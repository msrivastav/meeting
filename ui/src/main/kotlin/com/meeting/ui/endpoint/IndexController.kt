package com.meeting.ui.endpoint

import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class IndexController(val clientRegistrationRepository: ClientRegistrationRepository) {

    private val authorizationRequestBaseUri = "/oauth2/authorization"

    @GetMapping("/")
    fun getBaseLandPage() = "redirect:userHome"

    @GetMapping("/index")
    fun getIndexPage(model: Model): String {
        val map = HashMap<String, String>()

        (clientRegistrationRepository as Iterable<ClientRegistration>)
            .forEach {
                map[it.clientName] = authorizationRequestBaseUri.plus("/").plus(it.registrationId)
            }

        model.addAttribute("urls", map)

        return "index"
    }

    @PostMapping("/index")
    fun getIndexPageRed(model: Model) = "index"
}
