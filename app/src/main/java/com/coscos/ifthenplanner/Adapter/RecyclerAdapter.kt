package com.coscos.ifthenplanner.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coscos.ifthenplanner.R

class RecyclerAdapter(private val context: Context,
                      private val titleList: MutableList<String>,
                      private val ifList: MutableList<String>,
                      private val thenList: MutableList<String>,
                      private val colorList: MutableList<Int>,
                      private val notificationList: MutableList<Boolean>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    val spinnerColors: Array<String> = arrayOf("tag_pink", "tag_red", "tag_blue", "tag_purple", "tag_green", "tag_grey", "tag_black")

    var backgroundId: MutableList<Int> = mutableListOf()

    init {
        for (i in spinnerColors.indices) {
            backgroundId.add(context.resources.getIdentifier(spinnerColors[i], "drawable", context.packageName))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.let {
            it.titleStatement.text = titleList[position]
            it.ifStatement.text = ifList[position]
            it.thenStatement.text= thenList[position]
            it.colorTag.setBackgroundResource(backgroundId[colorList[position]])

            if (notificationList[position]) {
                it.notification.visibility = View.VISIBLE
            } else {
                it.notification.visibility = View.INVISIBLE
            }

            it.textViewOptions.setOnClickListener{

                onMenuItemClick?.invoke(position, holder.itemView)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val mView = layoutInflater.inflate(R.layout.list_item, parent, false)

        val holder = ViewHolder(mView)

        return holder
    }

    override fun getItemCount(): Int {
        return ifList.size
    }

    var onItemClick: ((pos: Int, view: View) -> Unit)? = null
    var onLongItemClick: ((pos: Int, view: View) -> Unit)? = null
    var onMenuItemClick: ((pos: Int, view: View) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener{
        override fun onLongClick(v: View): Boolean {
            onLongItemClick?.invoke(adapterPosition, v)
            return true
        }

        override fun onClick(v: View) {
            onItemClick?.invoke(adapterPosition, v)
        }
        val ifStatement: TextView = itemView.findViewById(R.id.if_statement)
        val thenStatement: TextView = itemView.findViewById(R.id.then_statement)
        val colorTag: View = itemView.findViewById(R.id.color_tag)
        val titleStatement: TextView = itemView.findViewById(R.id.plan_title)
        val textViewOptions:TextView = itemView.findViewById(R.id.textViewOptions)
        val notification: ImageView = itemView.findViewById(R.id.notification_bell)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

    }

}