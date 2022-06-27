package com.legossol.jpamybatisdemo.repository.batis;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MybatisRepository {
    void createOne(String name);
}
