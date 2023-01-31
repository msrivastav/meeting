package com.meeting.ui.endpoint

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class MeetingScheduleController {

    @GetMapping("/schedule/meeting")
    fun getScheduleMeeting(
        auth: Authentication,
        model: Model
    ): String {
        model.addAttribute("user", auth.name)
        model.addAttribute("callOptions", mapOf(1 to "Google Meet", 2 to "Zoom", 3 to "Microsoft Teams"))
        model.addAttribute("descTemplates", mapOf(1 to "Catchup", 2 to "Short Meeting", 3 to "1 : 1", 4 to "All Day"))
        return "scheduleMeeting"
    }

    @PostMapping("/schedule/meeting/attendeeSuggestion")
    @ResponseBody
    fun getAttendeeSuggestion(
        @RequestBody str: String,
        auth: Authentication,
        model: Model
    ): String {
        return "manoo, arpita"
    }

    @PostMapping("/schedule/meeting/recommendation")
    @ResponseBody
    fun getScheduleRecommendation(
        @RequestBody str: String,
        auth: Authentication,
        model: Model
    ): String {
        return "rec 1, rec 2"
    }

    @PostMapping("/schedule/setup/meeting")
    @ResponseBody
    fun setupMeeting(
        @RequestBody str: String,
        auth: Authentication,
        model: Model
    ): String {
        return "Meeting all setup"
    }
}
