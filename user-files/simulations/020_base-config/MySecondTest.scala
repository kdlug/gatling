import io.gatling.core.Predef._
import io.gatling.http.Predef._

import baseConfig.BaseSimulation

class MySecondTest extends BaseSimulation {
  // 2 Scenario Definition
  val scn = scenario("My First test")
    .exec(http("Get all Games")
    .get("videogames"))

  // 3 Load Scenario - How many users to run, How long to run for?
  setUp(
    scn.inject(atOnceUsers(1))
    ).protocols(httpConf)
}
