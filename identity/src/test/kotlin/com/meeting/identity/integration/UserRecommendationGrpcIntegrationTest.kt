package com.meeting.identity.integration

import com.meeting.*
import com.meeting.UserRecommendationServiceGrpc.UserRecommendationServiceBlockingStub
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.prometheus.client.CollectorRegistry
import kotlinx.coroutines.runBlocking
import org.grpcmock.GrpcMock.stubFor
import org.grpcmock.GrpcMock.unaryMethod
import org.grpcmock.springboot.AutoConfigureGrpcMock
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import kotlin.test.assertTrue

@SpringBootTest(properties = ["grpc.port=6000"])
@Testcontainers
@AutoConfigureGrpcMock(port = 6001)
class UserRecommendationGrpcIntegrationTest(@Value("\${grpcmock.server.port}") private val grpcMockPort: Int) {

    @MockBean
    private lateinit var collectorRegistry: CollectorRegistry

    private lateinit var grpcMockChannel: ManagedChannel

    @BeforeEach
    fun beforeEach() {
        grpcMockChannel = ManagedChannelBuilder.forAddress("localhost", grpcMockPort)
            .usePlaintext()
            .build()
    }

    @AfterEach
    fun afterEach() {
        grpcMockChannel.shutdown()
    }

    @Test
    fun `User recommendation service returns expected result successfully`() {
        val request = ProtoUserRecommendationRequest.newBuilder()
            .apply {
                orgId = 1
                namePart = "ma"
            }.build()

        // stub connector with grpc mock
        stubFor(
            unaryMethod(UserRecommendationServiceGrpcKt.recommendUsersMethod)
                // .withRequest(request)
                .willReturn(
                    ProtoUserRecommendationResponse.newBuilder()
                        .addUsers(
                            ProtoUser.newBuilder().setEmail("manoos@meetingworks.app").setIsExternal(false).build()
                        )
                        .build()
                )
        )

        val expectedResult = ProtoUserRecommendationResponse.newBuilder()
            .addUsers(
                ProtoUser.newBuilder()
                    .apply {
                        email = "manoo.srivastav@gmail.com"
                        isExternal = true
                    }
                    .build()
            )
            .addUsers(
                ProtoUser.newBuilder()
                    .apply {
                        email = "manoos@meetingworks.app"
                        isExternal = false
                    }
                    .build()
            )
            .build()

        val actualResult = runBlocking { stub.recommendUsers(request) }
        assertTrue { expectedResult.usersList.containsAll(actualResult.usersList) }
        assertTrue { actualResult.usersList.containsAll(expectedResult.usersList) }
        assertTrue { expectedResult.usersList.size == actualResult.usersList.size }
    }

    companion object {
        private const val DATABASE_USERNAME = "user"
        private const val DATABASE_PASSWORD = "password"
        private const val DATABASE_NAME = "identity"

        private const val GRPC_PORT = 6000
        private lateinit var channel: ManagedChannel
        lateinit var stub: UserRecommendationServiceBlockingStub

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
        fun injectProperties(registry: DynamicPropertyRegistry) {
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
            stub = UserRecommendationServiceGrpc.newBlockingStub(channel)
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            channel.shutdown()
        }
    }
}
