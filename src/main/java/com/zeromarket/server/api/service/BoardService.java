package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.BoardRequest;
import com.zeromarket.server.api.dto.BoardResponse;
import com.zeromarket.server.api.dto.PageResponse;
import com.zeromarket.server.common.entity.Board;
import java.util.List;

public interface BoardService {

    BoardResponse selectBoardById(Long id);

    PageResponse<BoardResponse> selectBoardList(BoardRequest boardRequest);

    Board insertBoard(BoardRequest board);

    int updateBoard(BoardRequest board);

    int deleteBoardById(Long id);


}
