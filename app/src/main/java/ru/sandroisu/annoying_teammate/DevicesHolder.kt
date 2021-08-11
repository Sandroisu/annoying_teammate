package ru.sandroisu.annoying_teammate

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView

class DevicesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val itemView = itemView

    fun bind(bltDevice: BLTDevice) {
        val tvName = itemView.findViewById<AppCompatTextView>(R.id.holder_device_name)
        tvName.text = bltDevice.name
    }
}