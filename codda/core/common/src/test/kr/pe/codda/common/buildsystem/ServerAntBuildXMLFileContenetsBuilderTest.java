package kr.pe.codda.common.buildsystem;

import org.junit.Test;

import junitlib.AbstractJunitTest;

public class ServerAntBuildXMLFileContenetsBuilderTest extends AbstractJunitTest {

	@Test
	public void test() {
		log.info(ServerAntBuildXMLFileContenetsBuilder.build("sample_base"));
	}
}
