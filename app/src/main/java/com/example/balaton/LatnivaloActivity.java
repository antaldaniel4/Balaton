package com.example.balaton;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class LatnivaloActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private LatnivaloAdapter adapter;
    private List<Latnivalo> latnivalokList = new ArrayList<>();
    private List<String> kedvencekNevek = new ArrayList<>();
    private Spinner spinnerVaros, spinnerKategoria;

    private String szurtVaros = "Város";
    private String szurtKategoria = "Kategória";
    private String felhasznaloId;
    private CheckBox checkOnlyFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latnivalo);

        db = FirebaseFirestore.getInstance();
        felhasznaloId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        checkOnlyFavorites = findViewById(R.id.checkOnlyFavorites);
        checkOnlyFavorites.setOnCheckedChangeListener((buttonView, isChecked) -> szuresEsFrissites());

        recyclerView = findViewById(R.id.recyclerLatnivalok);
        spinnerVaros = findViewById(R.id.spinnerVaros);
        spinnerKategoria = findViewById(R.id.spinnerKategoria);

        adapter = new LatnivaloAdapter(new ArrayList<>(), felhasznaloId); // üres lista
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //lekeroLatnivalok();
        lekeroKedvencekEsLatnivalok();
        //Init töltés  TODO gomb aktiválás iniciális töltéshez
        Button feltoltesGomb = findViewById(R.id.btnFeltoltes);
        //feltoltesGomb.setOnClickListener(v -> feltoltAdatokFirestoreba(this));

        // Itt fogadja a pöttyre kattintásból a város nevét
        Intent intent = getIntent();
        String varosSzuro = intent.getStringExtra("varosSzuro");
        boolean csakKedvencek = intent.getBooleanExtra("csakKedvencek", false);


        if (varosSzuro != null) {
            szurtVaros = varosSzuro;
        }
        checkOnlyFavorites.setChecked(csakKedvencek);
    }

    private void lekeroLatnivalok() {
        db.collection("latnivalok").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Set<String> varosokSet = new HashSet<>();
                Set<String> kategoriakSet = new HashSet<>();
                latnivalokList.clear();

                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Latnivalo latnivalo = doc.toObject(Latnivalo.class);

                    // Log kiírás
                    /*
                    Log.d("FIREBASE_LATNIVALO", "Név: " + latnivalo.getNev() +
                            ", Város: " + latnivalo.getVaros() +
                            ", Kategóriák: " + latnivalo.getKategoria());
                     */
                    latnivalo.setKedvenc(kedvencekNevek.contains(latnivalo.getNev()));

                    latnivalokList.add(latnivalo);
                    varosokSet.add(latnivalo.getVaros());
                    if (latnivalo.getKategoria() != null) {
                        kategoriakSet.addAll(latnivalo.getKategoria());
                    }
                }

                List<String> varosok = new ArrayList<>(varosokSet);
                varosok.add(0, "Összes");

                List<String> kategoriak = new ArrayList<>(kategoriakSet);
                kategoriak.add(0, "Összes");

                initSpinner(spinnerVaros, varosok, true);
                initSpinner(spinnerKategoria, kategoriak, false);


                //Debug kedvencek
                for (Latnivalo l : latnivalokList) {
                    Log.d("LATNIVALO_KEDVENC", l.getNev() + ": " + l.isKedvenc());
                }

                szuresEsFrissites();
            } else {
                Toast.makeText(this, "Hiba a letöltéskor: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void lekeroKedvencekEsLatnivalok() {
        // 1. Lekérjük a kedvenceket
        db.collection("kedvencek")
                .whereEqualTo("felhasznaloId", felhasznaloId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    kedvencekNevek.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String nev = doc.getString("latnivaloNev");
                        if (nev != null) {
                            kedvencekNevek.add(nev);
                        }
                    }
                    lekeroLatnivalok(); // csak akkor kérjük le a látnivalókat, ha már ismerjük a kedvenceket
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Kedvencek lekérdezése sikertelen: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void initSpinner(Spinner spinner, List<String> lista, boolean varosSpinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lista);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (varosSpinner) {
                    szurtVaros = lista.get(pos);
                } else {
                    szurtKategoria = lista.get(pos);
                }
                szuresEsFrissites();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void szuresEsFrissites() {
        List<Latnivalo> szurtLista = new ArrayList<>();
        boolean csakKedvencek = checkOnlyFavorites.isChecked();

        for (Latnivalo latnivalo : latnivalokList) {
            boolean varosOK = szurtVaros.equals("Összes") || latnivalo.getVaros().equals(szurtVaros);
            boolean kategoriaOK = szurtKategoria.equals("Összes") || latnivalo.getKategoria().contains(szurtKategoria);
            boolean kedvencOK = !csakKedvencek || latnivalo.isKedvenc();

            if (varosOK && kategoriaOK && kedvencOK) {
                szurtLista.add(latnivalo);
            }
        }
        adapter.frissit(szurtLista);
    }


    private void feltoltAdatokFirestoreba(Context context) {
        try {
            InputStream is = context.getAssets().open("latnivalok.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            JSONArray jsonArray = new JSONArray(jsonBuilder.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                Latnivalo l = new Latnivalo();
                l.setNev(obj.getString("name"));
                l.setVaros(obj.getString("city"));
                l.setCim(obj.getString("address"));
                l.setLeiras(obj.getString("description"));

                JSONArray katArray = obj.getJSONArray("category");
                List<String> kategoriak = new ArrayList<>();
                for (int j = 0; j < katArray.length(); j++) {
                    kategoriak.add(katArray.getString(j));
                }
                l.setKategoria(kategoriak);

                db.collection("latnivalok").add(l);
            }

            Toast.makeText(context, "Adatok sikeresen feltöltve!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("Firestore", "Hiba a JSON olvasása vagy feltöltés során", e);
            Toast.makeText(context, "Hiba: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
