package simulation

import java.io.File

import io.gatling.core.Predef._
import io.gatling.core.session.Session
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import io.netty.handler.codec.http.HttpHeaderNames._
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

import scala.io.Source

class OauthSimulation extends Simulation {

  //val eventServiceUrl = "http://int.hmhone.app.hmhco.com/api/eventservice/v1.1/caliper/events"
  val eventServiceUrl = "http://localhost:8080"

  val payload: String = {
    val path = getClass.getResource("/test.json").getPath
    Source.fromFile(new File(path)).getLines.mkString("")
  }

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(eventServiceUrl)
    .acceptHeader("*/*")
    .connectionHeader("keep-alive")
    .contentTypeHeader("application/json")
    .disableCaching
    .acceptEncodingHeader("gzip, deflate")

  def timedOut(tokenTime: Long): Boolean = {
    System.currentTimeMillis() - tokenTime > 3555
  }

  def getNewTestToken: String = {
    val url = "https://api.dev.eng.hmhco.com/token-service/api/v1/token"
    val creds = "928b4ad8-31d7-424f-95e1-2eb43c9bb527" + ":" + "test"
    val encoding = java.util.Base64.getEncoder.encodeToString(creds.getBytes())

    val post = new HttpPost(url)
    post.addHeader("Content-Type", "application/x-www-form-urlencoded")
    post.addHeader(AUTHORIZATION.toString, "Basic " + encoding)
    post.addHeader("accept-encoding", "gzip, deflate")
    post.setEntity(new StringEntity("grant_type=client_credentials&scope=eventservice.write"))

    val entity = HttpClientBuilder.create().build().execute(post).getEntity
    val bearer = "Bearer ".concat(compact(render(parse(scala.io.Source.fromInputStream(entity.getContent).mkString) \ "access_token")).replace("\"",""))
    println(bearer)
    bearer
  }

  def getAndStore(session: Session): Session = {
    val timeout = session.attributes.get("timeout")

    if (timeout.isDefined && !timedOut(timeout.get.asInstanceOf[Long])) {
      session.set("timeout", timeout.get)
    } else {
      session.set("timeout", System.currentTimeMillis()).set("authHeader", getNewTestToken)
    }
  }

  val scn: ScenarioBuilder = scenario("BasicSimulation")
    .exec { session => getAndStore(session) }
    .exec(
      http("request_1")
        .post("/v1.1/caliper/events")
        .body(StringBody(payload))
        .headers(Map("Authorization" -> "${authHeader}"))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
