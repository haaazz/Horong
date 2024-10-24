package ssafy.horong.domain.community.command;

import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.domain.community.entity.BoardType;
import ssafy.horong.domain.member.common.MemberRole;

public record CreatePostCommand(
        String title,
        String content,
        MultipartFile[] images,
        BoardType boardType
) {}
