package com.example.smarthouse_beta;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.widget.ToggleButton;
import java.io.InputStream;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 2;

    private Button buttonConnect;
    private Button buttonDisConnect;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;

    Switch switchSalonButon, switchMutfakButton, switchYatakButton,switchKapiButton,switchPencereButton,switchKlimaButton,switchSicaklikButton;

    //ToggleButton toggleButtonSalon, muftakToggleButton, yatakToggleButton, kapiToggleButton, pencereToggleButton, klimaToggleButton, sicaklikToggleButton;
    OutputStream outputStream;

    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID for SPP (Serial Port Profile)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        buttonConnect = findViewById(R.id.buttonConnect);
        buttonDisConnect = findViewById(R.id.buttonDisConnect);

        switchSicaklikButton = findViewById(R.id.switchButtonSicaklik);


        // Check if the device supports Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Request necessary permissions
        requestBluetoothPermission();

        // Set onClickListener for the connect button
        buttonConnect.setOnClickListener(v -> connectToBluetoothDevice());
        buttonDisConnect.setOnClickListener(v -> disconnectBluetooth());

        switchSalonButon = findViewById(R.id.switchButtonSalon);
        switchYatakButton = findViewById(R.id.switchButtonYatak);
        switchMutfakButton = findViewById(R.id.switchButtonMutfak);
        switchKapiButton = findViewById(R.id.switchButtonKapi);
        switchPencereButton = findViewById(R.id.switchButtonPencere);
        switchKlimaButton = findViewById(R.id.switchButtonKlima);

        // Diğer düğmelere onClickListener'larını ayarla

        switchSalonButon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sendData("3"); // ToggleButton açıkken '1' gönder
                    Toast.makeText(getApplicationContext(), "Salon Lambası Açıldı", Toast.LENGTH_SHORT).show();
                } else {
                    sendData("4"); // ToggleButton kapalıyken '0' gönder
                    Toast.makeText(getApplicationContext(), "Salon Lambası Kapandı", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchMutfakButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sendData("5"); // ToggleButton açıkken '5' gönder
                    Toast.makeText(getApplicationContext(), "Mutfak Lambası Açıldı", Toast.LENGTH_SHORT).show();
                } else {
                    sendData("6"); // ToggleButton kapalıyken '6' gönder
                    Toast.makeText(getApplicationContext(), "Mutfak Lambası Kapandı", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchYatakButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sendData("7"); // ToggleButton açıkken '7' gönder
                    Toast.makeText(getApplicationContext(), "Yatak Odası Lambası Açıldı", Toast.LENGTH_SHORT).show();
                } else {
                    sendData("8"); // ToggleButton kapalıyken '8' gönder
                    Toast.makeText(getApplicationContext(), "Yatak Odası Lambası Kapandı", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchKapiButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sendData("1"); // ToggleButton açıkken '1' gönder
                    Toast.makeText(getApplicationContext(), "Kapı Açıldı", Toast.LENGTH_SHORT).show();
                } else {
                    sendData("0"); // ToggleButton kapalıyken '0' gönder
                    Toast.makeText(getApplicationContext(), "Kapı Kapandı", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchPencereButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sendData("C"); // ToggleButton açıkken '11' gönder
                } else {
                    sendData("D"); // ToggleButton kapalıyken '12' gönder
                }
            }
        });

        switchKlimaButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sendData("A"); // ToggleButton açıkken '9' gönder
                    Toast.makeText(getApplicationContext(), "Klima Açıldı", Toast.LENGTH_SHORT).show();
                } else {
                    sendData("B"); // ToggleButton kapalıyken '10' gönder
                    Toast.makeText(getApplicationContext(), "Klima Kapandı", Toast.LENGTH_SHORT).show();
                }
            }
        });

/*
        sicaklikToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // ToggleButton on ise sıcaklığı textView'a yaz
                if (isChecked) {
                    sendData("2");
                    readData();
                } else {
                    sendData("13");
                    textViewSicaklik.setText(" - ");
                }
            }
        });*/
    }

    // Request Bluetooth permissions
    private void requestBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_BLUETOOTH_PERMISSION);
        }
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Bluetooth permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth permissions are required to connect", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Connect to Bluetooth device
    private void connectToBluetoothDevice() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        // Replace "00:22:09::02:74:4D" with your Bluetooth device MAC address
        String deviceAddress = "00:22:09:02:74:4D";
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            // Connection successful, do something here if needed
            Toast.makeText(this, "Connected to Bluetooth device", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            // Connection failed
            Toast.makeText(this, "Failed to connect to Bluetooth device", Toast.LENGTH_SHORT).show();
            try {
                bluetoothSocket.close();
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
        }
    }

    // Handle Bluetooth enable request result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth is enabled", Toast.LENGTH_SHORT).show();
                connectToBluetoothDevice();
            } else {
                Toast.makeText(this, "Bluetooth enable request is denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void disconnectBluetooth() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
                // Bağlantı başarıyla sonlandırıldı, kullanıcıya bilgi ver
                Toast.makeText(this, "Bluetooth connection disconnected", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                // Bağlantı kapatılırken bir hata oluştu
                Toast.makeText(this, "Failed to disconnect Bluetooth connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Bluetooth bağlantısı zaten kapalı
            Toast.makeText(this, "Bluetooth connection is already disconnected", Toast.LENGTH_SHORT).show();
        }
    }

    void sendData(String message) {
        if (bluetoothSocket != null) {
            try {
                outputStream = bluetoothSocket.getOutputStream();
                //outputStream.write(message.getBytes());
                PrintWriter printWriter = new PrintWriter(outputStream, true);
                printWriter.println(message);
            } catch (IOException e) {
                showToast("Error: " + e.getMessage());
            }
        } else {
            showToast("Bluetooth connection is not established.");
        }
    }

    void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}