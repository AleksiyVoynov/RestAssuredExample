package google;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

public class GoogleSearchTest {
    private final String testData = "facebook";
    private Response response;

    @BeforeClass
    public void setUp() {
        //make req
        response = RestAssured.given()
                .param("q", testData)
                .get("https://www.google.com/search");
    }

    @Test(priority = 1, description = "check response code")
    public void responseCode200() {
        Assert.assertEquals(response.getStatusCode(), 200,
                "request wasn't success");
    }

    @Test(priority = 2, description = "check response time")
    public void responseTimeWithinLimit() {
        long responseTime = response.getTime();
        long expectedMaxResponseTime = 3000; // 3 seconds
        Assert.assertTrue(responseTime < expectedMaxResponseTime,
                String.format("Response time is too long: %d ms", responseTime));
    }

    @Test(priority = 3, description = "check the searching result")
    public void searchingResults() {

        //parsing data
        List<String> results = Jsoup
                .parse(response.getBody().asString())
                .select("h3")
                .stream()
                .map(Element::text)
                .toList();

        boolean foundFacebook = results.stream()
                .allMatch(elementText -> elementText.toLowerCase().contains(testData.toLowerCase()));

        //validations
        Assert.assertEquals(results.size(), 7,
                "the results were less than expected");
        Assert.assertTrue(foundFacebook, String.format("headlines do not contain the word \"%s\"", testData));
    }

    @Test(priority = 4, description = "check for duplicate headings")
    public void noDuplicateHeadings() {
        List<String> results = Jsoup.parse(response.getBody().asString())
                .select("h3")
                .stream()
                .map(Element::text)
                .toList();

        long uniqueCount = results.stream().distinct().count();
        Assert.assertEquals(uniqueCount, results.size(), "There are duplicate headings");
    }

    @Test(priority = 5, description = "check if links are valid")
    public void validLinks() {
        List<String> links = Jsoup.parse(response.getBody().asString())
                .select("h3 > a")
                .stream()
                .map(element -> element.attr("href"))
                .toList();

        for (String link : links) {
            Response linkResponse = RestAssured.get(link);
            Assert.assertEquals(linkResponse.getStatusCode(), 200, String.format("Link is broken: %s", link));
        }
    }
}
