package com.aanassar.junit

import java.nio.file.Path

import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class TestSystemPropertyRule {

	@Rule public ExpectedException expected = ExpectedException.none();
	
	@Test 
	public void testSystemTempDirectory() {
		final File temporaryFile = File.createTempFile("test", "txt")
		temporaryFile.deleteOnExit()
		Path defaultTemporaryDirectory = temporaryFile.toPath().parent
		
	}
}
