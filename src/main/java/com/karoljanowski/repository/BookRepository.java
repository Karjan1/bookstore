package com.karoljanowski.repository;

import com.karoljanowski.domain.Book;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Karol Janowski on 2017-06-20.
 */
public interface BookRepository extends CrudRepository<Book, Long>{
    List<Book> findAll();
}
