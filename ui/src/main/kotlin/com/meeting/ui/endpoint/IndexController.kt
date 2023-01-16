package com.meeting.ui.endpoint

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class IndexController {

    @GetMapping("/index")
    fun getIndexPage(model: Model) = "index"

    @PostMapping("/index")
    fun getIndexPageRed(model: Model) = "index"
}
