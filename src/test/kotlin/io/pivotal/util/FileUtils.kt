package io.pivotal.util

import io.pivotal.service.AmazonServiceTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import java.io.File
import java.io.FileOutputStream


fun emptyMultipartFile(): MockMultipartFile {
    val emptyFile = touchFile()
    return MockMultipartFile("file", AmazonServiceTest.EMPTY_FILE_NAME, MediaType.TEXT_PLAIN_VALUE, emptyFile.inputStream())
}

fun touchFile(): File {
    val file = File(AmazonServiceTest.EMPTY_FILE_NAME)
    if (!file.exists())
        FileOutputStream(file).close();
    file.setLastModified(System.currentTimeMillis())
    return file
}