package me.trihung.learningapp2.UI;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.trihung.learningapp2.Adapter.BookAdapter;
import me.trihung.learningapp2.Adapter.Decoration.GridSpacingItemDecoration;
import me.trihung.learningapp2.Adapter.TuVungApdapter;
import me.trihung.learningapp2.DB.Database;
import me.trihung.learningapp2.Entity.Book;
import me.trihung.learningapp2.Entity.TuVung;
import me.trihung.learningapp2.My_Interface.InterfaceClickItemListener;
import me.trihung.learningapp2.My_Interface.InterfaceClickItemTuVungListener;
import me.trihung.learningapp2.R;

public class TuVungChuDeActivity extends AppCompatActivity {
    private Database db;
    private RecyclerView recyclerViewTV;
    private BookAdapter categoryAdapterTV;
    private TextView editTimChuDe;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuvung_chonchude);
        recyclerViewTV  = findViewById(R.id.rcv_categoryTV);
        db = new Database(this);
        categoryAdapterTV = new BookAdapter(getListCategoryTV(""), new InterfaceClickItemListener() {
            @Override
            public void onClickItem(Book book) {
                onClickGoToDetail(book);
            }

            @Override
            public void onClickItem(Object object) {

            }
        });
        LinearLayoutManager linearLayoutManagerTV = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewTV.setLayoutManager(gridLayoutManager);
        recyclerViewTV.addItemDecoration(new GridSpacingItemDecoration(20));
        categoryAdapterTV.setData(getListCategoryTV(""));
        recyclerViewTV.setAdapter(categoryAdapterTV);
        editTimChuDe = findViewById(R.id.edtTimChuDe);
        editTimChuDe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                categoryAdapterTV = new BookAdapter(getListCategoryTV(s.toString()), new InterfaceClickItemListener() {
                    @Override
                    public void onClickItem(Book book) {
                        Log.d("TAG", "onClickItemChuDe: "+book.getType());
                        onClickGoToDetail(book);
                    }

                    @Override
                    public void onClickItem(Object object) {

                    }
                });

                recyclerViewTV.setLayoutManager(gridLayoutManager);
                recyclerViewTV.setAdapter(categoryAdapterTV);
            }
        });

    }



    private List<Book> getListCategoryTV(String s) {
        List<Book> bookListTuVung = new ArrayList<>();
        try {
            Cursor c = db.query_hasresult("Select * from ChuDe WHERE tenChuDe like '%"+s+"%'");
            while(c.moveToNext()){
                String ten = c.getString(1);
                String hinhAnh = c.getString(2);
                String type = c.getString(3);
                int resourceId = this.getResources().getIdentifier(hinhAnh, "drawable", this.getPackageName());
                bookListTuVung.add(new Book(resourceId, ten, type));
            }
            return bookListTuVung;
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return null;
    }

    private void onClickGoToDetail(Book book){
        Intent intent = new Intent(this, ItemTuVungActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("tuVung_item", book);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
