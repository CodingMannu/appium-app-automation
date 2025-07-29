package apps.tta.android.stepdefinitions;

import apps.tta.android.pages.TTA_LoginPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static driver.DriverManager.getDriver;

public class TTA_LoginSteps {

    private final TTA_LoginPage loginPage = new TTA_LoginPage(getDriver());

    @Given("Validate the login screen or if on home screen then navigate to login screen")
    public void ValidateLoginScreenOrIfOnHomeScreenThenNavigateToLoginScreen() {
        loginPage.isLoginScreen();
    }

    @And("User enters mobile number {string}")
    public void userEntersMobileNumber(String mobileNumber) {
        loginPage.enterMobileNumber(mobileNumber);
    }

    @And("User clicks login button")
    public void userClicksLoginButton() {
        loginPage.clickLoginButton();
    }

    @Then("User enters OTP {string}")
    public void userEntersOTP(String otp) {
        loginPage.enterOTP(otp);
    }

    @And("User clicks outside of you can change the app language from here popup")
    public void clickOutsideOfYouCanChangeTheAppLanguageFromHerePopup(){
        loginPage.clickOutsideOfYouCanChangeTheAppLanguageFromHerePopup();
    }

    @And("User clicks on close button on Don't miss out on offers! popup")
    public void closeLocationAndNotificationPopup(){
        loginPage.closeLocationAndNotificationPopup();
    }

    @And("User clicks on close button on want to continue your consultation popup")
    public void closeWantToContinueYourConsultationPopup(){
        loginPage.closeWantToContinueYourConsultationPopup();
    }

    @And("User clicks on close button on popular popup")
    public void closePopularChoicePopup(){
        loginPage.closePopularChoicePopup();
    }








}
