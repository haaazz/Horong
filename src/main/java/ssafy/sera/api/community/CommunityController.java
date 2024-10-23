package ssafy.sera.api.community;

import co.elastic.clients.elasticsearch.license.PostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ssafy.sera.api.CommonResponse;
import ssafy.sera.api.community.request.CreateCommentRequest;
import ssafy.sera.api.community.request.CreatePostRequest;
import ssafy.sera.api.community.request.UpdateCommentRequest;
import ssafy.sera.api.community.request.UpdatePostRequest;
import ssafy.sera.api.community.response.GetPostResponse;
import ssafy.sera.domain.community.service.CommunityService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "community", description = "커뮤니티")
public class CommunityController {
    private final CommunityService communityService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "게시글 생성", description = "게시글을 생성하는 API입니다.")
    @PostMapping(value = "", consumes = {"multipart/form-data"})
    public CommonResponse<PostResponse> createPost(@ModelAttribute @Validated CreatePostRequest request) {
        log.info("[CommunityController] 게시글 생성 >>>> request: {}", request);
        communityService.createPost(request.toCommand());
        return CommonResponse.ok("게시글이 생성되었습니다.", null);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "게시글 수정", description = "게시글을 수정하는 API입니다.")
    @PatchMapping(value = "/{postId}")
    public CommonResponse<Void> updatePost(@PathVariable Long postId, @ModelAttribute @Validated UpdatePostRequest request) {
        UpdatePostRequest updatedRequest = new UpdatePostRequest(postId, request.title(), request.content());
        log.info("[CommunityController] 게시글 수정 >>>> request: {}", updatedRequest);
        communityService.updatePost(updatedRequest.toCommand());
        return CommonResponse.ok("게시글이 수정되었습니다.", null);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제하는 API입니다.")
    @DeleteMapping("/{postId}")
    public CommonResponse<Void> deletePost(@PathVariable Long postId) {
        log.info("[CommunityController] 게시글 삭제 >>>> postId: {}", postId);
        communityService.deletePost(postId);
        return CommonResponse.ok("게시글이 삭제되었습니다.", null);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "게시글 조회", description = "게시글을 조회하는 API입니다.")
    @GetMapping("/{postId}")
    public CommonResponse<GetPostResponse> getPost(@PathVariable Long postId) {
        log.info("[CommunityController] 게시글 조회 >>>> postId: {}", postId);
        GetPostResponse response = communityService.getPostById(postId);
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 조회하는 API입니다.")
    @GetMapping("/post")
    public CommonResponse<Page<GetPostResponse>> getPostList(@PageableDefault(size = 10) Pageable pageable) {
        log.info("[CommunityController] 게시글 목록 조회");
        Page<GetPostResponse> response = communityService.getPostList(pageable);
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "댓글 생성", description = "댓글을 생성하는 API입니다.")
    @PostMapping(value = "{postId}/")
    public CommonResponse<Void> createComment(@PathVariable Long postId, @RequestBody @Validated CreateCommentRequest request) {
        CreateCommentRequest newRequest = new CreateCommentRequest(postId, request.content());
        communityService.createComment(newRequest.toCommand());
        return CommonResponse.ok("댓글이 생성되었습니다.", null);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "댓글 수정", description = "댓글을 수정하는 API입니다.")
    @PatchMapping(value = "{postId}/{commentId}")
    public CommonResponse<Void> updateComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody @Validated UpdateCommentRequest request) {
        UpdateCommentRequest newRequest = new UpdateCommentRequest(commentId, request.content());
        communityService.updateComment(newRequest.toCommand());
        return CommonResponse.ok("댓글이 수정되었습니다.", null);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제하는 API입니다.")
    @DeleteMapping("{postId}/{commentId}")
    public CommonResponse<Void> deleteComment(@PathVariable Long commentId) {
        communityService.deleteComment(commentId);
        return CommonResponse.ok("댓글이 삭제되었습니다.", null);
    }


}
