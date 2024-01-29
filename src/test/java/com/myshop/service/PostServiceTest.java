package com.myshop.service;

import com.myshop.domain.*;
import com.myshop.dto.*;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.NotificationRepository;
import com.myshop.repository.PostRepository;
import com.myshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @InjectMocks
    private PostService postService;
    @BeforeEach
    void init() {
        postService = new PostService(postRepository, userRepository, notificationRepository);
    }

    @Test
    @DisplayName("게시물 생성 테스트")
    void createPostTest() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).build();
        CreatePostDto postDto = mock(CreatePostDto.class);
        Post post = new Post();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(postDto.toEntity(userId)).willReturn(post);

        // when
        postService.createPost(userId, postDto);

        // then
        verify(userRepository).findById(userId);
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("존재하지 않는 유저로 게시물 생성 시도 시 예외 발생 테스트")
    void createPostWithNonExistingUserTest() {
        // given
        Long userId = 1L;
        CreatePostDto postDto = mock(CreatePostDto.class);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(BadRequestException.class, () -> {
            postService.createPost(userId, postDto);
        });
    }

    @Test
    @DisplayName("모든 게시물 조회 테스트")
    void getPostsTest() {
        // given
        User user = User.builder()
                .id(1L)
                .name("User Name")
                .profileImg("profileImg.jpg")
                .build();
        Post post1 = Post.builder()
                .id(1L)
                .content("Content 1")
                .user(user)
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .content("Content 2")
                .user(user)
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();
        List<Post> posts = Arrays.asList(post1, post2);
        given(postRepository.findAll()).willReturn(posts);

        // when
        List<PostDto> result = postService.getPosts();

        // then
        assertEquals(posts.size(), result.size());
        assertEquals(post1.getId(), result.get(0).getId());
        assertEquals(post1.getContent(), result.get(0).getContent());
        assertEquals(post1.getUser().getName(), result.get(0).getName());
        assertEquals(post1.getUser().getProfileImg(), result.get(0).getProfileImg());
        assertEquals(post1.getUser().getId(), result.get(0).getUserId());
        assertEquals(post1.getLikes().size(), result.get(0).getLikeCount());
        assertEquals(post1.getComments().size(), result.get(0).getCommentCount());
    }

    @Test
    @DisplayName("특정 게시물 조회 테스트")
    void getPostByIdTest() {
        // given
        Long postId = 1L;
        User user = User.builder()
                .id(1L)
                .name("User Name")
                .profileImg("profileImg.jpg")
                .build();
        Post post = Post.builder()
                .id(postId)
                .content("Content 1")
                .user(user)
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        PostDetailDto result = postService.getPostById(postId);

        // then
        assertNotNull(result);
        assertEquals(post.getId(), result.getId());
        assertEquals(post.getContent(), result.getContent());
        assertEquals(post.getUser().getName(), result.getName());
        assertEquals(post.getUser().getProfileImg(), result.getProfileImg());
        assertEquals(post.getUser().getId(), result.getUserId());
    }

    @Test
    @DisplayName("존재하지 않는 게시물 조회 시 예외 발생 테스트")
    void getPostByIdWithNonExistingPostTest() {
        // given
        Long postId = 1L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        assertThrows(BadRequestException.class, () -> {
            postService.getPostById(postId);
        });
    }

    @Test
    @DisplayName("게시물 삭제 테스트")
    void deletePostTest() {
        // given
        Long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .content("Content")
                .user(new User())
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        postService.deletePost(postId);

        // then
        verify(postRepository).findById(postId);
        verify(postRepository).deleteById(postId);
    }

    @Test
    @DisplayName("존재하지 않는 게시물 삭제 시 예외 발생 테스트")
    void deleteNonExistingPostTest() {
        // given
        Long postId = 1L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        assertThrows(BadRequestException.class, () -> {
            postService.deletePost(postId);
        });
    }

    @Test
    @DisplayName("게시물에 좋아요 추가 테스트")
    void likePostTest() {
        // given
        Long userId = 1L;
        Long postId = 1L;
        User user = User.builder().id(userId).build();
        Post post = Post.builder().id(postId).likes(new ArrayList<>()).user(user).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        postService.likePost(userId, postId);

        // then
        assertTrue(post.getLikes().stream().anyMatch(like -> like.getUserId().equals(userId)));
        verify(notificationRepository).mSave(userId, post.getUser().getId(), NotiType.LIKE.name(), postId);
    }

    @Test
    @DisplayName("이미 좋아요한 게시물 좋아요 취소 테스트")
    void unlikePostTest() {
        // given
        Long userId = 1L;
        Long postId = 1L;
        Like existingLike = Like.builder().userId(userId).build();
        User user = User.builder().id(userId).build();
        Post post = Post.builder().id(postId).likes(new ArrayList<>(Collections.singletonList(existingLike))).user(user).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        postService.likePost(userId, postId);

        // then
        assertFalse(post.getLikes().stream().anyMatch(like -> like.getUserId().equals(userId)));
    }

    @Test
    @DisplayName("댓글 추가 테스트")
    void addCommentTest() {
        // given
        Long userId = 1L;
        Long postId = 1L;
        CreateCommentDto createCommentDto = mock(CreateCommentDto.class);
        User user = User.builder().id(userId).name("User Name").profileImg("profileImg.jpg").build();
        Comment comment = Comment.builder().content("Comment Content").writer(user).build();
        Post post = Post.builder().id(postId).comments(new ArrayList<>()).user(user).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(createCommentDto.toEntity(user)).willReturn(comment);

        // when
        List<CommentDto> result = postService.addComment(userId, postId, createCommentDto);

        // then
        assertTrue(result.stream().anyMatch(cDto -> cDto.getContent().equals(comment.getContent())
                && cDto.getName().equals(user.getName())
                && cDto.getProfileImg().equals(user.getProfileImg())
                && cDto.getUserId().equals(user.getId())));
        verify(notificationRepository).mSave(userId, post.getUser().getId(), NotiType.COMMENT.name(), postId);
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void removeCommentTest() {
        // given: 필요한 데이터 및 모의 객체 설정
        Long userId = 1L;
        Long postId = 1L;
        Long commentId = 1L;
        User user = User.builder().id(userId).build();
        Comment comment = Comment.builder().id(commentId).writer(user).build();
        Post post = Post.builder().id(postId).comments(new ArrayList<>(Arrays.asList(comment))).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when: 테스트 대상 메소드 실행
        postService.removeComment(userId, postId, commentId);

        // then: 결과 검증
        assertFalse(post.getComments().contains(comment));
    }

    @Test
    @DisplayName("존재하지 않는 댓글 삭제 시 예외 발생 테스트")
    void removeNonExistingCommentTest() {
        // given: 존재하지 않는 댓글의 데이터 설정
        Long userId = 1L;
        Long postId = 1L;
        Long commentId = 1L;
        User user = User.builder().id(userId).build();
        Post post = Post.builder().id(postId).comments(new ArrayList<>()).build(); // 댓글 없는 게시물

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when & then: 예외 발생 검증
        assertThrows(BadRequestException.class, () -> {
            postService.removeComment(userId, postId, commentId);
        });
    }

}