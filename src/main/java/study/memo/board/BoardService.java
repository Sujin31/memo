package study.memo.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.*;

@Service
public class BoardService {
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    CategoryRepository categoryRepository;

    public List<Board> getListNopage(){
        return boardRepository.selectBoardListNoPage();
    }

    public int getBoardListTotalNum(Map<String,Object> map){
        return boardRepository.selectBoardTotalNum(map);
    }

    public List<Board> getBoardListPaging(int page, Map<String,Object> map){
       return boardRepository.selectBoardListPaging(map);
    }

    public void saveBoard(BoardForm form){
        //board 게시판 생성

        Board board = new Board();
        board.setTitle(form.getTitle());
        board.setContent(form.getContent());
        board.setCategory(form.getCategory());
        int board_idx = boardRepository.insertJustBoard(board);

        //tag 게시판 생성
        List<String> tags = form.getTag();
        tags.removeAll(Collections.singleton(""));

        String tagSt = "'" + StringUtils.join(tags, "','") + "'";
        tagRepository.insertJustTag(tags);
        //tag idx 불러오기
        List<Integer> tag_idx = tagRepository.selectTagId(tagSt);
        //tag_mapping 하기
        tagRepository.insertTagMapping(board_idx,tag_idx);

    }

    public Board viewBoard(int idx){
        Board board = boardRepository.selectBoardByIdx(idx);
        return board;
    }

    public void updateBoard(BoardForm form){
        Board board = new Board();
        board.setIdx(form.getIdx());
        board.setTitle(form.getTitle());
        board.setContent(form.getContent());
        board.setCategory(form.getCategory());

        boardRepository.updateBoard(board);

        //tag 매핑 수정

        tagRepository.deleteTagMappingByBoardIdx(form.getIdx());

        List<String> tags = form.getTag();
        tags.removeAll(Collections.singleton(""));
        String tagSt = "'" + StringUtils.join(tags, "','") + "'";

        tagRepository.insertJustTag(tags);
        //tag idx 불러오기
        List<Integer> tag_idx = tagRepository.selectTagId(tagSt);
        //tag_mapping 하기

        tagRepository.insertTagMapping(form.getIdx(),tag_idx);
    }

    public void deleteBoard(BoardForm form){
        boardRepository.deleteBoard(form.getIdx());
    }

    public List<Category> getAllCategory(){
        return categoryRepository.selectCategoryList();
    }

}
