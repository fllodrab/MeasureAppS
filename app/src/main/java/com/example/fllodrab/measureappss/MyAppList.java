package com.example.fllodrab.measureappss;

import android.app.Application;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FllodraB
 * Clase para guardar un estado y que este no cambie durante la ejecución de la App (SINGLETON).
 */
public class MyAppList extends Notes {
    private List<MyApp> measures = new ArrayList<MyApp>();

    public List<MyApp> getMeasures() {
        return measures;
    }

    public void setMeasures(MyApp obj) {
        measures.add(obj);
    }
}
