package com.devin.dev.repository.post;

import com.devin.dev.controller.post.PostSearchCondition;
import com.devin.dev.dto.post.PostDetailsDto;
import com.devin.dev.dto.post.PostSimpleDto;
import com.devin.dev.dto.post.QPostDetailsDto;
import com.devin.dev.dto.post.QPostSimpleDto;
import com.devin.dev.entity.post.*;
import com.devin.dev.entity.user.User;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.devin.dev.entity.post.QPost.post;
import static com.devin.dev.entity.post.QPostTag.postTag;
import static com.devin.dev.entity.post.QSubject.subject;
import static com.devin.dev.entity.user.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class PostRepositoryQueryImpl implements PostRepositoryQuery {

    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional
    public Page<String> findPostnamePageByUser(User user, Pageable pageable) {
        QueryResults<String> results = queryFactory
                .select(post.title)
                .from(post)
                .where(post.user.eq(user))
                .orderBy(post.lastModifiedDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<String> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    /*
    select post.title, post.user.name, post.content, post.status
    from post
    where post.tag like tag
    and post.name like name
    and post.user.name like username

     */
    @Override
    public Page<PostSimpleDto> findPostDtoPageWithCondition(PostSearchCondition condition, Pageable pageable) {
        QueryResults<PostSimpleDto> results = queryFactory
                .selectDistinct(new QPostSimpleDto(
                        post.title,
                        user.name,
                        post.content,
                        post.status,
                        post.replies.size()
                ))
                .from(post)
                .innerJoin(post.user, user)
                .innerJoin(post.tags, postTag)
                .where(
                        usernameLike(condition.getUsername()),
                        titleLike(condition.getTitle()),
                        tagsInclude(condition.getTags())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<PostSimpleDto> content = results.getResults();

        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<PostSimpleDto> findPostDtoByUser(User user) {
        return queryFactory
                .select(new QPostSimpleDto(
                        post.title,
                        post.user.name,
                        post.content,
                        post.status,
                        post.replies.size()
                )) // QDTO 생성자 사용. @Projections 및 Q파일 컨파일
                .from(post)
                .where(post.user.eq(user))
                .fetch();
    }

    @Override
    public Page<Post> findAllByTagId(Long id, Pageable pageable) {
        QueryResults<Post> results = queryFactory
            .selectFrom(post)
            .where(post.tags.any().id.eq(id))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        List<Post> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Optional<PostDetailsDto> findDetailById(Long id) {
        Post post = queryFactory
                .selectFrom(QPost.post)
                .where(QPost.post.id.eq(id))
                .fetchOne();

        Optional<PostDetailsDto> postDetailsDtoOptional;
        if(post != null) {
            postDetailsDtoOptional = Optional.of(new PostDetailsDto(post));
        }
        else {
            postDetailsDtoOptional = Optional.empty();
        }

        return postDetailsDtoOptional;
    }

    private BooleanExpression usernameLike(String username) {
        return hasText(username) ? user.name.contains(username) : null;
    }

    private BooleanExpression titleLike(String title) {
        return hasText(title) ? post.title.contains(title) : null;
    }

    private BooleanExpression tagsInclude(List<String> tags) {
        if(tags == null)
            return null;

        JPAQuery<PostTag> where = queryFactory
                .select(postTag)
                .from(postTag)
                .innerJoin(postTag.tag, subject)
                .where(subject.name.in(tags));

        return postTag.in(where);//(where);
    }
}
