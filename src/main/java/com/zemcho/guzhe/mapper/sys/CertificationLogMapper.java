package com.zemcho.guzhe.mapper.sys;

import com.zemcho.guzhe.entity.sys.CertificationLog;
import org.apache.ibatis.annotations.Param;

public interface CertificationLogMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    Integer insert(@Param("data") CertificationLog data);

    /**
     * 根据身份证查询
     *
     * @param cardNum
     * @return
     */
    CertificationLog selectByCardNum(@Param("cardNum") String cardNum);
}
