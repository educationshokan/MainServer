import com.educationShokan.database.Database
import com.educationShokan.extensions.resourceFile
import com.educationShokan.main
import com.educationShokan.models.FileAddReq
import com.educationShokan.models.FileUploadReq
import com.educationShokan.models.ProjectReq
import com.educationShokan.models.ProjectUpdate
import com.educationShokan.persistence.MediaRepository
import com.educationShokan.persistence.ProjectRepository
import io.ktor.application.Application
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.response.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.contentType
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import java.nio.file.Files
import java.nio.file.Path

typealias Response = Map<String, Any>

@Suppress("UNCHECKED_CAST")
class EndpointTests {

    companion object {

        lateinit var server: NettyApplicationEngine
        lateinit var client: HttpClient
        const val url = "http://localhost:8080"

        @BeforeAll @JvmStatic
        fun init() {
            val exists = Files.exists("/".resourceFile.toPath())
            if (!exists) {
                listOf("/storage", "/deploy").forEach {
                    Files.createDirectories(it.resourceFile.toPath())
                }
            }
            server = embeddedServer(Netty, 8080, module = Application::main).start(wait = false)
            client = HttpClient {
                install(JsonFeature) {
                    serializer = JacksonSerializer()
                }
            }
            Database
            Files.walk("".resourceFile.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach { it.delete() }
            runBlocking {
                MediaRepository.wipe()
                ProjectRepository.wipe()
            }
            listOf("/storage", "/deploy").forEach {
                Files.createDirectories(it.resourceFile.toPath())
            }
        }

        @AfterEach
        fun clean() {
            runBlocking {
                ProjectRepository.wipe()
                MediaRepository.wipe()
                Files.walk("".resourceFile.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach { it.delete() }
            }
        }

    }

    @Test
    fun project() = runBlocking {
        var response: Response = client.get("$url/project")
        Assertions.assertEquals("success", response["status"])
        var projects = (response["data"] as? List<String>)!!
        Assertions.assertNotNull(projects)
        Assertions.assertEquals(0, projects.size)
        var status = client.post<HttpResponse>("$url/project") {
            contentType(ContentType.Application.Json)
            body = ProjectReq("Test project", null, null)
        }.status
        Assertions.assertEquals(HttpStatusCode.Created, status)
        response = client.get("$url/project")
        Assertions.assertEquals("success", response["status"])
        projects = (response["data"] as? List<String>)!!
        Assertions.assertNotNull(projects)
        Assertions.assertEquals(1, projects.size)
        val projectId = projects.first()
        response = client.get("$url/project/$projectId")
        Assertions.assertEquals("success", response["status"])
        val project = (response["data"] as? Map<String, String>)!!
        Assertions.assertNotNull(project)
        Assertions.assertEquals("Test project", project["name"])
        status = client.put<HttpResponse>("$url/project/$projectId") {
            contentType(ContentType.Application.Json)
            body = ProjectUpdate("New name", "A nice desc", null)
        }.status
        Assertions.assertEquals(HttpStatusCode.NoContent, status)
        status = client.delete<HttpResponse>("$url/project/$projectId").status
        Assertions.assertEquals(HttpStatusCode.NoContent, status)
    }

    @Test
    fun projectAndFile() = runBlocking {
        var location = client.post<HttpResponse>("$url/project/") {
            contentType(ContentType.Application.Json)
            body = ProjectReq("Files project", null, null)
        }.headers["Location"].toString()
        var response = client.get<Response>("$url$location")
        Assertions.assertEquals("success", response["status"])
        var project = (response["data"] as? Map<String, String>)!!
        Assertions.assertNotNull(project)
        location = client.post<HttpResponse>("$url/media") {
            contentType(ContentType.Application.Json)
            body = FileUploadReq("testing.txt", "A test file")
        }.headers["Location"].toString()
        val bytes = "This is a test string\nPendulum Shokan.".toByteArray()
        var status = client.post<HttpResponse>("$url$location") {
            body = ByteArrayContent(bytes, ContentType.Text.Plain)
        }.status
        Assertions.assertEquals(HttpStatusCode.Created, status)
        val fileId = location.split("/").last()
        status = client.put<HttpResponse>("$url/project/${project["id"]}/addFile") {
            contentType(ContentType.Application.Json)
            body = FileAddReq(fileId, null)
        }.status
        Assertions.assertEquals(HttpStatusCode.NoContent, status)
        response = client.get("$url/project/${project["id"]}")
        Assertions.assertEquals("success", response["status"])
        project = (response["data"] as? Map<String, String>)!!
        Assertions.assertTrue(fileId in (project["files"] as List<String>))
    }



}