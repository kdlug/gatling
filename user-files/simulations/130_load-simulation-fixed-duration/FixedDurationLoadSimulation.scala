import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration.DurationInt

import baseConfig.BaseSimulation

class FixedDurationLoadSimulation extends BaseSimulation {


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
        .forever() {
            exec(getAllVideoGames())
            .pause(5)
            .exec(getSpecificVideoGame())
            .pause(5)
            .exec(getAllVideoGames())
        }


    // 3 Load Scenario - How many users to run, How long to run for?
    setUp(
        scn.inject(
            nothingFor(5 seconds),
            atOnceUsers(10), // add 10 users
            rampUsers(50) during (30 seconds) // add 50 users during 30 seconds
        ).protocols(httpConf.inferHtmlResources()) // inferHtmlResources() - gatling will fetch everything on the page (css, jscript etc) 
    ).maxDuration(1 minute)
}
