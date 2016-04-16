package metamutator;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;

public class UnaryOperatorMetaMutator extends AbstractProcessor<CtExpression<Boolean>> {
	public static final String PREFIX =  "_unaryOperatorHotSpot";
	private static int index = 0;
	
	public enum UnaryOperator {
		NOT,
		SAME
	};
	
	private static final EnumSet<UnaryOperator> UNARY_OPERATORS = EnumSet.of(UnaryOperator.SAME,UnaryOperator.NOT);
	
	@Override
	public boolean isToBeProcessed(CtExpression<Boolean> element) {
		
		if (!isABooleanExpression(element)) {
			return false;
		}
		
		try {
			Selector.getTopLevelClass(element);
		} catch (NullPointerException e) {
			return false;
		}
		
		if (element.isParentInitialized() && element.getParent(CtConstructor.class) != null) {
			return false;
		}
		
		if (element.isParentInitialized() && element.getParent(CtField.class) != null) {
				return false;
		}
		
		if (element.isParentInitialized() && element.getParent(CtAnonymousExecutable.class) != null) {
			return false;
		}
		
		if (element instanceof CtStatement) {
			return false;
		}
		return true;
	}
	public boolean isABooleanExpression(CtExpression<Boolean> element) {
		if (element.getType() == null) {
			return false;
		}
		String type = element.getType().toString().toUpperCase();
		return type.contains("BOOLEAN");
	}
	public void process(CtExpression<Boolean> booleanExpression) {
		mutateOperator(booleanExpression,UNARY_OPERATORS);
	}
	
	private void mutateOperator(CtExpression<?> booleanExpression, EnumSet<UnaryOperator> operators) {
		int thisIndex = ++index;
		
		String originalExpression = booleanExpression.toString();
		
		String newExpression = UNARY_OPERATORS
				.stream()
				.map(op -> {
					String expr = originalExpression;
					if (op == UnaryOperator.NOT) {
						expr = "!(" + originalExpression + ")";
					}
					return String.format("("+ PREFIX + "%s.is(%s) && (%s))",
							thisIndex,op.getClass().getCanonicalName()+"."+op.toString(), expr);
				}).collect(Collectors.joining(" || "));
		
		
		CtCodeSnippetExpression<Boolean> codeSnippet = getFactory().Core()
				.createCodeSnippetExpression();
		codeSnippet.setValue('(' + newExpression + ')');
		
		Selector.generateSelector(booleanExpression,UnaryOperator.SAME,thisIndex,UNARY_OPERATORS,PREFIX);

		((CtExpression<Boolean>)booleanExpression).replace(codeSnippet);
		
	}
}
