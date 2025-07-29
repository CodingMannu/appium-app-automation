package utils;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

public class EmailSender {

    private static final Logger logger = LogManager.getLogger(EmailSender.class);

    /**
     * Sends the Extent report via email.
     *
     * @param reportPath absolute path of report file
     * @param prop       loaded Properties object with email config
     * @return true if sent successfully, false otherwise
     */
    public static boolean sendExtentReportByEmail(String reportPath, Properties prop) {
        if (reportPath == null) {
            logger.error("Report path is null. Email not sent.");
            return false;
        }

        try {
            File reportFile = new File(reportPath);
            if (!reportFile.exists()) {
                logger.error("Report file does not exist: {}", reportPath);
                return false;
            }

            URL reportUrl = reportFile.toURI().toURL();

            // Extract all properties once
            String host = prop.getProperty("email.smtp.host");
            int port = Integer.parseInt(prop.getProperty("email.smtp.port"));
            String senderEmail = prop.getProperty("email.sender");
            String senderName = prop.getProperty("email.sender.name");
            String senderPassword = prop.getProperty("email.sender.password");
            String[] receivers = prop.getProperty("email.receiver", "").split(",");

            ImageHtmlEmail email = new ImageHtmlEmail();
            email.setHostName(host);
            email.setSmtpPort(port);
            email.setAuthenticator(new DefaultAuthenticator(senderEmail, senderPassword));
            email.setSSLOnConnect(true);
            email.setFrom(senderEmail, senderName);
            email.setSubject("Automation Test Report");
            email.setMsg("""
                    Hi,
                    
                    Please find the attached automation test execution report.
                    
                    Regards,
                    Manoj Kumar
                    QA Tester | Astroyogi
                    Manoj.Kumar@newtayindia.co.in
                    """);

            for (String to : receivers) {
                email.addTo(to.trim());
            }

            email.attach(reportUrl, reportFile.getName(), "Extent Report");
            email.send();

            logger.info("📧 Report emailed successfully to: {}", Arrays.toString(receivers));
            return true;

        } catch (Exception e) {
            logger.error("Failed to send Extent report email.", e);
            return false;
        }
    }
}
