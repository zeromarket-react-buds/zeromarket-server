package com.zeromarket.server.api.mapper;

import com.zeromarket.server.api.dto.BoardRequest;
import com.zeromarket.server.common.entity.Board;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {

    Board selectBoardById(Long id);

    List<Board> selectBoardList(BoardRequest boardRequest);

    int insertBoard(Board board);

    int updateBoard(Board board);

    int deleteBoardById(Long id);

    int countBoard(BoardRequest boardRequest);
}
