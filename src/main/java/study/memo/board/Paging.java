package study.memo.board;

import java.util.HashMap;
import java.util.Map;

public class Paging {
    private final int pageSize = 20; //한 페이지당 보여 줄 게시글 수
    private final int blockSize = 5; //한번에 보이는 페이지 갯수(ex. 1~5)
    //private int page; //현재 페이지
    private int totalListSize; //조회된 게시글 전체 수
    private int totalPage; //전체 페이지

    public Paging(int totalListSize) {
        this.totalListSize = totalListSize;
        totalPage = (int) Math.ceil((double) totalListSize/pageSize);
    }

    public Map<String,Object> startAndEnd(int page){
        int start = (page - 1) * pageSize + 1;
        int end = page * pageSize;

        Map<String,Object> map = new HashMap<>();
        map.put("start",start);
        map.put("end",end);

        return map;
    }

    public String makePagingView(int page){
        String pagehtml = "";

        // <<
        if(page > 1 ) {
            pagehtml += "<a href='/?page=1'> <i class='fa-solid fa-angles-left'></i> </a>  ";
        }
        // <
        if(page > 1){
            pagehtml += "<a href='/?page=" + (page-1) +"'> <i class='fa-solid fa-angle-left'></i> </a>  ";
        }

        //페이지
        int start = (page -1) / blockSize * blockSize + 1;
        for(int i = 0; i < blockSize ; i++){
            if( (start+i) <= totalPage ){ //마지막 페이지를 넘어가면 안됨
                pagehtml += "<a href='/?page=" + (start + i ) + "'>  ";
                if((start + i) == page){
                    pagehtml += "<b>" + (start + i) + "</b>  </a>";
                }else {
                    pagehtml += (start + i) + "  </a>";
                }
            }
        }

        // >
        if(page < totalPage){
            pagehtml += "  <a href='/?page=" + (page+1) +"'> <i class='fa-solid fa-angle-right'></i> </a>";
        }

        // >>
        if(page != totalPage && totalPage > 1){
            pagehtml += "  <a href='/?page=" + totalPage + "'> <i class='fa-solid fa-angles-right'></i> </a>";
        }

        return pagehtml;
    }
}
