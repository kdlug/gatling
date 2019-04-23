import io.gatling.core.Predef._
import io.gatling.http.Predef._

import baseConfig.BaseSimulation

class CheckResponseBodyAndExtract extends BaseSimulation {
  // 2 Scenario Definition
  val scn = scenario("Video Game DB")
    .exec(http("Get a specific game")
    .get("videogames/1")
    .check(jsonPath("$.name").is("Resident Evil 4")))

    .exec(http("Get all video games - 1st call")
    .get("videogames")
    .check(jsonPath("$[1].id").saveAs("gameId"))) // save id to a variable gameId
    .exec { session => println(session); session } // debugging, check gameId

    .exec(http("Get a specific game - 2nd call with parameter")
    .get("videogames/${gameId}") // use parameter gameId
    .check(jsonPath("$.name").is("Gran Turismo 3"))
    .check(bodyString.saveAs("responseBody")))
    .exec { session => println(session("responseBody").as[String]); session } // debugging, check responseBody variable

  // 3 Load Scenario - How many users to run, How long to run for?
  setUp(
    scn.inject(atOnceUsers(1))
    ).protocols(httpConf)
}
