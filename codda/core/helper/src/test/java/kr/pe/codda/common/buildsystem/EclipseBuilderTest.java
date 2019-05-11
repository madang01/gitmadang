package kr.pe.codda.common.buildsystem;

import static org.junit.Assert.fail;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.BuildSystemException;

public class EclipseBuilderTest extends AbstractJunitTest 	{

	@Test
	public void testCreateCoddaCoreAllEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder(installedPath.getAbsolutePath());
			eclipseBuilder.createCoddaCoreAllEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
	
	@Test
	public void testCreateCoddaCoreLoggerEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder(installedPath.getAbsolutePath());
			eclipseBuilder.createCoddaCoreLoggerEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}

	@Test
	public void testCreateCoddaCoreHelperEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder(installedPath.getAbsolutePath());
			eclipseBuilder.createCoddaCoreHelperEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
	
	@Test
	public void testCreateCoddaSampleBaseServerEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder(installedPath.getAbsolutePath());
			eclipseBuilder.createCoddaSampleBaseServerEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
	
	@Test
	public void testCreateCoddaSampleBaseAppClientEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder(installedPath.getAbsolutePath());
			eclipseBuilder.createCoddaSampleBaseAppClientEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
	
	@Test
	public void testCreateCoddaSampleBaseWebClientEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder(installedPath.getAbsolutePath());
			eclipseBuilder.createCoddaSampleBaseWebClientEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
}
