package de.tum.in.test.integration.testuser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URISyntaxException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import de.tum.in.test.api.StrictTimeout;
import de.tum.in.test.api.WhitelistPath;
import de.tum.in.test.api.jupiter.Public;
import de.tum.in.test.api.localization.UseLocale;
import de.tum.in.test.api.structural.AttributeTestProvider;
import de.tum.in.test.api.structural.ClassTestProvider;
import de.tum.in.test.api.structural.ConstructorTestProvider;
import de.tum.in.test.api.structural.MethodTestProvider;
import de.tum.in.test.api.structural.testutils.ClassNameScanner;

@Public
@UseLocale("en")
@StrictTimeout(10)
@WhitelistPath("")
public class StructuralUser {

	private static final String TESTUSER_POM_XML = "src/test/resources/de/tum/in/test/integration/testuser/pom.xml";
	private static final String TESTUSER_BUILD_GRADLE = "src/test/resources/de/tum/in/test/integration/testuser/build.gradle";

	@Nested
	class Maven extends StrucuralTestSet {

		@BeforeEach
		void setupTest() {
			ClassNameScanner.setPomXmlPath(TESTUSER_POM_XML);
			ClassNameScanner.setBuildGradlePath(null);
		}
	}

	@Nested
	class Gradle extends StrucuralTestSet {

		@BeforeEach
		void setupTest() {
			ClassNameScanner.setPomXmlPath(null);
			ClassNameScanner.setBuildGradlePath(TESTUSER_BUILD_GRADLE);
		}
	}

	@Nested
	class InvalidConfigurations {

		private final Logger logger = ((Logger) LoggerFactory.getLogger(ClassNameScanner.class));
		private final ListAppender<ILoggingEvent> logs = new ListAppender<>();

		@BeforeEach
		void addLogger() {
			logger.addAppender(logs);
			logs.start();
		}

		@AfterEach
		void removeLogger() {
			logger.detachAppender(logs);
		}

		@Test
		void bothValid() {
			ClassNameScanner.setPomXmlPath(TESTUSER_POM_XML);
			ClassNameScanner.setBuildGradlePath(TESTUSER_BUILD_GRADLE);
			assertThat(ClassNameScanner.getPomXmlPath()).isEqualTo(TESTUSER_POM_XML);
			assertThat(ClassNameScanner.getBuildGradlePath()).isEqualTo(TESTUSER_BUILD_GRADLE);
			checkScan();
		}

		@Test
		void invalidMaven() {
			ClassNameScanner.setPomXmlPath(TESTUSER_BUILD_GRADLE);
			ClassNameScanner.setBuildGradlePath(null);
			checkScan();
		}

		@Test
		void invalidGradle() {
			ClassNameScanner.setPomXmlPath(null);
			ClassNameScanner.setBuildGradlePath(TESTUSER_POM_XML);
			checkScan();
		}

		@Test
		void noBuildToolFile() {
			ClassNameScanner.setPomXmlPath(null);
			ClassNameScanner.setBuildGradlePath(null);
			checkScan();
		}

		private void checkScan() {
			new ClassNameScanner("", "").getScanResult();
			logs.stop();
			if (!logs.list.isEmpty())
				fail(logs.list.toString());
		}
	}

	class StrucuralTestSet {

		@Nested
		class AttributeTestUser extends AttributeTestProvider {

			@TestFactory
			DynamicContainer testAttributes() throws URISyntaxException {
				structureOracleJSON = retrieveStructureOracleJSON(getClass().getResource("test.json"));
				return generateTestsForAllClasses();
			}
		}

		@Nested
		class ClassTestUser extends ClassTestProvider {

			@TestFactory
			DynamicContainer testClasses() throws URISyntaxException {
				structureOracleJSON = retrieveStructureOracleJSON(getClass().getResource("test.json"));
				return generateTestsForAllClasses();
			}
		}

		@Nested
		class ConstructorTestUser extends ConstructorTestProvider {

			@TestFactory
			DynamicContainer testConstructors() throws URISyntaxException {
				structureOracleJSON = retrieveStructureOracleJSON(getClass().getResource("test.json"));
				return generateTestsForAllClasses();
			}
		}

		@Nested
		class MethodTestUser extends MethodTestProvider {

			@TestFactory
			DynamicContainer testMethods() throws URISyntaxException {
				structureOracleJSON = retrieveStructureOracleJSON(getClass().getResource("test.json"));
				return generateTestsForAllClasses();
			}
		}
	}
}