package io.taesu.module.mailsender.email.api

import io.taesu.module.mailsender.app.API_V1
import io.taesu.module.mailsender.email.application.EmailSendService
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.fromSupplier
import java.nio.file.Files
import java.nio.file.Files.createFile
import java.util.*


/**
 * Created by itaesu on 2021/03/06.
 *
 * @author Lee Tae Su
 * @version 0.1
 * @since 0.1
 */
@Controller
class EmailSendingController(val service: EmailSendService) {
    @ResponseBody
    @PostMapping("$API_V1/emails")
    fun processModel(@ModelAttribute request: EmailSendRequest,
                     locale: Locale): Mono<EmailSendResponse> {
        val tempDirectoryMono = fromSupplier { Files.createTempDirectory("logo") }
        val inlineFilesMono = tempDirectoryMono.flatMapMany {
            val logo = with(request) {
                fromSupplier { InlineFile(createFile(it.resolve(logoContentId)), logoContentId) }
                    .flatMap { getLogoMono(it) }
            }
            val backgroundLogo = with(request) {
                fromSupplier { InlineFile(createFile(it.resolve(backgroundContentId)), backgroundContentId) }
                    .flatMap { getBackgroundMono(it) }
            }
            logo.mergeWith(backgroundLogo)
        }.collectList()
        return inlineFilesMono.flatMap { service.send(request, it) }
    }

    private fun EmailSendRequest.getLogoMono(inlineFile: InlineFile): Mono<InlineFile> {
        return if (logo == null || logo.filename().isBlank()) {
            Mono.just(InlineFile(ClassPathResource("img/logo.png").file.toPath(), logoContentId))
        } else {
            logo.transferTo(inlineFile.path).thenReturn(inlineFile)
        }
    }

    private fun EmailSendRequest.getBackgroundMono(inlineFile: InlineFile): Mono<InlineFile> {
        return if (background == null || background.filename().isNullOrBlank()) {
            Mono.just(InlineFile(ClassPathResource("img/background.png").file.toPath(), backgroundContentId))
        } else {
            background.transferTo(inlineFile.path).thenReturn(inlineFile)
        }
    }
}