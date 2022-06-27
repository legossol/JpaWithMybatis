package com.legossol.jpamybatisdemo.repository.jp;

import com.legossol.jpamybatisdemo.domain.MyTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyRepository extends JpaRepository<MyTable,Long> {



}