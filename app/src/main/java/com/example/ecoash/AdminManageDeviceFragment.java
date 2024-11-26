package com.example.ecoash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoash.device.Device;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AdminManageDeviceFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText searchInput;
    private AdminDeviceAdapter deviceAdapter;
    private DatabaseReference realtimeDatabase;
    private List<Device> allDevices;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_device, container, false);

        realtimeDatabase = FirebaseDatabase.getInstance().getReference("dispositivos");
        recyclerView = view.findViewById(R.id.recyclerView);
        searchInput = view.findViewById(R.id.searchInput);

        allDevices = new ArrayList<>();
        deviceAdapter = new AdminDeviceAdapter(allDevices, realtimeDatabase);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(deviceAdapter);

        loadDevices();

        return view;
    }

    private void loadDevices() {
        DeviceRepository.getAllDevices(new DeviceRepository.DevicesCallback() {
            @Override
            public void onSuccess(List<Device> devices) {
                allDevices.clear();
                allDevices.addAll(devices);
                deviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(requireContext(), "Error al cargar dispositivos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
