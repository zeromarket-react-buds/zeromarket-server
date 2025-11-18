package com.zeromarket.server.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.zeromarket.server.api.dto.BoardRequest;
import com.zeromarket.server.api.dto.BoardResponse;
import com.zeromarket.server.api.dto.PageResponse;
import com.zeromarket.server.api.mapper.BoardMapper;
import com.zeromarket.server.common.entity.Board;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class BoardServiceImplTest {

    @Mock
    BoardMapper boardMapper;

    BoardServiceImpl boardService;

    @BeforeEach
    void setUp() {
        boardService = new BoardServiceImpl(boardMapper);
    }

    @Test
    void selectBoardById는_엔티티를_DTO로_변환해서_반환한다() {
        // given
        Board board = new Board();
        board.setId(1L);
        board.setTitle("제목");
        board.setContent("내용");
        board.setWriterId(10L);

        given(boardMapper.selectBoardById(1L)).willReturn(board);

        // when
        BoardResponse result = boardService.selectBoardById(1L);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getContent()).isEqualTo("내용");
        assertThat(result.getWriterId()).isEqualTo(10L);
        then(boardMapper).should().selectBoardById(1L);
    }

    @Test
    void selectBoardList는_페이지_정보를_포함한_목록을_반환한다() {
        // given
        BoardRequest request = new BoardRequest();
        request.setCurrentPage(1);
        request.setPageSize(10);

        Board board1 = new Board();
        board1.setId(1L);
        board1.setTitle("제목1");

        Board board2 = new Board();
        board2.setId(2L);
        board2.setTitle("제목2");

        given(boardMapper.selectBoardList(request))
            .willReturn(List.of(board1, board2));
        given(boardMapper.countBoard(request))
            .willReturn(20);   // 전체 게시글 수

        // when
        PageResponse<BoardResponse> result = boardService.selectBoardList(request);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalCount()).isEqualTo(20);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getCurrentPage()).isEqualTo(1);

        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("제목1");
    }

    @Test
    void insertBoard는_성공시_엔티티를_반환한다() {
        // given
        BoardRequest request = new BoardRequest();
        request.setTitle("새 글");
        request.setContent("내용");
        request.setWriterId(10L);

        // insertBoard 호출 시, mapper가 1을 반환하도록 설정
        given(boardMapper.insertBoard(any(Board.class))).willReturn(1);

        // when
        Board result = boardService.insertBoard(request);

        // then
        assertThat(result.getTitle()).isEqualTo("새 글");
        assertThat(result.getContent()).isEqualTo("내용");
        assertThat(result.getWriterId()).isEqualTo(10L);
        then(boardMapper).should().insertBoard(any(Board.class));
    }

    @Test
    void insertBoard는_영향받은_행이_없으면_예외를_던진다() {
        // given
        BoardRequest request = new BoardRequest();
        given(boardMapper.insertBoard(any(Board.class))).willReturn(0);

        // when & then
        assertThatThrownBy(() -> boardService.insertBoard(request))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("게시글 삽입에 실패");
    }

    @Test
    void updateBoard는_mapper_결과를_그대로_반환한다() {
        // given
        BoardRequest request = new BoardRequest();
        request.setId(1L);
        given(boardMapper.updateBoard(any(Board.class))).willReturn(1);

        // when
        int result = boardService.updateBoard(request);

        // then
        assertThat(result).isEqualTo(1);
        then(boardMapper).should().updateBoard(any(Board.class));
    }

    @Test
    void deleteBoardById는_mapper_결과를_그대로_반환한다() {
        // given
        given(boardMapper.deleteBoardById(1L)).willReturn(1);

        // when
        int result = boardService.deleteBoardById(1L);

        // then
        assertThat(result).isEqualTo(1);
        then(boardMapper).should().deleteBoardById(1L);
    }
}
