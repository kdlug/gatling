import io.gatling.core.Predef._
import io.gatling.http.Predef._
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Random

import baseConfig.BaseSimulation

class CustomFeederJsonTemplate extends BaseSimulation {


// Preparing custom feeder with following structure

// {
//  "id": 0,
//  "name": "string",
//  "releaseDate": "2019-04-04T07:51:04.494Z",
//  "reviewScore": 0,
//  "category": "string",
//  "rating": "string"
//}

    // ids from 1-10 we have already in DB, so we wan't duplicate them
    var idNumbers = (11 to 20).iterator
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


    def getNextGamePost() = Map(
        "gameId" -> idNumbers.next(),
        "name" -> ("Game-" + randomString(5)),
        "releaseDate" -> getRandomDate(now, rnd),
        "reviewScore" -> rnd.nextInt(100),
        "category" -> ("Category-" + randomString(6)),
        "rating" -> ("Rating-" + randomString(4))
    )

    val customFeeder = Iterator.continually(getNextGamePost())

      
    // 2 Scenario Definition
    def postNewGame() = {
        repeat(5) { // we want to add 5 different games
        feed(customFeeder)
            .exec(http("Post new game")
                .post("videogames/")
                .body(ElFileBody("NewGameTemplate.json")).asJson // use a template
                .check(status.is(200)))
                .pause(1)
        }
    }

    val scn = scenario("Video Game DB")
    .exec(postNewGame())

    // 3 Load Scenario - How many users to run, How long to run for?
    setUp(
        scn.inject(atOnceUsers(1))
    ).protocols(httpConf)
}
