package gc.dtu.weeg.dtugc.utils;

import android.content.Context;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gc.dtu.weeg.dtugc.MainActivity;

import okhttp3.ResponseBody;

public class FileWriterUtils {
    private Context mContext;
//    Fragment mCurFrag;
    private String mfileurl;
    private writefileResult mListerner;
    private ResponseBody mBody;
    private Thread thread1;
  public FileWriterUtils(Context context, String Fileurl, ResponseBody body)
    {
        mContext = context;
        mfileurl = Fileurl;
        mBody = body;
        writefilethread test;
        test = new writefilethread();
        thread1 = new Thread(test);
      //  thread1.start();
    }

    private  class writefilethread implements Runnable
    {

        @Override
        public void run() {
            writeResponseBodyToDisk(mBody,mfileurl);
        }
        private boolean writeResponseBodyToDisk(ResponseBody body, String fileurl) {
            try {
                //判断文件夹是否存在
//            File files = new File(SD_HOME_DIR);//跟目录一个文件夹
//            if (!files.exists()) {
//                //不存在就创建出来
//                files.mkdirs();
//            }
                //创建一个文件
                File futureStudioIconFile = new File(fileurl);
                //初始化输入流
                InputStream inputStream = null;
                //初始化输出流
                OutputStream outputStream = null;
                try {
                    //设置每次读写的字节
                    byte[] fileReader = new byte[4096];
                    long fileSize = body.contentLength();
                    long fileSizeDownloaded = 0;
                    //请求返回的字节流
                    inputStream = body.byteStream();
                    //创建输出流
                    outputStream = new FileOutputStream(futureStudioIconFile);
                    //进行读取操作
                    while (true) {
                        int read = inputStream.read(fileReader);
                        if (read == -1) {
                            break;
                        }
                        //进行写入操作
                        outputStream.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                    }

                    //刷新
                    outputStream.flush();
                    ((MainActivity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mListerner!=null)
                            {
                                mListerner.OnFilewritesuccess();
                            }
                        }
                    });
                    return true;
                } catch (IOException e) {
                    return false;
                } finally {
                    if (inputStream != null) {
                        //关闭输入流
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        //关闭输出流
                        outputStream.close();
                    }
                }
            } catch (IOException e) {
                return false;
            }
        }
    }


    public interface  writefileResult{

      void OnFilewritesuccess();
    }
    public void startthread()
    {
        thread1.start();
    }
    public void SetOnFilewriteResult(writefileResult fileinterface)
    {
        mListerner = fileinterface;
    }
}
