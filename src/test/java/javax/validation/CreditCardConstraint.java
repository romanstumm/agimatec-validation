package javax.validation;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 02.04.2008 <br/>
 * Time: 18:13:37 <br/>
 *
 */
public class CreditCardConstraint implements Constraint<CreditCard>{
    public void initialize(CreditCard constraintAnnotation) {
        // do nothing
    }

    public boolean isValid(Object value) {
        // TODO RSt - not implemented, just an example
        return true;
    }
}
