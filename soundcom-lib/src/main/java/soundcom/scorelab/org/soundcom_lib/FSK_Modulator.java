package soundcom.scorelab.org.soundcom_lib;

import java.lang.*;
import java.util.*;


public class FSK_Modulator {

    private static int[] data;
    private static double sample_rate;
    private static double symbol_size;
    private static double sample_period;
    private static int number_of_carriers;
    private static int[] frequencies;
    private static int fs;                    //Start Frequency
    private static ArrayList<Double> modulated;
    private static ArrayList<SignalGenerator> carriers;


    //
    int s_0[] = {0, 0, 0, 0};
    int s_1[] = {0, 0, 0, 1};
    int s_2[] = {0, 0, 1, 0};
    int s_3[] = {0, 0, 1, 1};
    int s_4[] = {0, 1, 0, 0};
    int s_5[] = {0, 1, 0, 1};
    int s_6[] = {0, 1, 1, 0};
    int s_7[] = {0, 1, 1, 1};
    int s_8[] = {1, 0, 0, 0};
    int s_9[] = {1, 0, 0, 1};
    int s_10[] = {1, 0, 1, 0};
    int s_11[] = {1, 0, 1, 1};
    int s_12[] = {1, 1, 0, 0};
    int s_13[] = {1, 1, 0, 1};
    int s_14[] = {1, 1, 1, 0};
    int s_15[] = {1, 1, 1, 1};


    //

    public FSK_Modulator(int[] data, double sample_rate, double symbol_size, int number_of_carriers) {
        this.data = data;
        this.sample_rate = sample_rate;
        this.symbol_size = symbol_size;
        this.sample_period = 1 / sample_rate;
        this.number_of_carriers = 16;                //Make this ` for now.
        this.fs = 6000;
        initFrequencies();
        initCarriers();
    }

    public FSK_Modulator() {
    }

    public void initFrequencies() {
        frequencies = new int[number_of_carriers];
        frequencies[0] = fs;
//        System.out.println("This is frequency: " + frequencies[0]);
        for (int i = 1; i < number_of_carriers; i++) {

            frequencies[i] = frequencies[i -1] + 625;
//            System.out.println("This is frequency: " + frequencies[i]);
        }

    }

    public void initCarriers() {
        carriers = new ArrayList<SignalGenerator>();
        for (int i = 0; i < number_of_carriers; i++) {
            SignalGenerator s = new SignalGenerator(symbol_size, frequencies[i], 1.0 / 44100.0);
            carriers.add(s);
            System.out.println("Carrier : " + i + " Initialized");
        }
    }

    public void modulate() {


        int temp[] = new int[4];


        modulated = new ArrayList<Double>();
        modulated.addAll(carriers.get(0).generate_sync()); //Adding the synchronization signal.
        System.out.println("Starting Modulation Generation");
        for (int i = 0; i < data.length - 3; i += 4) {
            temp[0] = data[i];
            temp[1] = data[i + 1];
            temp[2] = data[i + 2];
            temp[3] = data[i + 3];

//            Log.d("RedTooth", "Running number: " + i );

            map(temp);

        }
        System.out.println("Done Generating Modulation Data");
    }

    public void map  (int[] temp   )   {
        if (Arrays.equals(temp, s_0)) {
            modulated.addAll(carriers.get(0).generate());

        } else if (Arrays.equals(temp, s_1)) {
            modulated.addAll(carriers.get(1).generate());

        } else if (Arrays.equals(temp, s_2)) {
            modulated.addAll(carriers.get(2).generate());

        } else if (Arrays.equals(temp, s_3)) {
            modulated.addAll(carriers.get(3).generate());

        } else if (Arrays.equals(temp, s_4)) {
            modulated.addAll(carriers.get(4).generate());

        } else if (Arrays.equals(temp, s_5)) {
            modulated.addAll(carriers.get(5).generate());

        } else if (Arrays.equals(temp, s_6)) {
            modulated.addAll(carriers.get(6).generate());

        } else if (Arrays.equals(temp, s_7)) {
            modulated.addAll(carriers.get(7).generate());

        } else if (Arrays.equals(temp, s_8)) {
            modulated.addAll(carriers.get(8).generate());

        } else if (Arrays.equals(temp, s_9)) {
            modulated.addAll(carriers.get(9).generate());

        } else if (Arrays.equals(temp, s_10)) {
            modulated.addAll(carriers.get(10).generate());

        } else if (Arrays.equals(temp, s_11)) {
            modulated.addAll(carriers.get(11).generate());

        } else if (Arrays.equals(temp, s_12)) {
            modulated.addAll(carriers.get(12).generate());

        } else if (Arrays.equals(temp, s_13)) {
            modulated.addAll(carriers.get(13).generate());

        } else if (Arrays.equals(temp, s_14)) {
            modulated.addAll(carriers.get(14).generate());

        } else if (Arrays.equals(temp, s_15)) {
            modulated.addAll(carriers.get(15).generate());
        }

    }


    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
    }

    public double getSample_rate() {
        return sample_rate;
    }

    public void setSample_rate(double sample_rate) {
        this.sample_rate = sample_rate;
    }

    public double getSymbol_size() {
        return symbol_size;
    }

    public void setSymbol_size(double symbol_size) {
        this.symbol_size = symbol_size;
    }

    public double getSample_period() {
        return sample_period;
    }

    public void setSample_period(double sample_period) {
        this.sample_period = sample_period;
    }

    public int getNumber_of_carriers() {
        return number_of_carriers;
    }

    public void setNumber_of_carriers(int number_of_carriers) {
        this.number_of_carriers = number_of_carriers;
    }

    public int[] getFrequencies() {
        return frequencies;
    }

    public void setFrequencies(int[] frequencies) {
        this.frequencies = frequencies;
    }

    public int getFs() {
        return fs;
    }

    public void setFs(int fs) {
        this.fs = fs;
    }

    public ArrayList<Double> getModulated() {
        return modulated;
    }

    public void setModulated(ArrayList<Double> modulated) {
        this.modulated = modulated;
    }

    public ArrayList<SignalGenerator> getCarriers() {
        return carriers;
    }

    public void setCarriers(ArrayList<SignalGenerator> carriers) {
        this.carriers = carriers;
    }
}
