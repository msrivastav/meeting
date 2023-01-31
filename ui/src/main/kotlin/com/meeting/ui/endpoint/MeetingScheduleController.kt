package com.meeting.ui.endpoint

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class MeetingScheduleController {

    @GetMapping("/schedule/meeting")
    fun getScheduleMeeting(
        auth: Authentication,
        model: Model
    ): String {
        model.addAttribute("user", auth.name)
        return "scheduleMeeting"
    }

    @PostMapping("/schedule/meeting/attendeeSuggestion")
    @ResponseBody
    fun getAttendeeSuggestion(
        @RequestBody str: String,
        auth: Authentication,
        model: Model
    ): String {
        model.addAttribute("user", auth.name)
        return "manoo, arpita"
    }
}
