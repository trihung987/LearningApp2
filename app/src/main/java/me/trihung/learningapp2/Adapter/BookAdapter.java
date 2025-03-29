package me.trihung.learningapp2.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import me.trihung.learningapp2.Entity.Book;
import me.trihung.learningapp2.My_Interface.InterfaceClickItemListener;
import me.trihung.learningapp2.R;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder>{
    private List<Book> bookList;
    private InterfaceClickItemListener interfaceClickItemListener;
    public void setData(List<Book> list){
        this.bookList = list;
        notifyDataSetChanged();
    }

    public BookAdapter(List<Book> bookList, InterfaceClickItemListener interfaceClickItemListener) {
        this.bookList = bookList;
        this.interfaceClickItemListener = interfaceClickItemListener;
    }

    //TRUYỀN VÀO 1 VIEW
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    //SET DỮ LIỆU CHO VIEW
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        final Book book = bookList.get(position);
        if (book == null){
            return;
        }
        holder.imageBook.setImageResource(book.getResoutceId());
        holder.tvTitle.setText(book.getTitle());

        holder.layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfaceClickItemListener.onClickItem(book);
            }
        });
    }




    @Override
    public int getItemCount() {
        if (bookList != null){
            return bookList.size();
        }
        return 0;
    }

    public  class BookViewHolder extends RecyclerView.ViewHolder {
        private androidx.cardview.widget.CardView layout_item;
        private ImageView imageBook;
        private TextView tvTitle;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_item = itemView.findViewById(R.id.layout_item);
            imageBook = itemView.findViewById(R.id.imgBook);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
