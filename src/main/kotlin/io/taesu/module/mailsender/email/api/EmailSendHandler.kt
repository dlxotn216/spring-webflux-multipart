package io.taesu.module.mailsender.email.api

import io.taesu.module.mailsender.email.application.EmailSendService
import org.springframework.http.codec.multipart.Part
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono


/**
 * Created by itaesu on 2021/03/06.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Component
class EmailSendHandler(val service: EmailSendService) {
    fun send(request: ServerRequest): Mono<ServerResponse> {
        val body = request.body(BodyExtractors.toMultipartData())
                .flatMap { parts ->
                    val map: Map<String, Part> = parts.toSingleValueMap()
                    Mono.just(EmailSendRequest("title", "afef", listOf(
                            Recipient("taesu@crscube.co.kr"),
                            Recipient("taeaefcube.co.kr"),
                    )))
                }
                .flatMap { service.send(it) }
        return ServerResponse.accepted().body(body, EmailSendResponse::class.java)
    }
}