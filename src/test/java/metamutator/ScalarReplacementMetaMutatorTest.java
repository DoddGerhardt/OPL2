package metamutator;

import org.junit.Test;

import spoon.Launcher;

public class ScalarReplacementMetaMutatorTest {
	@Test
	public void testScalarReplacement() {
		Selector.reset();
		Launcher l = new Launcher();
        l.addInputResource("src/test/java/resources/Foo.java");
        l.addProcessor(new ScalarReplacementMetaMutator());
        l.run();

	}
}
