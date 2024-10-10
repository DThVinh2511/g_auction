package com.ghtk.auction.service.impl;

import com.ghtk.auction.dto.stomp.NotifyMessage;
import com.ghtk.auction.entity.Comment;
import com.ghtk.auction.entity.Notifycation;
import com.ghtk.auction.event.CommentEvent;
import com.ghtk.auction.event.NotifycationEvent;
import com.ghtk.auction.repository.NotifycationRepository;
import com.ghtk.auction.service.NotifycationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotifycationServiceImpl implements NotifycationService {
    final ApplicationEventPublisher eventPublisher;
    final NotifycationRepository notifycationRepository;
    @Override
    public void addNotifycation(Long auctionId, NotifyMessage notify) {
        Notifycation notifycation = Notifycation.builder()
                .auctionId(auctionId)
                .message(notify.getContent())
                .createdAt(notify.getCreatedAt())
                .build();
        var saved = notifycationRepository.save(notifycation);

        eventPublisher.publishEvent(new NotifycationEvent(
                saved.getId(), auctionId, notify.getContent(), notify.getCreatedAt()));
    }
}
