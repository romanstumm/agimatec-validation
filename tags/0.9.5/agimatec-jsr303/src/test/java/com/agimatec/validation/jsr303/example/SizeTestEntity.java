package com.agimatec.validation.jsr303.example;

import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Map;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 18.11.2009 <br/>
 * Time: 11:58:46 <br/>
 * Copyright: Agimatec GmbH
 */
public class SizeTestEntity {
    @Size(max=2)
    public Map<String,String> map;
    @Size(max=2)
    public Collection<String> coll;
    @Size(max=2)
    public String text;

    @Size(max=2)
    public Object[] oa;
    @Size(max=2)
    public byte[] ba;
    @Size(max=2)
    public int[] it;
    @Size(max=2)
    public Integer[] oa2;
    @Size(max=2)
    public boolean[] boa;
    @Size(max=2)
    public char[] ca;
    @Size(max=2)
    public double[] da;
    @Size(max=2)
    public float[] fa;
    @Size(max=2)
    public long[] la;
    @Size(max=2)
    public short[] sa;
}
