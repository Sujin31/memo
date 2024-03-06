package study.memo.board;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BoardForm {
    private int idx;
    private String title;
    private String content;
    private int category;
    private List<String> tag;
}
