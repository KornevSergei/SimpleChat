package com.example.simplechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    //поля для добавления в базу даных файрбейс подключив библиотеку
    FirebaseDatabase database;
    //будем здесь хранить сообщения
    DatabaseReference messagesDatabaseReference;
    //будем здесь хранить пользователей

    //для слушания измениеий файрбейс
    ChildEventListener messagesChildEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //инициализируем БД
        //получаем доступ ко всей базе дынных к корневой папке
        database = FirebaseDatabase.getInstance();
        messagesDatabaseReference = database.getReference().child("messages");


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
                if (s.toString().trim().length() > 0) {
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

                //устанавливаем тект сообщения из введеденого текста
                SimpleMessage message = new SimpleMessage();
                message.setText(messageEditText.getText().toString());
                message.setName(userName);
                message.setImageUrl(null);

                //передаем информацию в базу
                messagesDatabaseReference.push().setValue(message);

                //устанавливаем пустую строку после отправки сообщения
                messageEditText.setText("");
            }
        });

        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //когда происходит собтие - включается один из этих методов, для постоянного получения собщений
        messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //когда получаем какое то значение, передаем на распознание в другой класс
                SimpleMessage message = snapshot.getValue(SimpleMessage.class);

                //передаём в список
                adapter.add(message);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        //обавляем введеные сообщения в адаптер
        messagesDatabaseReference.addChildEventListener(messagesChildEventListener);
    }
}