package com.agimatec.validation.jsr303;

import com.agimatec.validation.constraints.Length;
import com.agimatec.validation.example.FrenchAddress;
import junit.framework.TestCase;

import javax.validation.*;
import java.util.Set;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 31.10.2008 <br/>
 * Time: 16:45:11 <br/>
 * Copyright: Agimatec GmbH
 */
public class ComposedConstraintsTest extends TestCase {
    static ValidatorFactory factory;

    static {
        factory = Validation.getBuilder().build();
    }

    public void testMetaDataAPI_ComposedConstraints() {
        Validator addressValidator = factory.getValidator();
        ElementDescriptor ed =
                addressValidator.getConstraintsForProperty(FrenchAddress.class, "zipCode");
        assertEquals(1, ed.getConstraintDescriptors().size());
        for (ConstraintDescriptor cd : ed.getConstraintDescriptors()) {
            assertTrue(cd.isReportAsViolationFromCompositeConstraint());
            assertEquals(3, cd.getComposingConstraints().size());
            System.out.println("params: " + cd.getParameters());
            assertTrue("no composing constraints found!!",
                    !cd.getComposingConstraints().isEmpty());
            processConstraintDescriptor(cd); //check all constraints on zip code
        }
    }

    public void processConstraintDescriptor(ConstraintDescriptor cd) {
        //Length.class is understood by the tool
        if (cd.getAnnotation().annotationType().equals(Length.class)) {
            Length m = (Length) cd.getAnnotation();
            System.out.println("size.max = " + m.max());  //read and use the metadata
        }
        for (ConstraintDescriptor composingCd : cd.getComposingConstraints()) {
            processConstraintDescriptor(composingCd); //check composing constraints recursively
        }
    }

    public void testValidateComposed() {
        FrenchAddress adr = new FrenchAddress();
        Validator val = factory.getValidator();
        Set<ConstraintViolation<FrenchAddress>> findings = val.validate(adr);
        assertEquals(1, findings.size()); // with @ReportAsSingleConstraintViolation

//        assertEquals(3, findings.size()); // without @ReportAsSingleConstraintViolation

        ConstraintViolation<FrenchAddress> finding = findings.iterator().next();
        assertEquals("Wrong zipcode", finding.getMessage());

        adr.setZipCode("12345");
        findings = val.validate(adr);
        assertEquals(0, findings.size());

        adr.setZipCode("1234567234567");
        findings = val.validate(adr);
        assertTrue(findings.size() > 0); // too long
    }
}
