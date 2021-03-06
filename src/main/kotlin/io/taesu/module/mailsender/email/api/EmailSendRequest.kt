package io.taesu.module.mailsender.email.api

import org.springframework.http.codec.multipart.FilePart
import java.io.File

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
        val recipients: List<Recipient> = listOf(Recipient("taesu@crscube.co.kr")),
        val logo: FilePart? = null,
        val logoContentId: String? = null
){
    var blockedLogo: File? = null
}

class Recipient(val emailAddress: String)

class EmailSendResponse(
        val sentAt: String,
        val messageId: String
)