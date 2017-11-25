package com.omerakturk.rabbitmq_mesaj_app;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MainActivity extends Activity {
    private String QUEUE_NAME="channel1";
    private Button kanal1,kanal2,kanal3,kanal4,gonder;
    private EditText mesajGonderme;
    private TextView gelenMesaj,gidenMesaj;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        kanal1= (Button) findViewById(R.id._btn_kanal1);
        kanal2= (Button) findViewById(R.id._btn_kanal2);
        kanal3= (Button) findViewById(R.id._btn_kanal3);
        kanal4= (Button) findViewById(R.id._btn_kanal4);
        mesajGonderme= (EditText) findViewById(R.id._editText);
        gonder= (Button) findViewById(R.id._btn_gonder);
        gelenMesaj= (TextView) findViewById(R.id._txt_alangelen);
        gidenMesaj= (TextView) findViewById(R.id._txt_alangiden);

        try {
            mesajAl(QUEUE_NAME);
        } catch (IOException e) {
                e.printStackTrace();
        } catch (TimeoutException e) {
                e.printStackTrace();
        }



        kanal1.setOnClickListener ((v) -> QUEUE_NAME = "channel1");
        kanal2.setOnClickListener ((v) -> QUEUE_NAME = "channel2");
        kanal3.setOnClickListener ((v) -> QUEUE_NAME = "channel3");
        kanal4.setOnClickListener ((v) -> QUEUE_NAME = "channel4");
        gonder.setOnClickListener((v)-> {{try {mesajGonder(QUEUE_NAME);mesajAl(QUEUE_NAME);Log.d("TAG","----->Burada");} catch (IOException e) {e.printStackTrace();} catch (TimeoutException e) {e.printStackTrace();}}});
    }
    public void mesajGonder(String kanal ) throws IOException, TimeoutException {

        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("185.122.201.124");

        factory.setUsername("omer");
        factory.setPassword("omer123.");
        factory.setVirtualHost("/");
        factory.setPort(5672);
        connection = factory.newConnection();

        Channel channel = connection.createChannel();
        channel.queueDeclare(kanal, false, false, false, null);
        Toast.makeText(this, "Bağlantı Tamam MEsaj Gonderilcek", Toast.LENGTH_SHORT).show();
        gidenMesaj.setText(mesajGonderme.getText());

        AMQP.BasicProperties props= new AMQP.BasicProperties.Builder().correlationId("").replyTo(kanal).build();
        channel.basicPublish("", kanal, props,mesajGonderme.getText().toString().getBytes());

        channel.close();
        connection.close();
    }
    public void mesajAl(String kanal) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("185.122.201.124");
        factory.setUsername("omer");
        factory.setPassword("omer123.");
        factory.setVirtualHost("/");
        factory.setPort(5672);
        Connection connection = factory.newConnection();
        com.rabbitmq.client.Channel channel = connection.createChannel();

        channel.queueDeclare(kanal, false, false, false, null);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                gelenMesaj.setText(message);
            }
        };
        channel.basicConsume(kanal, true, consumer);
    }

}
