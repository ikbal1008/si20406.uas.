package ikbal.maulana.mycontactlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;

    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        firebaseFirestore = FirebaseFirestore.getInstance();

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        getData();

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener((v)-> {
                startActivity(new Intent(MainActivity.this, TambahContact.class ));

        });
    }

    private void getData() {

        Query query = firebaseFirestore.collection("Contacts");

        FirestoreRecyclerOptions<ContactClass> response = new FirestoreRecyclerOptions.Builder<ContactClass>()
                .setQuery(query, ContactClass.class).build();

        adapter = new FirestoreRecyclerAdapter<ContactClass, ContactsHolder>(response){

            @NonNull
            @Override
            public ContactsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact,parent, false);

                return new ContactsHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ContactsHolder holder, int position, @NonNull ContactClass model) {

                Picasso.get().load(model.getFoto()).fit().into(holder.fotoContact);

                holder.namaContact.setText(model.getNama());

                holder.teleponContact.setText(model.getTelepon());
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    public class ContactsHolder extends RecyclerView.ViewHolder{

        CircleImageView fotoContact;
        TextView namaContact, teleponContact;
        ConstraintLayout constraintLayout;

        public ContactsHolder(@NonNull View itemView) {
            super(itemView);
            fotoContact = itemView.findViewById(R.id.imageViewFoto);
            namaContact = itemView.findViewById(R.id.textViewNama);
            teleponContact = itemView.findViewById(R.id.textViewTelepon);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}