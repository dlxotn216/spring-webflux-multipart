package io.taesu.module.mailsender.email.api

import io.taesu.module.mailsender.email.application.EmailSendService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.fromSupplier
import java.nio.file.Files
import java.nio.file.Files.createFile


/**
 * Created by itaesu on 2021/03/06.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Controller
class EmailSendingController(val service: EmailSendService) {
    @ResponseBody
    @PostMapping("/api/v1/emails")
    fun processModel(@ModelAttribute request: EmailSendRequest): Mono<EmailSendResponse> {
        return fromSupplier { Files.createTempDirectory("logo") }
                .flatMapMany {
                    val logo = with(request) {
                        fromSupplier { InlineFile(createFile(it.resolve(logoContentId)), logoContentId) }
                                .flatMap { logo.transferTo(it.path).thenReturn(it) }
                    }
                    val backgroundLogo = with(request) {
                        fromSupplier { InlineFile(createFile(it.resolve(backgroundContentId)), backgroundContentId) }
                                .flatMap { backgroundLogo.transferTo(it.path).thenReturn(it) }
                    }
                    logo.mergeWith(backgroundLogo)
                }
                .collectList()
                .flatMap { service.send(request, it) }
    }
}