package me.trihung.learningapp2.My_Interface;

import me.trihung.learningapp2.Entity.Book;

public interface InterfaceClickItemListener {
    void onClickItem(Book book);

    void onClickItem(Object object);
}
