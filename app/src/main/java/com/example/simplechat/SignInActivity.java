package com.example.simplechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


//класс для авторизации
public class SignInActivity extends AppCompatActivity {

    //переменная для вывода резульата в лог
    private static final String TAG = "SignInActivity";

    private FirebaseAuth auth;

    //переменные для авторизации
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
    private EditText nameEditText;
    private TextView toggleLoginSingUpTextView;
    private Button loginSingUpButton;

    //переменная для переключения логина и регистрации
    private Boolean loginModeActive;

    //поля для добавления в базу даных файрбейс подключив библиотеку
    FirebaseDatabase database;
    //будем здесь хранить информацию о пользователях
    DatabaseReference usersDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();


        //инициализируем БД
        //получаем доступ ко всей базе дынных к корневой папке
        database = FirebaseDatabase.getInstance();

        //связываем
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        toggleLoginSingUpTextView = findViewById(R.id.toggleLoginSingUpTextView);
        loginSingUpButton = findViewById(R.id.loginSingUpButton);

        //делаем слушатель для кнопки
        loginSingUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //передаем значения строки емайла и пароля
                loginSingUpUser(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim());
            }
        });

        //проверяем, если пользователь залогинен - переправляем его на нужный экран
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
        }


    }

    //метод для создания новой учетной записи
    private void loginSingUpUser(String email, String password) {

        //делаем проверку на то в каком моде регистрации или лога пользщователь
        if (loginModeActive) {
            if (passwordEditText.getText().toString().trim().length() < 7) {
                Toast.makeText(this, "Меньше семи знаков", Toast.LENGTH_SHORT).show();

            } else if (emailEditText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Пустая почта", Toast.LENGTH_SHORT).show();


            } else {

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();

                                    //если ок делаем переход в нвоое активити
                                    //передаем имя пользователя в сообщения при переписке
                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                    intent.putExtra("userName", nameEditText.getText().toString().trim());
                                    startActivity(intent);

//                                updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
//                                updateUI(null);
                                    // ...
                                }

                                // ...
                            }
                        });
            }
        } else {

            //проверяем на совпдение пароля и повторного
            if (!passwordEditText.getText().toString().trim().equals(repeatPasswordEditText.getText().toString().trim())) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();

                //роверяем на пустоту ввода и количество символов
            } else if (passwordEditText.getText().toString().trim().length() < 7) {
                Toast.makeText(this, "Меньше семи знаков", Toast.LENGTH_SHORT).show();

            } else if (emailEditText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Пустая почта", Toast.LENGTH_SHORT).show();


            } else {


                //если не ок - ввыводим лог
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    //ссылаемся на метод
                                    createUser(user);

                                    //если ок делаем переход в нвоое активити
                                    //передаем имя пользователя в сообщения при переписке
                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                    intent.putExtra("userName", nameEditText.getText().toString().trim());
                                    startActivity(intent);

//                            updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                                }

                            }
                        });
            }

        }

    }

    //метод для создания пользователя
    private void createUser(FirebaseUser firebaseUser) {
        //создали обьект
        User user = new User();
        //ссылаемся на параметры класса
        user.setId(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        //получаем имя из поля ввода
        user.setName(nameEditText.getText().toString().trim());

        //если пользователь создается то сохраняем информацию в файрбейс базе
        usersDatabaseReference.push().setValue(user);

    }


    //метод для переключения логина и регистрации по клику на текст
    public void toggleLoginMode(View view) {

        if (loginModeActive) {
            loginModeActive = false;
            loginSingUpButton.setText("Зарегистрироваться");
            toggleLoginSingUpTextView.setText("Войти");
            repeatPasswordEditText.setVisibility(View.VISIBLE);
        } else {
            loginModeActive = true;
            loginSingUpButton.setText("Войти");
            toggleLoginSingUpTextView.setText("Зарегистрироваться");
            repeatPasswordEditText.setVisibility(View.GONE);
        }
    }
}