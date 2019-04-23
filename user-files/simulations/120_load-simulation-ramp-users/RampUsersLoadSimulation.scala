import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration.DurationInt

import baseConfig.BaseSimulation

class RampUsersLoadSimulation extends BaseSimulation {


    def getAllVideoGames() = {
        exec(
            http("Get all video games")
            .get("videogames")
            .check(status.is(200))
        )
    }

    def getSpecificVideoGame() = {
        exec(
            http("Get specific video game")
            .get("videogames/2")
            .check(status.is(200))
        )
    }

    // 2 Scenario Definition
    val scn = scenario("Video Game DB")
        .exec(getAllVideoGames())
        .pause(5)
        .pause(5)
        .exec(getSpecificVideoGame())
        .exec(getAllVideoGames())

    // 3 Load Scenario - How many users to run, How long to run for?
    setUp(
        scn.inject(
            nothingFor(5 seconds),
            // constant number of users every second for fixed duration
            // constantUsersPerSec(10) during (10 seconds) // Add 10 users every 10 seconds = 100 users total
            rampUsersPerSec(1) to (5) during (20 seconds) // start by adding 1 new user per second, then increase that to 5 users per secod over a period of 20 seconds

        )
    ).protocols(httpConf.inferHtmlResources()) // inferHtmlResources() - gatling will fetch everything on the page (css, jscript etc) 
}
