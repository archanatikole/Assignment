package options;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
		glue = {"stepdefs"},
		features = {"src/test/features"},
		monochrome = true)
public class CucumberTests {}
