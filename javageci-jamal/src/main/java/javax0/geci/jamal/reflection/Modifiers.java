package javax0.geci.jamal.reflection;

import javax0.geci.jamal.Reflection;
import javax0.geci.jamal.util.EntityStringer;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.ModifiersBuilder;
import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

import static javax0.geci.jamal.util.EntityStringer.isFingerPrintAField;

public class Modifiers implements Macro {
    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        final var fingerPrint = in.toString().trim();
        final int modifiers;
        if (isFingerPrintAField(fingerPrint)) {
            var field = EntityStringer.fingerprint2Field(fingerPrint);
            modifiers = field.getModifiers();
        } else {
            var method = EntityStringer.fingerprint2Method(fingerPrint);
            modifiers = method.getModifiers();
        }
        return GeciReflectionTools.unmask(modifiers);
    }
}