package com.myshop.controller;

import com.myshop.dto.*;
import com.myshop.global.context.TokenContext;
import com.myshop.global.context.TokenContextHolder;
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
    public void createPost(@RequestBody CreatePostDto postDto) {
        TokenContext context = TokenContextHolder.getContext();
        Long userId = context.getUserId();
        postService.createPost(userId, postDto);
    }

    @GetMapping
    public List<PostDto> getPosts() {
        return postService.getPosts();
    }

    @GetMapping("/{post_id}")
    public PostDetailDto getPostById(@PathVariable("post_id") Long postId) {
        return postService.getPostById(postId);
    }

    @DeleteMapping("/{post_id}")
    public void deletePost(
            @PathVariable("post_id") Long postId
    ){
        postService.deletePost(postId);
    }

    @PutMapping("/like/{post_id}")
    public void likePost(@PathVariable("post_id") Long postId) {
        TokenContext context = TokenContextHolder.getContext();
        Long userId = context.getUserId();
        postService.likePost(userId, postId);
    }

    @PostMapping("/comment/{post-id}")
    public List<CommentDto> addComment(
            @PathVariable(value = "post-id") Long postId,
            @RequestBody CreateCommentDto commentDto
    ) {
        TokenContext context = TokenContextHolder.getContext();
        Long userId = context.getUserId();
        return postService.addComment(userId, postId, commentDto);
    }

    @DeleteMapping("/comment/{post-id}/{comment-id}")
    public void removeComment(
            @PathVariable(value = "post-id") Long postId,
            @PathVariable(value = "comment-id") Long commentId
    ) {
        TokenContext context = TokenContextHolder.getContext();
        Long userId = context.getUserId();
        postService.removeComment(userId, postId, commentId);
    }

}