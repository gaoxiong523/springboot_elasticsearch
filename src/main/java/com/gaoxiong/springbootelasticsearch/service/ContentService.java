package com.gaoxiong.springbootelasticsearch.service;

import com.gaoxiong.springbootelasticsearch.dao.ContentRepository;
import com.gaoxiong.springbootelasticsearch.entity.ContentEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gaoxiong
 * @ClassName ContentService
 * @Description TODO
 * @date 2019/1/10 21:57
 */
@Service
@Slf4j
public class ContentService {
    @Autowired
    private ContentRepository repository;

    public ContentEntity findById(Long id){
        return repository.findById(id).get();
    }

    public Boolean saveContents( List<ContentEntity> contents ){
        repository.saveAll(contents);
        return true;
    }

    public Page<ContentEntity> search( ContentEntity content, Pageable pageable ){

        Page<ContentEntity> all = repository.findAll(Example.of(content, exampleMatcher()), pageable);

        return all;
    }

    private ExampleMatcher exampleMatcher (){
        return ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
    }

}
