package com.picoplaca.fredr.co.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picoplaca.fredr.co.R;
import com.picoplaca.fredr.co.models.Plate;

import java.util.List;

/**
 * Created by Fred Rueda on 30/08/2019
 * Developer Fred Rueda
 * Email fredjruedao@gmail.com
 */
public class AdapterBitacora extends RecyclerView.Adapter<AdapterBitacora.ViewHolder>{

    private final List<Plate> mValues;
    private final Context context;

    public AdapterBitacora(List<Plate> mValues, Context context) {
        this.mValues = mValues;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bitacora_list_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.numberPlate.setText(mValues.get(position).getPlate());
        holder.safePlate.setText(mValues.get(position).isSafePassage()+"");
        holder.contrav.setText(mValues.get(position).isContravention()+"");
        holder.dateReg.setText(mValues.get(position).getDateRegister());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView numberPlate;
        public final TextView safePlate;
        public final TextView contrav;
        public final TextView dateReg;

        public ViewHolder(View view) {
            super(view);
            numberPlate = view.findViewById(R.id.numberplate);
            safePlate = view.findViewById(R.id.safeplate);
            contrav = view.findViewById(R.id.contrav);
            dateReg = view.findViewById(R.id.datereg);
        }

    }


}
