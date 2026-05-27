package com.actuary.ibps.infrastructure.mapper;

import com.actuary.ibps.infrastructure.entity.FreeMessageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FreeMessageMapper {

    long countByQuery(@Param("direction") String direction,
                      @Param("startDate") String startDate,
                      @Param("endDate") String endDate);

    List<FreeMessageEntity> selectByPage(@Param("direction") String direction,
                                         @Param("startDate") String startDate,
                                         @Param("endDate") String endDate,
                                         @Param("offset") int offset,
                                         @Param("pageSize") int pageSize);
}
