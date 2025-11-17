package com.zeromarket.server.api.controller;

import com.zeromarket.server.api.dto.BoardRequest;
import com.zeromarket.server.api.dto.BoardResponse;
import com.zeromarket.server.api.dto.PageResponse;
import com.zeromarket.server.api.service.BoardService;
import com.zeromarket.server.common.entity.Board;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PostMapping
// ResponseEntity<Long> 또는 ResponseEntity<Board> (DTO로 변환 후)를 반환하도록 변경
    public ResponseEntity<Long> createBoard(@RequestBody BoardRequest boardRequest) {

        // Service는 이제 ID가 채워진 Board 객체를 반환합니다.
        Board createdBoard = boardService.insertBoard(boardRequest);

        // 획득된 ID를 응답 본문에 담아 반환
        Long generatedId = createdBoard.getId();

        // 201 Created 상태 코드와 함께 생성된 ID를 본문에 담아 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(generatedId);

        // 참고: 201 Created 상태일 경우, Location 헤더에 해당 리소스의 URI를 포함하는 것도 표준입니다.
        // return ResponseEntity.created(URI.create("/boards/" + generatedId)).body(generatedId);
    }

    @PutMapping("/{id}")
// 반환 타입을 ResponseEntity<Long>으로 변경
    public ResponseEntity<Long> updateBoard(@PathVariable("id") Long id, @RequestBody BoardRequest boardRequest) {

        // 경로 변수(Path Variable)의 ID를 요청 본문(Request Body)에 설정 (Service 계층으로 전달)
        boardRequest.setId(id);

        // Service 계층 호출. affectedRows를 받아오지만, 반환은 id를 합니다.
        int affectedRows = boardService.updateBoard(boardRequest);

        if (affectedRows > 0) {
            // 수정 성공 시 (200 OK)와 함께 수정된 리소스의 ID를 반환
            return ResponseEntity.ok(id);
        } else {
            // 수정 대상이 없거나 실패 시 (404 Not Found 또는 500 Internal Server Error 등을 고려)
            // 여기서는 수정 대상이 없을 경우 404를 반환하는 것이 일반적입니다.
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("id") Long id) {
        boardService.deleteBoardById(id);
        // 성공적으로 삭제되었음을 나타내는 204 No Content 상태 코드를 반환합니다.
        return ResponseEntity.noContent().build();
    }



}
