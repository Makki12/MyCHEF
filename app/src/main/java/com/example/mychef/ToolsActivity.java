package com.example.mychef;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;

public class ToolsActivity extends AppCompatActivity {

    private LinearLayout layoutTools;
    private EditText editTool;
    private Button buttonAddTool;
    private ArrayList<String> toolsList = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private static final String BASE_PREF_NAME = "ToolsPrefs";
    private static final String KEY_TOOLS = "tool_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);

        layoutTools = findViewById(R.id.layoutTools);
        editTool = findViewById(R.id.editTool);
        buttonAddTool = findViewById(R.id.buttonAddTool);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sharedPreferences = getSharedPreferences(BASE_PREF_NAME + "_" + uid, MODE_PRIVATE);

        yukleAraclar();
        guncelleListe();

        buttonAddTool.setOnClickListener(v -> {
            String tool = editTool.getText().toString().trim();
            if (!tool.isEmpty()) {
                toolsList.add(tool);
                kaydetAraclar();
                guncelleListe();
                editTool.setText("");
            } else {
                Toast.makeText(this, "Lütfen araç/gereç girin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guncelleListe() {
        layoutTools.removeAllViews();
        for (String tool : toolsList) {
            TextView tv = new TextView(this);
            tv.setText("- " + tool);
            tv.setTextSize(16f);
            tv.setPadding(8, 8, 8, 8);
            tv.setOnClickListener(v -> gosterAracSecenekleri(tool));
            layoutTools.addView(tv);
        }
    }

    private void gosterAracSecenekleri(String ad) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seçenekler");
        String[] secenekler = {"Adı Değiştir", "Sil"};
        builder.setItems(secenekler, (dialog, which) -> {
            if (which == 0) aracAdiDegistir(ad);
            else if (which == 1) aracSil(ad);
        });
        builder.show();
    }

    private void aracAdiDegistir(String eskiAd) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yeni Ad");

        EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Kaydet", (dialog, which) -> {
            String yeniAd = input.getText().toString().trim();
            if (!yeniAd.isEmpty()) {
                int index = toolsList.indexOf(eskiAd);
                if (index != -1) {
                    toolsList.set(index, yeniAd);
                    kaydetAraclar();
                    guncelleListe();
                }
            }
        });
        builder.setNegativeButton("İptal", null);
        builder.show();
    }

    private void aracSil(String ad) {
        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle("Emin misiniz?");
        confirm.setMessage("Bu araç silinecek.");
        confirm.setPositiveButton("Evet", (dialog, which) -> {
            toolsList.remove(ad);
            kaydetAraclar();
            guncelleListe();
        });
        confirm.setNegativeButton("Vazgeç", null);
        confirm.show();
    }

    private void kaydetAraclar() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray(toolsList);
        editor.putString(KEY_TOOLS, jsonArray.toString());
        editor.apply();
    }

    private void yukleAraclar() {
        String json = sharedPreferences.getString(KEY_TOOLS, null);
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    toolsList.add(jsonArray.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
