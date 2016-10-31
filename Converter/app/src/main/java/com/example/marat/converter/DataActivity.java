package com.example.marat.converter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.marat.agimamobiletest.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataActivity extends AppCompatActivity {

    final String T = "myLog";

    private final String DATE_ID = "date";
    private final String RATING_ID = "rating";
    private final String CURRENCY_ID = "currency";
    private final String MODE_GRAPHIC = "Graphic";
    private final String MODE_LIST = "List";

    ImageView graphicImg;
    ListView currencyList;
    EditText fromDate,toDate;
    Spinner currencySpinner;
    RadioGroup typeOfData;
    Button getDataBtn;

    int dateEditNum = 0;
    int fromDateDay,fromDateMonth,fromDateYear;
    int toDateDay,toDateMouth,toDateYear;
    String dateFrom,dateTo;
    String mode;

    Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fromDate = (EditText) findViewById(R.id.fromDate);
        toDate = (EditText) findViewById(R.id.toDate);
        currencySpinner  = (Spinner) findViewById(R.id.currencySpinner);
        typeOfData = (RadioGroup) findViewById(R.id.typeOfData);
        getDataBtn = (Button) findViewById(R.id.getDataBtn);
        currencyList = (ListView) findViewById(R.id.currencyList);
        graphicImg = (ImageView) findViewById(R.id.graphicImg);
        fromDateDay = calendar.get(Calendar.DAY_OF_MONTH);
        fromDateMonth = (calendar.get(Calendar.MONTH)+1);
        fromDateYear = calendar.get(Calendar.YEAR);
        toDateDay = fromDateDay;
        toDateMouth = fromDateMonth;
        toDateYear = fromDateYear;
        dateFrom = setZero(String.valueOf(fromDateDay))+"."+setZero(String.valueOf(fromDateMonth))+"."+String.valueOf(fromDateYear);
        dateTo = setZero(String.valueOf(toDateDay))+"."+setZero(String.valueOf(toDateMouth))+"."+String.valueOf(toDateYear);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.currencyOfData,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mode = MODE_LIST;
        currencySpinner.setAdapter(adapter);
        fromDate.setText(dateFrom);
        toDate.setText(dateTo);

    }

    private String getUrlFromSetting(){
        String finalURL = "";
        String currency_id = "";
        int spinnerItemSelected = currencySpinner.getSelectedItemPosition();

        if(spinnerItemSelected == 0){ currency_id = "R01235"; }
        if(spinnerItemSelected == 1) { currency_id = "R01239"; }

        if(typeOfData.getCheckedRadioButtonId() == R.id.listRadioButton){
            mode = MODE_LIST;
            graphicImg.setVisibility(View.INVISIBLE);
            currencyList.setVisibility(View.VISIBLE);
            finalURL = "http://www.cbr.ru/currency_base/dynamics.aspx?VAL_NM_RQ="+currency_id+"&date_req1="+dateFrom+"&date_req2="+dateTo+"&rt=1&mode=1";
        }
        if(typeOfData.getCheckedRadioButtonId() == R.id.graphicRadioButton){
            mode = MODE_GRAPHIC;
            currencyList.setVisibility(View.INVISIBLE);
            graphicImg.setVisibility(View.VISIBLE);
            finalURL = "http://www.cbr.ru/currency_base/GrafGen.aspx?date_req1="+dateFrom+"&date_req2="+dateTo+"&VAL_NM_RQ="+currency_id;
        }
        return finalURL;
    }

    public void onClickButton(View v){
        ParseData pd = new ParseData();
        pd.execute(getUrlFromSetting(),mode);
    }

    public void onClickDate(View v){
        int d = calendar.get(Calendar.DAY_OF_MONTH),m = calendar.get(Calendar.MONTH),y = calendar.get(Calendar.YEAR);

        if(v.getId() == R.id.fromDate){
            d = fromDateDay;
            m = fromDateMonth;
            y = fromDateYear;
            dateEditNum = 1;
        }
        if(v.getId() == R.id.toDate){
            d = toDateDay;
            m = toDateMouth;
            y = toDateYear;
            dateEditNum = 2;
        }
        DatePickerDialog f = new DatePickerDialog(this,myCall,y,m-1,d);
        f.show();
    }

    DatePickerDialog.OnDateSetListener myCall = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (dateEditNum == 1) {
                fromDateDay = dayOfMonth;
                fromDateMonth = monthOfYear+1;
                fromDateYear = year;
                dateFrom = setZero(String.valueOf(fromDateDay)) + "." + setZero(String.valueOf(fromDateMonth)) + "." + year;
                fromDate.setText(dateFrom);
            }
            if(dateEditNum == 2){
                toDateDay = dayOfMonth;
                toDateMouth = monthOfYear+1;
                toDateYear = year;
                dateTo = setZero(String.valueOf(toDateDay)) + "." + setZero(String.valueOf(toDateMouth)) + "." + year;
                toDate.setText(dateTo);
            }
        }
    };

    void AlDialog(String message){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Ошибка!");
        adb.setMessage(message);
        adb.setPositiveButton("Закрыть", null);
        adb.show();
    }

    private void setList(List<Map<String,String>> l){
        List<Map<String,String>> listMain = l;
        if(listMain.size() == 0){ return; }
        String from [] = {DATE_ID, RATING_ID, CURRENCY_ID};
        int to [] = {R.id.dateTV, R.id.ratingTV, R.id.currencyTV};
        SimpleAdapter adapter = new SimpleAdapter(this,listMain,R.layout.item,from,to);
        currencyList.setAdapter(adapter);
    }

    String setZero(String date){
        if(date.length() == 1){ return date = "0" + date; }
        else { return date; }
    }

    class ParseData extends AsyncTask<String,Void,Void>{

        private Bitmap bitmap = null;
        private List<Map<String,String>> list;
        private Map<String,String> map;
        private int error_code = 0;

        protected void onPreExecute(){
            error_code = 0;
        }

        protected Void doInBackground(String... values){
            String urlforParse = values[0];
            if(values[1].equals("Graphic")) {
                bitmap = getImage(urlforParse);
            }
            if(values[1].equals("List")){
                getList(urlforParse);
            }
            return null;
        }

        private void getList(String urlOfList){
            try {
                list = new ArrayList<Map<String,String>>();
                Document doc = Jsoup.connect(urlOfList).get();
                Elements elements = doc.getElementsByClass("data").get(0).getElementsByTag("td");
                int i = 0;
                while (i < elements.size()){
                    String date = elements.get(i).text();
                    String rating = elements.get(++i).text();
                    String currency = elements.get(++i).text();
                    map = new HashMap<String,String>();
                    map.put(DATE_ID,date);
                    map.put(RATING_ID,rating);
                    map.put(CURRENCY_ID,currency);
                    list.add(map);
                    i++;
                }
                elements.clear();
                error_code = -2;
            }catch(IndexOutOfBoundsException ioobe){
                error_code = 2;
            }
            catch(IOException e){
                error_code = 1;
            }
        }

        private Bitmap getImage(String urlOfImg){
            URL url = null;
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            try {
                url = new URL(urlOfImg);
                connection = (HttpURLConnection) url.openConnection();
                bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                error_code = -1;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch(IOException e){
                 e.printStackTrace();
                error_code = 1;
            }finally {
                connection.disconnect();
            }
            return bitmap;
        }

        protected void onPostExecute(Void results){
            switch(error_code){
                case 1: AlDialog("Нет соеденения."); break;
                case 2: AlDialog("За данный период нет данных");break;
                case -1: if(bitmap!=null) graphicImg.setImageBitmap(bitmap);
                         else AlDialog("Ошибка,нет данных");
                         break;
                case -2: if(list!=null) setList(list); break;
            }
        }
    }
}
