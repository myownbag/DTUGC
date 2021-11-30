package gc.dtu.weeg.dtugc;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import gc.dtu.weeg.dtugc.databean.Ask;
import gc.dtu.weeg.dtugc.databean.Take2;
import gc.dtu.weeg.dtugc.fregment.ICCardSettingFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("gc.dtu.weeg.dtugc", appContext.getPackageName());
        byte testbyte[] = {0x00,0x00};
        String test = "FFFF";
//        test += (char)testbyte[0];
//        test += (char)testbyte[1];
//        System.out.println("len is "+test.length());
//        System.out.println(test);


        Ask ask = new Ask();
        ask.icType= "1";
        ask.icSerial = "050107427863";
        ask.cmdType = "RM";

        ask.data = new Ask.DataDTO();
        ask.data.icResponse = test;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.71:7988") //设置网络请求的Url地址
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ICCardSettingFragment.Api1 request = retrofit.create(ICCardSettingFragment.Api1.class);

        Call<Take2> call = request.request(ask);

        call.enqueue(new Callback<Take2>() {
            @Override
            public void onResponse(Call<Take2> call, Response<Take2> response) {
                if(response.isSuccessful())
                {
                    Take2 t = response.body();
                    String res =  response.body().data.analysisResult;

                    System.out.println(res);
                }
                else
                {
                    // ToastUtils.showToast(MainActivity.getInstance(),"服务器解析失败");
                    System.out.println("服务器解析失败");

                }
            }

            @Override
            public void onFailure(Call<Take2> call, Throwable t) {
                // ToastUtils.showToast(MainActivity.getInstance(),"连接服务器失败");
                System.out.println("连接服务器失败");
            }
        });

    }
}
