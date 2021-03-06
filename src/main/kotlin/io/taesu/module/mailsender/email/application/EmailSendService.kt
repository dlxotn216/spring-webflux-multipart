package io.taesu.module.mailsender.email.application

import io.taesu.module.mailsender.app.utils.response
import io.taesu.module.mailsender.email.Email
import io.taesu.module.mailsender.email.EmailRepository
import io.taesu.module.mailsender.email.api.EmailSendRequest
import io.taesu.module.mailsender.email.api.EmailSendResponse
import io.taesu.module.mailsender.email.api.InlineFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.fromFuture
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.ses.SesAsyncClient
import software.amazon.awssdk.services.ses.model.RawMessage
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.util.*
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * Created by itaesu on 2021/03/06.
 *
 * @author Lee Tae Su
 * @version 0.1
 * @since 0.1
 */
@Component
class EmailSendService(private val config: EmailSendConfiguration,
                       private val builder: EmailMessageBuilder,
                       private val emailRepository: EmailRepository,
                       private val sesAsyncClient: SesAsyncClient) {
    fun send(request: EmailSendRequest, inlineFiles: List<InlineFile>): Mono<EmailSendResponse> {
        return fromFuture(this.sesAsyncClient.sendRawEmail(getSendEmailRequest(config.sender, request, inlineFiles)))
            .flatMap {
                emailRepository.save(Email(sender = config.sender,
                                           subject = request.subject,
                                           content = request.content,
                                           messageId = it.messageId(),
                                           sentAt = LocalDateTime.now()))
            }
            .map { EmailSendResponse(emailKey = it.key, sentAt = it.sentAt.response(), messageId = it.messageId) }
    }

    fun getSendEmailRequest(sender: String,
                            request: EmailSendRequest,
                            inlineFiles: List<InlineFile>): SendRawEmailRequest {
        val rawMessage: RawMessage = try {
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
            .rawMessage(rawMessage)
            .source(sender).build()
    }
}

@Component
class EmailMessageBuilder {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    fun buildMimeMessage(message: MimeMessage,
                         sender: String,
                         request: EmailSendRequest,
                         inlineFiles: List<InlineFile>) {
        with(MimeMessageHelper(message, true, "UTF-8")) {
            setSubject(request.subject)
            setText(request.content, true)
            setFrom(sender)
            setTo(request.to.mapToAddressArray(log))
            setCc(request.cc.mapToAddressArray(log))
            setBcc(request.bcc.mapToAddressArray(log))

            inlineFiles.forEach { addInline(it.contentId, it.path.toFile()) }
        }
    }
}

fun Set<String>.mapToAddressArray(log: Logger) = this.mapNotNull {
    return@mapNotNull try {
        InternetAddress(it, true)
    } catch (e: Exception) {
        log.warn("Invalid email address $it will be ignored.")
        null
    }
}.toTypedArray()

@ConfigurationProperties("app.config")
@ConstructorBinding
class EmailSendConfiguration(val sender: String)