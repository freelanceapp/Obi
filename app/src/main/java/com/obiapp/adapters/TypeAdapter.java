package com.obiapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.obiapp.R;
import com.obiapp.activities_fragments.activity_department_details.DepartmentDetailsActivity;
import com.obiapp.databinding.TypeRowBinding;
import com.obiapp.models.TypeModel;

import java.util.List;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.MyHolder> {

    private List<TypeModel> list;
    private Context context;
    private int selectedPos =0 ;
    private int oldPos =0 ;
    private DepartmentDetailsActivity  activity;

    public TypeAdapter(List<TypeModel> list, Context context) {
        this.list = list;
        this.context = context;
        activity = (DepartmentDetailsActivity) context;


    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TypeRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.type_row, parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        holder.binding.setModel(list.get(position));
        TypeModel typeModel = list.get(position);

        if (typeModel.isSelected()){
            holder.binding.cardView.setCardBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary));
            holder.binding.tvTitle.setTextColor(ContextCompat.getColor(context,R.color.white));
        }else {
            holder.binding.cardView.setCardBackgroundColor(ContextCompat.getColor(context,R.color.gray5));
            holder.binding.tvTitle.setTextColor(ContextCompat.getColor(context,R.color.gray9));

        }

        holder.itemView.setOnClickListener(view -> {
            selectedPos = holder.getAdapterPosition();
            updateSelectedPos(selectedPos);
            activity.setItemData(list.get(selectedPos));




        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private TypeRowBinding binding;

        public MyHolder(TypeRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }


    }

    public void updateSelectedPos(int selectedPos){
        this.selectedPos = selectedPos;

        if (oldPos!=this.selectedPos){
            TypeModel model2 = list.get(oldPos);
            model2.setSelected(false);
            list.set(oldPos,model2);
            notifyItemChanged(oldPos);
        }


        TypeModel model1 = list.get(this.selectedPos);
        model1.setSelected(true);
        list.set(selectedPos,model1);
        notifyItemChanged(this.selectedPos);

        oldPos = this.selectedPos;
    }
}
