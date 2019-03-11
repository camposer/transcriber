package io.pivotal.transcriber.service

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UuidService {
    fun randomUuidString(): String {
        return UUID.randomUUID().toString()
    }
}