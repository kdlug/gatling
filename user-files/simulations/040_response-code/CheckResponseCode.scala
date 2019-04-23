import io.gatling.core.Predef._
import io.gatling.http.Predef._

import baseConfig.BaseSimulation

class CheckResponseCode extends BaseSimulation {
  // 2 Scenario Definition
  val scn = scenario("Video Game DB")
    .exec(http("Get all video games - 1st call")
    .get("videogames")
    .check(status.is(200)))

    .exec(http("Get a specific game")
    .get("videogames/1")
    .check(status.in(200 to 210)))

    .exec(http("Get all video games - 2nd call")
    .get("videogames")
    .check(status.not(404), status.not(500)))

  // 3 Load Scenario - How many users to run, How long to run for?
  setUp(
    scn.inject(atOnceUsers(1))
    ).protocols(httpConf)
}
