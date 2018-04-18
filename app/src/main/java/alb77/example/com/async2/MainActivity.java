package alb77.example.com.async2;

import android.app.ProgressDialog;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private ImageView dlPhoto;
    private ProgressDialog waitDialog;
    private String downloadUrl = "http://novotempo.com/amiltonmenezes/files/2014/01/21-rosa-b.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button imageDownloaderBtn = (Button) findViewById(R.id.btn_downLoad);

        dlPhoto = (ImageView) findViewById(R.id.imageView);

        imageDownloaderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhotoDownloader().execute(downloadUrl);
            }
        });
    }
    private class PhotoDownloader extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... param){
            return downloadBitmap(param[0]);
        }

        @Override
        protected void onPreExecute(){
            Log.i("Async2", "onPreExecute Called");
            waitDialog = ProgressDialog.show(MainActivity.this, "Wait", "Downloading photo, please wait...");
        }

        @Override
        protected void onPostExecute(Bitmap result){
            Log.i("ASync2", "onPostExecute Called");
            dlPhoto.setImageBitmap(result);
            waitDialog.dismiss();
        }

        private Bitmap downloadBitmap(String url){
            final DefaultHttpClient client = new DefaultHttpClient();

            final HttpGet getRequest = new HttpGet(url);
            try{
                HttpResponse response = client.execute(getRequest);

                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK){
                    Log.v("PhotoDownloader", "Error" + statusCode + " while retrieving bitmap from " + url);
                    return null;
                }

                final HttpEntity entity = response.getEntity();
                if (entity != null){
                    InputStream inputStream = null;
                    try{
                        inputStream = entity.getContent();

                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        return bitmap;
                    }finally {
                        if (inputStream != null){
                            inputStream.close();
                        }
                        entity.consumeContent();
                    }
                }
            }catch (Exception e){
                getRequest.abort();
                Log.e("PhotoDownloader", "Something went wrong while retrieving bitmap from " + url + e.toString());
            }

            return null;
        }
    }
}

