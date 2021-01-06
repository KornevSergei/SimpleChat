package com.example.simplechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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



    DatabaseReference usersDatabaseReference;
    //для слушания измениеий файрбейс
    ChildEventListener usersChildEventListener;

    private static final int RC_IMAGE_PICKER = 123;


    //переменые для загрузки изображений в хранилище файрбейс
    FirebaseStorage storage;
    StorageReference chatImagesStorageReference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //инициализируем БД
        //получаем доступ ко всей базе дынных к корневой папке
        database = FirebaseDatabase.getInstance();
        messagesDatabaseReference = database.getReference().child("messages");
        usersDatabaseReference = database.getReference().child("users");
        chatImagesStorageReference = storage.getReference().child("ChatImages");


        //инициализирум для изобраений
        storage = FirebaseStorage.getInstance();



        progressBar = findViewById(R.id.progressBar);
        sendImageButton = findViewById(R.id.sendPhotoButton);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        messageEditText = findViewById(R.id.messageEditText);

        //получаем информацию из активити регистрации и присваиваем по ключу
        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("userName");
        } else {
            userName = "Стандартное имя";
        }


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

        //метод для отправки картинок в сообщении
        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //создали интент для получения контента изображений
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //устанавливаем тип интента, все варианты изображений
                intent.setType("image/*");
                //указываем что будем брать изображения с локального хранилища
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                //оздаём активти выбора и помещаем
                startActivityForResult(Intent.createChooser(intent, "Выберите изображения"),RC_IMAGE_PICKER);

            }
        });

        //когда происходит собтие - включается один из этих методов, для постоянного получения собщений
        usersChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                //делаем проверку на определения конкретного юзера из файрбейс
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    //если всё совпадает присваиваем имя
                    userName = user.getName();
                }

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

        usersDatabaseReference.addChildEventListener(usersChildEventListener);







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

    //делаем метод для меню выхода
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    //елаем выход из логина
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                //после выхода возвращаемся на главный экран
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //метод для обработки результат запроса изображений
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //проверяем на соответствие вызовов выбора изображения
        if (requestCode == RC_IMAGE_PICKER && requestCode == RESULT_OK){
            Uri selectedImageUri = data.getData();


            StorageReference imageReference = chatImagesStorageReference.child(selectedImageUri.getLastPathSegment());

        }
    }
}