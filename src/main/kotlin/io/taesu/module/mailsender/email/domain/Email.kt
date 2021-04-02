package io.taesu.module.mailsender.email

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.time.LocalDateTime

/**
 * Created by itaesu on 2021/03/06.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Table("MSG_EMAIL")
data class Email(
    @Id
    @Column("EMAIL_KEY")    // 설정 해줘야 저장 후에 ID가 set 된다. MappingR2dbcConverter#populateIdIfNecessary
    val key: Long = 0L,
    @Column("MSG_ID")
    val messageId: String,
    @Column("SENDER")
    val sender: String,
    @Column("SUBJECT")
    val subject: String,
    @Column("CONTENT")
    val content: String,
    @Column("SENT_AT")
    val sentAt: LocalDateTime
) : Persistable<Long> {

    // Persistable 인터페이스 구현하여 새로운 엔티티인지 구별해 줄 수 있음
    // key를 nullable 하게 주지 않아도 좋음.
    override fun getId(): Long = key

    override fun isNew(): Boolean = key == 0L

}

class Recipient(val emailAddress: String)

interface EmailRepository : ReactiveCrudRepository<Email, Long>