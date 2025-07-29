package apps.tta.android.pages;

import base.TestBase;
import driver.DriverManager;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.WaitUtils;

public class TTA_LoginPage extends TestBase {

    private static final Logger logger = LogManager.getLogger(TTA_LoginPage.class);

    @FindBy(id = "com.netway.phone.advice:id/etvMobileNumber")
    WebElement mobileInputField;

    @FindBy(id = "com.netway.phone.advice:id/tvLogin")
    WebElement loginButton;

    @FindBy(id = "com.netway.phone.advice:id/lineField")
    WebElement otpInputField;

    @FindBy(id = "com.netway.phone.advice:id/account")
    WebElement signUpButtonLocator;

    @FindBy(id = "com.netway.phone.advice:id/tvRecommendationCategory")
    WebElement clickOutsideOfYouCanChangeTheAppLanguageFromHerePopup;

    @FindBy(id = "com.netway.phone.advice:id/close")
    WebElement closeLocationAndNotificationPopup;

    @FindBy(id = "com.netway.phone.advice:id/tvClose")
    WebElement closeWantToContinueYourConsultationPopup;

    @FindBy(id = "com.netway.phone.advice:id/tvClose")
    WebElement closePopularChoicePopup;


    public TTA_LoginPage(AppiumDriver driver) {
        super(driver);
        logger.info("TTA_LoginPage Initialized.");
    }

    /**
     * Checks if we are on the login page.
     * If not, attempts to navigate from the home screen.
     */
    public void isLoginScreen() {

        logger.info("Checking if we are on the Login screen...");

        try {
            if (mobileInputField.isDisplayed()) {
                logger.info("We are already on Login screen.");
            }
        } catch (Exception e) {
            logger.info("We are NOT on Login screen. Navigating from Home screen...");
            try {
                waitUtils().waitForElementToBeClickable(signUpButtonLocator, 10);
                signUpButtonLocator.click();
                logger.info("Clicked Account button on Home screen.");

                waitUtils().waitForElementToBeVisible(mobileInputField, 10);
                logger.info("Login screen loaded after clicking Account button.");
            } catch (Exception ex) {
                logger.error("Unable to navigate to Login screen. Possibly already logged in or UI changed.", ex);
                DriverManager.getDriver().quit();
            }
        }
    }


    public void enterMobileNumber(String number) {
        waitUtils().waitForElementToBeVisible(mobileInputField, 10);
        mobileInputField.clear();
        mobileInputField.sendKeys(number);
        logger.info("Entered mobile number: {}", number);
    }

    public void clickLoginButton() {
        waitUtils().waitForElementToBeClickable(loginButton, 10);
        loginButton.click();
        logger.info("Clicked Login button.");
    }

    public void enterOTP(String otp) {
        waitUtils().waitForElementToBeVisible(otpInputField, 10);
        otpInputField.sendKeys(otp);
        logger.info("Entered OTP: {}", otp);
    }


    public void clickOutsideOfYouCanChangeTheAppLanguageFromHerePopup() {
        WaitUtils.executionDelay(13);
        try {
            logger.info("Clicked outside of change language popup - in one attempt");
            mobileActions().tapCenterOfScreen();
        } catch (Exception e) {
            logger.info("Clicked outside of change language popup - in second attempt");
            mobileActions().tapCenterOfScreen();
        }
    }


    public void closeLocationAndNotificationPopup() {
        waitUtils().waitForElementToBeClickable(closeLocationAndNotificationPopup, 5);
        closeLocationAndNotificationPopup.click();
        logger.info("Clicked on close popup - location and notification popup.");
    }

    public void closeWantToContinueYourConsultationPopup() {
        waitUtils().waitForElementToBeClickable(closeWantToContinueYourConsultationPopup, 5);
        closeWantToContinueYourConsultationPopup.click();
        logger.info("Clicked on close popup - continue your consultation popup");
    }

    public void closePopularChoicePopup() {
        waitUtils().waitForElementToBeClickable(closePopularChoicePopup, 5);
        closeWantToContinueYourConsultationPopup.click();
        logger.info("Clicked on closed popular choice popup");
    }


}


/*

close popup(location, Notification) - id
com.netway.phone.advice:id/close

allow access(location, Notification) - id
com.netway.phone.advice:id/mb_allow_access

maybe later - id
com.netway.phone.advice:id/maybe_later



want to continue your consultation click outside popup- id
com.netway.phone.advice:id/touch_outside

want to continue your consultation click close button of popup- id
com.netway.phone.advice:id/tvClose

want to continue your consultation click yes -id
com.netway.phone.advice:id/tvYes

want to continue your consultation click second option -id
com.netway.phone.advice:id/tvLater

Popular choice close button - id
com.netway.phone.advice:id/tvClose

language ignore first time - id
com.netway.phone.advice:id/newMotionLayout








 */
