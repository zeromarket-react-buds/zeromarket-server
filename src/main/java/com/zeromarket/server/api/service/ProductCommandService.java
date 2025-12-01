package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.ProductCreateRequest;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ProductCommandService {

    Long createProduct(ProductCreateRequest request);

    void updateHidden(Long productId, boolean hidden);

    void deleteProduct(Long productId);
}
