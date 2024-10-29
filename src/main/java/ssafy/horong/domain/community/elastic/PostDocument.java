package ssafy.horong.domain.community.elastic;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@Document(indexName = "posts")
public class PostDocument {

    @Id
    private String id;  // 고유 ID: "postId-language" 형식 사용
    private Long postId; // 실제 게시물 ID
    private String title;
    private String author;

    @Field(type = FieldType.Text, analyzer = "nori") // 한국어 분석기
    private String contentKo; // 한국어 콘텐츠

    @Field(type = FieldType.Text, analyzer = "ik_max_word") // 중국어 분석기
    private String contentZh; // 중국어 콘텐츠

    @Field(type = FieldType.Text, analyzer = "kuromoji") // 일본어 분석기
    private String contentJa; // 일본어 콘텐츠

    @Field(type = FieldType.Text, analyzer = "english") // 영어 분석기
    private String contentEn; // 영어 콘텐츠

    private String language; // 언어 정보
}