package metamutator;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

import metamutator.UnaryOperatorMetaMutator.UnaryOperator;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtStatement;
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

public class ScalarReplacementMetaMutator extends AbstractProcessor<CtVariable<?>> {
	public static final String PREFIX = "_scalarReplacementHotSpot";
	private static int index = 0;
	
	private Map<String,Map<String,List<CtVariable<?>>>> variables;
	
	private List<CtVariable<?>> variablesCompatibles;

	public ScalarReplacementMetaMutator() {
		variables = new HashMap<String,Map<String,List<CtVariable<?>>>>();
	}
	@Override
	public boolean isToBeProcessed(CtVariable<?> variable) {
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
	
	public boolean areInTheSamePart(CtVariable<?> current, CtVariable<?> old) {
		HashSet<CtMethod<?>> methods = new HashSet<CtMethod<?>>();
		CtElement parent = current;
		while ((parent.isParentInitialized()) && ((parent = parent.getParent()) != null)) {
			if (parent instanceof CtMethod) {
				methods.add((CtMethod<?>) parent);
			}
		}
		parent = old;
		while ((parent.isParentInitialized()) && ((parent = parent.getParent()) != null)) {
			if (methods.contains(parent)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void process(CtVariable<?> variable) {
		CtCodeSnippetStatement snippet = getFactory().Core().createCodeSnippetStatement();
		StringBuilder sb = new StringBuilder();
		Collection<Integer> list = new LinkedList<Integer>();
		list.add(0);
		int i = 1;
		for (CtVariable<?> v : variablesCompatibles) {
			if (v != variable) {
				sb.append("(");
				sb.append(PREFIX);
				sb.append(index);
				sb.append("==");
				sb.append(i);
				sb.append(")?");
				sb.append(v.getSimpleName());
				sb.append(":");
				list.add(i);
			}
			i = i+1;
		}
		sb.append(variable.getSimpleName());
		snippet.setValue(sb.toString());
		//EnumSet<Integer> en = new HashSet<Integer>(list);
		
		//Selector.generateSelector(variable,i,index,en,PREFIX);

		((CtStatement)(variable)).replace(snippet);
	}
}
