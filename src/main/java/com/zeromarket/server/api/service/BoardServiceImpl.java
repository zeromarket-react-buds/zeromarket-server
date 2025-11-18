package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.BoardRequest;
import com.zeromarket.server.api.dto.BoardResponse;
import com.zeromarket.server.api.dto.PageResponse;
import com.zeromarket.server.api.mapper.BoardMapper;
import com.zeromarket.server.common.entity.Board;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BoardServiceImpl implements BoardService{

    private BoardMapper boardMapper;

    @Override
    public BoardResponse selectBoardById(Long id) {
        Board board = boardMapper.selectBoardById(id);
        BoardResponse response = new BoardResponse();
        BeanUtils.copyProperties(board, response);
        return response;
    }

    @Override
    public PageResponse<BoardResponse> selectBoardList(BoardRequest boardRequest) {

        List<Board> boardList = boardMapper.selectBoardList(boardRequest);
        int boardCount = boardMapper.countBoard(boardRequest);

        List<BoardResponse> responseList = boardList.stream()
            .map(board -> {
                BoardResponse response = new BoardResponse();
                // BeanUtils 사용 또는 별도 매퍼로 변환
                BeanUtils.copyProperties(board, response);
                return response;
            })
            .collect(Collectors.toList());

        PageResponse<BoardResponse> result = new PageResponse(responseList, boardCount, boardRequest.getPageSize(), boardRequest.getCurrentPage());
        return result;
    }

    @Override
    public Board insertBoard(BoardRequest boardRequest) {
        Board boardEntity = new Board();
        BeanUtils.copyProperties(boardRequest, boardEntity);

        int affectedRows = boardMapper.insertBoard(boardEntity);

        if (affectedRows > 0) {
            // 성공적으로 삽입되었다면, ID가 채워진 Entity 자체를 반환
            return boardEntity;
        }

        // 삽입 실패 시 예외 처리 (예시)
        throw new RuntimeException("게시글 삽입에 실패했습니다.");
    }

    @Override
    public int updateBoard(BoardRequest boardRequest) {
        // BeanUtils 사용: board 필드를 boardEntity 객체의 같은 이름 필드로 복사
        Board boardEntity = new Board();
        BeanUtils.copyProperties(boardRequest, boardEntity);

        return boardMapper.updateBoard(boardEntity);
    }

    @Override
    public int deleteBoardById(Long id) {
        return boardMapper.deleteBoardById(id);
    }

}
