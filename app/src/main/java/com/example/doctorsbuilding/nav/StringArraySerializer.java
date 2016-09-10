package com.example.doctorsbuilding.nav;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by hossein on 8/13/2016.
 */
public class StringArraySerializer extends Vector<String> implements KvmSerializable {

    String n1 = "http://docTurn/";

    @Override
    public Object getProperty(int arg0) {
        return this.get(arg0);
    }

    @Override
    public int getPropertyCount() {
        return this.size();
    }

    @Override
    public void setProperty(int arg0, Object arg1) {
        this.add(arg1.toString());
    }

    @Override
    public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
        arg2.setName("string");
        arg2.type = PropertyInfo.STRING_CLASS;
        arg2.setNamespace(n1);
    }
}
