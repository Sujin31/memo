package study.memo.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Vector;

@Repository
public class TagRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    /*
     * tag 테이블 중복 ignore
     * */
    public void insertJustTag(List<String> tag){
        //List<Integer> tag_idx = new Vector<>();

        String sql = "INSERT IGNORE tags (tag_name) VALUES (?)";
        for (String row : tag) {
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"idx"});
                ps.setString(1, row);
                return ps;
            });
        }
    }

    /*
     * 태그 idx(매칭용)
     * */
    public List<Integer> selectTagId(String tag){
        return jdbcTemplate.queryForList("select idx from tags where tag_name in ("+tag+")", Integer.class);
    }

    /*
    * tag_mapping
    * */
    public void insertTagMapping(int board_idx, List<Integer> tag_idx){
        String sql = "INSERT tag_mapping (tag_idx,board_idx) VALUES (?,?)";
        for (int i : tag_idx) {
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"idx"});
                ps.setInt(1, i);
                ps.setInt(2, board_idx);
                return ps;
            });
        }
    }

    /*
    *  Board update/delete 할 때 사용
    * */
    public void deleteTagMappingByBoardIdx(int board_idx){
        String sql = "delete from tag_mapping where board_idx = ?";
        jdbcTemplate.update(sql,board_idx);
    }
}
