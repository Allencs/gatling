import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._


class WsGatling extends Simulation {

  val Protocol = http
    .wsBaseUrl("ws://123.207.167.163:9010")
//    .wsBaseUrl("ws://10.203.29.217:8085")


  val myCheck = ws.checkTextMessage("check")
                  .check(regex(".*").saveAs("res"))

  val scenario_ws = scenario("WebSocket")
    .exec(ws("Connect")
        .connect("/ajaxchattest")
//      .connect("/spring_websocket/websocket")
        .onConnected(
          exec(ws("SendMsg")
          .sendText("HelloWebSocket")
          .await(0.5 seconds)(myCheck))
          )
        )

    .exec{session =>
      println("response: " + session("res").as[String])
    session
  }.pause(1 minutes)



    exec(ws("Close").close)

  setUp(
    scenario_ws.inject(atOnceUsers(50))

  ).protocols(Protocol)

}
