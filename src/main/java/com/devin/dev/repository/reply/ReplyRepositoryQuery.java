package com.devin.dev.repository.reply;

import com.devin.dev.dto.ReplyLikeDto;
import com.devin.dev.entity.post.Post;
import com.devin.dev.entity.reply.Reply;
import com.devin.dev.entity.reply.ReplyLike;
import com.devin.dev.entity.user.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReplyRepositoryQuery {

    List<Reply> findReplyPageByPost(Post post, Pageable pageable);

    Optional<ReplyLikeDto> findReplyLikeByReplyAndUser(Long replyId, User user);

    List<ReplyLikeDto> findReplyLikesById(Long replyId);

    Long findReplyLikeCountById(Long replyId);

    Optional<ReplyLike> findReplyLikeByLikeId(Long replyLikeId);

    boolean deleteByReplyLikeId(Long replyLikeId);
}
