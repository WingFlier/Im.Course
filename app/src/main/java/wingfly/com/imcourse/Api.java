package wingfly.com.imcourse;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tiko on 6/1/18.
 */

public class Api
{
    private String BASE_URL = "http://im.aua.am";

    private String LOGIN = "/Account/Login";

    public void init(Context context, Response.Listener<String> success,
                     Response.ErrorListener err)
    {
        RequestQueue queue = Volley.newRequestQueue(context);

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + LOGIN,
                success, err)
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("UserName", "");
                params.put("Password", "");
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response)
            {
                Log.i("response",response.headers.toString());
                Map<String, String> responseHeaders = response.headers;
                String rawCookies = responseHeaders.get("Set-Cookie");
                Log.i("cookies",rawCookies);
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(stringRequest);
    }
}
