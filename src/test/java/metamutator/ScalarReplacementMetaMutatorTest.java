package metamutator;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.NameFilter;

public class ScalarReplacementMetaMutatorTest {
	@Test
	public void testScalarReplacement() {
		Selector.reset();
		Launcher l = new Launcher();
        l.addInputResource("src/test/java/resources/Foo.java");
        l.addProcessor(new ScalarReplacementMetaMutator());
        l.run();

        CtClass c = (CtClass) l.getFactory().Package().getRootPackage().getElements(new NameFilter("Foo")).get(0);
        
        System.out.println(c.toString());
	}
}
