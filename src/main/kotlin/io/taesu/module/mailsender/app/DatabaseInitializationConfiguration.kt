package io.taesu.module.mailsender.app

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.util.StreamUtils
import java.nio.charset.Charset

/**
 * Created by itaesu on 2021/04/02.
 *
 * @author Lee Tae Su
 * @version 0.1
 * @since 0.1
 */
@Configuration(proxyBeanMethods = false)
class DatabaseInitializationConfiguration {
    @Autowired
    fun initializeDatabase(r2dbcEntityTemplate: R2dbcEntityTemplate) {
        val schema = StreamUtils.copyToString(
            ClassPathResource("schema.sql").inputStream,
            Charset.defaultCharset()
        )
        r2dbcEntityTemplate.databaseClient.sql(schema).fetch().rowsUpdated().block()
    }
}