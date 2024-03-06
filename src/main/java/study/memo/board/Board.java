package study.memo.board;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Objects;

@Setter
@Getter
public class Board {
    private int idx;
    private String title;
    private String content;
    private int category;
    private String sfile;
    private String ofile;
    private Timestamp regidate;
    private String category_name;

    private String tags; //태그 모음
}
