package com.example.ecoash.views.fragments;

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

import com.example.ecoash.R;
import com.example.ecoash.adapters.DeviceAdapter;
import com.example.ecoash.models.Device;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminManageDeviceFragment extends Fragment {

    private static final String TAG = "AdminManageDevice";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private DeviceAdapter deviceAdapter;
    private DatabaseReference realtimeDatabase;
    private List<Device> devices;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_device, container, false);

        realtimeDatabase = FirebaseDatabase.getInstance().getReference("dispositivos");
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        devices = new ArrayList<>();
        // Se mantiene la funcionalidad de eliminar dispositivos con el listener onDelete
        deviceAdapter = new DeviceAdapter(getContext(), devices, true, device -> deleteDevice(device.getId()));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(deviceAdapter);

        loadDevices();

        return view;
    }

    private void loadDevices() {
        progressBar.setVisibility(View.VISIBLE);

        realtimeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                devices.clear();
                for (DataSnapshot deviceSnapshot : snapshot.getChildren()) {
                    Device device = deviceSnapshot.getValue(Device.class);
                    if (device != null) {
                        device.setId(deviceSnapshot.getKey());
                        devices.add(device);
                    }
                }
                progressBar.setVisibility(View.GONE);
                deviceAdapter.notifyDataSetChanged();
                emptyView.setVisibility(devices.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar dispositivos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteDevice(String deviceId) {
        realtimeDatabase.child(deviceId).removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Dispositivo eliminado correctamente", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error al eliminar dispositivo", Toast.LENGTH_SHORT).show();
        });
    }
}
