package me.trihung.learningapp2.UI;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import me.trihung.learningapp2.Adapter.TuVungApdapter;
import me.trihung.learningapp2.DB.Database;
import me.trihung.learningapp2.Entity.TuVung;
import me.trihung.learningapp2.My_Interface.InterfaceClickItemTuVungListener;
import me.trihung.learningapp2.R;

import java.util.ArrayList;
import java.util.List;

public class ItemOnTapTuVungActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TuVungApdapter tuVungApdapter;
    TextView tv_name_user;
    ImageButton tvArrowBack;
    EditText edtTimTuVung;
    Bundle bundle;
    Database db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_ontaptuvung);
        tv_name_user = findViewById(R.id.tv_name_user);
        tvArrowBack = findViewById(R.id.btnBack);;
        edtTimTuVung = findViewById(R.id.edtTimTuVung);
        db = new Database(ItemOnTapTuVungActivity.this);


        tvArrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bundle  = getIntent().getExtras();
        if (bundle == null){
            return;
        }
        int idND  = (int) bundle.get("ontaptuVung_item");


        recyclerView  = findViewById(R.id.rcv_categoryLuyenThi);
        tuVungApdapter = new TuVungApdapter(ItemOnTapTuVungActivity.this,getListTuVung(idND), new InterfaceClickItemTuVungListener() {
            @Override
            public void onClickItemTuVung(TuVung tuVung) {
                Toast.makeText(ItemOnTapTuVungActivity.this, "Chưa có", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClickItem(Object object) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(tuVungApdapter);

        edtTimTuVung.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tuVungApdapter = new TuVungApdapter(ItemOnTapTuVungActivity.this,getListTuVungBySearch(idND, s.toString()), new InterfaceClickItemTuVungListener() {
                    @Override
                    public void onClickItemTuVung(TuVung tuVung) {
                        Toast.makeText(ItemOnTapTuVungActivity.this, "Chưa có", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onClickItem(Object object) {

                    }
                });
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(tuVungApdapter);
            }
        });
    }

    private List<TuVung> getListTuVung(int idND) {
        List<TuVung> list = new ArrayList<>();
        try {
            Cursor c = db.query_hasresult("SELECT tiengAnh, phienAm, tiengViet, grouptv FROM ChiTietTuVung c join TuVung t on c.idTuVung = t.idTuVung WHERE idND = "+idND+"");
            while(c.moveToNext()){
                String tiengAnh = c.getString(0);
                String phienAm = c.getString(1);
                String tiengViet = c.getString(2);
                String group = c.getString(3);
                list.add(new TuVung(tiengAnh,tiengViet, phienAm, group));
            }
            return list;
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return null;
    }

    private List<TuVung> getListTuVungBySearch(int idND,String keyWord) {
        List<TuVung> list = new ArrayList<>();
        try {
            Cursor c = db.query_hasresult("SELECT tiengAnh, phienAm, tiengViet, grouptv FROM ChiTietTuVung c join TuVung t on c.idTuVung = t.idTuVung WHERE idND = "+idND+" and tiengAnh like '%"+keyWord+"%'");
            while(c.moveToNext()){
                String tiengAnh = c.getString(0);
                String phienAm = c.getString(1);
                String tiengViet = c.getString(2);
                String group = c.getString(3);
                list.add(new TuVung(tiengAnh,tiengViet, phienAm, group));
            }
            return list;
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return null;
    }
}