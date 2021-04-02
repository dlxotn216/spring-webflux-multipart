package io.taesu.module.mailsender

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import java.util.*
import javax.annotation.PostConstruct

@ConfigurationPropertiesScan
@SpringBootApplication
class MailsenderApplication {
    @PostConstruct
    fun onConstruct() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }
}

fun main(args: Array<String>) {
    runApplication<MailsenderApplication>(*args)
}
