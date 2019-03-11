package io.pivotal.transcriber.controller

import io.pivotal.transcriber.service.AmazonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transcriptions")
class TranscriptionController {
    @Autowired
    lateinit var amazonService: AmazonService

    @PostMapping
    fun post(@RequestBody request: TranscriptionPostRequest): TranscriptionPostResponse {
        val result = amazonService.startFileTranscription(request.url)
        return TranscriptionPostResponse("")
    }
}

data class TranscriptionPostRequest(val url: String)
data class TranscriptionPostResponse(val name: String)