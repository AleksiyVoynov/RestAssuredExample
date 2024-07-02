# Requirements:
* MacOS Sonoma and above
* Java 17
* Gradle


# Guide:

## To run tests and generate Allure report:
* open terminal
* cd (path to /RestAssuredExample)
* run `gradle clean`
* run `gradle test`
* run `allure serve build/allure-results`