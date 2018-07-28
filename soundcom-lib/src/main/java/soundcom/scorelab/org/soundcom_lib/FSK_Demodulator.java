package soundcom.scorelab.org.soundcom_lib;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.Collections;
import java.lang.Math;



public class FSK_Demodulator {


    private double sample_rate;
    private double symbol_size;
    private double sample_period;
    private int number_of_carriers;
    private int[] frequencies;
    private int fs;                    //Start Frequency
    private ArrayList<Integer> demodulated;
    private ArrayList<SignalGenerator> carriers;
    private ArrayList<Double> modulated;


    public FSK_Demodulator(double sample_rate, double symbol_size, ArrayList<Double> modulated) {
        this.sample_rate = sample_rate;
        this.symbol_size = symbol_size;
        this.sample_period = 1 / sample_rate;
        this.number_of_carriers = 16;
        this.fs = 6000;
        this.modulated = modulated;

        demodulated = new ArrayList<Integer>();


        initFrequencies();
        initCarriers();

    }

    public FSK_Demodulator() {
    }

    public void initFrequencies() {
        frequencies = new int[number_of_carriers];
        frequencies[0] = fs;
        for (int i = 1; i < number_of_carriers; i++) {

            frequencies[i] = frequencies[i - 1] + 625;

        }

    }

    public void initCarriers() {
        carriers = new ArrayList<SignalGenerator>();
        for (int i = 0; i < number_of_carriers; i++) {
            SignalGenerator s = new SignalGenerator(symbol_size, frequencies[i], 1.0 / 44100.0);
            s.generate();
            carriers.add(s);
            System.out.println("Carrier : " + i + " Initialized");
        }
    }


    public void demodulate() {
        try {
            double temp[] = toArray();
            ArrayList<Double> holder = new ArrayList<Double>();
            for (int i = 0; i < modulated.size(); i += (int) (symbol_size * sample_rate)) {

                double[] Symbol = Arrays.copyOfRange(temp, i, (int) (i + symbol_size * sample_rate));

                for (int j = 0; j < number_of_carriers; j++) {
                    holder.add(Math.abs(trapz(carriers.get(j).getData(), Symbol)));
                }

                double max = Collections.max(holder);

                for (int n = 0; n < holder.size(); n++) {
                    if (max == holder.get(n)) {
                        demodulated.addAll(getBits(n));
                    }
                }
                holder.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> getBits(int n) {
        System.out.println("Carrier number: " + n + " detected");

        ArrayList<Integer> bits = new ArrayList<Integer>();
        if (n == 0) {
            bits.add(0);
            bits.add(0);
            bits.add(0);
            bits.add(0);

        } else if (n == 1) {
            bits.add(0);
            bits.add(0);
            bits.add(0);
            bits.add(1);
        } else if (n == 2) {
            bits.add(0);
            bits.add(0);
            bits.add(1);
            bits.add(0);
        } else if (n == 3) {
            bits.add(0);
            bits.add(0);
            bits.add(1);
            bits.add(1);
        } else if (n == 4) {
            bits.add(0);
            bits.add(1);
            bits.add(0);
            bits.add(0);
        } else if (n == 5) {
            bits.add(0);
            bits.add(1);
            bits.add(0);
            bits.add(1);
        } else if (n == 6) {
            bits.add(0);
            bits.add(1);
            bits.add(1);
            bits.add(0);
        } else if (n == 7) {
            bits.add(0);
            bits.add(1);
            bits.add(1);
            bits.add(1);
        } else if (n == 8) {
            bits.add(1);
            bits.add(0);
            bits.add(0);
            bits.add(0);
        } else if (n == 9) {
            bits.add(1);
            bits.add(0);
            bits.add(0);
            bits.add(1);
        } else if (n == 10) {
            bits.add(1);
            bits.add(0);
            bits.add(1);
            bits.add(0);
        } else if (n == 11) {
            bits.add(1);
            bits.add(0);
            bits.add(1);
            bits.add(1);
        } else if (n == 12) {
            bits.add(1);
            bits.add(1);
            bits.add(0);
            bits.add(0);
        } else if (n == 13) {
            bits.add(1);
            bits.add(1);
            bits.add(0);
            bits.add(1);
        } else if (n == 14) {
            bits.add(1);
            bits.add(1);
            bits.add(1);
            bits.add(0);
        } else if (n == 15) {
            bits.add(1);
            bits.add(1);
            bits.add(1);
            bits.add(1);
        } else {
            try {
                throw new Exception("NOT A VALID NUMBER");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bits;
    }


    public double trapz(ArrayList<Double> Carrier, double[] Symbol) {
        double r = 0;
        try {
            if (Carrier.size() != Symbol.length) {
                System.out.println("Symbols Are Not the Same Size");
                throw new Exception("Symbols Are Not the Same Size");
            }

            double sum = 0;

            for (int i = 0; i < Carrier.size() - 1; i++) {
                double a = Carrier.get(i) * Symbol[i];
                double b = Carrier.get(i + 1) * Symbol[i + 1];
                sum += a + b;

            }
            r = sum * 0.5;

        } catch (Exception e) {
//            e.printStackTrace();
        }
        return r;

    }

    public double[] toArray() {
        double r[] = new double[modulated.size()];
        for (int i = 0; i < modulated.size(); i++) {
            r[i] = modulated.get(i);
        }
        return r;
    }

    public ArrayList<Integer> getDemodulated() {
        return demodulated;
    }

}
