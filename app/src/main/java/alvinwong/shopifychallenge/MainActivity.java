package alvinwong.shopifychallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView total_Spent;
    private TextView total_sold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        parseResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);

    }
    public void parseResponse(String response){
        JSONObject response_json = null;
        total_Spent = (TextView)findViewById(R.id.total_spent);
        total_sold = (TextView)findViewById(R.id.total_sold);

        try {
            response_json = new JSONObject(response);
            JSONArray orders = response_json.getJSONArray("orders");

            ArrayList<JSONObject> user_orders = getOrders(orders, "Napoleon", "Batz");
            double total_price = getTotalSpent(user_orders);
            String str_total_price = Double.toString(total_price);
            total_Spent.setText("$" + str_total_price);

            int bronzeBagsSold = getBronzeBagsSold(orders);
            String str_bronzeBags_sold = Integer.toString(bronzeBagsSold);
            total_sold.setText(str_bronzeBags_sold + " bags");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<JSONObject> getOrders(JSONArray orders, String firstname, String lastname){
        ArrayList<JSONObject> user_orders = new ArrayList<JSONObject>();
        for (int i = 0; i < orders.length(); i++){
            try {
                if (orders.getJSONObject(i).getJSONObject("customer").getString("first_name").equals(firstname) && orders.getJSONObject(i).getJSONObject("customer").getString("last_name").equals(lastname)){
                    user_orders.add(orders.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return user_orders;
    }

    public double getTotalSpent(ArrayList<JSONObject> user_orders){
        double total_price = 0.00;
        for (JSONObject order : user_orders){
            try {
                total_price += order.getDouble("total_price");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return total_price;
    }
    public int getBronzeBagsSold (JSONArray orders){
        int sold = 0;
        for (int i = 0; i < orders.length(); i++){
            try {
                JSONArray line_items = orders.getJSONObject(i).getJSONArray("line_items");
                for (int j = 0; j < line_items.length(); j++){
                    if (line_items.getJSONObject(j).getString("title").equals("Awesome Bronze Bag")){
                        sold++;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return sold;
    }

}
