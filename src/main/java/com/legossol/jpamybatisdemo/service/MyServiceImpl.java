package com.legossol.jpamybatisdemo.service;

import com.legossol.jpamybatisdemo.domain.MyTable;
import com.legossol.jpamybatisdemo.repository.jp.MyRepository;
import com.legossol.jpamybatisdemo.repository.batis.MybatisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyServiceImpl {

    private final MyRepository jpaRepository;
    private final MybatisRepository mybatisRepository;

    @Transactional(value = "transactionManager")
    public void createOne(String name) {
        MyTable build = MyTable.builder().name(name).build();
        try {
            jpaRepository.save(build);
        } catch (Exception e) {
            log.info("jpa오류");
        }

        String forMybatisBuild = name + "newName";
        try{
            mybatisRepository.createOne(forMybatisBuild);
        } catch (Exception e) {
            log.info("mybatis 오류");
            e.printStackTrace();
        }

    }
}
