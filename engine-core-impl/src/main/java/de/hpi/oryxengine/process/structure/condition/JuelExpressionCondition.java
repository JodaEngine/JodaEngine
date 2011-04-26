package de.hpi.oryxengine.process.structure.condition;

import java.util.Map;
import java.util.Map.Entry;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hpi.oryxengine.process.structure.Condition;
import de.hpi.oryxengine.process.token.Token;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;

/**
 * This {@link Condition} accepts JuelExpression. It means that this Condition is able to process a juelExpression
 */
public class JuelExpressionCondition implements Condition {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String juelExpression;

    public JuelExpressionCondition(String juelEspression) {

        this.juelExpression = juelEspression;
    }

    @Override
    public boolean evaluate(Token token) {

        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();

        Map<String, Object> variableMap = token.getInstance().getContext().getVariableMap();
        if (variableMap != null) {

            // Binding the variables that are in the processContext with the declared variabxles in the expression
            for (Entry<String, Object> theEntry : variableMap.entrySet()) {
                String theEntryKey = theEntry.getKey();
                Object theEntryValue = theEntry.getValue();
                ValueExpression valueExpression =
                    factory.createValueExpression(theEntryValue, theEntryValue.getClass());
                context.setVariable(theEntryKey, valueExpression);
            }
        }

        ValueExpression e = factory.createValueExpression(context, juelExpression, boolean.class);

        return (Boolean) e.getValue(context);
    }
}