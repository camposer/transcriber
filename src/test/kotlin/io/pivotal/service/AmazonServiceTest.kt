package io.pivotal.service

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.transcribe.AmazonTranscribe
import com.amazonaws.services.transcribe.model.LanguageCode
import com.amazonaws.services.transcribe.model.Media
import com.amazonaws.services.transcribe.model.MediaFormat
import com.amazonaws.services.transcribe.model.StartTranscriptionJobRequest
import io.pivotal.transcriber.service.AmazonService
import io.pivotal.transcriber.service.UuidService
import io.pivotal.util.emptyMultipartFile
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.io.File
import java.lang.Exception

@ExtendWith(MockitoExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AmazonServiceTest {
    companion object {
        const val EMPTY_FILE_NAME = ".empty"
        const val ENDPOINT_URL = "endpointUrl"
        const val BUCKET_NAME = "bucketName"
    }

    private val url = "$ENDPOINT_URL/$BUCKET_NAME/$EMPTY_FILE_NAME"

    @Mock
    lateinit var uuidService: UuidService
    @Mock
    lateinit var s3Client: AmazonS3Client
    @Mock
    lateinit var transcribeCLient: AmazonTranscribe

    @InjectMocks
    lateinit var amazonService: AmazonService

    @BeforeEach
    fun setUp() {
        amazonService.endpointUrl = ENDPOINT_URL
        amazonService.bucketName = BUCKET_NAME
    }

    @Nested
    inner class UploadFile {
        private val emptyFile = emptyMultipartFile()

        @Test
        fun `return a url`() {
            val expected = url
            val actual = amazonService.uploadFile(emptyFile)
            assertEquals(expected, actual)
            verify(s3Client, times(1)).putObject(eq(BUCKET_NAME), eq(EMPTY_FILE_NAME), any(File::class.java))
        }

        @Test
        fun `throw exception when s3Client fails`() {
            `when`(s3Client.putObject(eq(BUCKET_NAME), eq(EMPTY_FILE_NAME), any(File::class.java))).thenThrow()
            assertThrows(Exception::class.java) {
                amazonService.uploadFile(emptyFile)
            }
        }
    }

    @Nested
    inner class StartFileTranscription {
        @Test
        fun `return uuid`() {
            val uuid = "uuid"
            `when`(uuidService.randomUuidString()).thenReturn(uuid)
            val expected = "$uuid"
            val actual = amazonService.startFileTranscription(url)
            assertEquals(expected, actual)
            verify(transcribeCLient, times(1)).startTranscriptionJob(
                StartTranscriptionJobRequest()
                    .withTranscriptionJobName(uuid)
                    .withLanguageCode(LanguageCode.EnGB)
                    .withMedia(media(url))
                    .withMediaFormat(MediaFormat.Mp3.toString())
                    .withMediaSampleRateHertz(AmazonService.MEDIA_SAMPLE_RATE_HERTZ)
            )
        }

        @Test
        fun `throw exception when transcribeClient fails`() {
            `when`(transcribeCLient.startTranscriptionJob(any(StartTranscriptionJobRequest::class.java))).thenThrow()
            assertThrows(Exception::class.java) {
                amazonService.startFileTranscription(url)
            }
        }

        private fun media(url: String): Media {
            val media = Media()
            media.mediaFileUri = url
            return media
        }
    }
}