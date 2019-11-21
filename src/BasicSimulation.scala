import java.util.concurrent.TimeUnit
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._


class BasicSimulation extends Simulation{

  val httpProtocol = http
    .baseUrl("http://192.168.3.13:8080")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  /*
  val scenario_one = scenario("BasicSimulation").during(Duration.apply(5, SECONDS)){
    exec(http("token")
        .get("/pftest/myApi/token").check(bodyString.saveAs("token"),
      status.is(200), header("Content-Type").saveAs("header"))  // 保存请求响应到参数
    )
      .exec{session =>
        /*session是虚拟用户的状态,包含键字符串的映射Map[String, Any]
         */
//        println("session:" + session)
        token = session("token").as[String]
        println("token value is: " + token)
        println("header: " + session("header").as[String])
        session
      }


  }

   */

  object Token{
    val get_token = exec(http("token")
      .get("/pftest/myApi/token")
      .check(bodyString.saveAs("token"),
             status.is(200),
             header("Content-Type").saveAs("header"))  // 保存请求响应到参数
    )
      .exec{session =>
        /*session是虚拟用户的状态,包含键字符串的映射Map[String, Any]
         */
//        println("session:" + session)
        println("token value is: " + session("token").as[String])
        println("header: " + session("header").as[String])  // 打印头信息
        session
      }
  }

  object PersonInfo{

    var headers = Map("Behavior" -> "PerformanceTest",
                      "Position" -> "IT",
                      "access-token" -> "${token}",
                      "Content-Type" -> "application/json")

    val get_info = exec(
      http("personInfo")
        .post("/pftest/myApi/personInfo")
        .headers(headers)
        .body(StringBody("""{"username": "GoodBoy", "pw": "root123"}""")).asJson
        .check(bodyString.saveAs("personInfo"))
    )
      .exec{session =>
        println(session("personInfo").as[String])
        session
      }

  }


  val test_myApi = scenario("personInfo").exec(Token.get_token, PersonInfo.get_info)

  setUp(
    test_myApi.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
