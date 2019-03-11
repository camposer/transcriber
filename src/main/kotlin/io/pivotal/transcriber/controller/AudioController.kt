package io.pivotal.transcriber.controller

import io.pivotal.transcriber.service.AmazonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("audios")
class AudioController {
    @Autowired
    lateinit var amazonService: AmazonService

    @PostMapping
    fun post(@RequestParam("file") multipartFile: MultipartFile): AudioPostResponse {
        val url = amazonService.uploadFile(multipartFile)
        return AudioPostResponse(url)
    }
}

data class AudioPostResponse(val url: String)