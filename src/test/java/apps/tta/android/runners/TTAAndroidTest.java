package apps.tta.android.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@CucumberOptions(
        features = { "src/test/resources/features/tta/android" },
        glue = {
                "apps.tta.android.stepdefinitions",
                "base"
        },
        dryRun = false,
        monochrome = true,
        plugin = {
                "pretty",
                "json:target/jsonReports/cucumber-report.json",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "rerun:target/failed_scenarios.txt",
        },
//        tags = "@Login_Promo_001"
        tags = "@Login_Repeat_001"
)

@Test
public class TTAAndroidTest extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
