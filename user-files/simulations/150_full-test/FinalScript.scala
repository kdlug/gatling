import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration.DurationInt
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Random

import baseConfig.BaseSimulation

// passing parameters
// export JAVA_OPTS="-DUSERS=3 -DRAMPDURATION=5 -DDURATION=30"
// ./gatling.sh

// call GetAllGames
// Call Create a new Game (custom feeder)
// Call Get Single Game (of the game created above)
// Delete Game (delete the game just created)
// Create methods for all API calls
// Add checks & assertions after each call
// Add pause time
// Load scenation with runtime parameters
// Print message to console at start / end of test
// 

class FinalScript extends BaseSimulation {

    // 0. Parameters
    private def getProperty(propertyName: String, defaultValue: String) = {
        Option(System.getenv(propertyName))
            .orElse(Option(System.getProperty(propertyName)))
            .getOrElse(defaultValue)
    }

    def userCount: Int = getProperty("USERS", "5").toInt
    def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt
    def testDuration: Int = getProperty("DURATION", "60").toInt


    // Preparing data
    var idNumbers = (20 to 1000).iterator
    // generate random games name = Game-XXXXX
    var rnd = new Random()
    //generate date
    val now = LocalDate.now()
    val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // helper which generates a random string with given length
    def randomString(length: Int) = {
        rnd.alphanumeric.filter(_.isLetter).take(length).mkString
    }

    // helper which generates random date
    // parameters: startDate, random
    def getRandomDate(startDate: LocalDate, random: Random):String = {
        startDate.minusDays(random.nextInt(30)).format(pattern)
    }

    /*** Before ***/
    before {
        println(s"Running test with ${userCount} users")
        println(s"Ramping users over ${rampDuration} seconds")
        println(s"Total test duration ${testDuration} seconds")
    }

    /*** Custom Feeder ***/
    val customFeeder = Iterator.continually(generateNewGame())
    
    def generateNewGame() = Map(
        "gameId" -> idNumbers.next(),
        "name" -> ("Game-" + randomString(5)),
        "releaseDate" -> getRandomDate(now, rnd),
        "reviewScore" -> rnd.nextInt(100),
        "category" -> ("Category-" + randomString(6)),
        "rating" -> ("Rating-" + randomString(4))
    )


    // 1. Scenarios
    def getAllVideoGames() = {
        exec(
            http("Get all video games")
            .get("videogames")
            .check(status.is(200))
        )
    }

    def postNewGame() = {
        feed(customFeeder)
            .exec(
                http("Post new game")
                .post("videogames/")
                .body(ElFileBody("NewGameTemplate.json")).asJson // use a template
                .check(status.is(200))
                .check(jsonPath("$.status").is("Record Added Successfully"))
            )
    }

    def getLastPostedGame() = {
        exec(http("Get last posted game")
            .get("videogames/${gameId}")
            .check(status.is(200))
            .check(jsonPath("$.name").is("${name}"))
        )
    }

    def deleteLastPostedGame() = {
        exec(http("Delete a game")
            .delete("videogames/${gameId}")
            .check(status.is(200))
            .check(jsonPath("$.status").is("Record Deleted Successfully"))
        )
    }

    // 2 Scenario Definition
    val scn = scenario("Video Game DB")
        .forever() { // loop until time exceed
            exec(getAllVideoGames())
            .pause(1)
            .exec(postNewGame())
            .pause(1)
            .exec(getLastPostedGame())
            .pause(1)
            .exec(deleteLastPostedGame())
    }

    /*** Setup Load Simulation ***/
    setUp(
        scn.inject(
            nothingFor(5 seconds),
            atOnceUsers(10), // start from 10 users
            rampUsers(userCount) during (rampDuration second))
        ).protocols(httpConf).maxDuration(testDuration seconds)

    /*** After ***/
    after {
        println("Stress test completed")
    }
}
