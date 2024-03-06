package study.memo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.memo.board.TagRepository;

import java.util.List;
import java.util.Vector;

@SpringBootTest
class MemoApplicationTests {

	@Test
	@Transactional
	void contextLoads() {
		TagRepository tagRepository = new TagRepository();
		List<String> list = new Vector<>();
		list.add("자바");
		list.add("웹");

		tagRepository.insertJustTag(list);
	}

}
