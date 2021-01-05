package com.example.simplechat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //создаём поля для всех элементов
    private ListView messageListView;
    private SimpleMessageAdapter adapter;
    private ProgressBar progressBar;
    private ImageButton sendImageButton;
    private Button sendMessageButton;
    private EditText messageEditText;


    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressBar = findViewById(R.id.progressBar);
        sendImageButton = findViewById(R.id.sendPhotoButton);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        messageEditText = findViewById(R.id.messageEditText);


        //инициализируем
        userName = "Default User";

        messageListView = findViewById(R.id.messageListView);

        //создаём лист с элементами конструктора и передаём в адаптер
        List<SimpleMessage> simpleMessages = new ArrayList<>();
        adapter = new SimpleMessageAdapter(this, R.layout.message_item, simpleMessages);
        messageListView.setAdapter(adapter);

        //по умолчани прогресс бар невидимый
        progressBar.setVisibility(ProgressBar.INVISIBLE);



        //если набираем текст - то кнопа отправки становится активной
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0){
                    sendMessageButton.setEnabled(true);
                } else {
                    sendMessageButton.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //ограничиваем количество вводимых символов в сообщении
        messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});





        //даём возможность кликаь на кнопки
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //устанавливаем пустую строку после отправки сообщения
                messageEditText.setText("");
            }
        });

        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}