package org.jodaengine.forms.processor.juel;

import java.util.ArrayList;
import java.util.Map;

import net.htmlparser.jericho.Config;
import net.htmlparser.jericho.FormField;
import net.htmlparser.jericho.FormFields;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

import org.jodaengine.forms.processor.FormProcessor;
import org.jodaengine.process.instance.ProcessInstanceContext;
import org.jodaengine.resource.allocation.Form;

/**
 * This class uses JUEL to fill in a form and pass results.
 * Variables are set and read hierarchically.
 * See method comments.
 */
public class JuelFormProcessor implements FormProcessor {

    private AbstractFormFieldHandler firstHandler;

    /**
     * Instantiates a new juel form processor using a chain of responsibility to realize the hierachy as described in
     * the interface {@link FormProcessor}.
     */
    public JuelFormProcessor() {

        firstHandler = new JuelExpressionHandler();
        firstHandler.setNext(new ContextVariableHandler());
    }

    @Override
    public String prepareForm(Form form, ProcessInstanceContext context) {

        Config.CurrentCompatibilityMode.setFormFieldNameCaseInsensitive(false);
        String formContent = form.getFormContentAsHTML();
        Source source = new Source(formContent);
        FormFields formFields = source.getFormFields();
        OutputDocument document = new OutputDocument(source);

        firstHandler.setFormValues(form, new ArrayList<FormField>(formFields), context, document);
        document.replace(formFields);

        return document.toString();

    }

    @Override
    public void readFilledForm(Map<String, String> enteredValues, Form form, ProcessInstanceContext context) {

        firstHandler.readInput(enteredValues, form, context);
//        for (Entry<String, String> entry : enteredValues.entrySet()) {
//            String fieldName = entry.getKey();
//            String enteredValue = entry.getValue();
//
//            JodaFormField formField = form.getFormField(fieldName);
//            Object objectToSet = convertStringInput(enteredValue, formField.getDataClazz());
//            String variableToSet = formField.getWriteVariable();
//            context.setVariable(variableToSet, objectToSet);
//        }
    }

    

}