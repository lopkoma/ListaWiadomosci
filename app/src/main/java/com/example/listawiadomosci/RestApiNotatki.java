package com.example.listawiadomosci;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RestApiNotatki {
    @GET("notatki")
    Call<List<NoteModel>> getAllNoteFromDB();

    @POST("notatki")
    Call<NoteModel>createNoteIntoDB(@Body NoteModel post);

    @PATCH("/notatki/{id}")
    Call<NoteModel> editNoteFormDB(@Path("id") String id, @Body NoteModel model);

    @DELETE("/notatki/{id}")
    Call<Void> deleteNoteFromDB(@Path("id") String id);
}
