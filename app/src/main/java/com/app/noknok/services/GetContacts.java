package com.app.noknok.services;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.app.noknok.definitions.Config;
import com.app.noknok.interfaces.AsyncResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dev on 26/6/17.
 */

public class GetContacts extends AsyncTask<String, String, String> {


    SharedPreferences sp ;
    public static final HashMap<String, String> mContactMap = new HashMap<>();

    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Email.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Email.DATA
    };
    public AsyncResponse response = null;
    Context mContext;

    public GetContacts(Context context, AsyncResponse asyncResponse) {
        mContext = context;
        response = asyncResponse;
        sp = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        ContentResolver cr = mContext.getContentResolver();
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
        if (cursor != null) {
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, phoneNo;
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    phoneNo = cursor.getString(numberIndex);

                    String str = phoneNo.replaceAll("[^0-9+]", "");

                    if (str.contains("+")) {
                        mContactMap.put(str, name);
                    }else {
                        mContactMap.put(sp.getString(Config.MYCOUNTRYCODE,"")+str,name);
                    }

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("number", str);
                        jsonObject.put("name", name);
                        Log.d("666666", name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(jsonObject);
                }

            } finally {
                cursor.close();
            }
        }
        return jsonArray.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        response.processFinish(s);
    }
}


