package ssafy.horong.api.community;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.api.community.request.*;
import ssafy.horong.api.community.response.GetMessageListResponse;
import ssafy.horong.api.community.response.GetPostResponse;
import ssafy.horong.common.util.S3Util;
import ssafy.horong.domain.community.service.CommunityService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "community", description = "커뮤니티")
public class CommunityController {
    private final CommunityService communityService;
    private final S3Util s3Util;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "게시글 생성", description = """
        게시글을 작성하는 API입니다.
        최대 5개의 이미지 파일을 업로드할 수 있으며, 게시판 타입과 함께 게시글을 생성합니다.
""")
    @PostMapping("")
    public CommonResponse<?> createPost(@RequestBody @Validated CreatePostRequest request) {
        log.info("권한정보 {}", SecurityContextHolder.getContext().getAuthentication());
        log.info("[CommunityController] 게시글 생성 >>>> request: {}", request);
        communityService.createPost(request.toCommand());
        return CommonResponse.ok("게시글이 생성되었습니다.", null);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "게시글 수정", description = "게시글을 수정하는 API입니다.")
    @PatchMapping("/{postId}")
    public CommonResponse<Void> updatePost(@PathVariable Long postId, @Validated @RequestBody UpdatePostRequest request) {
        log.info("[CommunityController] 게시글 수정 >>>> request: {}", request);
        communityService.updatePost(request.toCommand(postId));
        return CommonResponse.ok("게시글이 수정되었습니다.", null);
    }


    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제하는 API입니다.")
    @DeleteMapping("/{postId}")
    public CommonResponse<Void> deletePost(@PathVariable Long postId) {
        log.info("[CommunityController] 게시글 삭제 >>>> postId: {}", postId);
        communityService.deletePost(postId);
        return CommonResponse.ok("게시글이 삭제되었습니다.", null);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "게시글 조회", description = "게시글을 조회하는 API입니다.")
    @GetMapping("/{postId}")
    public CommonResponse<GetPostResponse> getPost(@PathVariable Long postId) {
        log.info("[CommunityController] 게시글 조회 >>>> postId: {}", postId);
        GetPostResponse response = communityService.getPostById(postId);
        return CommonResponse.ok(response);
    }

    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 조회하는 API입니다.")
    @GetMapping("/posts")
    public CommonResponse<Page<GetPostResponse>> getPostList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable) {
        log.info("[CommunityController] 게시글 목록 조회");
        Page<GetPostResponse> response = communityService.getPostList(pageable);
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "댓글 생성", description = "댓글을 생성하는 API입니다.")
    @PostMapping("/{postId}/comments")
    public CommonResponse<Void> createComment(@PathVariable Long postId, @RequestBody @Validated CreateCommentRequest request) {
        CreateCommentRequest newRequest = new CreateCommentRequest(postId, request.content(), request.contentByCountries());
        communityService.createComment(newRequest.toCommand());
        return CommonResponse.ok("댓글이 생성되었습니다.", null);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "댓글 수정", description = "댓글을 수정하는 API입니다.")
    @PatchMapping("/{postId}/comments/{commentId}")
    public CommonResponse<Void> updateComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody @Validated UpdateCommentRequest request) {
        UpdateCommentRequest newRequest = new UpdateCommentRequest(commentId, request.contentByCountries());
        communityService.updateComment(newRequest.toCommand());
        return CommonResponse.ok("댓글이 수정되었습니다.", null);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제하는 API입니다.")
    @DeleteMapping("/{postId}/comments/{commentId}")
    public CommonResponse<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        communityService.deleteComment(commentId);
        return CommonResponse.ok("댓글이 삭제되었습니다.", null);
    }

//    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
//    @Operation(summary = "게시글 검색", description = "게시글을 검색하는 API입니다.")
//    @GetMapping("/posts/search/{keyword}")
//    public CommonResponse<Page<GetPostResponse>> searchPosts(
//            @PathVariable String keyword,
//            @Parameter(hidden = true) @PageableDefault(size = 10, sort = "createdDate") Pageable pageable) {
//        log.info("[CommunityController] 게시글 검색 >>>> keyword: {}", keyword);
//        SearchPostsRequest request = new SearchPostsRequest(keyword);
//        Page<GetPostResponse> response = communityService.searchPosts(request.toCommand(), pageable);
//        return CommonResponse.ok(response);
//    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "메시지 전송", description = "메시지를 전송하는 API입니다.")
    @PostMapping("/messages")
    public CommonResponse<Void> sendMessage(@RequestBody @Validated SendMessageRequest request) {
        log.info("[CommunityController] 메시지 전송 >>>> request: {}", request);
        communityService.sendMessage(request.toCommand());
        return CommonResponse.ok("메시지가 전송되었습니다.", null);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "메시지 리스트 조회", description = "메시지 리스트를 조회하는 API입니다.")
    @GetMapping("/messages/{senderId}")
    public CommonResponse<List<GetMessageListResponse>> getMessageList(@PathVariable Long senderId) {
        log.info("[CommunityController] 메시지 리스트 조회 >>>> senderId: {}", senderId);
        GetMessageListRequest request = new GetMessageListRequest(senderId);
        List<GetMessageListResponse> response = communityService.getMessageList(request.toCommand());
        return CommonResponse.ok(response);
    }
}
