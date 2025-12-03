package com.zeromarket.server.api.controller.display;

import com.zeromarket.server.api.dto.display.CategoryLevel1;
import com.zeromarket.server.api.dto.display.CategoryLevel2;
import com.zeromarket.server.api.dto.display.CategoryLevel3;
import com.zeromarket.server.api.service.display.CategoryQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
@Tag(name = "카테고리 API", description = "카테고리 관련 API")
public class CategoryController {

    private final CategoryQueryService categoryQueryService;

    @GetMapping("/level1")
    public List<CategoryLevel1> getLevel1() {
        return categoryQueryService.getLevel1();
    }

    @GetMapping("/level2")
    public List<CategoryLevel2> getLevel2(@RequestParam Long parentId) {
        return categoryQueryService.getLevel2(parentId);
    }

    @GetMapping("/level3")
    public List<CategoryLevel3> getLevel3(@RequestParam Long parentId) {
        return categoryQueryService.getLevel3(parentId);
    }
}
