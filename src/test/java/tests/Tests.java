package tests;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import testData.Region;

import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class Tests {
    @CsvSource({"https://www.apple.com/,Apple", "https://www.google.com/,Google"})
    @DisplayName("Checking titles of the web sites")
    @Tag("Blocker")
    @ParameterizedTest(name = "Url \"{0}\" should have title \"{1}\"")
    void pageHasProperTitle(String url, String title) {
        open(url);
        Assertions.assertEquals(title(), title);
    }

    @CsvFileSource(resources = "/testData.csv")
    @DisplayName("Clicking on each element of main menu opens proper page")
    @ParameterizedTest(name = "Clicking \"{0}\" element of main menu opens \"{1}\" page")
    @Tags({@Tag("Critical"), @Tag("Smoke")})
    void eachElementOfMenuOpensCorrectUrl(String element, String url) {
        open("https://www.apple.com/");
        $(".ac-gn-list").$(byText(element)).parent().click();
        Assertions.assertEquals(WebDriverRunner.getWebDriver().getCurrentUrl(), url);
    }

    @MethodSource("regionPageContentData")
    @DisplayName("Content displays correctly for different regions")
    @Tag("Minor")
    @ParameterizedTest(name = "Region \"{0}\" displays the following content \"{1}\"")
    void contentDisplaysCorrectlyForDifferentRegions(Region region, List<String> content) {
        open("https://www.apple.com/");
        $("[data-analytics-title=\"choose your country\"]").click();
        $$("a span").findBy(text(region.toString())).parent().click();
        $("[data-unit-id=\"iphone-14-pro\"]").shouldHave(text(content.get(0)));
        $("[data-unit-id=\"iphone-14\"]").shouldHave(text(content.get(1)));
    }

    static Stream<Arguments> regionPageContentData() {
        return Stream.of(
                Arguments.of(Region.ITALIA, List.of("Pro. E oltre.", "Grande e graaaande.")),
                Arguments.of(Region.FRANCE, List.of("Pro. Plus ultra.", "Un grand. Avec un grand Plus."))
        );
    }
}
