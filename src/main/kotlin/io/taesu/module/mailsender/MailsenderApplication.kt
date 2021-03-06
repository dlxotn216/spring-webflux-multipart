package io.taesu.module.mailsender

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MailsenderApplication

fun main(args: Array<String>) {
    runApplication<MailsenderApplication>(*args)
}
