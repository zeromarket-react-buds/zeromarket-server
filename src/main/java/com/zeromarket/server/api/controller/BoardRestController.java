package com.zeromarket.server.api.controller;

import com.zeromarket.server.api.dto.BoardRequest;
import com.zeromarket.server.api.dto.BoardResponse;
import com.zeromarket.server.api.dto.PageResponse;
import com.zeromarket.server.api.service.BoardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/boards")
public class BoardRestController {

    private BoardService boardService;

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable("id") Long id) {
        BoardResponse board = boardService.selectBoardById(id);

        return ResponseEntity.ok(board);
    }

    @GetMapping
    public ResponseEntity<PageResponse<BoardResponse>> getBoardList(@ModelAttribute BoardRequest boardRequest) {
        PageResponse<BoardResponse> result = boardService.selectBoardList(boardRequest);

        return ResponseEntity.ok(result);
    }

}
