package com.coscos.ifthenplanner

import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class RecyclerAdapter(private val context: Context,
                      private val titleList: MutableList<String>,
                      private val ifList: MutableList<String>,
                      private val thenList: MutableList<String>,
                      private val colorList: MutableList<Int>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

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

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

    }

}