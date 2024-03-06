package study.memo.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@Repository
public class BoardRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Board> selectBoardListNoPage(){
        List<Board> list = new Vector<>(
                jdbcTemplate.query("select b.* ,c.category as category_name " +
                                        "from board b " +
                                        "join category c on c.idx = b.category ", boardRowMapper()));
        //list.addAll(jdbcTemplate.queryForList("select * from board;",Board.class));
        return list;
    }

    /*
    * 게시글 전체 갯수 구하기(페이징하기 위해)
    * */
    public int selectBoardTotalNum(Map<String,Object> map){
        String sql = "select count(foo.num) as cnt " +
                    "from ( " +
                    " select row_number() over(order by idx) as num, b.*, GROUP_CONCAT(t.tag_name) as tag_name " +
                    " from tag_mapping tm  " +
                    " right outer join board b on b.idx = tm.board_idx  " +
                    " left outer join tags t on t.idx = tm.tag_idx  ";

        if( map.get("category") != null || map.get("searchWord") != null){
            sql += " where ";
            if(map.get("category") != null){
                sql += " category = " + map.get("category");
            }
            if(map.get("category") != null && map.get("searchWord") != null){
                sql += " and ";
            }
            if(map.get("searchWord") != null){
                sql += " " +map.get("searchField") + " like '%" + map.get("searchWord") + "%' " ;
            }
        }

        sql += " group by b.idx )foo";

        Map<String, Object> maps = jdbcTemplate.queryForMap(sql);
        Long cntL = (Long)maps.get("cnt");
        return cntL.intValue();
    }

    public List<Board> selectBoardListPaging(Map<String,Object> map){
        String sql = "select foo.* " +
                "from ( " +
                " select row_number() over(order by idx DESC) as num, b.*, GROUP_CONCAT(t.tag_name) as tag_name, c.category as category_name" +
                " from tag_mapping tm  " +
                " right outer join board b on b.idx = tm.board_idx  " +
                " left outer join tags t on t.idx = tm.tag_idx  " +
                " join category c on c.idx = b.category ";

        if( map.get("category") != null || map.get("searchWord") != null){
            sql += " where ";
            if(map.get("category") != null){
                sql += " b.category = " + map.get("category");
            }
            if(map.get("category") != null && map.get("searchWord") != null){
                sql += " and ";
            }
            if(map.get("searchWord") != null){
                sql += " " +map.get("searchField") + " like '%" + map.get("searchWord") + "%' " ;
            }
        }
        sql += " group by b.idx )foo where foo.num between "+ map.get("start") +" and " + map.get("end");

        return jdbcTemplate.query(sql,boardRowMapper());
    }

    /*
    * board 테이블만 추가하고 idx 리턴
    * */
    public int insertJustBoard(Board board){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withCatalogName("study");
        jdbcInsert.withTableName("board").usingGeneratedKeyColumns("idx");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", board.getTitle());
        parameters.put("content", board.getContent());
        parameters.put("category", board.getCategory());
        parameters.put("regidate", timestamp);


        Number key = jdbcInsert.executeAndReturnKey(new
                MapSqlParameterSource(parameters));

        return key.intValue();
    }

    /*
    * 상세보기
    * */
    public Board selectBoardByIdx(int idx){
        String sql = "select b.idx,b.title ,b.content ,b.regidate ,GROUP_CONCAT(t.tag_name) as tags , c.category as category_name " +
                    "from board b " +
                    "left outer join tag_mapping tm ON tm.board_idx = b.idx " +
                    "left outer join tags t on t.idx = tm.tag_idx " +
                    "join category c on c.idx = b.category " +
                    "where b.idx = ? " +
                    "group by b.idx ";

        Board board = jdbcTemplate.queryForObject(sql, boardRowMapperAndTag(), idx);
        return board;

    }
    
    /*
    * 수정하기
    * */
    public void updateBoard(Board board){
        String sql = "update board set title = ?, content = ?, category = ? where idx = ?";
        jdbcTemplate.update(sql,board.getTitle(),board.getContent(),board.getCategory(),board.getIdx());
    }

    /*
    * 삭제하기
    * */
    public void deleteBoard(int idx){
        String sql = "delete from board where idx = " + idx;
        jdbcTemplate.update(sql);
    }
    
    /*
    * 스마트에디터 이미지 DB 추가
    * */
    public void insertImages(int board_idx){
        //board_idx 생성 후
    }

    
    /*   MAPPER   */

    private RowMapper<Board> boardRowMapper() {
        return (rs, rowNum) -> {
            Board board = new Board();
            board.setIdx(rs.getInt("idx"));
            board.setTitle(rs.getString("title"));
            board.setContent(rs.getString("content"));
            board.setCategory(rs.getInt("category"));
            board.setCategory_name(rs.getString("category_name"));
            board.setRegidate(rs.getTimestamp("regidate"));
            return board;
        };
    }

    private RowMapper<Board> boardRowMapperAndTag() {
        return (rs, rowNum) -> {
            Board board = new Board();
            board.setIdx(rs.getInt("idx"));
            board.setTitle(rs.getString("title"));
            board.setContent(rs.getString("content"));
            board.setCategory_name(rs.getString("category_name"));
            board.setRegidate(rs.getTimestamp("regidate"));
            board.setTags(rs.getString("tags"));
            return board;
        };
    }
}
