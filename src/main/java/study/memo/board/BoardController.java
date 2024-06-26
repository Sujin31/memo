package study.memo.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class BoardController {

    @Autowired
    BoardService boardService;
    private final String myIp = "27.35.204.34";
    private final String myIp2 = "0:0:0:0:0:0:0:1";

    private boolean checkMyIp(){
        boolean result = false;
        String ip = null;
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        ip = request.getRemoteAddr();

        if(myIp.equals(ip) || myIp2.equals(ip)){
            result = true;
        }
        return result;
    }


    @GetMapping("")
    public String boardList(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                            @RequestParam(value = "category", required = false, defaultValue = "0") int category,
                            @RequestParam(value = "searchField", required = false) String searchField,
                            @RequestParam(value = "searchWord", required = false) String searchWord,
                            Model model){

        Map<String, Object> map = new HashMap<>();
        map.put("searchField",searchField);
        map.put("searchWord",searchWord);
        if(category == 0){
            map.put("category",null);
        }else {
            map.put("category", category);
        }
        //전체 게시글 수
        int totalNum = boardService.getBoardListTotalNum(map);

        //페이징을 위한 작업
        Paging paging = new Paging(totalNum);
        Map<String, Object> between = paging.startAndEnd(page);
        map.put("start",between.get("start"));
        map.put("end",between.get("end"));
        
        //게시글 불러오기
        List<Board> boardList = boardService.getBoardListPaging(page,map);

        //페이징 html
        String pagingHtml = paging.makePagingView(page);

        //카테고리
        List<Category> categories = boardService.getAllCategory();

        model.addAttribute("categories",categories);
        model.addAttribute("totalNum",totalNum);
        model.addAttribute("boardList",boardList);
        model.addAttribute("pagingHtml",pagingHtml);

        return "board/list";
    }

    @GetMapping("write")
    public String boardWrite(Model model){
        if (!checkMyIp()){
            model.addAttribute("msg","작성권한이 없습니다.");
            model.addAttribute("url","/");
            return "common/alert";
        }
        List<Category> categories = boardService.getAllCategory();
        model.addAttribute("category",categories);
        return "board/write";
    }

    @PostMapping("write")
    public String createBoard(BoardForm form){
        boardService.saveBoard(form);
        return "redirect:/";
    }

    @GetMapping("view")
    public String BoardView(@RequestParam("idx") int idx, Model model){
        Board board = boardService.viewBoard(idx);
        model.addAttribute("board",board);

        if(board.getTags()!=null) {
            List<String> tagList = List.of(board.getTags().split(","));
            model.addAttribute("tagList", tagList);
        }
        return "board/view";
    }

    @GetMapping("edit")
    public String BoardEdit(@RequestParam("idx") int idx, Model model){

        if (!checkMyIp()){
            model.addAttribute("msg","작성권한이 없습니다.");
            model.addAttribute("url","/view?idx="+idx);
            return "common/alert";
        }

        List<Category> categories = boardService.getAllCategory();
        Board board = boardService.viewBoard(idx);
        String[] tags = new String[0];
        if(board.getTags() != null){
            tags = board.getTags().split(",");
        }

        model.addAttribute("category",categories);
        model.addAttribute("board",board);
        model.addAttribute("tags",tags);
        return "board/edit";
    }

    @PostMapping("edit")
    public String updateBoard(BoardForm form){
        boardService.updateBoard(form);
        return "redirect:/view?idx="+form.getIdx();
    }

    @PostMapping("del")
    public String deleteBoard(BoardForm form, Model model){

        if (!checkMyIp()){
            model.addAttribute("msg","작성권한이 없습니다.");
            model.addAttribute("url","/view?idx="+form.getIdx());
            return "common/alert";
        }

        boardService.deleteBoard(form);
        return "redirect:/";
    }

    /*
    * 스마트에디터2 멀티 이미지 업로드
    * */
    @RequestMapping(value="multiImageUploader")
    public void smarteditorMultiImageUpload(HttpServletRequest request, HttpServletResponse response){
        try {
            //파일정보
            String sFileInfo = "";
            //파일명을 받는다 - 일반 원본파일명
            String sFilename = request.getHeader("file-name");
            //파일 확장자
            String sFilenameExt = sFilename.substring(sFilename.lastIndexOf(".")+1);
            //확장자를소문자로 변경
            sFilenameExt = sFilenameExt.toLowerCase();

            //이미지 검증 배열변수
            String[] allowFileArr = {"jpg","png","bmp","gif"};

            //확장자 체크
            int nCnt = 0;
            for(int i=0; i<allowFileArr.length; i++) {
                if(sFilenameExt.equals(allowFileArr[i])){
                    nCnt++;
                }
            }

            //이미지가 아니라면
            if(nCnt == 0) {
                PrintWriter print = response.getWriter();
                print.print("NOTALLOW_"+sFilename);
                print.flush();
                print.close();
            } else {
                //디렉토리 설정 및 업로드

                //파일경로
                String filePath = "";
                String property = System.getProperty("os.name");
                if (property.contains("Win")){
                    //윈도우
                    filePath = "C:/Users/Sujin/Desktop/test";
                }else{
                    //리눅스
                    filePath = "/home/study/img";
                }

                File file = new File(filePath);

                if(!file.exists()) {
                    file.mkdirs();
                }

                String sRealFileNm = "";
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                String today= formatter.format(new java.util.Date());
                sRealFileNm = today+UUID.randomUUID().toString() + sFilename.substring(sFilename.lastIndexOf("."));
                String rlFileNm = filePath + "/" + sRealFileNm;

                ///////////////// 서버에 파일쓰기 /////////////////
                InputStream inputStream = request.getInputStream();
                OutputStream outputStream=new FileOutputStream(rlFileNm);
                int numRead;
                byte bytes[] = new byte[Integer.parseInt(request.getHeader("file-size"))];

                while((numRead = inputStream.read(bytes,0,bytes.length)) != -1){
                    outputStream.write(bytes,0,numRead);
                }
                if(inputStream != null) {
                    inputStream.close();
                }
                outputStream.flush();
                outputStream.close();

                ///////////////// 이미지 /////////////////
                // 정보 출력
                sFileInfo += "&bNewLine=true";
                // img 태그의 title 속성을 원본파일명으로 적용시켜주기 위함
                sFileInfo += "&sFileName="+ sFilename;
                sFileInfo += "&sFileURL="+"/img/"+sRealFileNm;

                

                PrintWriter printWriter = response.getWriter();
                printWriter.print(sFileInfo);
                printWriter.flush();
                printWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
