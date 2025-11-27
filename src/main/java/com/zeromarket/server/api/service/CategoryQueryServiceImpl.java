package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.CategoryLevel1;
import com.zeromarket.server.api.dto.CategoryLevel2;
import com.zeromarket.server.api.dto.CategoryLevel3;
import com.zeromarket.server.api.mapper.CategoryQueryMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private final CategoryQueryMapper mapper;

    @Override
    public List<CategoryLevel1> getLevel1() {
        return mapper.getLevel1();
    }

    @Override
    public List<CategoryLevel2> getLevel2(Long parentId) {
        return mapper.getLevel2(parentId);
    }

    @Override
    public List<CategoryLevel3> getLevel3(Long parentId) {
        return mapper.getLevel3(parentId);
    }
}
