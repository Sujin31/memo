package study.memo.board;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String property = System.getProperty("os.name");
        if (property.contains("Win")){
            //윈도우
            registry.addResourceHandler("/img/**")
                .addResourceLocations("file:///C:/Users/Sujin/Desktop/test/");
        }else{
            //리눅스
            registry.addResourceHandler("/img/**")
                    .addResourceLocations("file:///home/study/img/");
        }




    }
}
