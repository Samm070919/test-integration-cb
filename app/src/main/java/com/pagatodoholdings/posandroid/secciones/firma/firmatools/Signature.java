package com.pagatodoholdings.posandroid.secciones.firma.firmatools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jesusmario on 27/11/17.
 */

public class Signature implements Serializable {

    private static final long serialVersionUID = -7332384231293918281L;

    private final List<Integer> signatureStrokes = new ArrayList<>();

    public Signature() {

        this.signatureStrokes.clear();
    }


    public void newStroke() {
       final int curentSignStroke = 1;
        signatureStrokes.add(curentSignStroke);
    }


    public int getNumberStrokes() {
        return signatureStrokes.size();
    }

}
