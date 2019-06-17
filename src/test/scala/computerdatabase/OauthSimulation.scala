package computerdatabase

import java.io.File

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.io.Source

class OauthSimulation extends Simulation {

  //val eventServiceUrl = "http://int.hmhone.app.hmhco.com/api/eventservice/v1.1/caliper/events"
  val eventServiceUrl = "http://localhost:8080/v1.1/caliper/events"

  val payload: String = {
    val path = getClass.getResource("/test.json").getPath
    println(path)
    val fileString = Source.fromFile(new File(path)).getLines.mkString("")
    println(fileString)
    fileString
  }

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(eventServiceUrl)
    .acceptHeader("*/*")
    .authorizationHeader("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI5MjhiNGFkOC0zMWQ3LTQyNGYtOTVlMS0yZWI0M2M5YmI1MjciLCJzY29wZSI6ImV2ZW50c2VydmljZS53cml0ZSIsImlzcyI6Imh0dHBzOlwvXC93d3cuaG1oY28uY29tIiwiZXhwIjoxNTYwNzg1NzA1LCJpYXQiOjE1NjA3ODIxMDUsImp0aSI6ImI0OGFiZDBmLTkwNjMtNDJkNC1hZmIyLTRjZTIxNmI4NjZiNyJ9.2OxtHoQ4vKHpKmGkIho9z17opuJr9Vi71IsYwVys_SFopchoWaXJoa8GV3U6W6gg7rDVupuv0XxTWtQsSmnQl88HZaiQROJkJMsATDqF6IPH5Bxo1QBsu3JEKMomboGz5nThwmE-i8c5kq4C8USAeQVHjJO-p3lojgcaXDDsz-yVVLwZ2-qSDwajWP99J5oaEVk1HhsOxNYet7rVYLrGFS30Z1CvDJvcj-kmGvwRSSRgh819oYyozdVWZY0Z_3L6pCV-XyYmkxWp0L9I2LyDHJqMMeWu9gWAMQBcPfHUWXbPhB1yELF2T9ZiYuGKNPFvrR8T_gqvFdEFYPe98ZN3kA")
    .connectionHeader("keep-alive")
    .contentTypeHeader("application/json")
    .disableCaching
    .acceptEncodingHeader("gzip, deflate")

  val scn: ScenarioBuilder = scenario("BasicSimulation")
    .exec()
    .exec(
      http("request_1")
        .post("/")
        .body(StringBody(payload))
    )
  // .pause(5)

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
