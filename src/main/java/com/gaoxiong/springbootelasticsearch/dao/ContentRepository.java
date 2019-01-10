package com.gaoxiong.springbootelasticsearch.dao;

import com.gaoxiong.springbootelasticsearch.entity.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author gaoxiong
 * @ClassName ContentRepository
 * @Description TODO
 * @date 2019/1/10 21:56
 */
public interface ContentRepository extends JpaRepository<ContentEntity,Long> {
}
