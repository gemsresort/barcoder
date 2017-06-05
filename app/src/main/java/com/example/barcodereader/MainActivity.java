package com.example.barcodereader;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import java.io.BufferedReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;


public class MainActivity extends AppCompatActivity {

   String myJSON;
    public static final String KEY_NAME = "barcodenumber";
    public static final String KEY_INGREDIENTS = "ingredients";
 private static final String TAG_RESULTS="result";
    private ProgressDialog loading;
    private static final String TAG_NAME = "firstname";
    private static final String TAG_ING ="ingredients";

   JSONArray peoples = null;

    ArrayList<HashMap<String, String>> personList;

  private    ListView list1;

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String JSON_ARRAY = "result";
    public static final String DATA_URL = "http://http://192.168.3.103/SmileAdventure/login.php?firstname=";
    private static final String REGISTERED_URL="http://192.168.3.103/SmileAdventure/signup-process.php";
    public static final String LOGIN_URL = "http://192.168.3.107/volley/login.php";
    public static final String key_barcode="firstname";
    final String firstname1 = "lens";
    TextView  barcodeResults, barcodeResults1, barcodeResults2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       list1 = (ListView) findViewById(R.id.listView);
  //     personList = new ArrayList<HashMap<String,String>>();
        barcodeResults = (TextView) findViewById(R.id.barcode_result);
        barcodeResults1 = (TextView) findViewById(R.id.barcode_result1);


    //    getData();

    }

    public void getData() {



    }



        protected void showList(){
            try {
                JSONObject jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray(TAG_RESULTS);

                for(int i=0;i<peoples.length();i++){
                    JSONObject c = peoples.getJSONObject(i);

                    String name = c.getString(TAG_NAME);
                    String address = c.getString(TAG_ING);

                    HashMap<String,String> persons = new HashMap<String,String>();


                    persons.put(TAG_NAME,name);
                    persons.put(TAG_ING,address);

                    personList.add(persons);
                }

                ListAdapter adapter = new SimpleAdapter(
                        MainActivity.this, personList, R.layout.list_item,
                        new String[]{TAG_NAME,TAG_ING},
                        new int[]{ R.id.firstname, R.id.ingredients}
                );

                list1.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

   public  void scanBarcode(View view){
        Intent intent =new Intent(this,ScanBarcodeActivity.class);
        startActivityForResult(intent,0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if(requestCode==0) {

            if(  resultCode== CommonStatusCodes.SUCCESS){
                if(data!=null) {
                    Barcode BARCODE = data.getParcelableExtra("barcode");
                    final String firstname = "+BARCODE.displayValue";
                    String url = this.DATA_URL + firstname;

                    Log.d(TAG, "Barcode read: " + firstname);

                    barcodeResults.setText("Barcode Value : " +BARCODE.displayValue);
                    barcodeResults1.setText("ingredient1 : "+firstname1);

//S

                    StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loading.dismiss();
                            showJSON(response);

                        }


                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                                }
                            });

                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    requestQueue.add(stringRequest);




                    //S
//                    StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
//                            new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//
//                                    if (response.trim().equals("success")&& data!=null  )  {
//                                        Barcode BARCODE=data.getParcelableExtra("barcode");
//                                        barcodeResults.setText("Barcode Value : "+ BARCODE.displayValue);
//
//                                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
////                        Toast.makeText(MainActivity.this, "barcode Success", Toast.LENGTH_SHORT).show();
//                                        finish();
//                                    }
//                                }
//                            },
//                            new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//
//                                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
//
//                                }
//                            }
//                    ) {
//                        protected Map<String, String> getParams() throws AuthFailureError {
//                            Map<String, String> map = new HashMap<String, String>();
//                            map.put(KEY_BARCODE,firstname );
//                            return map;
//                        }
//                    };
//                    RequestQueue requestQueue = Volley.newRequestQueue(this);
//                    requestQueue.add(stringRequest);
                } else
                {
                    barcodeResults.setText("No Barcode found");
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showJSON(String response) {
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(this.JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {

                JSONObject collegeData = result.getJSONObject(i);
                String firstname = collegeData.getString(this.KEY_NAME);
                String INGREDIENTS = collegeData.getString(this.KEY_INGREDIENTS);

                // list.add(createEmployee("employee",Output));
                // textViewResult.setText("Name:\t" + name + "\nAddress:\t" + address + "\nVice Chancellor:\t" + vc);
                HashMap<String,String> employees = new HashMap<>();
                employees.put(TAG_NAME,firstname );
                employees.put(TAG_ING,INGREDIENTS);
                list.add(employees);



            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ListAdapter adapter = new SimpleAdapter(
                MainActivity.this, list, R.layout.list_item,
                new String[]{this.TAG_NAME,this.TAG_ING},
                new int[]{R.id.firstname, R.id.ingredients});

        list1.setAdapter(adapter);

    }

}