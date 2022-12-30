/*
package com.meeting.zoom.integration

import com.meeting.ProtoUser
import com.meeting.ProtoUserRecommendationRequest
import com.meeting.ProtoUserRecommendationResponse
import com.meeting.UserRecommendationServiceGrpcKt.UserRecommendationServiceCoroutineStub
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.prometheus.client.CollectorRegistry
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import kotlin.test.assertEquals


@SpringBootTest(properties = ["grpc.port=6000"])
@Testcontainers
class UserRecommendationGrpcIntegrationTest {

    @MockBean
    private lateinit var collectorRegistry: CollectorRegistry

    @Test
    fun `User recommendation service returns expected result successfully`() {
        val request = ProtoUserRecommendationRequest.newBuilder().setUserIdPrefix("ma").build()

        val expectedResult = ProtoUserRecommendationResponse.newBuilder()
            .addUsers(
                ProtoUser.newBuilder()
                    .apply { userEmailId = "manoo.srivastav" }
                    .apply { isExternal = true }
                    .build())
            .build()
        val actualResult = runBlocking { stub.recommendUsers(request) }

        assertEquals(expectedResult, actualResult)
    }

    companion object {
        private const val DATABASE_USERNAME = "user"
        private const val DATABASE_PASSWORD = "password"
        private const val DATABASE_NAME = "identity"

        private const val GRPC_PORT = 6000
        private lateinit var channel: ManagedChannel
        lateinit var stub: UserRecommendationServiceCoroutineStub

        @Container
        @JvmStatic
        private val mysqlContainer =
            MySQLContainer(DockerImageName.parse("mysql:latest"))
                .withUsername(DATABASE_USERNAME)
                .withPassword(DATABASE_PASSWORD)
                .withDatabaseName(DATABASE_NAME)
                .withInitScript("sql/setup-mysql.sql")

        @DynamicPropertySource
        @JvmStatic
        fun injectDatabaseProperties(registry: DynamicPropertyRegistry) {
            val mysqlDatabaseUrl =
                "jdbc:mysql://" + mysqlContainer.host + ":" + mysqlContainer.firstMappedPort + "/" + DATABASE_NAME
            registry.add("spring.datasource.url") { mysqlDatabaseUrl }
            registry.add("spring.datasource.username") { DATABASE_USERNAME }
            registry.add("spring.datasource.password") { DATABASE_PASSWORD }
        }

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            channel = ManagedChannelBuilder.forAddress("localhost", GRPC_PORT).usePlaintext().build()
            stub = UserRecommendationServiceCoroutineStub(channel)
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            channel.shutdown()
        }
    }
}*/
