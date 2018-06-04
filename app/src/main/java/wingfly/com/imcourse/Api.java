package wingfly.com.imcourse;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Header;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tiko on 6/1/18.
 */

public class Api
{
    private RequestQueue queue;
    private String BASE_URL = "http://im.aua.am";

    private String LOGIN = "/Account/Login";
    private String CLASSES = "/Student/ClassRegistration";

    public Api init(Context context)
    {
        if (queue == null)
            queue = Volley.newRequestQueue(context);
        return this;
    }

    public void login(Response.Listener<String> success,
                      Response.ErrorListener err)
    {


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
                List<Header> allHeaders = response.allHeaders;
                Header header = allHeaders.get(5);
                String value = header.getValue();
                if (!(header.getName().equals("Set-Cookie")
                        && value.startsWith("ASP.NET")))
                {
                    header = allHeaders.get(6);
                    value = header.getValue();
                }
                String cookie = value.substring(value.indexOf("=") + 1, value.indexOf(";"));
                Log.i("cookies", cookie);
                MainActivity.getInstance().saveCookie(cookie);
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(stringRequest);
    }

    public void getAvailableClasses(Response.Listener<String> success,
                                    Response.ErrorListener err)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + CLASSES,
                success, err)
        {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response)
            {
                return super.parseNetworkResponse(response);
            }

            //function to set cookies
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> headers = super.getHeaders();

                if (headers == null
                        || headers.equals(Collections.emptyMap()))
                {
                    headers = new HashMap<String, String>();
                }
                MainActivity.getInstance().addSessionCookie(headers);

                return headers;
            }
        };
        queue.add(stringRequest);

    }
}
