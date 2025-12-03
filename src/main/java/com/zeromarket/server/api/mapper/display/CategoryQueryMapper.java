package com.zeromarket.server.api.mapper.display;

import com.zeromarket.server.api.dto.display.CategoryLevel1;
import com.zeromarket.server.api.dto.display.CategoryLevel2;
import com.zeromarket.server.api.dto.display.CategoryLevel3;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryQueryMapper {

    List<CategoryLevel1> getLevel1();

    List<CategoryLevel2> getLevel2(Long parentId);

    List<CategoryLevel3> getLevel3(Long parentId);
}
