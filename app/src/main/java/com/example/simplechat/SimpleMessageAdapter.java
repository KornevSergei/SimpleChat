package com.example.simplechat;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;


//класс который содержит обьекты сообщений
public class SimpleMessageAdapter extends ArrayAdapter<SimpleMessage> {
    public SimpleMessageAdapter(Context context, int resource, List<SimpleMessage> messages) {
        super(context, resource, messages);
    }


    //получаем параметры элемента сообщений
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.message_item, parent, false);
        }

        //связываем с элементами
        ImageView photoImageView = convertView.findViewById(R.id.photoImageView);
        TextView textTextView = convertView.findViewById(R.id.textView);
        TextView nameTextView = convertView.findViewById(R.id.nameView);

        //передаем все параметры в обьект
        SimpleMessage message = getItem(position);

        //проверяем на текст или картинку
        Boolean isText = message.getImageUrl() == null;

        //если ок то даём видимость  если нет - устанавливаем текст
        if (isText) {
            textTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            textTextView.setText(message.getText());
        } else {
            textTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            //подключение библиотеки Глайд
            Glide.with(photoImageView.getContext())
                    .load(message.getImageUrl())
                    .into(photoImageView);
        }

        //неважно от картинки или текста всега устанавливаем имя отправителя
        nameTextView.setText(message.getName());

        //возвращаем все полученые резщульаты
        return convertView;
    }
}
