package ssafy.horong.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.domain.member.common.Language;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentByLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language;

    @Column(length = 1000, nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    private boolean isOriginal;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)  // Cascade 설정 추가
    private List<ContentImage> contentImages;

    public enum ContentType {
        COMMENT, POST, MESSAGE
    }
}