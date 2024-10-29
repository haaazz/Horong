package ssafy.horong.domain.community.elastic;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Data
@Builder
@Document(indexName = "posts")
public class PostDocument {

    @Id
    private String id;  // 고유 ID: "postId-language" 형식 사용
    private Long postId; // 실제 게시물 ID
    private String title;
    private String author;
    private String content;
    private String language;
}

