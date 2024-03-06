package study.memo.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Vector;

@Repository
public class CategoryRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Category> selectCategoryList(){
        List<Category> list = jdbcTemplate.query("select * from category", categoryRowMapper());
        return list;
    }

    private RowMapper<Category> categoryRowMapper() {
        return (rs, rowNum) -> {
            Category cate = new Category();
            cate.setIdx(rs.getInt("idx"));
            cate.setCategory(rs.getString("category"));
            return cate;
        };
    }
}
