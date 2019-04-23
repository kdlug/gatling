import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration.DurationInt

import baseConfig.BaseSimulation

class AddPauseTime extends BaseSimulation {
  // 2 Scenario Definition
  val scn = scenario("Video Game DB")
    .exec(http("Get all video games - 1st call")
    .get("videogames"))
    .pause(5) //will pause script for 5sec

    .exec(http("Get specific game")
    .get("videogames/1"))
    .pause(1, 20) // random pause between 1 and 20

    .exec(http("Get all video games - 2nd call")
    .get("videogames"))
    .pause(3000.milliseconds) 

  // 3 Load Scenario - How many users to run, How long to run for?
  setUp(
    scn.inject(atOnceUsers(1))
    ).protocols(httpConf)
}
