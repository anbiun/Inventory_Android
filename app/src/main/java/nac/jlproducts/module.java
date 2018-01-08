package nac.jlproducts;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.SupportActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DialogTitle;
import android.util.Log;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.Map;

import nac.jlproducts.stockscan.R;
import nac.jlproducts.stockscan.fChangPwd;
import nac.jlproducts.stockscan.fReq;


/**
 * Created by nac on 5/1/2561.
 */

public class module {

    public static String SOAP_ACTION = "http://jlproducts.co.th:99/FindTag";
    public static String METHOD_NAME = "FindTag";
    public static String NAMESPACE = "http://jlproducts.co.th:99";
    public static String URL = "http://jlproducts.co.th:99/Android/Android.asmx";

    public static Map<String,String> ParamList = new HashMap<String,String>();
    public static String TagID = "";
    public static String UserName = "";
    public static ProgressDialog Dialog = null;
    private static String TAG = "";

    private static SoapObject Request = null;
    public static SoapPrimitive ResultString = null;

    public static void Qry() {
        try {
            Request = new SoapObject(NAMESPACE,METHOD_NAME);

            for ( Map.Entry<String, String> entry : ParamList.entrySet() ) {
                String key = entry.getKey();
                String value = entry.getValue();
                Request.addProperty(key,value);
            }

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);
            ResultString = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result From WebService: " + ResultString);
            xcode.setCode(excode.Success);
            //excode.set(excode.);
        } catch (Exception ex) {
            if (ex.getMessage().contains("unexpected end of stream")) {
                xcode.setCode(xcode.Cantconnect);
            } else if (ex.getMessage().contains("Unable to resolve host")) {
                xcode.setCode(xcode.Cantconnect);
            } else if (ex.getMessage().contains("Server was unable to process request.")) {
                xcode.setCode(xcode.Notfound);
            }
            ResultString = null;
            Log.e(TAG, "Error: " + ex.getMessage());
        }

    }
    public static class xcode {
        public static final Integer Cantconnect = 1;
        public static final Integer Notfound = 2;
        public static final Integer CantInsert = 3;
        public static final Integer Success = 10;
        private static Integer curCode;

        public static void setCode(Integer newVal) {
            curCode = newVal;
        }
        public static Integer getCode() {
            return curCode;
        }
    }
    public static String CombineString(String[] StringArr) {
        String result = "";
        for (int i = 0;i < StringArr.length;i++) {
            if (i == StringArr.length - 1) {
                result += StringArr[i].toString();
                return result;
            }
            result += StringArr[i].toString() + " ";
        }
        return result;
    }
}

