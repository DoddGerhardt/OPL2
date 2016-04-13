package metamutator;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import bsh.Interpreter;
import resources.Foo;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.NameFilter;
import static org.apache.commons.lang.reflect.MethodUtils.invokeExactMethod;


public class UnaryOperatorMetaMutatorTest {
	@Test
	 public void testUnaryOperatorMetaMutator() throws Exception {
		 Launcher l = new Launcher();
		 l.addInputResource("src/test/java/resources/Foo.java");
		 l.addProcessor(new UnaryOperatorMetaMutator());
		 l.run();
		 
	     CtClass c = (CtClass) l.getFactory().Package().getRootPackage().getElements(new NameFilter("Foo")).get(0);

	     System.out.println("// Metaprogram: ");
	     System.out.println(c.toString());
	        
	     Interpreter bsh = new Interpreter();
	     
	     // there is no selector before loading the class
	     assertEquals(0,Selector.getAllSelectors().size());

	     // creating a new instance of the class
	     Object o = ((Class) bsh.eval(c.toString())).newInstance();   
	     assertEquals(6,Selector.getAllSelectors().size());
     
	     Selector sel1 = Selector.getSelectorByName(UnaryOperatorMetaMutator.PREFIX + "1");	     
	     Selector sel2 = Selector.getSelectorByName(UnaryOperatorMetaMutator.PREFIX + "2");
	     Selector sel3 = Selector.getSelectorByName(UnaryOperatorMetaMutator.PREFIX + "3");     
	     
	     // a || b
	     sel1.choose(0);
	     sel2.choose(0);
	     sel3.choose(0);
	     assertEquals(false,invokeExactMethod(o,"op",new Object[] { Boolean.FALSE, Boolean.FALSE }));
	     assertEquals(true,invokeExactMethod(o,"op",new Object[] { Boolean.TRUE, Boolean.FALSE }));
	     assertEquals(true,invokeExactMethod(o,"op",new Object[] { Boolean.FALSE, Boolean.TRUE }));
	     assertEquals(true,invokeExactMethod(o,"op",new Object[] { Boolean.TRUE, Boolean.TRUE }));
	     
	     // !a || b
	     sel1.choose(1);
	     sel2.choose(0);
	     assertEquals(true,invokeExactMethod(o,"op",new Object[] { Boolean.FALSE, Boolean.FALSE }));
	     assertEquals(false,invokeExactMethod(o,"op",new Object[] { Boolean.TRUE, Boolean.FALSE }));
	     assertEquals(true,invokeExactMethod(o,"op",new Object[] { Boolean.FALSE, Boolean.TRUE }));
	     assertEquals(true,invokeExactMethod(o,"op",new Object[] { Boolean.TRUE, Boolean.TRUE }));
	     
	     // a || !b
	     sel1.choose(0);
	     sel2.choose(1);
	     assertEquals(true,invokeExactMethod(o,"op",new Object[] { Boolean.FALSE, Boolean.FALSE }));
	     assertEquals(true,invokeExactMethod(o,"op",new Object[] { Boolean.TRUE, Boolean.FALSE }));
	     assertEquals(false,invokeExactMethod(o,"op",new Object[] { Boolean.FALSE, Boolean.TRUE }));
	     assertEquals(true,invokeExactMethod(o,"op",new Object[] { Boolean.TRUE, Boolean.TRUE }));
	     
	     Selector sel4 = Selector.getSelectorByName(UnaryOperatorMetaMutator.PREFIX + "4");
	     
	     // a > b
	     sel4.choose(0);
	     assertEquals(true,invokeExactMethod(o,"op2",new Object[] { 5, 4 }));
	     assertEquals(false,invokeExactMethod(o,"op2",new Object[] { 4, 5 }));
	     assertEquals(false,invokeExactMethod(o,"op2",new Object[] { 4, 4 }));

	     // !(a > b)
	     sel4.choose(1);
	     assertEquals(false,invokeExactMethod(o,"op2",new Object[] { 5, 4 }));
	     assertEquals(true,invokeExactMethod(o,"op2",new Object[] { 4, 5 }));
	     assertEquals(true,invokeExactMethod(o,"op2",new Object[] { 4, 4 }));

	     Selector sel5 = Selector.getSelectorByName(UnaryOperatorMetaMutator.PREFIX + "5");
	     
	     // ((Foo.class) == c)
	     sel5.choose(0);
	     assertEquals(true,invokeExactMethod(o,"op3",new Object[] { Foo.class }));
	     assertEquals(false,invokeExactMethod(o,"op3",new Object[] { UnaryOperatorMetaMutator.class }));
	     
	     // !((Foo.class) == c)
	     sel5.choose(1);
	     assertEquals(false,invokeExactMethod(o,"op3",new Object[] { Foo.class }));
	     assertEquals(true,invokeExactMethod(o,"op3",new Object[] { UnaryOperatorMetaMutator.class }));
	 }
}
