package com.vaadin.starter.bakery.testbench;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.vaadin.starter.bakery.testbench.elements.ui.LoginViewElement;
import com.vaadin.starter.bakery.ui.utils.BakeryConst;
import com.vaadin.testbench.IPAddress;
import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.TestBenchDriverProxy;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.parallel.ParallelTest;

import io.github.bonigarcia.wdm.WebDriverManager;

public abstract class AbstractIT<E extends TestBenchElement> extends ParallelTest {
	public String APP_URL = "http://localhost:8080/";

	static {
		// Let notifications persist longer during tests
		BakeryConst.NOTIFICATION_DURATION = 10000;
	}

	@Rule
	public ScreenshotOnFailureRule screenshotOnFailure = new ScreenshotOnFailureRule(this, true);

	@Override
	public void setup() throws Exception {
		if ("chrome".equals(getDesiredCapabilities().getBrowserName())
				&& Boolean.getBoolean("headless")) {
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.setHeadless(true);
			setDriver(new ChromeDriver(chromeOptions));
		} else {
			super.setup();
		}
		if (getRunLocallyBrowser() == null) {
			APP_URL = "http://" + IPAddress.findSiteLocalAddress() + ":8080/";
		}
	}

	@BeforeClass
	public static void setupClass() {
		WebDriverManager.chromedriver().setup();
	}

	@Override
	public TestBenchDriverProxy getDriver() {
		return (TestBenchDriverProxy) super.getDriver();
	}

	protected LoginViewElement openLoginView() {
		return openLoginView(getDriver(), APP_URL);
	}

	protected LoginViewElement openLoginView(WebDriver driver, String url) {
		driver.get(url);
		return $(LoginViewElement.class).waitForFirst();
	}

	protected abstract E openView();

}
