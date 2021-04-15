package io.taesu.module.mailsender.app

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver
import org.springframework.web.server.i18n.LocaleContextResolver


/**
 * Created by itaesu on 2021/03/06.
 *
 * @author Lee Tae Su
 * @version 0.1
 * @since 0.1
 */
@Configuration
class AppConfig {
    @Bean
    fun localeContextResolver(): LocaleContextResolver = AcceptHeaderLocaleContextResolver()
}

