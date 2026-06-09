package com.zemcho.ddql.entity.personalCenter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Region {

    private Integer id;

    private Integer pid;

    private Integer level;

    private String nickName;

    private String name;

    private String code;

}
