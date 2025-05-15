package com.example.balaton;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LatnivaloAdapter extends RecyclerView.Adapter<LatnivaloAdapter.ViewHolder> {
    private List<Latnivalo> lista;
    private String felhasznaloId;

    public LatnivaloAdapter(List<Latnivalo> lista, String felhasznaloId) {
        this.lista = lista;
        this.felhasznaloId = felhasznaloId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.latnivalo_card, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        Latnivalo l = lista.get(pos);

        holder.textNev.setText(l.getNev());
        holder.textVaros.setText("Város: " + l.getVaros());
        holder.textKategoria.setText("Kategóriák: " + String.join(", ", l.getKategoria()));
        holder.textLeiras.setText(l.getLeiras());

        String favDocId = felhasznaloId + "_" + l.getNev(); // vagy latnivaloId, ha van
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 🔍 Ellenőrzés: kedvenc-e?
        db.collection("kedvencek").document(favDocId).get().addOnSuccessListener(snapshot -> {
            boolean kedvenc = snapshot.exists();
            holder.btnFavorite.setImageResource(kedvenc ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        });

        // ❤️ Gombra kattintás
        holder.btnFavorite.setOnClickListener(v -> {
            DocumentReference ref = db.collection("kedvencek").document(favDocId);
            ref.get().addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    ref.delete();
                    holder.btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                } else {
                    Map<String, Object> data = new HashMap<>();
                    data.put("felhasznaloId", felhasznaloId);
                    data.put("latnivaloNev", l.getNev());
                    data.put("timestamp", FieldValue.serverTimestamp());

                    ref.set(data);
                    holder.btnFavorite.setImageResource(R.drawable.ic_favorite);
                }
            });
        });
        holder.btnShare.setOnClickListener(v -> {
            String megosztandoSzoveg =
                    "📍 " + l.getNev() + "\n" +
                            "🏙️ Város: " + l.getVaros() + "\n" +
                            "📂 Kategóriák: " + String.join(", ", l.getKategoria()) + "\n" +
                            "ℹ️ " + l.getLeiras();

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, megosztandoSzoveg);

            Intent shareIntent = Intent.createChooser(sendIntent, "Megosztás...");
            v.getContext().startActivity(shareIntent);
        });

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void frissit(List<Latnivalo> ujLista) {
        this.lista = ujLista;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton btnFavorite;
        ImageButton btnShare;
        TextView textNev, textVaros, textKategoria, textLeiras;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNev = itemView.findViewById(R.id.textNev);
            textVaros = itemView.findViewById(R.id.textVaros);
            textKategoria = itemView.findViewById(R.id.textKategoria);
            textLeiras = itemView.findViewById(R.id.textLeiras);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnShare = itemView.findViewById(R.id.btnShare);
        }
    }
}