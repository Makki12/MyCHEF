package com.example.mychef;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;

public class MalzemeActivity extends AppCompatActivity {

    private LinearLayout layoutMalzemeler;
    private LinearLayout categoryButtonsLayout;
    private Button buttonMalzemeEkle;

    private String aktifKategori = "Hepsi";
    private ArrayList<String> kategoriler = new ArrayList<>();
    private HashMap<String, ArrayList<String>> malzemeMap = new HashMap<>();

    private SharedPreferences sharedPreferences;
    private static final String BASE_PREF_NAME = "MalzemePrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_malzeme);

        layoutMalzemeler = findViewById(R.id.layoutMalzemeler);
        categoryButtonsLayout = findViewById(R.id.categoryButtonsLayout);
        buttonMalzemeEkle = findViewById(R.id.buttonMalzemeEkle);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sharedPreferences = getSharedPreferences(BASE_PREF_NAME + "_" + uid, MODE_PRIVATE);

        kategoriler.add("Hepsi");
        kategoriler.add("Süt Ürünleri");
        kategoriler.add("Tahıl");
        kategoriler.add("Et Ürünleri");
        kategoriler.add("Bakliyat");
        kategoriler.add("Meyve & Sebze");
        kategoriler.add("Baharat");
        kategoriler.add("Diğer Malzemeler");

        for (String kategori : kategoriler) {
            malzemeMap.put(kategori, new ArrayList<>());

            Button btn = new Button(this);
            btn.setText(kategori);
            btn.setOnClickListener(v -> {
                aktifKategori = kategori;
                listeyiGuncelle();
            });
            categoryButtonsLayout.addView(btn);
        }

        malzemeleriYukle();
        listeyiGuncelle();

        buttonMalzemeEkle.setOnClickListener(v -> malzemeEklePopup());
    }

    private void malzemeEklePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yeni Malzeme Ekle");

        View view = getLayoutInflater().inflate(R.layout.dialog_malzeme_ekle, null);
        EditText editMalzeme = view.findViewById(R.id.editYeniMalzeme);
        Spinner spinner = view.findViewById(R.id.spinnerKategori);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kategoriler.subList(1, kategoriler.size()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        builder.setView(view);
        builder.setPositiveButton("Ekle", (dialog, which) -> {
            String malzeme = editMalzeme.getText().toString().trim();
            String kategori = spinner.getSelectedItem().toString();

            if (!malzeme.isEmpty()) {
                malzemeMap.get(kategori).add(malzeme);
                malzemeMap.get("Hepsi").add(malzeme);
                listeyiGuncelle();
                malzemeleriKaydet();
            } else {
                Toast.makeText(this, "Malzeme boş olamaz", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("İptal", null);
        builder.create().show();
    }

    private void listeyiGuncelle() {
        layoutMalzemeler.removeAllViews();
        ArrayList<String> liste = malzemeMap.get(aktifKategori);
        if (liste != null) {
            for (String item : liste) {
                TextView tv = new TextView(this);
                tv.setText("- " + item);
                tv.setTextSize(16f);
                tv.setPadding(8, 8, 8, 8);
                tv.setOnClickListener(v -> secenekGoster(item));
                layoutMalzemeler.addView(tv);
            }
        }
    }

    private void secenekGoster(String item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seçenekler");
        String[] secenekler = {"Adı Değiştir", "Sil", "Kategorisini Değiştir"};
        builder.setItems(secenekler, (dialog, which) -> {
            switch (which) {
                case 0: malzemeAdiDegistir(item); break;
                case 1: malzemeSil(item); break;
                case 2: kategoriDegistir(item); break;
            }
        });
        builder.show();
    }

    private void malzemeAdiDegistir(String eskiAd) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yeni Ad");

        EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Kaydet", (dialog, which) -> {
            String yeniAd = input.getText().toString().trim();
            if (!yeniAd.isEmpty()) {
                for (String kategori : kategoriler) {
                    ArrayList<String> liste = malzemeMap.get(kategori);
                    if (liste.contains(eskiAd)) {
                        liste.set(liste.indexOf(eskiAd), yeniAd);
                    }
                }
                listeyiGuncelle();
                malzemeleriKaydet();
            }
        });
        builder.setNegativeButton("İptal", null);
        builder.show();
    }

    private void malzemeSil(String ad) {
        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle("Emin misiniz?");
        confirm.setMessage("Bu malzeme silinecek.");
        confirm.setPositiveButton("Evet", (dialog, which) -> {
            for (String kategori : kategoriler) {
                malzemeMap.get(kategori).remove(ad);
            }
            listeyiGuncelle();
            malzemeleriKaydet();
        });
        confirm.setNegativeButton("Vazgeç", null);
        confirm.show();
    }

    private void kategoriDegistir(String ad) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yeni Kategori Seç");

        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kategoriler.subList(1, kategoriler.size()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        builder.setView(spinner);
        builder.setPositiveButton("Kaydet", (dialog, which) -> {
            String yeniKategori = spinner.getSelectedItem().toString();
            for (String kategori : kategoriler) {
                malzemeMap.get(kategori).remove(ad);
            }
            malzemeMap.get("Hepsi").add(ad);
            malzemeMap.get(yeniKategori).add(ad);
            listeyiGuncelle();
            malzemeleriKaydet();
        });
        builder.setNegativeButton("İptal", null);
        builder.show();
    }

    private void malzemeleriKaydet() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String kategori : kategoriler) {
            JSONArray jsonArray = new JSONArray(malzemeMap.get(kategori));
            editor.putString(kategori, jsonArray.toString());
        }
        editor.apply();
    }

    private void malzemeleriYukle() {
        for (String kategori : kategoriler) {
            ArrayList<String> liste = new ArrayList<>();
            String json = sharedPreferences.getString(kategori, null);
            if (json != null) {
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        liste.add(jsonArray.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            malzemeMap.put(kategori, liste);
        }
    }
}
