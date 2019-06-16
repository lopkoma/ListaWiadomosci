package com.example.listawiadomosci;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    private TextView textView;
    private RestApiNotatki restApiNotatki;
    private static final String url_to_server = "https://polar-shore-16801.herokuapp.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = findViewById(R.id.text_view_result);

        Gson gson = new GsonBuilder().serializeNulls().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url_to_server)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        restApiNotatki = retrofit.create(RestApiNotatki.class);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        FloatingActionButton addMessage = (FloatingActionButton) findViewById(R.id.addMessage);
        addMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               addNewNoteToDataBase();
            }
        });

        FloatingActionButton updateMessage = (FloatingActionButton) findViewById(R.id.updateMsg);
        updateMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               updateNoteInDataBase();
            }
        });

        FloatingActionButton delMessage = (FloatingActionButton) findViewById(R.id.deleteMsg);
        delMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNoteDialog();
            }
        });
        getAllNotes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNewNote(String id,String title,String body) {

        NoteModel model = new NoteModel(id,title, body);

        Call<NoteModel> noteModelCall = restApiNotatki.createNoteIntoDB(model);
        noteModelCall.enqueue(new Callback<NoteModel>() {
            @Override
            public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                if(!response.isSuccessful()){
                    textView.setText("Code: " +response.code());
                    return;
                }

                NoteModel noteModel1Response = response.body();
                Toast.makeText(getApplicationContext(), "Tytuł : "
                        + noteModel1Response.getTitle()
                        + "Dane : "
                        + noteModel1Response.getContent(), Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
                return;
            }

            @Override
            public void onFailure(Call<NoteModel> call, Throwable t) {
                textView.setText(t.getMessage());
            }
        });
    }

    private void updateNote(String id, String title, String note) {

        NoteModel noteModel = new NoteModel(title, note);
        Call<NoteModel> noteModelCall = restApiNotatki.editNoteFormDB(id,noteModel);
        noteModelCall.enqueue(new Callback<NoteModel>() {
            @Override
            public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Udalo sie !: "
                            + response.body().getId(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            @Override
            public void onFailure(Call<NoteModel> call, Throwable t) {
                textView.setText(t.getMessage());
            }
        });
    }


    private void deleteMessageFromDataBase(String numerToDelete) {

        Call<Void> noteModelCall = restApiNotatki.deleteNoteFromDB(numerToDelete);
        noteModelCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                textView.setText("Code: " +response.code());
                Toast.makeText(getApplicationContext(), "Usuniento Wiadomosci z bazy danych : ",
                        Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                textView.setText(t.getMessage());
            }
        });
    }

    //////////////////////////////////////// OBSLUGA PRZYCISKOW ///////////////////////////////////////////////////////
    private void getAllNotes() {
        Call<List<NoteModel>> call = restApiNotatki.getAllNoteFromDB();
        call.enqueue(new Callback<List<NoteModel>>() {
            @Override
            public void onResponse(Call<List<NoteModel>> call, Response<List<NoteModel>> response) {
                if(!response.isSuccessful()){
                    textView.setText("Code: " +response.code());
                    return;
                }
                List<NoteModel> noteModels = response.body();
                for (NoteModel nodeList: noteModels) {
                    String s = "";
                    s += "Id: " + nodeList.getId() + "\n";
                    s += "Tytul: " + nodeList.getTitle() + "\n";
                    s += "Tresc: " + nodeList.getContent() + "\n\n";
                    textView.append(s);
                }
            }

            @Override
            public void onFailure(Call<List<NoteModel>> call, Throwable t) {
                textView.setText(t.getMessage());
            }
        });
    }

    private void addNewNoteToDataBase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dodawania notatki w baziedanych");
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.note_add_db, null);

        final EditText postTitleInput = (EditText) viewInflated.findViewById(R.id.postTitle);
        final EditText postBodyInput = (EditText) viewInflated.findViewById(R.id.postBody);
        final EditText postIDInput = (EditText) viewInflated.findViewById(R.id.postId);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String title = postTitleInput.getText().toString();
                String body = postBodyInput.getText().toString();
                String id = postIDInput.getText().toString();
                addNewNote(id, title,body);

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void deleteNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Usuwanie wpisu");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.note_detele, null);

        final EditText editText = (EditText) viewInflated.findViewById(R.id.numberToDelete);

        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                String numberToDelete = editText.getText().toString();
                deleteMessageFromDataBase(numberToDelete);

                finish();
                startActivity(getIntent());

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void updateNoteInDataBase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edycja danych");
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.note_edit, null);
        final EditText editText = (EditText) viewInflated.findViewById(R.id.deleteNumber);
        final EditText postTitleInput = (EditText) viewInflated.findViewById(R.id.postTitle);
        final EditText postBodyInput = (EditText) viewInflated.findViewById(R.id.postBody);

        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                String id = editText.getText().toString();
                String title = postTitleInput.getText().toString();
                String body = postBodyInput.getText().toString();

                updateNote(id, title, body);
                finish();
                startActivity(getIntent());

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
