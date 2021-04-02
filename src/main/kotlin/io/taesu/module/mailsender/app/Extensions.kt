package io.taesu.module.mailsender.app.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by itaesu on 2021/02/09.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
fun LocalDateTime?.response() =
    this?.let {
        ZonedDateTime.of(this, ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss z"))
    } ?: ""