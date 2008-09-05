package javax.validation;

import java.util.Collection;
import java.util.HashSet;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 05.09.2008 <br/>
 * Time: 11:02:24 <br/>
 * Copyright: Agimatec GmbH
 */
public class RecursiveFoo {
    @NotEmpty
    @Valid
    Collection<RecursiveFoo> foos = new HashSet();

    public Collection<RecursiveFoo> getFoos() {
        return foos;
    }

    public void setFoos(Collection<RecursiveFoo> foos) {
        this.foos = foos;
    }
}
