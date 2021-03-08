package io.taesu.module.mailsender.email.application

import io.taesu.module.mailsender.app.utils.response
import io.taesu.module.mailsender.email.Email
import io.taesu.module.mailsender.email.api.EmailSendRequest
import io.taesu.module.mailsender.email.api.EmailSendResponse
import io.taesu.module.mailsender.email.api.InlineFile
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.ses.SesAsyncClient
import software.amazon.awssdk.services.ses.model.RawMessage
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.util.*
import javax.mail.Session
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * Created by itaesu on 2021/03/06.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Component
class EmailSendService(private val builder: EmailMessageBuilder,
                       private val sesAsyncClient: SesAsyncClient) {

    fun send(request: EmailSendRequest, inlineFiles: List<InlineFile>): Mono<EmailSendResponse> {
        val email = Email(
                sender = "no-reply@crscube.io",
                subject = request.subject,
                content = request.content)

        return Mono.fromFuture(this.sesAsyncClient.sendRawEmail(getSendEmailRequest(email.sender, request, inlineFiles)))
                .map {
                    EmailSendResponse(
                            sentAt = LocalDateTime.now().response(),
                            messageId = it.messageId())
                }
    }

    fun getSendEmailRequest(sender: String, request: EmailSendRequest, inlineFiles: List<InlineFile>): SendRawEmailRequest {
        val rawMessage = try {
            ByteArrayOutputStream().use {
                val session = Session.getDefaultInstance(Properties())
                val message = MimeMessage(session)
                builder.buildMimeMessage(message, sender, request, inlineFiles)

                message.writeTo(it)
                RawMessage.builder().data(SdkBytes.fromByteArray(it.toByteArray())).build()
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e.message, e)
        }

        return SendRawEmailRequest
                .builder()
                .destinations(request.recipients)
                .rawMessage(rawMessage)
                .source(sender).build()
    }
}

@Component
class EmailMessageBuilder {
    fun buildMimeMessage(message: MimeMessage, sender: String, request: EmailSendRequest, inlineFiles: List<InlineFile>) {
        with(MimeMessageHelper(message, true, "UTF-8")) {
            setSubject(request.subject)
            setText(request.content, true)
            setFrom(sender)
            setTo(request.recipients.map {
                try {
                    return@map InternetAddress(it)
                } catch (e: AddressException) {
                    // log.error("address is not valid {}", e.ref, e)
                    return@map null
                }
            }.filterNotNull().toTypedArray())

            inlineFiles.forEach { addInline(it.contentId, it.path.toFile()) }
        }
    }
}