package soundcom.scorelab.org.soundcom_lib;

import android.content.Context;
import android.widget.ImageView;

import soundcom.scorelab.org.soundcom_lib.FSK_Modulator;
import soundcom.scorelab.org.soundcom_lib.AudioHandler;
import soundcom.scorelab.org.soundcom_lib.ImageHandler;
import soundcom.scorelab.org.soundcom_lib.StringHanlder;

import java.util.ArrayList;

/**
 * Created by misha on 2016/09/12.
 */
public class Transmitter {

    private static String                      modulation;
    private static ImageView                   image;
    private static String                      src;
    private static double                      sample_rate;
    private static double                      symbol_size;
    private static int []                      data;
    private static double                      sample_period;
    private static int                         number_of_carriers;
    private static ArrayList<Double>           modulated;

    private static ImageHandler                image_handler;
    private static FSK_Modulator               fsk_modulator;
    private static AudioHandler                audio_handler;
    private static StringHanlder               string_handler;

    private static Context                     context;

    public Transmitter() {
    }

    public Transmitter(String modulation,ImageView image, double sample_rate, double symbol_size, double sample_period, int number_of_carriers,Context context) { //Contructor for image
        this.modulation = modulation;
        this.image = image;
        this.sample_rate = sample_rate;
        this.symbol_size = symbol_size;
        this.sample_period = sample_period;
        this.number_of_carriers = number_of_carriers;

        this.context  = context;

        initImage();
        initString();
        initModulator();
    }

    public Transmitter(String modulation,String src, double sample_rate, double symbol_size, double sample_period, int number_of_carriers,Context context) {//Constructore for String
        this.modulation = modulation;
        this.src = src;
        this.sample_rate = sample_rate;
        this.symbol_size = symbol_size;
        this.sample_period = sample_period;
        this.number_of_carriers = number_of_carriers;

        this.context  = context;

        long startTime = System.nanoTime();
        initString();
        initModulator();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;

        System.out.println("Time taken for modulation: " + duration + "ms") ;

    }

    public void initImage(){
        image_handler =     new ImageHandler(image);
        System.out.println("This is the length of Data : " + image_handler.getData_size());
        data =              new int [image_handler.getData_size()];
        data =              image_handler.getBitImage();

    }

    public void initString(){
        string_handler = new StringHanlder(src);
        System.out.println("This is the length of Data : " + string_handler.getB().length);
        data =              new int [string_handler.getB().length];
        data =              string_handler.getB();

    }

    public void initModulator (){
        if (modulation.equals("FSK")) {
            fsk_modulator = new FSK_Modulator(data, sample_rate, symbol_size, number_of_carriers);
            fsk_modulator.modulate();
            modulated = fsk_modulator.getModulated();
            System.out.println("This is the length of modulated : " + modulated.size());
        }
    }
    public void writeAudio (){
        audio_handler = new AudioHandler(castToDouble(modulated),context,"FSK.wav");
        audio_handler.writeFile();
        audio_handler.close();

    }
    public Double [] castToDouble(ArrayList<Double> in ){
       Double [] r = new Double[in.size()];
        for (int i = 0; i <in.size() ; i++) {
            r[i] = (Double) in.get(i);
        }
        return r;
    }
}
