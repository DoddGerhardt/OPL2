package metamutator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.support.reflect.reference.CtVariableReferenceImpl;

public class ScalarReplacementMetaMutator extends AbstractProcessor<CtElement> {
	public static final String PREFIX = "_scalarReplacementHotSpot";
	private static int index = 0;
	private Map<String,Map<String,List<CtVariable<?>>>> variables;
	private List<CtVariable<?>> variablesCompatibles;
	private CtVariable<?> variable;
	
	public ScalarReplacementMetaMutator() {
		variables = new HashMap<String,Map<String,List<CtVariable<?>>>>();
	}
	@Override
	public boolean isToBeProcessed(CtElement element) {
		if (element instanceof CtVariable) {
			this.variable = (CtVariable<?>) element;
		} else if (element instanceof CtVariableAccess) {
			this.variable = ((CtVariableAccess<?>) (element)).getVariable().getDeclaration();
		} else {
			return false;
		}
		System.out.println(variable.getSimpleName());
		CtClass<?> c = variable.getParent(CtClass.class);
		Map<String,List<CtVariable<?>>> map = variables.get(c.getSimpleName());
		if (map == null) {
			map = new HashMap<String,List<CtVariable<?>>>();
			variables.put(c.getSimpleName(),map);
		}
		String type = variable.getType().getSimpleName().toString();
		List<CtVariable<?>> listVariables = map.get(type);
		if(listVariables == null) {
			listVariables = new LinkedList<CtVariable<?>>();
			map.put(type,listVariables);
		}
		if (!listVariables.contains(variable)) {
			listVariables.add(variable);
		}
		variablesCompatibles = new LinkedList<CtVariable<?>>();
		for (CtVariable<?> v : listVariables) {
			if (v != variable) {
				if (areInTheSamePart(v,variable)) {
					variablesCompatibles.add(v);
				}
			}
		}
		return true;
	}
	
	public boolean areInTheSamePart(CtVariable<?> v1, CtVariable<?> v2) {
		HashSet<CtMethod<?>> methods = new HashSet<CtMethod<?>>();
		CtElement parent = v1;
		while ((parent.isParentInitialized()) && ((parent = parent.getParent()) != null)) {
			if (parent instanceof CtMethod) {
				methods.add((CtMethod<?>) parent);
			}
		}
		parent = v2;
		while ((parent.isParentInitialized()) && ((parent = parent.getParent()) != null)) {
			if (methods.contains(parent)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void process(CtElement element) {
		/**
		System.out.print(variable.getSimpleName());
		System.out.print(" : ");
		 for (CtVariable<?> v : variablesCompatibles) {
		 	System.out.print(v.getSimpleName());
			System.out.print(" ");
		 }
		 System.out.println("");
		**/
	}
}
