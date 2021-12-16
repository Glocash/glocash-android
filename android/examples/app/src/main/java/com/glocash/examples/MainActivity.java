package com.glocash.examples;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.glocash.payment.GlocashPayRequest;
import com.glocash.payment.GlocashActivity;
import com.glocash.payment.GlocashPayResponse;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText REQ_INVOICE = (EditText)MainActivity.this.findViewById(R.id.REQ_INVOICE);
        long timeStampSec = System.currentTimeMillis() / 1000;
        String invoiceNum = String.format("%010d", timeStampSec);
        Random r = new Random();
        REQ_INVOICE.setText("TEST"+invoiceNum+r.nextInt(9999));

    }

    public void paytest(View v) {
        System.out.println("===paybtn===");

        EditText REQ_EMAIL = (EditText)MainActivity.this.findViewById(R.id.REQ_EMAIL);
        EditText REQ_INVOICE = (EditText)MainActivity.this.findViewById(R.id.REQ_INVOICE);
        EditText BIL_METHOD = (EditText)MainActivity.this.findViewById(R.id.BIL_METHOD);
        EditText CUS_EMAIL = (EditText)MainActivity.this.findViewById(R.id.CUS_EMAIL);
        EditText BIL_PRICE = (EditText)MainActivity.this.findViewById(R.id.BIL_PRICE);
        EditText BIL_CURRENCY = (EditText)MainActivity.this.findViewById(R.id.BIL_CURRENCY);
        EditText BIL_QUANTITY = (EditText)MainActivity.this.findViewById(R.id.BIL_QUANTITY);
        EditText BIL_GOODSNAME = (EditText)MainActivity.this.findViewById(R.id.BIL_GOODSNAME);
        EditText URL_SUCCESS = (EditText)MainActivity.this.findViewById(R.id.URL_SUCCESS);
        EditText URL_PENDING = (EditText)MainActivity.this.findViewById(R.id.URL_PENDING);
        EditText URL_FAILED = (EditText)MainActivity.this.findViewById(R.id.URL_FAILED);
        EditText URL_NOTIFY = (EditText)MainActivity.this.findViewById(R.id.URL_NOTIFY);
        EditText BIL_CC3DS = (EditText)MainActivity.this.findViewById(R.id.BIL_CC3DS);
        EditText REQ_SANDBOX = (EditText)MainActivity.this.findViewById(R.id.REQ_SANDBOX);
        EditText SECRET_KEY = (EditText)MainActivity.this.findViewById(R.id.SECRET_KEY);

        GlocashPayRequest e=new GlocashPayRequest();

        Map<String, String> Parameters= new HashMap<String, String>();
        Parameters.put("REQ_EMAIL", REQ_EMAIL.getText().toString());  //商户的GC账户邮箱
        Parameters.put("REQ_INVOICE", REQ_INVOICE.getText().toString());  //订单号（允许数字、大小写字母、符号_与-）
        Parameters.put("BIL_METHOD", BIL_METHOD.getText().toString());   //交易的支付方式代码
        Parameters.put("CUS_EMAIL", CUS_EMAIL.getText().toString());   //付款人的邮箱
        Parameters.put("BIL_PRICE", BIL_PRICE.getText().toString());   //请求的付款金额
        Parameters.put("BIL_CURRENCY", BIL_CURRENCY.getText().toString());   //付款的货币代码（ISO4217）
        Parameters.put("BIL_QUANTITY", BIL_QUANTITY.getText().toString());     //商品数量
        Parameters.put("BIL_GOODSNAME", BIL_GOODSNAME.getText().toString());    //商户信息
        Parameters.put("URL_SUCCESS", URL_SUCCESS.getText().toString());   //付款成功后的跳转地址
        Parameters.put("URL_PENDING", URL_PENDING.getText().toString());   //付款处理中的跳转地址（非实时到帐）
        Parameters.put("URL_FAILED", URL_FAILED.getText().toString());      //付款失败后的跳转地址
        Parameters.put("URL_NOTIFY", URL_NOTIFY.getText().toString());      //付款状态通知的请求地址
        Parameters.put("BIL_CC3DS", BIL_CC3DS.getText().toString());   //信用卡付款时是否启用3DS验证（非0则启用）
        Parameters.put("REQ_SANDBOX", REQ_SANDBOX.getText().toString());  //如果参数值为ON则当前交易处于测试环境下
        Parameters.put("SECRET_KEY", SECRET_KEY.getText().toString());    //商户账号的安全密钥
        //更多参数请浏览网页

        String re=e.setData(Parameters);

        //跳转页面

        Intent intent = new Intent(MainActivity.this,
                GlocashActivity.class);
        intent.putExtra(GlocashActivity.ACTION_REQUEST,e);
        intent.putExtra(GlocashActivity.ACTION_CLASS,MainActivity.class);
        MainActivity.this.startActivityForResult(intent, 1);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(this.getClass().getSimpleName(), "onActivityResult");

        if (resultCode == GlocashActivity.RESULT_OK) {
            if (null != data) {
                GlocashPayResponse result = (GlocashPayResponse) data.getSerializableExtra(GlocashActivity.RESULT_DATA);
                String start="";
                if (null != result) {
                    Map<String, String> r=result.getData();
                    for (Map.Entry<String, String> entry : r.entrySet()) {
                        Log.i(TAG, entry.getKey()+":"+entry.getValue());
                    }
                    start=r.get("BIL_STATUS");
                }

                if (start!=null) {
                    if(start.equals("paid")){
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }

        }
        else if(resultCode == GlocashActivity.RESULT_ERROR){
            GlocashPayResponse result=(GlocashPayResponse) data.getSerializableExtra(GlocashActivity.RESULT_DATA);
            if (null != result) {
                Map<String, String> r=result.getData();
                AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                        .setTitle("错误")//标题
                        .setMessage(r.get("message"))//内容
                        .setIcon(R.mipmap.ic_launcher)//图标
                        .create();
                alertDialog1.show();
            }
        }
    }

}