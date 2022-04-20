package com.bekk.queuingtrackingsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomerAdapter(
    val customerList : MutableList<Int>
) : RecyclerView.Adapter<CustomerAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val tvCustomerNumber = itemView.findViewById<TextView>(R.id.tvCustomerNumber)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_customer_numbers, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvCustomerNumber.text = customerList[position].toString()

    }

    override fun getItemCount(): Int {
        return customerList.size
    }

}