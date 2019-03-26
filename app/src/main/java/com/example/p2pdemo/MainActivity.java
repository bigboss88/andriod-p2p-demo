package com.example.p2pdemo;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager p2p;
    private WifiP2pManager.Channel chan;
    private P2PRevicer rec;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private String[] names;
    private TextView out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        out = (TextView) findViewById(R.id.lbl_out);
        //change in Wi-Fi P2P status
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        //change in list of available peers
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        //state of Wi-Fi P2P connectivity has changed
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        //this device has changed
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        p2p = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        chan = p2p.initialize(this,getMainLooper(),null);
        rec = new P2PRevicer(this,p2p,chan);
        p2p.discoverPeers(chan, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peersList) {
            if (!peersList.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(peersList.getDeviceList());
                int i=0;
                names =  new String[peersList.getDeviceList().size()];

                for (WifiP2pDevice device : peersList.getDeviceList()){
                    names[i] = device.deviceName;
                    i++;
                }
            }

            if (peers.size() ==0){
                Toast.makeText(getApplicationContext(),"Something Wrong",Toast.LENGTH_SHORT).show();
            }
            else{
                String n ="";
                for(int i = 0;i< names.length;i++){
                    n+=names[i];
                    n+='\n';
                }
                out.setText(n);
            }
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }



        return super.onOptionsItemSelected(item);
        // automatically handle clicks on the Home/Up button, so long
    }

    public void onResume(){
        super.onResume();

        registerReceiver(rec,intentFilter);
    }

    public void onPause(){
        super.onPause();
        unregisterReceiver(rec);
    }

}
