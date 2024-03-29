package com.myshop.controller;

import com.myshop.dto.*;
import com.myshop.global.utils.AuthenticationUtils;
import com.myshop.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public PostDetailDto createPost(
            @RequestBody CreatePostDto postDto
    ) {
        Long userId = AuthenticationUtils.getUserIdByToken();
        return postService.createPost(userId, postDto);
    }

    @GetMapping
    public List<PostDto> getPosts() {
        return postService.getPosts();
    }

    @GetMapping("/{post_id}")
    public PostDetailDto getPostById(
            @PathVariable("post_id") Long postId
    ) {
        return postService.getPostById(postId);
    }

    @DeleteMapping("/{post_id}")
    public void deletePost(
            @PathVariable("post_id") Long postId
    ){
        Long userId = AuthenticationUtils.getUserIdByToken();
        postService.deletePost(userId, postId);
    }

    @PutMapping("/like/{post_id}")
    public void likePost(
            @PathVariable("post_id") Long postId
    ) {
        Long userId = AuthenticationUtils.getUserIdByToken();
        postService.likePost(userId, postId);
    }

    @PostMapping("/comment/{post_id}")
    public List<CommentDto> addComment(
            @PathVariable(value = "post_id") Long postId,
            @RequestBody CreateCommentDto commentDto
    ) {
        Long userId = AuthenticationUtils.getUserIdByToken();
        return postService.addComment(userId, postId, commentDto);
    }

    @DeleteMapping("/comment/{post_id}/{comment_id}")
    public void removeComment(
            @PathVariable(value = "post_id") Long postId,
            @PathVariable(value = "comment_id") Long commentId
    ) {
        Long userId = AuthenticationUtils.getUserIdByToken();
        postService.removeComment(userId, postId, commentId);
    }

}
