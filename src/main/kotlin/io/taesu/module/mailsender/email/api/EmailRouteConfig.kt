package io.taesu.module.mailsender.email.api

import io.taesu.module.mailsender.email.application.EmailSendService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.io.File


/**
 * Created by itaesu on 2021/03/06.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
// @Configuration
// class EmailRouteConfig {
//     @Bean
//     fun route(sendHandler: EmailSendHandler): RouterFunction<ServerResponse> {
//         return RouterFunctions
//                 .route(RequestPredicates.POST("/api/v1/emails").and(RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA)),
//                         HandlerFunction { sendHandler.send(it) })
//
//     }
// }

@Controller
class EmailSendRequestController(val service: EmailSendService) {
    @ResponseBody
    @PostMapping("/api/v1/emails")
    fun processModel(@ModelAttribute request: EmailSendRequest): Mono<EmailSendResponse> {
        val fileWait = if (request.logoContentId != null && request.logo != null) {
            val file = File.createTempFile("logo", "qwer")
            request.blockedLogo = file
            request.logo.transferTo(file)
        } else {
            Mono.just("nothing")
        }

        return fileWait.flatMap {
            service.send(request)
        }
    }
}