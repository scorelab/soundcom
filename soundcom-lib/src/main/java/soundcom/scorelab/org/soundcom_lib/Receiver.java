/**
 * Created by user on 29-05-2018.
 */
package soundcom.scorelab.org.soundcom_lib;
import android.content.Context;

import soundcom.scorelab.org.soundcom_lib.FSK_Demodulator;
import soundcom.scorelab.org.soundcom_lib.AudioHandler;
import soundcom.scorelab.org.soundcom_lib.ImageHandler;
import soundcom.scorelab.org.soundcom_lib.StringHanlder;
import soundcom.scorelab.org.soundcom_lib.MatchedFilter;
import soundcom.scorelab.org.soundcom_lib.Recorder;

import java.util.ArrayList;

/**
 * Created by misha on 2016/09/12.
 */
public class Receiver {

    private String file_name;

    private double sample_rate;
    private double symbol_size;
    private double sample_period;
    private double duration;
    private int number_of_carriers;
    private ArrayList<Double> modulated;
    private ArrayList<Integer> demodulated;


    //    private static ImageHandler image_handler;
    private FSK_Demodulator fsk_demodulator;
    private AudioHandler audio_handler;
    private StringHanlder string_handler;
    private MatchedFilter matched_filter;
    private ArrayList<Double> recoverd_signal;

    private String recoverd_string;


    private Context context;

    private Recorder r;

    public Receiver(String file_name, double sample_rate, double symbol_size, double duration, int number_of_carriers, Context context) {
        this.file_name = file_name;
        this.sample_rate = sample_rate;
        this.sample_period = 1.0 / sample_rate;
        this.symbol_size = symbol_size;
        this.duration = duration;
        this.number_of_carriers = number_of_carriers;
        this.context = context;

    }

    public void record() {
        try {
            r = new Recorder("TEST");
            r.start();
            Thread.sleep((long) duration * 1000);
            r.stop();
            System.out.println("Recorded Data");
            audio_handler = new AudioHandler(context, file_name);
            System.out.println("Reading Data");
            modulated = audio_handler.read();
            concatinateRecording();
            System.out.println("Initializing matched filter");

            matched_filter = new MatchedFilter(duration, symbol_size, sample_rate, modulated);
            recoverd_signal = new ArrayList<Double>();
            System.out.println("Signal Recovered.");

            recoverd_signal.addAll(matched_filter.getRecovered_signal());



            System.out.println("This is the size of recoeverd signal: " + recoverd_signal.size());


            audio_handler = new AudioHandler(toArray(recoverd_signal), context, "Recovered.wav");
            audio_handler.writeFile();
            audio_handler.close();

            fsk_demodulator = new FSK_Demodulator(sample_rate, symbol_size, recoverd_signal);

//            System.out.println("Writing Data File:");
//            writeData();
//            System.out.println("Data File Written");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void demodulate() {
        if(recoverd_signal.get(0) == -1.0)
        {
            recoverd_string = "-1";
            return;
        }

        System.out.println("Starting Demodulation");
        fsk_demodulator.demodulate();
        demodulated = fsk_demodulator.getDemodulated();
        System.out.println("This is the size of demodulated: " + demodulated.size());


        printBitStream();


        string_handler = new StringHanlder(demodulated);
        recoverd_string = string_handler.getString();


        System.out.println("THIS IS DEMODULATED STRING:");
        System.out.println("________________________________________________________________________________________________________");
        System.out.println(recoverd_string);
    }

    public void printBitStream() {
        for (int i = 0; i < demodulated.size(); i++) {
            System.out.print(demodulated.get(i));
        }
        System.out.println("");
    }

    public Double[] toArray(ArrayList<Double> input) {
        Double[] ret = new Double[input.size()];
        for (int i = 0; i < input.size(); i++) {
            ret[i] = input.get(i);
        }

        return ret;
    }


    public String getRecoverd_string() {
        return recoverd_string;
    }

    public void concatinateRecording() {
        System.out.println("Concateting recoded waveform.");

        while (modulated.size() != 524288) {
            modulated.remove(modulated.size() - 1);
        }
        System.out.println("This is the size of concatinated waveform: " + modulated.size());

    }
}
