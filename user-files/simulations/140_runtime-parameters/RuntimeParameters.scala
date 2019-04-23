import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration.DurationInt

import baseConfig.BaseSimulation

// passing parameters
// export JAVA_OPTS="-DUSERS=3 -DRAMPDURATION=5 -DDURATION=30"
// ./gatling.sh
class RuntimeParameters extends BaseSimulation {

    // get parameter
    private def getProperty(propertyName: String, defaultValue: String) = {
        Option(System.getenv(propertyName))
            .orElse(Option(System.getProperty(propertyName)))
            .getOrElse(defaultValue)
    }

    // define parameters
    def userCount: Int = getProperty("USERS", "5").toInt
    def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt
    def testDuration: Int = getProperty("DURATION", "60").toInt

    // executes before running test
    before {
        println(s"Running test with ${userCount} users")
        println(s"Ramping users over ${rampDuration} seconds")
        println(s"Total test duration ${testDuration} seconds")
    }

    def getAllVideoGames() = {
        exec(
            http("Get all video games")
            .get("videogames")
            .check(status.is(200))
        )
    }

    // 2 Scenario Definition
    val scn = scenario("Video Game DB")
        .forever() { // loop until time exceed
            exec(getAllVideoGames())
        }


    // 3 Load Scenario - How many users to run, How long to run for?
    setUp(
        scn.inject(
            nothingFor(5 seconds),
            atOnceUsers(10), // add 10 users
            rampUsers(userCount) during (rampDuration second)) // add 50 users during 30 seconds
        ).protocols(httpConf).maxDuration(testDuration seconds)
}
