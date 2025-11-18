package com.zeromarket.server.api.controller;

import com.zeromarket.server.api.dto.BoardRequest;
import com.zeromarket.server.api.dto.BoardResponse;
import com.zeromarket.server.api.dto.PageResponse;
import com.zeromarket.server.api.service.BoardService;
import com.zeromarket.server.common.entity.Board;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@AllArgsConstructor
@RequestMapping("/api/boards")
@Tag(name = "Board API", description = "게시판 CRUD API")
public class BoardRestController {

    private BoardService boardService;

    @Operation(summary = "게시글 단건 조회", description = "게시글 ID로 단건 조회")
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable("id") Long id) {
        BoardResponse board = boardService.selectBoardById(id);
        return ResponseEntity.ok(board);
    }

    @Operation(summary = "게시글 목록 조회", description = "검색/페이징 포함 게시글 목록 조회")
    @GetMapping
    public ResponseEntity<PageResponse<BoardResponse>> getBoardList(@ModelAttribute BoardRequest boardRequest) {
        PageResponse<BoardResponse> result = boardService.selectBoardList(boardRequest);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "게시글 생성")
    @PostMapping
    public ResponseEntity<Long> createBoard(@RequestBody BoardRequest boardRequest) {
        Board createdBoard = boardService.insertBoard(boardRequest);
        Long generatedId = createdBoard.getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(generatedId);
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateBoard(@PathVariable("id") Long id, @RequestBody BoardRequest boardRequest) {
        boardRequest.setId(id);
        int affectedRows = boardService.updateBoard(boardRequest);

        if (affectedRows > 0) {
            return ResponseEntity.ok(id);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("id") Long id) {
        boardService.deleteBoardById(id);
        return ResponseEntity.noContent().build();
    }
}
