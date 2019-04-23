import io.gatling.core.Predef._
import io.gatling.http.Predef._

import baseConfig.BaseSimulation

class CsvFeeder extends BaseSimulation {

    val csvFeeder = csv("gameCsvFile.csv").circular

    // 2 Scenario Definition
    def getSpecificVideoGame() = {
        repeat(10) {
        feed(csvFeeder)
            .exec(http("Get a specific game")
                .get("videogames/${gameId}")
                .check(jsonPath("$.name").is("${gameName}"))
                .check(status.is(200)))
                .pause(1)
        }
    }

    val scn = scenario("Video Game DB")
    .exec(getSpecificVideoGame())

    // 3 Load Scenario - How many users to run, How long to run for?
    setUp(
        scn.inject(atOnceUsers(1))
    ).protocols(httpConf)
}
