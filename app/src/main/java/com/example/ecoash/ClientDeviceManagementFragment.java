package com.example.ecoash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoash.device.Device;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ClientDeviceManagementFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private ClientDeviceAdapter deviceAdapter;
    private FirebaseAuth auth;
    private List<Device> devices;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_device_management, container, false);

        auth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        devices = new ArrayList<>();
        deviceAdapter = new ClientDeviceAdapter(devices);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(deviceAdapter);

        loadDevices();

        return view;
    }

    private void loadDevices() {
        String userEmail = auth.getCurrentUser().getEmail();
        progressBar.setVisibility(View.VISIBLE);

        DeviceRepository.getDevicesByUser(userEmail, new DeviceRepository.DevicesCallback() {
            @Override
            public void onSuccess(List<Device> devicesLoaded) {
                progressBar.setVisibility(View.GONE);
                devices.clear();
                devices.addAll(devicesLoaded);
                deviceAdapter.notifyDataSetChanged();

                emptyView.setVisibility(devices.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar dispositivos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
