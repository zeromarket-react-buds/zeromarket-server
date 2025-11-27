package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.CategoryLevel1;
import com.zeromarket.server.api.dto.CategoryLevel2;
import com.zeromarket.server.api.dto.CategoryLevel3;
import java.util.List;

public interface CategoryQueryService {

    List<CategoryLevel1> getLevel1();

    List<CategoryLevel2> getLevel2(Long parentId);

    List<CategoryLevel3> getLevel3(Long parentId);


}
