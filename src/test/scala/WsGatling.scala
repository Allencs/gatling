import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random


class WsGatling extends Simulation {

  val Protocol = http
//    .wsBaseUrl("ws://123.207.167.163:9010")
//    .wsBaseUrl("ws://10.203.29.217:8085")
    .wsBaseUrl("ws://localhost:8088")

  val feeder = Iterator.continually(Map("userId" -> (Random.alphanumeric.take(3).mkString)))

  val myCheck = ws.checkTextMessage("check")
                  .check(regex(".*").saveAs("res"))

  val scenario_ws = scenario("WebSocket")
    .exec(feed(feeder))
    .exec(ws("Connect")
        .connect("/websocket/${userId}")
        .await(0.2 seconds)(myCheck)
//      .connect("/spring_websocket/websocket/${userId}")
        .onConnected(
          exec(ws("SendMsg")
          .sendText("it's from gatling")
          .await(0.5 seconds)(myCheck))
          )
        )

    .exec{session =>
      println("response: " + session("res").as[String])
    session
  }.pause(0.5 minutes)

    exec(ws("Close").close)

  setUp(
    scenario_ws.inject(rampUsers(50) during(25 seconds))

  ).protocols(Protocol)

}
