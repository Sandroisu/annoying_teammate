package ru.sandroisu.annoying_teammate

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView

class DevicesHolder(itemView: View, private val listener: OnStarSelectedListener) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(bltDevice: BLTDevice) {
        val tvName = itemView.findViewById<AppCompatTextView>(R.id.holder_device_name)
        val tvMac = itemView.findViewById<AppCompatTextView>(R.id.holder_device_mac)
        tvMac.text = bltDevice.macAddress
        tvName.text = bltDevice.name
        val ivStar = itemView.findViewById<AppCompatImageView>(R.id.holder_device_star)
        if (bltDevice.starSelected) {
            ivStar.setImageResource(R.drawable.ic_baseline_star_24)
        } else {
            ivStar.setImageResource(R.drawable.ic_baseline_star_border_24)
        }
        if (bltDevice.starSelected) {
            bltDevice.addTimeWaste()
            if (bltDevice.timeWaste != 0L) {
                val tvWaste = itemView.findViewById<AppCompatTextView>(R.id.holder_device_waste)
                tvWaste.text = bltDevice.timeWaste.toString()
            }
        }
        ivStar.setOnClickListener {
            bltDevice.starSelected = if (bltDevice.starSelected) {
                ivStar.setImageResource(R.drawable.ic_baseline_star_border_24)
                listener.onStarUnselected(bltDevice)
                false
            } else {
                ivStar.setImageResource(R.drawable.ic_baseline_star_24)
                listener.onStarSelected(bltDevice)
                true
            }
        }
    }
}