package soundcom.scorelab.org.soundcom_lib;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class SignalGenerator {
    private double symbol_size;
    private double f;
    private double step_size;
    private double sample_rate;
    private ArrayList<Double> data;
    private ArrayList<Double> sync;

    public SignalGenerator(double symbol_size, double f, double step_size) {
        this.symbol_size = symbol_size;
        this.f = f;
        this.step_size = step_size;
        this.sample_rate = 1.0 / step_size;
    }

    public double getSymbol_size() {
        return symbol_size;
    }

    public void setSymbol_size(double symbol_size) {
        this.symbol_size = symbol_size;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getStep_size() {
        return step_size;
    }

    public void setStep_size(double step_size) {
        this.step_size = step_size;
    }

    public ArrayList<Double> getData() {
        return data;
    }

    public ArrayList<Double> getSync() {
        return sync;
    }

    public ArrayList<Double> generate() {

        try {
            data = new ArrayList<Double>();
            double rad = 0;
//        System.out.println("Starting Generation for carrier with freuqnecy: " + f);
            for (int i = 0; i < symbol_size / step_size; i++) {
                rad = (2 * Math.PI * f * i * step_size);
                data.add(Math.cos(rad));
//            System.out.println("Value of cos at " + i + " is " +data.get(i));
            }
//        System.out.println("Data generated for Carrier at : " + f);
        }catch(Exception e){
            e.printStackTrace();
        }
        return data;
    }


    public ArrayList<Double> generate_sync() {

        sync = new ArrayList<Double>();
        double rad = 0;
        double k = ((16000 - 6000) / symbol_size);

//        System.out.println("Starting Generation for carrier with freuqnecy: " + f);
        for (int i = 0; i < symbol_size * sample_rate; i++) {

            rad = (2 * Math.PI * ((k / 2) * i * step_size + 6000) * i * step_size);
            sync.add(Math.cos(rad));
//            System.out.println("Value of cos at " + i + " is " +data.get(i));
        }
//        System.out.println("Data generated for Carrier at : " + f);
        return sync;

    }

}
