package io.pivotal.controller

import com.beust.klaxon.Klaxon
import io.pivotal.transcriber.Application
import io.pivotal.transcriber.controller.AudioPostResponse
import io.pivotal.transcriber.service.AmazonService
import io.pivotal.util.emptyMultipartFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [ Application::class ])
@AutoConfigureMockMvc
class AudioControllerTest {
    @MockBean
    lateinit var amazonService: AmazonService

    @Autowired
    lateinit var mvc: MockMvc

    @Nested
    inner class Post {
        fun `upload audio`() {

        }
    }

    @Test
    fun post_uploadAudio() {
        val url = "url"
        val emptyFile = emptyMultipartFile()
        `when`(amazonService.uploadFile(emptyFile)).thenReturn(url)

        val response = mvc.perform(
                multipart("/audios").file(emptyFile)
            ).andReturn().response

        assertNotNull(response)
        assertEquals(response.status, HttpStatus.OK.value())
        assertEquals(Klaxon().parse<AudioPostResponse>(response.contentAsString), AudioPostResponse(url))
    }
}