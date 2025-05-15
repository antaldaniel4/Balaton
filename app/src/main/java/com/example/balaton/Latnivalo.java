package com.example.balaton;

import android.widget.Toast;


import android.content.Context;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.PropertyName;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Latnivalo {
    private String Nev;
    private String Varos;
    private String Cim;
    private List<String> Kategoria;
    private String Leiras;
    private boolean kedvenc;




    public String getNev() {
        return Nev != null ? Nev : "";
    }

    public String getVaros() {
        return Varos != null ? Varos : "";
    }

    public String getCim() {
        return Cim != null ? Cim : "";
    }

    public String getLeiras() {
        return Leiras != null ? Leiras : "";
    }

    public List<String> getKategoria() {
        return Kategoria != null ? Kategoria : new ArrayList<>();
    }
    public boolean isKedvenc() {
        return kedvenc;
    }

    public void setKedvenc(boolean kedvenc) {
        this.kedvenc = kedvenc;
    }


    public void setNev(String nev) {
        Nev = nev;
    }

    public void setVaros(String varos) {
        Varos = varos;
    }

    public void setCim(String cim) {
        Cim = cim;
    }

    public void setKategoria(List<String> kategoria) {
        Kategoria = kategoria;
    }

    public void setLeiras(String leiras) {
        Leiras = leiras;
    }

    public Latnivalo() {} // Firestore-hoz kell
    private void feltoltAdatokFirestoreba(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                l.setNev(obj.getString("Név"));
                l.setVaros(obj.getString("Város"));
                l.setCim(obj.getString("Cím"));
                l.setLeiras(obj.getString("Leírás"));

                JSONArray katArray = obj.getJSONArray("Kategória");
                List<String> kategoriak = new ArrayList<>();
                for (int j = 0; j < katArray.length(); j++) {
                    kategoriak.add(katArray.getString(j));
                }
                l.setKategoria(kategoriak);

                db.collection("latnivalok").add(l);
            }

            Toast.makeText(context, "Adatok feltöltve!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Hiba a feltöltéskor: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



}