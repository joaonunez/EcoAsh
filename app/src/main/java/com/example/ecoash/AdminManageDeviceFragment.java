package com.example.ecoash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoash.device.Device;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
        realtimeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allDevices.clear();
                for (DataSnapshot deviceSnapshot : snapshot.getChildren()) {
                    Device device = deviceSnapshot.getValue(Device.class);
                    if (device != null) {
                        device.setId(deviceSnapshot.getKey());
                        allDevices.add(device);
                    }
                }
                deviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error al cargar dispositivos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
