import java.util.concurrent.TimeUnit

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._


class BasicSimulation extends Simulation{

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn = scenario("BasicSimulation").during(Duration.apply(60, SECONDS)){
    exec(http("token")
        .get("/pftest/myApi/token")

    )
  }

//    .exec(http("token")
//      .get("/pftest/myApi/token"))
//    .pause(Duration.apply(5, TimeUnit.MILLISECONDS))

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
