package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones;

import android.bluetooth.BluetoothDevice;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.ConfigMenuConfiguracinoSubVincularItemBinding;
import java.util.ArrayList;
import java.util.List;

public class ConfigMenuBluetoothAdapter extends RecyclerView.Adapter<ConfigMenuBluetoothAdapter.Holder> {
    private List<BluetoothDevice> devicesList = new ArrayList<>();
    private OnItemTouched itemTouchedCallback;
    private boolean mIsDeviceBonding;

    public interface OnItemTouched {
        void itemTouched(BluetoothDevice deviceSelected);
    }

    ConfigMenuBluetoothAdapter(OnItemTouched callback) {
        this.itemTouchedCallback = callback;
    }

    ConfigMenuBluetoothAdapter(List<BluetoothDevice> devicesList){
        this.devicesList = devicesList;
    }

    public void deviceHasStopedBonding(){
        mIsDeviceBonding = false;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        final ConfigMenuConfiguracinoSubVincularItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.config_menu_configuracino_sub_vincular_item, parent,
                false
        );

        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final int fixedPosition = position;
        setItemText(holder, fixedPosition);

        /*holder.binding.btnItemLista.setOnClickListener(new View.OnClickListener()
        );*/

        holder.binding.btnItemLista.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View v) {
                                                               if (itemTouchedCallback != null && !mIsDeviceBonding) {
                                                                   itemTouchedCallback.itemTouched(devicesList.get(fixedPosition));
                                                                   mIsDeviceBonding = true;
                                                               }
                                                           }
                                                       }
        );
        /*holder.binding.SelectedItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (itemTouchedCallback != null && !mIsDeviceBonding) {
                itemTouchedCallback.itemTouched(devicesList.get(fixedPosition));
                mIsDeviceBonding = true;
            }else{
                holder.binding.SelectedItem.setChecked(false);
            }
        });*/
    }

    private BluetoothDevice getCurrentDevice(int fixedPosition) {
        return devicesList.get(fixedPosition);
    }

    public void moveItemToTop(BluetoothDevice device) {
        int i = 0;
        for (BluetoothDevice bluetoothDevice : devicesList) {
            if (device.getAddress().equals(bluetoothDevice.getAddress())) {
                devicesList.remove(i);
                final int position = 0;
                devicesList.add(position, device);
                notifyDataSetChanged();
                break;
            }
            i++;
        }
    }

    private void setItemText(Holder holder, int position) {
        BluetoothDevice device = getCurrentDevice(position);
        String text = device.getName();

        if (text == null) {
            text = device.getAddress();
        }

        holder.binding.btnItemLista.setText(text.substring(text.length() - 4, text.length()));
    }

    @Override
    public int getItemCount() {
        return devicesList.size();
    }

    void insertItem(BluetoothDevice device) {
        devicesList.add(device);
        notifyItemInserted(devicesList.size() - 1);
    }

    /**
     * Esto es para colocar los dispositivos vinculados hasta arriba en la lista
     *
     * @param device
     */
    void insertItemOnTop(BluetoothDevice device) {
        final int position = 0;
        devicesList.add(position, device);
        notifyItemInserted(position);
    }

    void clearList() {
        devicesList.clear();
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder {
        private final ConfigMenuConfiguracinoSubVincularItemBinding binding;

        Holder(final ConfigMenuConfiguracinoSubVincularItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.setIsRecyclable(false);
        }
    }
}
