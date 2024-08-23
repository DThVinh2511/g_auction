package com.ghtk.auction.repository.Custom.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import com.ghtk.auction.component.RedisTemplateFactory;
import com.ghtk.auction.dto.redis.AuctionRoom;
import com.ghtk.auction.dto.request.comment.CommentFilter;
import com.ghtk.auction.dto.stomp.BidMessage;
import com.ghtk.auction.dto.stomp.CommentMessage;
import com.ghtk.auction.entity.Comment;
import com.ghtk.auction.repository.CommentRepository;
import com.ghtk.auction.repository.Custom.AuctionSessionRepository;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class AuctionSessionRepositoryImpl implements AuctionSessionRepository {
    private final RedisTemplateFactory redisTemplateFactory;
    private final RedisTemplate<String, String> redisTemplate;
    private final CommentRepository commentRepository;
    

    @Override
    public boolean existsJoinable(Long auctionId, Long userId) {
        String key = getUserJoinableKey(userId);
        return redisTemplate.opsForSet().isMember(key, auctionId.toString());
    }
    @Override
    public List<Long> getJoinableByUser(Long userId) {
        String key = getUserJoinableKey(userId);
        return redisTemplate.opsForSet().members(key).stream()
            .map(Long::parseLong)
            .toList();
    }
    @Override
    public List<Long> getJoinableByAuction(Long auctionId) {
        String key = getAuctionJoinableKey(auctionId);
        return redisTemplate.opsForSet().members(key).stream()
            .map(Long::parseLong)
            .toList();
    }
    @Override
    public void addJoinable(Long auctionId, Long... userId) {
        String[] idStrings = new String[userId.length];
        for (int i = 0; i < userId.length; i++) {
            String userKey = getUserJoinableKey(userId[i]);
            redisTemplate.opsForSet().add(userKey, auctionId.toString());
            idStrings[i] = userId[i].toString();
        }

        String auctionKey = getAuctionJoinableKey(auctionId);
        redisTemplate.opsForSet().add(auctionKey, idStrings);
    }
    @Override
    public void deleteAllJoinableByAuction(Long auctionId) {
        String auctionKey = getAuctionJoinableKey(auctionId);
        Collection<String> userIds = redisTemplate.opsForSet().members(auctionKey);
        redisTemplate.delete(auctionKey);

        userIds.stream()
            .map(Long::parseLong)
            .forEach(userId -> {
                String userKey = getUserJoinableKey(userId);
                redisTemplate.opsForSet().remove(userKey, auctionId.toString());
            });
    }

    @Override
    public Optional<AuctionRoom> getAuctionRoom(Long auctionId) {
        String key = getAuctionRoomKey(auctionId);
        var template = redisTemplateFactory.get(AuctionRoom.class);
        return Optional.ofNullable(template.opsForValue().get(key));
    }
    @Override
    public void setAuctionRoom(Long auctionId, AuctionRoom auctionRoom) {
        String key = getAuctionRoomKey(auctionId);
        var template = redisTemplateFactory.get(AuctionRoom.class);
        template.opsForValue().set(key, auctionRoom);
    }
    @Override
    public void deleteAuctionRoom(Long auctionId) {
        String key = getAuctionRoomKey(auctionId);
        redisTemplate.delete(key);
    }

    @Override
    public Optional<LocalDateTime> getJoin(Long auctionId, Long userId) {
        String key = getJoinKey(auctionId, userId);
        return Optional.ofNullable(redisTemplate.opsForValue().get(key))
            .map(LocalDateTime::parse);
    }
    @Override
    public List<Pair<Long, LocalDateTime>> getJoinsByUser(Long userId) {
        String key = getUserJoinKey(userId);
        return redisTemplate.opsForSet().members(key).stream()
            .map(Long::parseLong)
            .map(auctionId -> Pair.of(auctionId, getJoin(auctionId, userId).get()))
            .toList();
    }
    @Override
    public List<Pair<Long, LocalDateTime>> getJoinsByAuction(Long auctionId) {
        String key = getAuctionJoinKey(auctionId);
        return redisTemplate.opsForSet().members(key).stream()
            .map(Long::parseLong)
            .map(userId -> Pair.of(userId, getJoin(auctionId, userId).get()))
            .toList();
    }
    @Override
    public void addJoin(Long auctionId, Long userId) {
        String key = getJoinKey(auctionId, userId);
        redisTemplate.opsForValue().set(key, LocalDateTime.now().toString());

        String auctionKey = getAuctionJoinKey(auctionId);
        redisTemplate.opsForSet().add(auctionKey, userId.toString());

        String userKey = getUserJoinKey(userId);
        redisTemplate.opsForSet().add(userKey, auctionId.toString());
    }
    @Override
    public void deleteJoin(Long auctionId, Long userId) {
        String key = getJoinKey(auctionId, userId);
        redisTemplate.delete(key);

        String auctionKey = getAuctionJoinKey(auctionId);
        redisTemplate.opsForSet().remove(auctionKey, userId.toString());
        
        String userKey = getUserJoinKey(userId);
        redisTemplate.opsForSet().remove(userKey, auctionId.toString());
    }
    @Override
    public void deleteAllJoinByUser(Long userId) {
        String userKey = getUserJoinKey(userId);
        Collection<String> auctionIds = redisTemplate.opsForSet().members(userKey);
        redisTemplate.delete(userKey);

        auctionIds.stream()
            .map(Long::parseLong)
            .forEach(auctionId -> {
                String joinKey = getJoinKey(auctionId, userId);
                redisTemplate.delete(joinKey);
                String auctionKey = getAuctionJoinKey(auctionId);
                redisTemplate.opsForSet().remove(auctionKey, userId.toString());
            });
    }

    @Override
    public void deleteAllJoinByAuction(Long auctionId) {
      String auctionKey = getAuctionJoinKey(auctionId);
      Collection<String> userIds = redisTemplate.opsForSet().members(auctionKey);
      redisTemplate.delete(auctionKey);

      userIds.stream()
          .map(Long::parseLong)
          .forEach(userId -> {
              String joinKey = getJoinKey(auctionId, userId);
              redisTemplate.delete(joinKey);
              String userKey = getUserJoinKey(userId);
              redisTemplate.opsForSet().remove(userKey, auctionId.toString());
          });
    }
    
    @Override
    public Optional<BidMessage> getLastBid(Long auctionId) {
        String key = getLastBidKey(auctionId);
        var template = redisTemplateFactory.get(BidMessage.class);
        return Optional.ofNullable(template.opsForValue().get(key));
    }
    @Override
    public List<BidMessage> getBids(Long auctionId) {
        String key = getBidsKey(auctionId);
        var template = redisTemplateFactory.get(BidMessage.class);
        return template.opsForList().range(key, 0, -1);
    }
    @Override
    public void addBid(Long auctionId, BidMessage auctionBid) {
        String bidsKey = getBidsKey(auctionId);
        String lastBidKey = getLastBidKey(auctionId);
        var template = redisTemplateFactory.get(BidMessage.class);
        template.opsForList().leftPush(bidsKey, auctionBid);
        template.opsForValue().set(lastBidKey, auctionBid);
    }
    @Override
    public void deleteAllBids(Long auctionId) {
        String bidsKey = getBidsKey(auctionId);
        redisTemplate.delete(bidsKey);
        String lastBidKey = getLastBidKey(auctionId);
        redisTemplate.delete(lastBidKey);
    }

    @Override
    public List<CommentMessage> getComments(Long auctionId) {
        return getComments(auctionId, new CommentFilter());
    }
    @Override
    public List<CommentMessage> getComments(Long auctionId, CommentFilter filter) {
        List<Comment> comments;
        LocalDateTime from = filter.getFrom();
        LocalDateTime to = filter.getTo();
        if (from != null) {
            if (to == null) {
                comments = commentRepository.findAllByAuctionIdAndCreatedAtAfter(auctionId, from);
            } else {
                comments = commentRepository.findAllByAuctionIdAndCreatedAtBetween(auctionId, from, to);
            }
        } else {
            comments = commentRepository.findAllByAuctionId(auctionId);
        }
        return comments.stream()
            .map(comment -> {
                return CommentMessage.builder()
                    .commentId(comment.getId())
                    .userId(comment.getUserId())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .build();
            })
            .toList();
    }
    @Override
    public void addComment(Long auctionId, CommentMessage comment) {
        Comment entity = Comment.builder()
            .auctionId(auctionId)
            .userId(comment.getUserId())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .build();
        commentRepository.save(entity);
    }
    @Override
    public void deleteAllComments(Long auctionId) {
        // no-op
    }



    private String getUserJoinableKey(Long userId) {
        return "user-joinable:" + userId;
    }
    private String getAuctionJoinableKey(Long auctionId) {
        return "auction-joinable:" + auctionId;
    }
    private String getAuctionRoomKey(Long auctionId) {
        return "auction-room:" + auctionId;
    }
    private String getJoinKey(Long auctionId, Long userId) {
        return "auction-user-join:" + auctionId + ":" + userId;
    }
    private String getUserJoinKey(Long userId) {
        return "user-join:" + userId;
    }
    private String getAuctionJoinKey(Long auctionId) {
        return "auction-join:" + auctionId;
    }
    private String getBidsKey(Long auctionId) {
        return "auction-bids:" + auctionId;
    }
    private String getLastBidKey(Long auctionId) {
        return "auction-last-bid:" + auctionId;
    }
}
