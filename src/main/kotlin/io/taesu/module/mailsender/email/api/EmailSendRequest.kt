package io.taesu.module.mailsender.email.api

import org.springframework.http.codec.multipart.FilePart
import java.nio.file.Path

/**
 * Created by itaesu on 2021/03/06.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
class EmailSendRequest(
    val subject: String,
    val content: String,
    val to: Set<String>,
    cc: Set<String>?,
    bcc: Set<String>?,
    val logo: FilePart?,
    logoContentId: String?,
    val background: FilePart?,
    backgroundContentId: String?) {

    val cc = cc ?: emptySet()
    val bcc = bcc ?: emptySet()

    val logoContentId: String = logoContentId ?: "logo.png"
    val backgroundContentId: String = backgroundContentId ?: "background.png"
}

class InlineFile(val path: Path, val contentId: String)

class EmailSendResponse(
    val emailKey: Long,
    val sentAt: String,
    val messageId: String
)