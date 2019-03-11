package io.pivotal.transcriber.config

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.transcribe.AmazonTranscribe
import com.amazonaws.services.transcribe.AmazonTranscribeClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsConfig {
    @Value("\${amazonProperties.region}")
    lateinit var region: String

    @Bean
    fun awsCredentialsProvider(
        @Value("\${amazonProperties.accessKey}") accessKey: String,
        @Value("\${amazonProperties.secretKey}") secretKey: String
    ): AWSCredentialsProvider {
        val credentials = BasicAWSCredentials(accessKey, secretKey)
        return AWSStaticCredentialsProvider(credentials)
    }

    @Bean
    fun s3Client(awsCredentialsProvider: AWSCredentialsProvider): AmazonS3 {
        return AmazonS3ClientBuilder.standard()
            .withCredentials(awsCredentialsProvider)
            .withRegion(this.region)
            .build()
    }

    @Bean
    fun transcribeClient(awsCredentialsProvider: AWSCredentialsProvider): AmazonTranscribe {
        return AmazonTranscribeClientBuilder.standard()
            .withCredentials(awsCredentialsProvider)
            .withRegion(this.region)
            .build()
    }
}