package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/board/write") //localhost:8080/board/write
    public String boardWriteForm() {

        return "boardWrite";
    }

    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, @RequestParam("file") MultipartFile file) throws Exception{

        boardService.write(board, file);

        model.addAttribute("message", "글 작성이 완료되었습니다.");

        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }

    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            @RequestParam(name="searchKeyword", defaultValue = "") String searchKeyword) {

        Page<Board> list = null;

        if(searchKeyword == null) {
            list = boardService.boardList(pageable);
        }else {
            list = boardService.boardSearchList(searchKeyword, pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "boardList";
    }

    @GetMapping("/board/view") // localhost:8080/board/view?id=1 하면 1번 게시글을 불러옴.
    public String boardView(Model model, @RequestParam(name = "id") Integer id) {

        model.addAttribute("board", boardService.boardView(id));
        return "boardView";

    }

    @GetMapping("/board/delete")
    public String boardDelete(@RequestParam(name = "id") Integer id, Model model) {

        boardService.boardDelete(id);

        model.addAttribute("message", "글 삭제 완료");

        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }

    @GetMapping("/board/modify/{id}") // id부분이 인식이 되어서 integer 형태의 id로 들어온다는 것
    public String boardModify(@PathVariable("id") Integer id, Model model){

        model.addAttribute("board", boardService.boardView(id));
        return "boardModify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, @RequestParam("file") MultipartFile file) throws Exception {

        Board boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        boardService.write(boardTemp, file);

        model.addAttribute("message", "글 수정 완료");

        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }
}
