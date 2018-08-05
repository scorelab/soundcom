package soundcom.scorelab.org.soundcom_lib;


import android.util.Log;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.jtransforms.fft.DoubleFFT_1D;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


public class MatchedFilter {

    private double duration;
    private double symbol_size;
    private double sample_rate;
    private double sample_period;

    private double[] chirp_fft;
    private double[] signal_fft;
    private double[] filter_out;


    private int max_index;
    private int start_index;

    private ArrayList<Double> modulated;
    private ArrayList<Double> chirp_signal;
    private double[] chirp_signal_a;

    private ArrayList<Double> recovered_signal;

    private DoubleFFT_1D fft;

    private SignalGenerator signal_generator;

    public MatchedFilter() {
    }

    public MatchedFilter(double duration, double symbol_size, double sample_rate, ArrayList<Double> modulated) {
        this.duration = duration;
        this.symbol_size = symbol_size;
        this.sample_rate = sample_rate;
        this.sample_period = 1.0 / sample_rate;
        this.modulated = modulated;

        this.recovered_signal = new ArrayList<Double>();

        getSync();
        matchSignal();
        recoverSignal();

    }

    public void getSync() {
        System.out.println("Generating Sync Signal");

        signal_generator = new SignalGenerator(symbol_size, 16000, sample_period);
        signal_generator.generate_sync();
        chirp_signal = signal_generator.getSync();
        chirp_signal_a = toArray((chirp_signal));

        Collections.reverse(chirp_signal);
    }


    public void matchSignal() {
        try {

            System.out.println("Applying matched Filter");

            System.out.println("This is size of zero padded modulated waveform " + modulated.size());
            fft = new DoubleFFT_1D(modulated.size());

            signal_fft = new double[modulated.size() * 2];
            System.arraycopy(toArray(modulated), 0, signal_fft, 0, modulated.size());
            fft.realForwardFull(signal_fft);

            //Working.
            chirp_fft = new double[modulated.size() * 2];
            System.arraycopy(chirp_signal_a, 0, chirp_fft, 0, chirp_signal_a.length);

            fft.realForwardFull(chirp_fft);
            System.out.println("Frequency Domain Transforms complete");


//            System.out.println("This is the size of signal_fft:" + signal_fft.length);
//            System.out.println("This is the size of chirp_fft:" + chirp_fft.length);

            filter_out = new double[signal_fft.length];
            for (int i = 0; i < signal_fft.length; i++) {
                filter_out[i] = chirp_fft[i] * signal_fft[i];
            }
            fft.realInverseFull(filter_out, true);
            System.out.println("Multiplication and IFFT Complete");

            filter_out = abs(filter_out);
            System.out.println("Absoloute Value found");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void recoverSignal() {
        try {
            System.out.println("Recovering Signal");

            double[] sorted = new double[filter_out.length];
            System.arraycopy(filter_out, 0, sorted, 0, filter_out.length);

            Arrays.sort(sorted);
            double max = sorted[sorted.length - 1];
            System.out.println("This is min: " + sorted[0]);
            System.out.println("This is max: " + max);

            System.out.println("Finding Max index");
            start_index = maxIndex(filter_out, max) +(int) (symbol_size*sample_rate);
            System.out.println("Offset is: " + (int) (symbol_size*sample_rate));

            for (int n = start_index; n < start_index +1323000; n++) {
                recovered_signal.add(modulated.get(n));
            }
        } catch (Exception e) {
            e.printStackTrace();
            recovered_signal.clear();
            recovered_signal.add(-1.0);
        }
    }

    public int maxIndex(double[] input, double max) {
        int max_ = 0;
        for (int i = 0; i < input.length; i++) {
            if (input[i] == max) {
                max_ = i;
                System.out.println("Max index found");
                break;
            }
        }
        return max_;
    }

    public ArrayList<Double> getRecovered_signal() {
        return recovered_signal;
    }

    public double[] toArray(ArrayList<Double> in) {
        double[] ret = new double[in.size() * 2];
        for (int i = 0; i < in.size(); i++) {
            ret[i] = in.get(i);
        }
        return ret;
    }

    public ArrayList<Double> toArrayList(double[] in) {
        ArrayList<Double> ret = new ArrayList<Double>();
        for (int i = 0; i < in.length; i++) {
            ret.add(in[i]);
        }
        return ret;
    }

    public double[] abs(double[] input) {
        double[] ret = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            ret[i] = Math.abs(input[i]);

        }
        return ret;
    }

    public ArrayList<Double> zeroPad(ArrayList<Double> input){
        ArrayList<Double> r  = new ArrayList<Double>();
        for (int i = 0; i < 268435456; i++) {
            if (i < input.size()){
                r.add (input.get(i));
            }
            else{
                r.add(-1.0);
            }
        }

        return r;
    }
}
