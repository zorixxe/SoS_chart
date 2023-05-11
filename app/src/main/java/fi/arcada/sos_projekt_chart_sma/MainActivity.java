package fi.arcada.sos_projekt_chart_sma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //kek

    String currency, datefrom, dateto;

    LineChart chart;
    double[] currencyValuesArray;
    int smaval1, smalval2;

    boolean state1, state2;
    TextView DateText;
    Button SMABTN1, SMABTN2;
    FloatingActionButton settingBTN;

    SharedPreferences sharedpref;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpref = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = sharedpref.edit();

        try {
            String value1 = sharedpref.getString("sma1", "10");
            String value2 = sharedpref.getString("sma2", "20");

            smaval1 = Integer.parseInt(value1);
            smalval2 = Integer.parseInt(value2);

        } catch (NumberFormatException e) {

            smaval1 = 10;
            smalval2 = 20;
            Toast.makeText(this, "sma has been set to defult as input is not valid", Toast.LENGTH_LONG).show();
        }



     /**   smaval1 = Integer.parseInt(sharedpref.getString("sma1", "10"));
        smalval2 = Integer.parseInt(sharedpref.getString("sma2", "20"));
      */

        currency = sharedpref.getString("reply", "SEK");
        datefrom = sharedpref.getString("datefrom", "2022-01-01");
        dateto = sharedpref.getString("dateto", "2022-04-01");

        chart = (LineChart) findViewById(R.id.chart);
        SMABTN1 = findViewById(R.id.SMABTN1);
        SMABTN2 = findViewById(R.id.SMABTN2);
        settingBTN = findViewById(R.id.settingBTN);

        DateText = findViewById(R.id.DateText);

        state1 = false;
        state2 = false;


        SMABTN1.setText("SMA " + smaval1);
        SMABTN2.setText("SMA " + smalval2);
        DateText.setText(currency + " || " + datefrom + " - " + dateto);


        // TEMPORÄRA VÄRDEN


        // Hämta växelkurser från API
        ArrayList<Double> currencyValues = getCurrencyValues(currency, datefrom, dateto);

        currencyValuesArray = new double[currencyValues.size()];
        for (int i = 0; i < currencyValues.size(); i++) {
            currencyValuesArray[i] = currencyValues.get(i);
        }


        // Skriv ut dem i konsolen
     //   System.out.println(currencyValues.toString());


    //    simpleChart(temp);


        Toast.makeText(this, "kek", Toast.LENGTH_LONG).show();

// Version 2
        // Vi skapar en ArrayList med våra datalinjer
        ArrayList<DataLine> dataLines = new ArrayList<>();

        dataLines.add(new DataLine(currencyValuesArray, currency, 0, Color.BLACK));
        // En till datalinje med glidande medelvärde (sma)



        // Anropa vår metod
        betterChart(dataLines);

    }

    // Version 2
    // metod för att skapa linjediagram med flera linjer (dataserier som objekt)
    public void betterChart(ArrayList<DataLine> dataLines) {

        // vi behöver en till ArrayList för dataserierna
        List<ILineDataSet> dataSeries = new ArrayList<>();

        // Vi loopar våra datalinjer och sätter in varje linje i dataSeries
        for (DataLine dataLine: dataLines) {
            LineDataSet lineDataSet = new LineDataSet(dataLine.getEntries(), dataLine.label);
            lineDataSet.setColor(dataLine.color);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setDrawValues(false);
            // Lägg till linjen till nuvarande dataserie
            dataSeries.add(lineDataSet);
        }

        LineData lineData = new LineData(dataSeries);

        chart.setData(lineData);
        chart.invalidate();
    }



    // Färdig metod som hämtar växelkursdata
    public ArrayList<Double> getCurrencyValues(String currency, String from, String to) {

        CurrencyApi api = new CurrencyApi();
        ArrayList<Double> currencyData = null;

        String urlString = String.format("https://api.exchangerate.host/timeseries?start_date=%s&end_date=%s&symbols=%s",
                from.trim(),
                to.trim(),
                currency.trim());

        try {
            String jsonData = api.execute(urlString).get();

            if (jsonData != null) {
                currencyData = api.getCurrencyData(jsonData, currency.trim());
                Toast.makeText(getApplicationContext(), String.format("Hämtade %s valutakursvärden från servern", currencyData.size()), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Kunde inte hämta växelkursdata från servern: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return currencyData;
    }





    public void openSettings(View view){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void setSMABTN1 (View view){
        ArrayList<DataLine> dataLines = new ArrayList<>();
        double[] sma1 = Statistics.sma(currencyValuesArray, smaval1);
        double[] sma2 = Statistics.sma(currencyValuesArray, smalval2);
        dataLines.add(new DataLine(currencyValuesArray, currency, 0, Color.BLACK));


        if(state1 == false){

            dataLines.add(new DataLine(sma1, "SMA-" + smaval1, smaval1, Color.RED));

            state1 = true;
        }

        else state1 = false;

        if (state2){
            dataLines.add(new DataLine(sma2, "SMA-" + smalval2, smalval2, Color.BLUE));

        }


        betterChart(dataLines);
    }

    public void setSMBTN2 (View view){

        ArrayList<DataLine> dataLines = new ArrayList<>();
        double[] sma1 = Statistics.sma(currencyValuesArray, smaval1);
        double[] sma2 = Statistics.sma(currencyValuesArray, smalval2);
        dataLines.add(new DataLine(currencyValuesArray, currency, 0, Color.BLACK));


        if(state2 == false){

            dataLines.add(new DataLine(sma2, "SMA-" + smalval2, smalval2, Color.BLUE));

            state2 = true;
        }

        else state2 = false;

        if (state1){
            dataLines.add(new DataLine(sma1, "SMA-" + smaval1, smaval1, Color.RED));
        }


        betterChart(dataLines);
    }

}

