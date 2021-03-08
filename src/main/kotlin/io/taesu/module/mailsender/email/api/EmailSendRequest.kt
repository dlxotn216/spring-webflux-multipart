package io.taesu.module.mailsender.email.api

import org.springframework.http.codec.multipart.FilePart
import java.io.File
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
        val recipients: List<String>,
        val logo: FilePart,
        val logoContentId: String,
        val backgroundLogo: FilePart,
        val backgroundContentId: String)

class InlineFile(val path: Path, val contentId: String)

class EmailSendResponse(
        val sentAt: String,
        val messageId: String
)