package kr.pe.codda.common.buildsystem;

import static org.junit.Assert.fail;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.BuildSystemException;

public class EclipseBuilderTest extends AbstractJunitTest 	{

	@Test
	public void testCreateCoddaCoreAllEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder("D:\\gitmadang\\codda");
			eclipseBuilder.createCoddaCoreAllEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
	
	@Test
	public void testCreateCoddaCoreLoggerEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder("D:\\gitmadang\\codda");
			eclipseBuilder.createCoddaCoreLoggerEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}

	@Test
	public void testCreateCoddaCoreHelperEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder("D:\\gitmadang\\codda");
			eclipseBuilder.createCoddaCoreHelperEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
	
	@Test
	public void testCreateCoddaSampleBaseServerEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder("D:\\gitmadang\\codda");
			eclipseBuilder.createCoddaSampleBaseServerEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
	
	@Test
	public void testCreateCoddaSampleBaseAppClientEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder("D:\\gitmadang\\codda");
			eclipseBuilder.createCoddaSampleBaseAppClientEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
	
	@Test
	public void testCreateCoddaSampleBaseWebClientEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder("D:\\gitmadang\\codda");
			eclipseBuilder.createCoddaSampleBaseWebClientEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
}
