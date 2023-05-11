package fi.arcada.sos_projekt_chart_sma;

public class Statistics {


    static double[] movingAvg(double[] temp, int window) {

        double[] ma = new double[temp.length];


        for (int i = window-1; i < temp.length; i++) {
            double sum = 0;
            for (int j = 0; j < window; j++) {
                sum += temp[i-j];
            }
            ma[i] = sum/window;

        }

        return ma;
    }

    static double[] sma(double[] dataset, int window){
        double[] ma = new double[dataset.length-window];

        for(int i = 0; i < ma.length; i++ ) {
            double sum = 0;
            for (int j = 0; j<window; j++){
                sum+=dataset[i-j+window];
            }
            ma[i] = sum/window;
        }
        return ma;
    }
}
