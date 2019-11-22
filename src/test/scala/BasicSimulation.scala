package src.test.scala

import java.util.concurrent.TimeUnit
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._


class BasicSimulation extends Simulation{

  var _token:String = ""

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


  def token_= (token:String): Unit = {
    _token = token
  }

  object Token{
    val get_token = exec(http("token")
      .get("/pftest/myApi/token")
      .check(bodyString.saveAs("token"),
             status.is(200).saveAs("isOK"),
             header("Content-Type").saveAs("header"))  // 保存请求响应到参数
    )
      .exec{session =>
        /*session是虚拟用户的状态,包含键字符串的映射Map[String, Any]
         */
//        println("session:" + session)

        token_=(session("token").as[String])
//        println("token value is: " + session("token").as[String])
//        println("header: " + session("header").as[String])  // 打印头信息
        session
      }
  }

  object PersonInfo{

    var headers = Map("Behavior" -> "PerformanceTest",
                      "Position" -> "IT",
                      "access-token" -> "${token}",
                      "Content-Type" -> "application/json")

    val get_info = doIf(session => session("isOK").as[Int].equals(200)){
      exec(
        http("personInfo")
        .post("/pftest/myApi/personInfo")
        .headers(headers)
        .body(StringBody("""{"username": "GoodBoy", "pw": "root123"}""")).asJson
        .check(bodyString.saveAs("personInfo"),
               substring("Python"))
      )
        .exec{session =>
//          println(session("personInfo").as[String])
          session
        }

    }
  }


  val test_myApi = scenario("myApi").during(Duration.apply(30, SECONDS)){
    exec(Token.get_token, PersonInfo.get_info)  //, PersonInfo.get_info
  }

  before{
    println("Simulation is about to start!")
  }

  setUp(
    test_myApi.inject(rampUsers(2) during(2 seconds))
    /*注入用户模式：
      atOnceUsers
      rampUsers
      constantUsersPerSec
      rampUsersPerSec
      heavisideUsers
      nothingFor
      incrementUsersPerSec
     */
  ).protocols(httpProtocol)
      .assertions(
        global.responseTime.max.lt(300),
        details("token").successfulRequests.percent.gt(99)
      )

  after {
    println("Simulation is finished!")
}

}
/*说明：
incrementUsersPerSec(incrementUsersPerSec)
  .times(numberOfSteps)
  .eachLevelLasting(levelDuration)
  .separatedByRampsLasting(rampDuration)
  .startingFrom(initialUsersPerSec)

[Inject a succession of numberOfSteps levels each one
during levelDuration and increasing the number of users
per sec by incrementUsersPerSec starting from zero or the
optional initialUsersPerSec and separated by optional ramps lasting rampDuration]


atOnceUsers
[Injects a specific number of users at the same time]


rampUsers
[Injects a given number of users with a linear ramp during a given duration]


constantUsersPerSec
[Injects users at a constant rate, defined in users per second, during a given duration]
constantUsersPerSec(nbUsers) during(dur unit)
Example:
constantUsersPerSec(10) during(5 seconds)



rampUsersPerSec
[Injects users from starting rate to target rate,
defined in users per second, during a given duration]

rampUsersPerSec(rate1) to (rate2) during(dur unit)
Example:
rampUsersPerSec(10) to(20) during(10 minutes)


heavisideUsers
[Injects a given number of users following a smooth approximation stretched to a duration]
heavisideUsers(nbUsers) during(dur unit)


nothingFor
[Pauses for a specific duration]
nothingFor(dur unit)
 */
