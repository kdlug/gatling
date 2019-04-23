import io.gatling.core.Predef._
import io.gatling.http.Predef._

import baseConfig.BaseSimulation

class CsvFeederToCustom extends BaseSimulation {

    var idNumbers = (1 to 10).iterator

    // val customFeeder = Iterator.continually(Map("gameId" -> idNumbers.next()))

    def getNextGameId() = Map("gameId" -> idNumbers.next())
    val customFeeder = Iterator.continually(getNextGameId())
    
    // 2 Scenario Definition
    def getSpecificVideoGame() = {
        repeat(10) {
        feed(customFeeder)
            .exec(http("Get a specific game")
                .get("videogames/${gameId}")
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
