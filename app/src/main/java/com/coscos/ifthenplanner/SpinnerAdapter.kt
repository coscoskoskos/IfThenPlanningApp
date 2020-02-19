package com.coscos.ifthenplanner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class SpinnerAdapter(context: Context, itemLayoutId: Int, spinnerItems: Array<String>, spinnerColors: Array<String>) : BaseAdapter() {

    data class ViewHolder(var view: View, var textView: TextView)

    val inflater = LayoutInflater.from(context)
    val layoutId = itemLayoutId
    val names = spinnerItems

    var backgroundId: MutableList<Int> = mutableListOf()

    init {
        for (i in spinnerColors.indices) {
            backgroundId.add(context.resources.getIdentifier(spinnerColors[i], "drawable", context.packageName))
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view = convertView
        var holder: ViewHolder? = null

        if (view == null) {
            view = inflater.inflate(layoutId, null)
            holder = ViewHolder(view.findViewById(R.id.view), view.findViewById(R.id.text_view))
            view.setTag(holder)
        } else {
            holder = view.tag as ViewHolder
        }

        holder.view.setBackgroundResource(backgroundId[position])
        holder.textView.text = names[position]

        return view!!
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return names.count()
    }

}