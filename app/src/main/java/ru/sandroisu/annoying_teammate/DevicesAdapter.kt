import ru.sandroisu.annoying_teammate.BLTDevice
import ru.sandroisu.annoying_teammate.DevicesHolder
import ru.sandroisu.annoying_teammate.OnStarSelectedListener
import ru.sandroisu.annoying_teammate.R

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DevicesAdapter(private val devices: List<BLTDevice>, private val listener : OnStarSelectedListener) :
    RecyclerView.Adapter<DevicesHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.device_holder, parent, false)
        return DevicesHolder(view, listener)
    }

    override fun onBindViewHolder(holder: DevicesHolder, position: Int) {
        holder.bind(devices[position])
    }

    override fun getItemCount(): Int {
        return devices.size
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

}