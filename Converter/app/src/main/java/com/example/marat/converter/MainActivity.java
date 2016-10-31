package com.example.marat.converter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.marat.agimamobiletest.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    final String TAG = "myLog";

    TextView tvDate, tvDollar, tvEuro,tvResult;
    EditText fromEdit;
    EditText dateEdit;
    DatePicker datePicker;
    Spinner fromSpinner, toSpinner;
    Button convertBtn;
    MyTask mt;
    Currency currency;
    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        dateEdit = (EditText) findViewById(R.id.dateEdit);

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvDollar = (TextView) findViewById(R.id.tvDollar);
        tvEuro = (TextView) findViewById(R.id.tvEuro);

        fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        toSpinner = (Spinner) findViewById(R.id.toSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.currency,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);
        tvResult = (TextView) findViewById(R.id.tvResult);
        convertBtn = (Button) findViewById(R.id.convertBtn);
        fromEdit = (EditText) findViewById(R.id.fromEdit);
        fromSpinner.setPrompt("Перевести из:");
        toSpinner.setPrompt("Перевести в:");
        currency = new Currency();
        int month = calendar.get(Calendar.MONTH)+1;
        dateEdit.setText(setZero(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))) + "." +
                setZero(String.valueOf(month)) + "." + calendar.get(Calendar.YEAR));
        getCurrency();

    }

    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0,1,0,"База данных по курсам валют");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == 1){
            Intent intent = new Intent(this,DataActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    void AlDialog(){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Ошибка!");
        adb.setMessage("Нет соединения!");
        adb.setPositiveButton("Закрыть", null);
        adb.show();
    }

    String getDateFromSpinner(){
        String date, day, month, year;

        day = String.valueOf(datePicker.getDayOfMonth());
        month = String.valueOf(datePicker.getMonth() + 1);
        year = String.valueOf(datePicker.getYear());

        if(day.length() == 1) day = "0" + day;
        if(month.length() == 1) month = "0" + month;

        date = day + "." + month + "." + year;
        return date;
    }

     void setDollarAndEvro(){
         String dollarOnTV = "Доллар: " + currency.getDollarCurrency();
         String euroOnTV = "Евро: " + currency.getEuroCurrency();
         tvDollar.setText(dollarOnTV);
         tvEuro.setText(euroOnTV);
    }

    public void getCurrency(){
        mt = new MyTask(currency);
        mt.execute(dateEdit.getText().toString());
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.dateEdit:
                DatePickerDialog dateDialog = new DatePickerDialog(this,myCall,calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                dateDialog.show();
        }
    }

    DatePickerDialog.OnDateSetListener myCall = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateEdit.setText(setZero(String.valueOf(dayOfMonth)) + "." + setZero(String.valueOf(monthOfYear+1)) + "." + year);
            getCurrency();
            calendar.set(year,monthOfYear,dayOfMonth);
        }
    };

    private String setZero(String date){
        if(date.length() == 1){ return date = "0" + date; }
        else { return date; }
    }

    public void Convert(View v){
        int fromPosition = fromSpinner.getSelectedItemPosition();
        int toPosition = toSpinner.getSelectedItemPosition();
        String resultS = "Error of valute";
        if(fromEdit.getText().toString().isEmpty()){ return; }
        if(fromPosition == toPosition){
            tvResult.setText("Ошибка! Одна валюта в двух полях."); return;
        }
        if(fromPosition == 0 && toPosition == 1){
            resultS = currency.convertDollarToEuro(fromEdit.getText().toString());
        }
        if(fromPosition == 0 && toPosition == 2){
            resultS = currency.convertDollarToRubble(fromEdit.getText().toString());
        }
        if(fromPosition == 1 && toPosition == 0){
            resultS = currency.convertEuroToDollar(fromEdit.getText().toString());
        }
        if(fromPosition == 1 && toPosition == 2){
            resultS = currency.convertEuroToRubble(fromEdit.getText().toString());
        }
        if(fromPosition == 2 && toPosition == 0){
            resultS = currency.convertRubbletoDollar(fromEdit.getText().toString());
        }
        if(fromPosition == 2 && toPosition == 1){
            resultS = currency.convertRubbleToEuro(fromEdit.getText().toString());
        }
        tvResult.setText(resultS);
    }

    class MyTask extends AsyncTask<String, Void, Void> {

        final String TAG = "myLog";
        int NO_INTERNET_ERROR_ID = 0;
        private final String DOLLAR_ID = "R01235";
        private final String EURO_ID = "R01239";

        XmlPullParser parser = null;

        String dollar;
        String euro;
        Currency currency;

        MyTask(Currency currency){
            this.currency = currency;
        }

        @Override
        protected Void doInBackground(String... params) {
            getXmlFromUrl(params[0]);
            dollar = getCurrencyById(DOLLAR_ID);
            euro = getCurrencyById(EURO_ID);
            return null;
        }

        private void getXmlFromUrl(String date) {
            String urlAdress = "http://www.cbr.ru/scripts/XML_daily.asp?date_req=" + date;
             NO_INTERNET_ERROR_ID = 0;
            URL url = null;
            try {
                url = new URL(urlAdress);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                parser = XmlPullParserFactory.newInstance().newPullParser();
                parser.setInput(connection.getInputStream(),null);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                Log.d(TAG, "PARSE EXCEPTION");
            } catch (IOException e) {
               NO_INTERNET_ERROR_ID = 1;
                e.printStackTrace();
            }
        }

        private String getCurrencyById(String id) {

            String currency = "-1";
            String tag = "";
            String id_p = "";
            boolean isFounded = false;

            try {
                while(parser.getEventType() != XmlPullParser.END_DOCUMENT && !isFounded){
                    if(parser.getEventType() == XmlPullParser.START_TAG){
                        tag = parser.getName();
                        if(tag.equals("Valute")){
                            id_p = parser.getAttributeValue(0);
                        }
                    }
                    if(parser.getEventType() == XmlPullParser.TEXT && tag.equals("Value") && id_p.equals(id)){
                        currency = parser.getText();
                        isFounded = true;
                    }
                    parser.next();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            currency = currency.replaceAll("\\s","");
            currency = currency.replace(",",".");
            return currency;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            if(NO_INTERNET_ERROR_ID == 1){ AlDialog();}
            currency.setDollarCurrency(dollar);
            currency.setEuroCurrency(euro);
            setDollarAndEvro();
        }
    }
}
