package soundcom.scorelab.org.soundcom_lib;


import java.util.ArrayList;

/**
 * Created by misha on 2016/09/15.
 */
public class StringHanlder {
    private String src;
    private int[] b;
    private int bits;

    private static ArrayList<Integer> demodulated;

    public StringHanlder(String src) { //Constructor For Getting Binary Sequence
        this.src = src;
        this.bits = 16;                 //16 for UTF-16
        generate();
    }

    public StringHanlder(ArrayList<Integer> demodulated) {
        this.demodulated = demodulated;
        this.bits = 16;                 //16 for UTF-16

    }

    public void generate() {
        String temp = toBinary();
        System.out.println("This is Binary String temp: " + temp);
        System.out.println("This is the size of  Binary String temp: " + temp.length());
        b = new int[temp.length()];
        for (int i = 0; i < temp.length() - 1; i++) {
            b[i] = Integer.parseInt(temp.substring(i, i + 1));
        }
    }

    public String toBinary() {
        String result = "";
        String tmpStr;
        int tmpInt;
        char[] messChar = src.toCharArray();

        for (int i = 0; i < messChar.length; i++) {
            tmpStr = Integer.toBinaryString(messChar[i]);
            tmpInt = tmpStr.length();
            if (tmpInt != bits) {
                tmpInt = bits - tmpInt;
                if (tmpInt == bits) {
                    result += tmpStr;
                } else if (tmpInt > 0) {
                    for (int j = 0; j < tmpInt; j++) {
                        result += "0";
                    }
                    result += tmpStr;
                } else {
                    System.err.println("argument 'bits' is too small");
                }
            } else {
                result += tmpStr;
            }
//            result += " "; // separator
        }

        return result;
    }

    public String getString() {
        String bin = arToString();
        StringBuilder b = new StringBuilder();
        int len = bin.length();
        int i = 0;
        while (i + 16 <= len) {
            char c = convert(bin.substring(i, i + 16));
            i += 16;
            b.append(c);
        }
        String recovered = b.toString();
        System.out.println(recovered);
        return recovered;
    }

    private char convert(String bs) {
        return (char) Integer.parseInt(bs, 2);
    }

    public String arToString() {
        String r = "";
        for (int i : demodulated) {
            r +=i+"";

        }
        return r;
    }

    public int[] getB() {
        return b;
    }
}
