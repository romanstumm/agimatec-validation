package com.agimatec.validation.jsr303.groups.inheritance;

import com.agimatec.validation.jsr303.groups.Billable;

import javax.validation.groups.Default;

/**
 * Customer can buy without harrassing checking process.
 * spec: Example 3.3. Groups can inherit other groups
 */
public interface BuyInOneClick extends Default, Billable {
}
