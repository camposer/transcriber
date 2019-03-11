package io.pivotal.transcriber.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.transcribe.AmazonTranscribe
import com.amazonaws.services.transcribe.model.LanguageCode
import com.amazonaws.services.transcribe.model.Media
import com.amazonaws.services.transcribe.model.MediaFormat
import com.amazonaws.services.transcribe.model.StartTranscriptionJobRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.util.*


@Service
class AmazonService {
    companion object {
        const val MEDIA_SAMPLE_RATE_HERTZ = 8000
    }

    @Value("\${amazonProperties.endpointUrl}")
    lateinit var endpointUrl: String
    @Value("\${amazonProperties.bucketName}")
    lateinit var bucketName: String

    @Autowired
    lateinit var uuidService: UuidService
    @Autowired
    lateinit var s3Client: AmazonS3
    @Autowired
    lateinit var transcribeClient: AmazonTranscribe


    fun uploadFile(multipartFile: MultipartFile): String {
        val fileName = fileName(multipartFile)
        val file = multipartFileToFile(multipartFile)
        this.s3Client.putObject(bucketName, fileName, file)
        return "$endpointUrl/$bucketName/$fileName"
    }

    // TODO Extract transcriber config parameters to be passed by the user
    fun startFileTranscription(url: String): String {
        val uuid = uuidService.randomUuidString()
        val transcriptionRequest = StartTranscriptionJobRequest()
            .withTranscriptionJobName(uuid)
            .withLanguageCode(LanguageCode.EnGB)
            .withMedia(media(url))
            .withMediaFormat(MediaFormat.Mp3.toString())
            .withMediaSampleRateHertz(MEDIA_SAMPLE_RATE_HERTZ)
        transcribeClient.startTranscriptionJob(transcriptionRequest)
        return uuid
    }

    private fun fileName(multipartFile: MultipartFile): String {
        return multipartFile.originalFilename.replace(" ", "_")
    }

    private fun multipartFileToFile(multipartFile: MultipartFile): File {
        val file = File(multipartFile.originalFilename)
        FileOutputStream(file).use { w -> w.write(multipartFile.bytes) }
        return file
    }

    private fun media(url: String): Media {
        val media = Media()
        media.mediaFileUri = url
        return media
    }

}