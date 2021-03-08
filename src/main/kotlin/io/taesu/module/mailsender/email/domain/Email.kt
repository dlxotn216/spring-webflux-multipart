package io.taesu.module.mailsender.email

/**
 * Created by itaesu on 2021/03/06.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
class Email(
        val sender: String,
        val subject: String,
        val content: String
)

class Recipient(val emailAddress: String)
