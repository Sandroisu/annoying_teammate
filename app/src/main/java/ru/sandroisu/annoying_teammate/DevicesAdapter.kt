package ru.sandroisu.annoying_teammate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class DevicesAdapter(private val devices: ArrayList<BLTDevice>) : RecyclerView.Adapter<DevicesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.device_holder, parent, false)
        return DevicesHolder(view)
    }

    override fun onBindViewHolder(holder: DevicesHolder, position: Int) {
        holder.bind(devices[position])
    }

    override fun getItemCount(): Int {
        return devices.count()
    }
}