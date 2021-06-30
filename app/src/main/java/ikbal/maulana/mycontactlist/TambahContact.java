package ikbal.maulana.mycontactlist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class TambahContact extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;

    ImageView FotoContact;
    EditText TextNama, TextTelepon, TextSosmed, TextAlamat;
    Button TombolSimpan, TombolKembali;
    ProgressBar progressBar;

    Uri filePath;
    String fotoUrl;

    static final int IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_contact);

        FotoContact = findViewById(R.id.imageViewFoto);
        TextNama = findViewById(R.id.editTextNama);
        TextTelepon = findViewById(R.id.editTextTelepon);
        TextSosmed = findViewById(R.id.editTextSosmed);
        TextAlamat = findViewById(R.id.editTextAlamat);

        TombolSimpan = findViewById(R.id.buttonSimpan);
        TombolKembali = findViewById(R.id.buttonKembali);

        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);


        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        FotoContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ambilGambar();
            }
        });

        TombolSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        TombolKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void uploadImage(){
        if(filePath !=null){
            final StorageReference ref = storageReference.child(TextTelepon.getText().toString());
            UploadTask uploadTask = ref.putFile(filePath);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri imagePath = task.getResult();

                    fotoUrl = imagePath.toString();
                    SimpanData(TextNama.getText().toString(),
                            TextTelepon.getText().toString(),
                            TextSosmed.getText().toString(),
                            TextAlamat.getText().toString(),
                            fotoUrl);

                    Toast.makeText(TambahContact.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                    double proges = (100.0* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int)proges);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(TambahContact.this, "Gagal Upload: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void SimpanData(String nama, String telepon, String sosmed, String alamat, String foto){
        Map<String, Object> contactData = new HashMap<>();

        contactData.put("nama", nama);
        contactData.put("telepon", telepon);
        contactData.put("sosmed", sosmed);
        contactData.put("alamat", alamat);
        contactData.put("foto", foto);

        firebaseFirestore.collection("contacts").document(telepon).set(contactData).isSuccessful();
    }

    private void ambilGambar(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data!= null && data.getData() != null){
            filePath = data.getData();
            Picasso.get().load(filePath).fit().into(FotoContact);
        }else {
            Toast.makeText(this, "Tidak ada gambar yang di pilih", Toast.LENGTH_SHORT).show();
        }
    }
}