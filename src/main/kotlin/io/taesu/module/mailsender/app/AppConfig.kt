package io.taesu.module.mailsender.app

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesAsyncClient

/**
 * Created by itaesu on 2021/03/06.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
class AppConfig {
}

@Configuration
@ConfigurationProperties("app.config.aws.sdk")
class AwsSDKConfig {
    lateinit var region: String
    lateinit var profile: String

    @Bean
    fun sesAsyncClient(): SesAsyncClient {
        return SesAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(ProfileCredentialsProvider.builder()
                        .profileName(profile)
                        .build()).build()
    }
}