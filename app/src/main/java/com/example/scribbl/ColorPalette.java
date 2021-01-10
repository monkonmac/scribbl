package com.example.scribbl;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class ColorPalette extends BottomSheetDialogFragment implements View.OnClickListener {
    public static final String TAG = "ColorPalette";
    private ArrayList<View> tvList;
    private PaletteListener mPaletteListener;

    public ColorPalette(PaletteListener paletteListener){
        this.mPaletteListener = paletteListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.color_palette, container, false);
        initPalette(v);
        return v;
    }

    private void initPalette(View v) {
        tvList.add(v.findViewById(R.id.color1));
        tvList.add(v.findViewById(R.id.color2));
        tvList.add(v.findViewById(R.id.color3));
        tvList.add(v.findViewById(R.id.color4));
        tvList.add(v.findViewById(R.id.color5));
        tvList.add(v.findViewById(R.id.color6));
        tvList.add(v.findViewById(R.id.color7));
        tvList.add(v.findViewById(R.id.color8));
        tvList.add(v.findViewById(R.id.color9));
        tvList.add(v.findViewById(R.id.color10));
        tvList.add(v.findViewById(R.id.color11));
        tvList.add(v.findViewById(R.id.color12));
        tvList.add(v.findViewById(R.id.color13));
        tvList.add(v.findViewById(R.id.color14));
        tvList.add(v.findViewById(R.id.color15));

        setClickListener();
    }

    private void setClickListener() {
        for (int i = 0; i <tvList.size(); i++) {
            tvList.get(i).setOnClickListener(this);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        if(view.getTag().toString().contains("colorbox")){
            String colorString = view.getTag().toString().substring(9, 16);
            int color = Color.parseColor(colorString);
            if(mPaletteListener != null)
                mPaletteListener.colorSelected(color);
        }
    }

    public interface PaletteListener{
        void colorSelected(int color);
    }
}